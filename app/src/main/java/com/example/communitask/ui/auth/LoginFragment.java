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
import com.example.communitask.databinding.FragmentLoginBinding;
import com.example.communitask.util.UiState;
import com.example.communitask.util.ValidationUtils;
import com.example.communitask.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private boolean hasNavigated;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.loginButton.setOnClickListener(v -> submitLogin());
        binding.registerButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_registerFragment));

        authViewModel.getLoginState().observe(getViewLifecycleOwner(), this::renderLoginState);
    }

    private void submitLogin() {
        clearErrors();

        String email = getInputText(binding.emailEditText.getText());
        String password = getInputText(binding.passwordEditText.getText());

        boolean isValid = true;
        if (!ValidationUtils.isEmailValid(email)) {
            binding.emailInputLayout.setError(getString(R.string.auth_error_invalid_email));
            isValid = false;
        }

        if (!ValidationUtils.isPasswordValid(password)) {
            binding.passwordInputLayout.setError(getString(R.string.auth_error_invalid_password));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        authViewModel.login(email, password);
    }

    private void renderLoginState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        binding.loginButton.setEnabled(!isLoading);

        if (state.getStatus() == UiState.Status.ERROR) {
            binding.loginErrorText.setText(R.string.auth_error_login_failed);
            binding.loginErrorText.setVisibility(View.VISIBLE);
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS && !hasNavigated) {
            navigateToFeed();
        }
    }

    private void navigateToFeed() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.loginFragment) {
            return;
        }

        hasNavigated = true;
        navController.navigate(R.id.action_loginFragment_to_taskFeedFragment);
    }

    private void clearErrors() {
        binding.emailInputLayout.setError(null);
        binding.passwordInputLayout.setError(null);
        binding.loginErrorText.setVisibility(View.GONE);
    }

    private String getInputText(CharSequence value) {
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
