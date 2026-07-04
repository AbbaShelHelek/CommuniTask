package com.example.communitask.ui.feed;

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
import com.example.communitask.databinding.FragmentTaskFeedBinding;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.ui.tasklist.TaskAdapter;
import com.example.communitask.util.UiState;
import com.example.communitask.viewmodel.TaskFeedViewModel;

import java.util.List;

public class TaskFeedFragment extends Fragment {

    private FragmentTaskFeedBinding binding;
    private TaskFeedViewModel viewModel;
    private TaskAdapter taskAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskFeedViewModel.class);

        setupRecyclerView();
        binding.createTaskButton.setOnClickListener(v -> navigateToTaskEditor());
        viewModel.getTasksState().observe(getViewLifecycleOwner(), this::renderTasksState);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadTasks();
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
            showErrorState();
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

    private void showErrorState() {
        binding.taskRecyclerView.setVisibility(View.GONE);
        binding.taskLoadingGroup.setVisibility(View.GONE);
        binding.taskEmptyText.setVisibility(View.GONE);
        binding.taskErrorText.setText(R.string.task_feed_error);
        binding.taskErrorText.setVisibility(View.VISIBLE);
    }

    private void navigateToTaskDetails(CommunityTask task) {
        if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
            return;
        }

        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.taskFeedFragment) {
            return;
        }

        Bundle arguments = new Bundle();
        arguments.putString("taskId", task.getId());
        navController.navigate(R.id.action_taskFeedFragment_to_taskDetailFragment, arguments);
    }

    private void navigateToTaskEditor() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.taskFeedFragment) {
            return;
        }
        navController.navigate(R.id.action_taskFeedFragment_to_taskEditorFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
