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
import com.example.communitask.databinding.FragmentTaskEditorBinding;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.util.UiState;
import com.example.communitask.util.ValidationUtils;
import com.example.communitask.viewmodel.TaskEditorViewModel;

public class TaskEditorFragment extends Fragment {

    private static final String ARG_TASK_ID = "taskId";

    private FragmentTaskEditorBinding binding;
    private TaskEditorViewModel viewModel;
    private String taskId;
    private boolean editMode;
    private boolean canSave = true;
    private boolean hasLoadedTask;
    private boolean hasFilledFields;
    private Boolean latestOwnerPermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskEditorViewModel.class);
        taskId = getArguments() == null ? null : getArguments().getString(ARG_TASK_ID);
        editMode = taskId != null && !taskId.trim().isEmpty();

        binding.taskEditorTitleText.setText(editMode
                ? R.string.task_editor_edit_title
                : R.string.task_editor_create_title);
        binding.saveTaskButton.setOnClickListener(v -> submitTask());

        viewModel.getTaskState().observe(getViewLifecycleOwner(), this::renderTaskState);
        viewModel.getOwnerPermissionState().observe(getViewLifecycleOwner(), this::renderOwnerState);
        viewModel.getSaveState().observe(getViewLifecycleOwner(), this::renderSaveState);

        if (editMode) {
            viewModel.loadTask(taskId);
        }
    }

    private void submitTask() {
        clearErrors();

        if (!canSave) {
            binding.taskEditorErrorText.setText(R.string.task_editor_permission_error);
            binding.taskEditorErrorText.setVisibility(View.VISIBLE);
            return;
        }

        String title = getTrimmedInputText(binding.titleEditText.getText());
        String description = getTrimmedInputText(binding.descriptionEditText.getText());
        String category = getTrimmedInputText(binding.categoryEditText.getText());

        boolean isValid = true;
        if (!ValidationUtils.isTaskTitleValid(title)) {
            binding.titleInputLayout.setError(getString(R.string.task_editor_title_error));
            isValid = false;
        }

        if (!ValidationUtils.isRequiredTextValid(category)) {
            binding.categoryInputLayout.setError(getString(R.string.task_editor_category_error));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        if (editMode) {
            viewModel.updateTask(title, description, category);
        } else {
            viewModel.createTask(title, description, category);
        }
    }

    private void renderTaskState(UiState<CommunityTask> state) {
        if (!editMode || state == null) {
            return;
        }

        if (state.getStatus() == UiState.Status.LOADING) {
            binding.taskEditorLoadingGroup.setVisibility(View.VISIBLE);
            binding.taskEditorErrorText.setVisibility(View.GONE);
            binding.saveTaskButton.setEnabled(false);
            return;
        }

        binding.taskEditorLoadingGroup.setVisibility(View.GONE);

        if (state.getStatus() == UiState.Status.ERROR) {
            binding.taskEditorErrorText.setText(R.string.task_editor_load_error);
            binding.taskEditorErrorText.setVisibility(View.VISIBLE);
            canSave = false;
            setFormEnabled(false);
            return;
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            CommunityTask task = state.getData();
            if (task == null) {
                binding.taskEditorErrorText.setText(R.string.task_editor_missing_error);
                binding.taskEditorErrorText.setVisibility(View.VISIBLE);
                canSave = false;
                setFormEnabled(false);
                return;
            }

            hasLoadedTask = true;
            if (!hasFilledFields) {
                binding.titleEditText.setText(task.getTitle());
                binding.descriptionEditText.setText(task.getDescription());
                binding.categoryEditText.setText(task.getCategory());
                hasFilledFields = true;
            }
            applyOwnerPermission();
        }
    }

    private void renderOwnerState(Boolean isOwner) {
        latestOwnerPermission = isOwner;
        if (editMode && hasLoadedTask) {
            applyOwnerPermission();
        }
    }

    private void renderSaveState(UiState<Void> state) {
        if (state == null) {
            return;
        }

        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        binding.saveTaskButton.setEnabled(!isLoading && canSave);

        if (state.getStatus() == UiState.Status.ERROR) {
            binding.taskEditorErrorText.setText(R.string.task_editor_save_error);
            binding.taskEditorErrorText.setVisibility(View.VISIBLE);
            return;
        }

        if (state.getStatus() != UiState.Status.LOADING) {
            binding.taskEditorErrorText.setVisibility(View.GONE);
        }

        if (state.getStatus() == UiState.Status.SUCCESS) {
            NavController navController = NavHostFragment.findNavController(this);
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == R.id.taskEditorFragment) {
                navController.navigateUp();
            }
        }
    }

    private void applyOwnerPermission() {
        if (!editMode) {
            canSave = true;
            setFormEnabled(true);
            binding.taskEditorErrorText.setVisibility(View.GONE);
            return;
        }

        canSave = Boolean.TRUE.equals(latestOwnerPermission);
        setFormEnabled(canSave);
        if (canSave) {
            binding.taskEditorErrorText.setVisibility(View.GONE);
        } else {
            binding.taskEditorErrorText.setText(R.string.task_editor_permission_error);
            binding.taskEditorErrorText.setVisibility(View.VISIBLE);
        }
    }

    private void clearErrors() {
        binding.titleInputLayout.setError(null);
        binding.categoryInputLayout.setError(null);
        binding.taskEditorErrorText.setVisibility(View.GONE);
    }

    private void setFormEnabled(boolean enabled) {
        binding.titleEditText.setEnabled(enabled);
        binding.descriptionEditText.setEnabled(enabled);
        binding.categoryEditText.setEnabled(enabled);
        binding.saveTaskButton.setEnabled(enabled);
    }

    private String getTrimmedInputText(CharSequence value) {
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
