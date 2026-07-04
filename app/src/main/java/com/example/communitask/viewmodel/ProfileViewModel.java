package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.AuthRepository;
import com.example.communitask.data.repository.UserRepository;
import com.example.communitask.model.UserProfile;
import com.example.communitask.util.UiState;
import com.example.communitask.util.ValidationUtils;

public class ProfileViewModel extends ViewModel {

    public static final String ERROR_NO_USER = "NO_USER";
    public static final String ERROR_PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
    public static final String ERROR_INVALID_DISPLAY_NAME = "INVALID_DISPLAY_NAME";

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<UiState<UserProfile>> profileState;
    private final MutableLiveData<UiState<Void>> updateState;
    private final MutableLiveData<UiState<Void>> logoutState;
    private UserProfile currentProfile;

    public ProfileViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        profileState = new MutableLiveData<>(UiState.idle());
        updateState = new MutableLiveData<>(UiState.idle());
        logoutState = new MutableLiveData<>(UiState.idle());
    }

    public LiveData<UiState<UserProfile>> getProfileState() {
        return profileState;
    }

    public LiveData<UiState<Void>> getUpdateState() {
        return updateState;
    }

    public LiveData<UiState<Void>> getLogoutState() {
        return logoutState;
    }

    public void loadCurrentUserProfile() {
        String uid = authRepository.getCurrentUserId();
        if (uid == null) {
            profileState.setValue(UiState.error(ERROR_NO_USER));
            return;
        }

        profileState.setValue(UiState.loading());
        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        profileState.setValue(UiState.error(ERROR_PROFILE_NOT_FOUND));
                        return;
                    }

                    UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                    if (profile == null) {
                        profileState.setValue(UiState.error(ERROR_PROFILE_NOT_FOUND));
                        return;
                    }

                    if (profile.getUid() == null || profile.getUid().trim().isEmpty()) {
                        profile.setUid(documentSnapshot.getId());
                    }
                    currentProfile = profile;
                    profileState.setValue(UiState.success(profile));
                })
                .addOnFailureListener(exception -> profileState.setValue(UiState.error(null)));
    }

    public void updateDisplayName(String displayName) {
        String trimmedDisplayName = getTrimmedText(displayName);
        if (!ValidationUtils.isRequiredTextValid(trimmedDisplayName)) {
            updateState.setValue(UiState.error(ERROR_INVALID_DISPLAY_NAME));
            return;
        }

        if (currentProfile == null) {
            updateState.setValue(UiState.error(ERROR_PROFILE_NOT_FOUND));
            return;
        }

        UserProfile updatedProfile = new UserProfile(
                currentProfile.getUid(),
                trimmedDisplayName,
                currentProfile.getEmail(),
                currentProfile.getCreatedAtMillis()
        );

        updateState.setValue(UiState.loading());
        userRepository.updateUserProfile(updatedProfile)
                .addOnSuccessListener(unused -> {
                    currentProfile = updatedProfile;
                    updateState.setValue(UiState.success(null));
                    profileState.setValue(UiState.success(updatedProfile));
                })
                .addOnFailureListener(exception -> updateState.setValue(UiState.error(null)));
    }

    public void logout() {
        logoutState.setValue(UiState.loading());
        authRepository.logout();
        currentProfile = null;
        logoutState.setValue(UiState.success(null));
    }

    private String getTrimmedText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
