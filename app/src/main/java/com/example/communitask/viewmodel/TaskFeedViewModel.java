package com.example.communitask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.communitask.data.repository.TaskRepository;
import com.example.communitask.model.CommunityTask;
import com.example.communitask.util.UiState;

import java.util.List;

public class TaskFeedViewModel extends ViewModel {

    private final TaskRepository taskRepository = new TaskRepository();
    private final MutableLiveData<UiState<List<CommunityTask>>> tasksState =
            new MutableLiveData<>(UiState.idle());

    public LiveData<UiState<List<CommunityTask>>> getTasksState() {
        return tasksState;
    }

    public void loadTasks() {
        tasksState.setValue(UiState.loading());
        taskRepository.loadAllTasks(new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<CommunityTask> tasks) {
                tasksState.setValue(UiState.success(tasks));
            }

            @Override
            public void onError(Exception exception) {
                String message = exception == null ? null : exception.getMessage();
                tasksState.setValue(UiState.error(message));
            }
        });
    }
}
