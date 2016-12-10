package com.done.todolist.contract;

import android.support.annotation.NonNull;

import com.done.todolist.BasePresenter;
import com.done.todolist.BaseView;
import com.done.todolist.entity.Task;
import com.done.todolist.entity.TasksFilterType;

import java.util.List;

/**
 * Created by Done on 2016/12/8.
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }


    interface Presenter extends BasePresenter{
        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestTask);

        void completeTask(@NonNull Task completedTask);

        void activeTask(@NonNull Task activeTask);

        void clearCompleteTask();

        void setFiltering(TasksFilterType type);

        TasksFilterType getFiltering();

    }
}
