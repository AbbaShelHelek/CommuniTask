package com.example.communitask.ui.mytasks;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.communitask.R;
import com.example.communitask.databinding.FragmentMyTasksBinding;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.ui.tasklist.TaskAdapter;
import com.example.communitask.util.UiState;
import com.example.communitask.viewmodel.MyTasksViewModel;

import java.util.List;

public class MyTasksFragment extends Fragment {

    private FragmentMyTasksBinding binding;
    private MyTasksViewModel viewModel;
    private TaskAdapter taskAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MyTasksViewModel.class);

        setupRecyclerView();
        viewModel.getTasksState().observe(getViewLifecycleOwner(), this::renderTasksState);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadMyTasks();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(this::navigateToTaskDetails);
        binding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.taskRecyclerView.setAdapter(taskAdapter);
    }

    private void renderTasksState(UiState<List<CommunityTask>> state) {
        if (state == null) {
            return;
        }

        if (state.getStatus() == UiState.Status.LOADING) {
            showLoadingState();
            return;
        }

        if (state.getStatus() == UiState.Status.ERROR) {
            showErrorState(state.getErrorMessage());
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            List<CommunityTask> tasks = state.getData();
            if (tasks == null || tasks.isEmpty()) {
                taskAdapter.submitTasks(null);
                showEmptyState();
                return;
            }

            taskAdapter.submitTasks(tasks);
            showListState();
        }
    }

    private void showLoadingState() {
        binding.taskRecyclerView.setVisibility(View.GONE);
        binding.taskLoadingGroup.setVisibility(View.VISIBLE);
        binding.taskEmptyText.setVisibility(View.GONE);
        binding.taskErrorText.setVisibility(View.GONE);
    }

    private void showListState() {
        binding.taskRecyclerView.setVisibility(View.VISIBLE);
        binding.taskLoadingGroup.setVisibility(View.GONE);
        binding.taskEmptyText.setVisibility(View.GONE);
        binding.taskErrorText.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.taskRecyclerView.setVisibility(View.GONE);
        binding.taskLoadingGroup.setVisibility(View.GONE);
        binding.taskEmptyText.setVisibility(View.VISIBLE);
        binding.taskErrorText.setVisibility(View.GONE);
    }

    private void showErrorState(String errorMessage) {
        binding.taskRecyclerView.setVisibility(View.GONE);
        binding.taskLoadingGroup.setVisibility(View.GONE);
        binding.taskEmptyText.setVisibility(View.GONE);

        if (MyTasksViewModel.ERROR_NO_USER.equals(errorMessage)) {
            binding.taskErrorText.setText(R.string.my_tasks_signed_out);
        } else {
            binding.taskErrorText.setText(R.string.my_tasks_error);
        }
        binding.taskErrorText.setVisibility(View.VISIBLE);
    }

    private void navigateToTaskDetails(CommunityTask task) {
        if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
            return;
        }

        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.myTasksFragment) {
            return;
        }

        Bundle arguments = new Bundle();
        arguments.putString("taskId", task.getId());
        navController.navigate(R.id.action_myTasksFragment_to_taskDetailFragment, arguments);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
