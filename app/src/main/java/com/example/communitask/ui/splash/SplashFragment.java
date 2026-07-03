package com.example.communitask.ui.splash;

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
import com.example.communitask.databinding.FragmentSplashBinding;
import com.example.communitask.viewmodel.AuthViewModel;

public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getSignedIn().observe(getViewLifecycleOwner(), isSignedIn -> {
            NavController navController = NavHostFragment.findNavController(this);
            if (navController.getCurrentDestination() == null
                    || navController.getCurrentDestination().getId() != R.id.splashFragment) {
                return;
            }

            int actionId = Boolean.TRUE.equals(isSignedIn)
                    ? R.id.action_splashFragment_to_taskFeedFragment
                    : R.id.action_splashFragment_to_loginFragment;
            navController.navigate(actionId); // Using equals to avoid null exceptions.
        });

        authViewModel.checkCurrentSession();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
