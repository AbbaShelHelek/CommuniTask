package com.example.communitask.ui.task;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.communitask.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConfirmDeleteTaskDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "confirm_delete_task_request";
    public static final String RESULT_CONFIRMED = "confirmed";
    public static final String RESULT_TASK_ID = "taskId";
    public static final String ARG_TASK_ID = "taskId";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String taskId = arguments == null ? null : arguments.getString(ARG_TASK_ID);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.task_delete_dialog_title)
                .setMessage(R.string.task_delete_dialog_message)
                .setPositiveButton(R.string.task_delete_dialog_confirm, (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(RESULT_CONFIRMED, true);
                    result.putString(RESULT_TASK_ID, taskId);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                    dismiss();
                })
                .setNegativeButton(R.string.task_delete_dialog_cancel, (dialog, which) -> dismiss())
                .create();
    }
}
