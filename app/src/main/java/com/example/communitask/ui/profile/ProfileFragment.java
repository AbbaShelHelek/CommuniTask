package com.example.communitask.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.communitask.R;
import com.example.communitask.databinding.FragmentProfileBinding;
import com.example.communitask.model.UserProfile;
import com.example.communitask.util.UiState;
import com.example.communitask.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding.saveProfileButton.setOnClickListener(v -> submitDisplayNameUpdate());
        binding.logoutButton.setOnClickListener(v -> profileViewModel.logout());

        profileViewModel.getProfileState()
                .observe(getViewLifecycleOwner(), this::renderProfileState);
        profileViewModel.getUpdateState()
                .observe(getViewLifecycleOwner(), this::renderUpdateState);
        profileViewModel.getLogoutState()
                .observe(getViewLifecycleOwner(), this::renderLogoutState);

        profileViewModel.loadCurrentUserProfile();
    }

    private void submitDisplayNameUpdate() {
        binding.displayNameInputLayout.setError(null);
        String displayName = getTrimmedInputText(binding.displayNameEditText.getText());
        profileViewModel.updateDisplayName(displayName);
    }

    private void renderProfileState(UiState<UserProfile> state) {
        if (state == null) {
            return;
        }

        if (state.getStatus() == UiState.Status.LOADING) {
            showLoadingState();
            return;
        }

        if (state.getStatus() == UiState.Status.ERROR) {
            showProfileError(state.getErrorMessage());
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            bindProfile(state.getData());
            showProfileContent();
        }
    }

    private void renderUpdateState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        binding.saveProfileButton.setEnabled(!isLoading);

        if (state.getStatus() == UiState.Status.ERROR) {
            if (ProfileViewModel.ERROR_INVALID_DISPLAY_NAME.equals(state.getErrorMessage())) {
                binding.displayNameInputLayout.setError(
                        getString(R.string.profile_error_display_name_required));
                hideMessage();
                return;
            }

            showErrorMessage(R.string.profile_error_update_failed);
            return;
        }

        binding.displayNameInputLayout.setError(null);

        if (state.getStatus() == UiState.Status.SUCCESS) {
            showSuccessMessage(R.string.profile_update_success);
        }
    }

    private void renderLogoutState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        binding.logoutButton.setEnabled(!isLoading);

        if (state.getStatus() == UiState.Status.ERROR) {
            showErrorMessage(R.string.profile_error_load_failed);
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            showSuccessMessage(R.string.profile_logout_success);
        }
    }

    private void bindProfile(UserProfile profile) {
        if (profile == null) {
            return;
        }

        binding.displayNameEditText.setText(getDisplayText(profile.getDisplayName()));
        binding.emailEditText.setText(getDisplayText(profile.getEmail()));
    }

    private void showLoadingState() {
        binding.profileLoadingGroup.setVisibility(View.VISIBLE);
        binding.displayNameInputLayout.setVisibility(View.GONE);
        binding.emailInputLayout.setVisibility(View.GONE);
        binding.saveProfileButton.setVisibility(View.GONE);
        binding.logoutButton.setEnabled(false);
        hideMessage();
    }

    private void showProfileContent() {
        binding.profileLoadingGroup.setVisibility(View.GONE);
        binding.displayNameInputLayout.setVisibility(View.VISIBLE);
        binding.emailInputLayout.setVisibility(View.VISIBLE);
        binding.saveProfileButton.setVisibility(View.VISIBLE);
        binding.saveProfileButton.setEnabled(true);
        binding.logoutButton.setEnabled(true);
    }

    private void showProfileError(String errorMessage) {
        binding.profileLoadingGroup.setVisibility(View.GONE);
        binding.displayNameInputLayout.setVisibility(View.GONE);
        binding.emailInputLayout.setVisibility(View.GONE);
        binding.saveProfileButton.setVisibility(View.GONE);
        binding.logoutButton.setEnabled(true);

        if (ProfileViewModel.ERROR_NO_USER.equals(errorMessage)) {
            showErrorMessage(R.string.profile_error_signed_out);
            return;
        }

        if (ProfileViewModel.ERROR_PROFILE_NOT_FOUND.equals(errorMessage)) {
            showErrorMessage(R.string.profile_error_not_found);
            return;
        }

        showErrorMessage(R.string.profile_error_load_failed);
    }

    private void showErrorMessage(int messageResId) {
        binding.profileErrorText.setText(messageResId);
        binding.profileErrorText.setTextColor(requireContext().getColor(R.color.auth_error_text));
        binding.profileErrorText.setVisibility(View.VISIBLE);
    }

    private void showSuccessMessage(int messageResId) {
        binding.profileErrorText.setText(messageResId);
        binding.profileErrorText.setTextColor(requireContext().getColor(R.color.auth_accent));
        binding.profileErrorText.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        binding.profileErrorText.setVisibility(View.GONE);
    }

    private String getTrimmedInputText(CharSequence value) {
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    private String getDisplayText(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
