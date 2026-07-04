package com.example.communitask.ui.tasklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitask.R;
import com.example.communitask.databinding.ItemCommunityTaskBinding;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(CommunityTask task);
    }

    private final List<CommunityTask> tasks = new ArrayList<>();
    private final OnTaskClickListener listener;

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCommunityTaskBinding binding = ItemCommunityTaskBinding.inflate(
                inflater,
                parent,
                false
        );
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void submitTasks(List<CommunityTask> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ItemCommunityTaskBinding binding;

        TaskViewHolder(ItemCommunityTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CommunityTask task, OnTaskClickListener listener) {
            Context context = binding.getRoot().getContext();

            binding.taskTitleText.setText(getTextOrFallback(
                    task.getTitle(),
                    context.getString(R.string.task_list_title_fallback)
            ));
            binding.taskDescriptionText.setText(getTextOrFallback(
                    task.getDescription(),
                    context.getString(R.string.task_list_description_fallback)
            ));

            String category = getTextOrFallback(
                    task.getCategory(),
                    context.getString(R.string.task_list_category_fallback)
            );
            binding.taskCategoryText.setText(
                    context.getString(R.string.task_list_category_format, category)
            );

            String status = getStatusText(context, task.getStatus());
            binding.taskStatusText.setText(
                    context.getString(R.string.task_list_status_format, status)
            );

            String owner = getTextOrFallback(
                    task.getOwnerName(),
                    context.getString(R.string.task_list_owner_fallback)
            );
            binding.taskOwnerText.setText(
                    context.getString(R.string.task_list_owner_format, owner)
            );

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }

        private String getStatusText(Context context, TaskStatus status) {
            if (status == null) {
                return context.getString(R.string.task_list_status_fallback);
            }

            switch (status) {
                case OPEN:
                    return context.getString(R.string.task_list_status_open);
                case IN_PROGRESS:
                    return context.getString(R.string.task_list_status_in_progress);
                case COMPLETED:
                    return context.getString(R.string.task_list_status_completed);
                default:
                    return context.getString(R.string.task_list_status_fallback);
            }
        }

        private String getTextOrFallback(String value, String fallback) {
            if (value == null || value.trim().isEmpty()) {
                return fallback;
            }
            return value.trim();
        }
    }
}
