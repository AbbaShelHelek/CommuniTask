package com.example.communitask.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Central access point for Firebase services used by repositories.
 */
public final class FirebaseProvider {

    private static FirebaseAuth auth;
    private static FirebaseFirestore firestore;

    private FirebaseProvider() {
    }

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }
}
