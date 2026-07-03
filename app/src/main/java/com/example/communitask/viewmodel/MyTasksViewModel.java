package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.AuthRepository;
import com.example.communitask.data.repository.TaskRepository;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.util.UiState;

import java.util.List;

public class MyTasksViewModel extends ViewModel {

    public static final String ERROR_NO_USER = "NO_USER";

    private final AuthRepository authRepository = new AuthRepository();
    private final TaskRepository taskRepository = new TaskRepository();
    private final MutableLiveData<UiState<List<CommunityTask>>> tasksState =
            new MutableLiveData<>(UiState.idle());

    public LiveData<UiState<List<CommunityTask>>> getTasksState() {
        return tasksState;
    }

    public void loadMyTasks() {
        String currentUserId = authRepository.getCurrentUserId();
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            tasksState.setValue(UiState.error(ERROR_NO_USER));
            return;
        }

        tasksState.setValue(UiState.loading());
        taskRepository.loadTasksForOwner(currentUserId, new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<CommunityTask> tasks) {
                tasksState.setValue(UiState.success(tasks));
            }

            @Override
            public void onError(Exception exception) {
                String message = exception == null ? null : exception.getMessage();
                tasksState.setValue(UiState.error(message));
            }
        });
    }
}
