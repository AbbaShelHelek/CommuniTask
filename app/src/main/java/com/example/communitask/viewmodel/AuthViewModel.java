package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.AuthRepository;
import com.example.communitask.util.UiState;

/**
 * Holds authentication state and delegates auth operations to repositories.
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> signedIn;
    private final MutableLiveData<UiState<Void>> loginState;
    private final MutableLiveData<UiState<Void>> registrationState;

    public AuthViewModel() {
        authRepository = new AuthRepository();
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
}
