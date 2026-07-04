package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.AuthRepository;
import com.example.communitask.data.repository.TaskRepository;
import com.example.communitask.data.repository.UserRepository;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.model.TaskStatus;
import com.example.communitask.model.UserProfile;
import com.example.communitask.util.UiState;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Provides task loading, ownership state, and owner-restricted task write operations.
 */
public class TaskEditorViewModel extends ViewModel {

    private final TaskRepository taskRepository;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<UiState<CommunityTask>> taskState;
    private final MutableLiveData<UiState<Void>> saveState;
    private final MutableLiveData<UiState<Void>> deleteState;
    private final MutableLiveData<Boolean> ownerPermissionState;
    private CommunityTask currentTask;

    public TaskEditorViewModel() {
        taskRepository = new TaskRepository();
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        taskState = new MutableLiveData<>(UiState.idle());
        saveState = new MutableLiveData<>(UiState.idle());
        deleteState = new MutableLiveData<>(UiState.idle());
        ownerPermissionState = new MutableLiveData<>(false);
    }

    public LiveData<UiState<CommunityTask>> getTaskState() {
        return taskState;
    }

    public LiveData<UiState<Void>> getSaveState() {
        return saveState;
    }

    public LiveData<UiState<Void>> getDeleteState() {
        return deleteState;
    }

    public LiveData<Boolean> getOwnerPermissionState() {
        return ownerPermissionState;
    }

    /**
     * Loads one task and updates ownership state for the current authenticated user.
     */
    public void loadTask(String taskId) {
        if (isBlank(taskId)) {
            clearLoadedTask();
            taskState.setValue(UiState.error(null));
            return;
        }

        taskState.setValue(UiState.loading());
        taskRepository.getTaskById(taskId)
                .addOnSuccessListener(this::handleLoadedTaskDocument)
                .addOnFailureListener(exception -> {
                    clearLoadedTask();
                    taskState.setValue(UiState.error(null));
                });
    }

    /**
     * Creates a new task for the signed-in user and stores the profile display name as ownerName.
     */
    public void createTask(String title, String description, String category) {
        String currentUserId = authRepository.getCurrentUserId();
        if (isBlank(currentUserId)) {
            saveState.setValue(UiState.error(null));
            return;
        }

        saveState.setValue(UiState.loading());
        userRepository.getUserProfile(currentUserId)
                .addOnSuccessListener(documentSnapshot -> createTaskWithProfile(
                        currentUserId,
                        documentSnapshot,
                        title,
                        description,
                        category
                ))
                .addOnFailureListener(exception -> saveState.setValue(UiState.error(null)));
    }

    /**
     * Updates editable fields on the loaded task when the current user owns it.
     */
    public void updateTask(String title, String description, String category) {
        if (!isCurrentUserOwner() || currentTask == null || isBlank(currentTask.getId())) {
            saveState.setValue(UiState.error(null));
            return;
        }

        saveState.setValue(UiState.loading());
        CommunityTask updatedTask = new CommunityTask(
                currentTask.getId(),
                cleanText(title),
                cleanOptionalText(description),
                cleanText(category),
                currentTask.getStatus(),
                currentTask.getOwnerUid(),
                currentTask.getOwnerName(),
                currentTask.getCreatedAtMillis(),
                System.currentTimeMillis()
        );

        taskRepository.updateTask(updatedTask)
                .addOnSuccessListener(unused -> {
                    currentTask = updatedTask;
                    ownerPermissionState.setValue(isCurrentUserOwner());
                    taskState.setValue(UiState.success(updatedTask));
                    saveState.setValue(UiState.success(null));
                })
                .addOnFailureListener(exception -> saveState.setValue(UiState.error(null)));
    }

    /**
     * Deletes the loaded task only when the current user owns the matching task ID.
     */
    public void deleteTask(String taskId) {
        if (!isCurrentUserOwner()
                || currentTask == null
                || isBlank(taskId)
                || !taskId.equals(currentTask.getId())) {
            deleteState.setValue(UiState.error(null));
            return;
        }

        deleteState.setValue(UiState.loading());
        taskRepository.deleteTask(taskId)
                .addOnSuccessListener(unused -> {
                    clearLoadedTask();
                    deleteState.setValue(UiState.success(null));
                })
                .addOnFailureListener(exception -> deleteState.setValue(UiState.error(null)));
    }

    private void handleLoadedTaskDocument(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot == null || !documentSnapshot.exists()) {
            clearLoadedTask();
            taskState.setValue(UiState.success(null));
            return;
        }

        CommunityTask task = documentSnapshot.toObject(CommunityTask.class);
        if (task == null) {
            clearLoadedTask();
            taskState.setValue(UiState.error(null));
            return;
        }

        if (isBlank(task.getId())) {
            task.setId(documentSnapshot.getId());
        }
        currentTask = task;
        ownerPermissionState.setValue(isCurrentUserOwner());
        taskState.setValue(UiState.success(task));
    }

    private void createTaskWithProfile(String currentUserId, DocumentSnapshot documentSnapshot,
                                       String title, String description, String category) {
        if (documentSnapshot == null || !documentSnapshot.exists()) {
            saveState.setValue(UiState.error(null));
            return;
        }

        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
        if (userProfile == null) {
            saveState.setValue(UiState.error(null));
            return;
        }

        long now = System.currentTimeMillis();
        CommunityTask task = new CommunityTask(
                null,
                cleanText(title),
                cleanOptionalText(description),
                cleanText(category),
                TaskStatus.OPEN,
                currentUserId,
                cleanOptionalText(userProfile.getDisplayName()),
                now,
                now
        );

        taskRepository.createTask(task)
                .addOnSuccessListener(unused -> {
                    currentTask = task;
                    ownerPermissionState.setValue(true);
                    saveState.setValue(UiState.success(null));
                })
                .addOnFailureListener(exception -> saveState.setValue(UiState.error(null)));
    }

    private void clearLoadedTask() {
        currentTask = null;
        ownerPermissionState.setValue(false);
    }

    private boolean isCurrentUserOwner() {
        String currentUserId = authRepository.getCurrentUserId();
        return currentTask != null
                && !isBlank(currentUserId)
                && currentUserId.equals(currentTask.getOwnerUid());
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private String cleanOptionalText(String value) {
        return cleanText(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
