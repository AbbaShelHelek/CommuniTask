package com.example.communitask.data.repository;

import com.example.communitask.data.firebase.FirebaseProvider;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.util.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repository boundary for Firestore community task documents.
 */
public class TaskRepository {

    private static final String OWNER_UID_FIELD = "ownerUid";

    public interface TaskListCallback {
        void onSuccess(List<CommunityTask> tasks);

        void onError(Exception exception);
    }

    public Task<Void> createTask(CommunityTask communityTask) {
        if (communityTask.getId() == null || communityTask.getId().trim().isEmpty()) {
            DocumentReference documentReference = FirebaseProvider.getFirestore()
                    .collection(AppConstants.TASKS_COLLECTION)
                    .document();
            communityTask.setId(documentReference.getId());
            return documentReference.set(communityTask);
        }

        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION)
                .document(communityTask.getId())
                .set(communityTask);
    }

    public Task<DocumentSnapshot> getTaskById(String taskId) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION)
                .document(taskId)
                .get();
    }

    public Query getAllTasksQuery() {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION);
    }

    public Query getTasksForOwnerQuery(String ownerUid) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION)
                .whereEqualTo(OWNER_UID_FIELD, ownerUid);
    }

    public void loadAllTasks(TaskListCallback callback) {
        getAllTasksQuery()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                        callback.onSuccess(mapTaskDocuments(queryDocumentSnapshots)))
                .addOnFailureListener(callback::onError);
    }

    public void loadTasksForOwner(String ownerUid, TaskListCallback callback) {
        if (ownerUid == null || ownerUid.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        getTasksForOwnerQuery(ownerUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                        callback.onSuccess(mapTaskDocuments(queryDocumentSnapshots)))
                .addOnFailureListener(callback::onError);
    }

    public Task<Void> updateTask(CommunityTask communityTask) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION)
                .document(communityTask.getId())
                .set(communityTask);
    }

    public Task<Void> deleteTask(String taskId) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.TASKS_COLLECTION)
                .document(taskId)
                .delete();
    }

    private List<CommunityTask> mapTaskDocuments(Iterable<QueryDocumentSnapshot> documents) {
        List<CommunityTask> tasks = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            CommunityTask task = document.toObject(CommunityTask.class);
            if (task.getId() == null || task.getId().trim().isEmpty()) {
                task.setId(document.getId());
            }
            tasks.add(task);
        }

        Collections.sort(tasks, new Comparator<CommunityTask>() {
            @Override
            public int compare(CommunityTask first, CommunityTask second) {
                return Long.compare(second.getCreatedAtMillis(), first.getCreatedAtMillis());
            }
        });
        return tasks;
    }
}
