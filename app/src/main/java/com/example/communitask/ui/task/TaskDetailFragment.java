package com.example.communitask.ui.task;

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
import com.example.communitask.databinding.FragmentTaskDetailBinding;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.model.TaskStatus;
import com.example.communitask.util.UiState;
import com.example.communitask.viewmodel.TaskEditorViewModel;

public class TaskDetailFragment extends Fragment {

    private static final String ARG_TASK_ID = "taskId";

    private FragmentTaskDetailBinding binding;
    private TaskEditorViewModel viewModel;
    private String taskId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskEditorViewModel.class);
        taskId = getArguments() == null ? null : getArguments().getString(ARG_TASK_ID);

        registerDeleteResultListener();
        binding.editTaskButton.setOnClickListener(v -> navigateToEditor());
        binding.deleteTaskButton.setOnClickListener(v -> showDeleteDialog());

        viewModel.getTaskState().observe(getViewLifecycleOwner(), this::renderTaskState);
        viewModel.getOwnerPermissionState().observe(getViewLifecycleOwner(), this::renderOwnerState);
        viewModel.getDeleteState().observe(getViewLifecycleOwner(), this::renderDeleteState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTask();
    }

    private void loadTask() {
        if (taskId == null || taskId.trim().isEmpty()) {
            showMissingState();
            return;
        }
        viewModel.loadTask(taskId);
    }

    private void registerDeleteResultListener() {
        getParentFragmentManager().setFragmentResultListener(
                ConfirmDeleteTaskDialogFragment.REQUEST_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean confirmed = result.getBoolean(
                            ConfirmDeleteTaskDialogFragment.RESULT_CONFIRMED,
                            false
                    );
                    String confirmedTaskId = result.getString(
                            ConfirmDeleteTaskDialogFragment.RESULT_TASK_ID
                    );
                    if (confirmed && taskId != null && taskId.equals(confirmedTaskId)) {
                        viewModel.deleteTask(taskId);
                    }
                }
        );
    }

    private void renderTaskState(UiState<CommunityTask> state) {
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
            CommunityTask task = state.getData();
            if (task == null) {
                showMissingState();
                return;
            }
            renderTask(task);
            showContentState();
        }
    }

    private void renderOwnerState(Boolean isOwner) {
        boolean canManage = Boolean.TRUE.equals(isOwner);
        binding.taskManagementGroup.setVisibility(canManage ? View.VISIBLE : View.GONE);
        binding.editTaskButton.setEnabled(canManage);
        binding.deleteTaskButton.setEnabled(canManage);
    }

    private void renderDeleteState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        boolean canManage = binding.taskManagementGroup.getVisibility() == View.VISIBLE;
        binding.editTaskButton.setEnabled(!isLoading && canManage);
        binding.deleteTaskButton.setEnabled(!isLoading && canManage);

        if (state.getStatus() == UiState.Status.ERROR) {
            binding.taskDetailErrorText.setText(R.string.task_detail_delete_error);
            binding.taskDetailErrorText.setVisibility(View.VISIBLE);
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            NavController navController = NavHostFragment.findNavController(this);
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == R.id.taskDetailFragment) {
                navController.navigateUp();
            }
        }
    }

    private void renderTask(CommunityTask task) {
        binding.taskTitleText.setText(getTextOrFallback(
                task.getTitle(),
                getString(R.string.task_list_title_fallback)
        ));
        binding.taskDescriptionText.setText(getTextOrFallback(
                task.getDescription(),
                getString(R.string.task_list_description_fallback)
        ));
        binding.taskCategoryText.setText(getString(
                R.string.task_detail_category_format,
                getTextOrFallback(task.getCategory(), getString(R.string.task_list_category_fallback))
        ));
        binding.taskStatusText.setText(getString(
                R.string.task_detail_status_format,
                getStatusText(task.getStatus())
        ));
        binding.taskOwnerText.setText(getString(
                R.string.task_detail_owner_format,
                getTextOrFallback(task.getOwnerName(), getString(R.string.task_list_owner_fallback))
        ));
    }

    private void navigateToEditor() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.taskDetailFragment) {
            return;
        }

        Bundle arguments = new Bundle();
        arguments.putString(ARG_TASK_ID, taskId);
        navController.navigate(R.id.action_taskDetailFragment_to_taskEditorFragment, arguments);
    }

    private void showDeleteDialog() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.taskDetailFragment) {
            return;
        }

        Bundle arguments = new Bundle();
        arguments.putString(ConfirmDeleteTaskDialogFragment.ARG_TASK_ID, taskId);
        navController.navigate(
                R.id.action_taskDetailFragment_to_confirmDeleteTaskDialogFragment,
                arguments
        );
    }

    private void showLoadingState() {
        binding.taskDetailLoadingGroup.setVisibility(View.VISIBLE);
        binding.taskDetailContent.setVisibility(View.GONE);
        binding.taskDetailErrorText.setVisibility(View.GONE);
        binding.taskDetailMissingText.setVisibility(View.GONE);
    }

    private void showContentState() {
        binding.taskDetailLoadingGroup.setVisibility(View.GONE);
        binding.taskDetailContent.setVisibility(View.VISIBLE);
        binding.taskDetailErrorText.setVisibility(View.GONE);
        binding.taskDetailMissingText.setVisibility(View.GONE);
    }

    private void showErrorState() {
        binding.taskDetailLoadingGroup.setVisibility(View.GONE);
        binding.taskDetailContent.setVisibility(View.GONE);
        binding.taskDetailErrorText.setText(R.string.task_detail_load_error);
        binding.taskDetailErrorText.setVisibility(View.VISIBLE);
        binding.taskDetailMissingText.setVisibility(View.GONE);
    }

    private void showMissingState() {
        binding.taskDetailLoadingGroup.setVisibility(View.GONE);
        binding.taskDetailContent.setVisibility(View.GONE);
        binding.taskDetailErrorText.setVisibility(View.GONE);
        binding.taskDetailMissingText.setVisibility(View.VISIBLE);
    }

    private String getStatusText(TaskStatus status) {
        if (status == null) {
            return getString(R.string.task_list_status_fallback);
        }

        switch (status) {
            case OPEN:
                return getString(R.string.task_list_status_open);
            case IN_PROGRESS:
                return getString(R.string.task_list_status_in_progress);
            case COMPLETED:
                return getString(R.string.task_list_status_completed);
            default:
                return getString(R.string.task_list_status_fallback);
        }
    }

    private String getTextOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
