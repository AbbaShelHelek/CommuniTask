package com.example.communitask.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.communitask.R;
import com.example.communitask.databinding.FragmentRegisterBinding;
import com.example.communitask.util.UiState;
import com.example.communitask.util.ValidationUtils;
import com.example.communitask.viewmodel.AuthViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.registerButton.setOnClickListener(v -> submitRegistration());
        binding.loginButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigateUp());

        authViewModel.getRegistrationState()
                .observe(getViewLifecycleOwner(), this::renderRegistrationState);
    }

    private void submitRegistration() {
        clearErrors();

        if (authViewModel.hasPendingProfileCreation()) {
            authViewModel.retryPendingProfileCreation();
            return;
        }

        String displayName = getTrimmedInputText(binding.displayNameEditText.getText());
        String email = getTrimmedInputText(binding.emailEditText.getText());
        String password = getRawInputText(binding.passwordEditText.getText());
        String confirmPassword = getRawInputText(binding.confirmPasswordEditText.getText());

        boolean isValid = true;
        if (!ValidationUtils.isRequiredTextValid(displayName)) {
            binding.displayNameInputLayout.setError(
                    getString(R.string.auth_error_display_name_required));
            isValid = false;
        }

        if (!ValidationUtils.isEmailValid(email)) {
            binding.emailInputLayout.setError(getString(R.string.auth_error_invalid_email));
            isValid = false;
        }

        if (!ValidationUtils.isPasswordValid(password)) {
            binding.passwordInputLayout.setError(getString(R.string.auth_error_invalid_password));
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(
                    getString(R.string.auth_error_password_mismatch));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        authViewModel.register(displayName, email, password);
    }

    private void renderRegistrationState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        binding.registerButton.setEnabled(!isLoading);

        if (state.getStatus() == UiState.Status.ERROR) {
            int errorMessageId = authViewModel.hasPendingProfileCreation()
                    ? R.string.auth_error_profile_setup_failed
                    : R.string.auth_error_registration_failed;
            binding.registerErrorText.setText(errorMessageId);
            binding.registerErrorText.setVisibility(View.VISIBLE);
            return;
        }

        binding.registerErrorText.setVisibility(View.GONE);

        if (state.getStatus() == UiState.Status.SUCCESS) {
            navigateToFeed();
        }
    }

    private void navigateToFeed() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.registerFragment) {
            return;
        }

        navController.navigate(R.id.action_registerFragment_to_taskFeedFragment);
    }

    private void clearErrors() {
        binding.displayNameInputLayout.setError(null);
        binding.emailInputLayout.setError(null);
        binding.passwordInputLayout.setError(null);
        binding.confirmPasswordInputLayout.setError(null);
        binding.registerErrorText.setVisibility(View.GONE);
    }

    private String getTrimmedInputText(CharSequence value) {
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    private String getRawInputText(CharSequence value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
