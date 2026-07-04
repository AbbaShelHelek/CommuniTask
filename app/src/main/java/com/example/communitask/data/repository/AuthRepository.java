package com.example.communitask.data.repository;

import com.example.communitask.data.firebase.FirebaseProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Repository boundary for Firebase Authentication operations.
 */
public class AuthRepository {

    public Task<AuthResult> register(String email, String password) {
        return FirebaseProvider.getAuth().createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return FirebaseProvider.getAuth().signInWithEmailAndPassword(email, password);
    }

    public void logout() {
        FirebaseProvider.getAuth().signOut();
    }

    public boolean isUserSignedIn() {
        return FirebaseProvider.getAuth().getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseProvider.getAuth().getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getUid();
    }
}
