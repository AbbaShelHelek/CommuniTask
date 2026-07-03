package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.AuthRepository;
import com.example.communitask.data.repository.UserRepository;
import com.example.communitask.model.UserProfile;
import com.example.communitask.util.UiState;

/**
 * Holds authentication state and delegates auth operations to repositories.
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> signedIn;
    private final MutableLiveData<UiState<Void>> loginState;
    private final MutableLiveData<UiState<Void>> registrationState;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        signedIn = new MutableLiveData<>();
        loginState = new MutableLiveData<>(UiState.idle());
        registrationState = new MutableLiveData<>(UiState.idle());
    }

    public LiveData<Boolean> getSignedIn() {
        return signedIn;
    }

    public LiveData<UiState<Void>> getLoginState() {
        return loginState;
    }

    public LiveData<UiState<Void>> getRegistrationState() {
        return registrationState;
    }

    public void checkCurrentSession() {
        signedIn.setValue(authRepository.isUserSignedIn());
    }

    public void login(String email, String password) {
        loginState.setValue(UiState.loading());
        authRepository.login(email, password)
                .addOnSuccessListener(authResult -> {
                    signedIn.setValue(true);
                    loginState.setValue(UiState.success(null));
                })
                .addOnFailureListener(exception -> {
                    signedIn.setValue(false);
                    loginState.setValue(UiState.error(null));
                });
    }

    public void register(String displayName, String email, String password) {
        registrationState.setValue(UiState.loading());
        authRepository.register(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authRepository.getCurrentUserId();
                    if (uid == null) {
                        signedIn.setValue(false);
                        registrationState.setValue(UiState.error(null));
                        return;
                    }

                    UserProfile userProfile = new UserProfile(
                            uid,
                            displayName,
                            email,
                            System.currentTimeMillis()
                    );
                    userRepository.createUserProfile(userProfile)
                            .addOnSuccessListener(unused -> {
                                signedIn.setValue(true);
                                registrationState.setValue(UiState.success(null));
                            })
                            .addOnFailureListener(exception -> {
                                signedIn.setValue(true);
                                registrationState.setValue(UiState.error(null));
                            });
                })
                .addOnFailureListener(exception -> {
                    signedIn.setValue(false);
                    registrationState.setValue(UiState.error(null));
                });
    }
}
