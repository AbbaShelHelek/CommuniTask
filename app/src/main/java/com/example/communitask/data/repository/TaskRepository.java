package com.example.communitask.data.repository;

import com.example.communitask.data.firebase.FirebaseProvider;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.util.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * Repository boundary for Firestore community task documents.
 */
public class TaskRepository {

    private static final String OWNER_UID_FIELD = "ownerUid";

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
}
