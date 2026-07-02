package com.example.communitask.data.repository;

import com.example.communitask.data.firebase.FirebaseProvider;
import com.example.communitask.model.UserProfile;
import com.example.communitask.util.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Repository boundary for Firestore user profile documents.
 */
public class UserRepository {

    public Task<Void> createUserProfile(UserProfile userProfile) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.USERS_COLLECTION)
                .document(userProfile.getUid())
                .set(userProfile);
    }

    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.USERS_COLLECTION)
                .document(uid)
                .get();
    }

    public Task<Void> updateUserProfile(UserProfile userProfile) {
        return FirebaseProvider.getFirestore()
                .collection(AppConstants.USERS_COLLECTION)
                .document(userProfile.getUid())
                .set(userProfile);
    }
}
