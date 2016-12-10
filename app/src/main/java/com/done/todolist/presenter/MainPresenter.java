package com.done.todolist.presenter;

import android.support.annotation.NonNull;

import com.done.todolist.contract.MainContract;
import com.done.todolist.data.TaskDataSource;
import com.done.todolist.data.TasksRepository;
import com.done.todolist.entity.Task;
import com.done.todolist.entity.TasksFilterType;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Done on 2016/12/8.
 */

public class MainPresenter implements MainContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final MainContract.View mTaskView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TYPE;

    private boolean mFirstLoad = true;

    public MainPresenter(@NonNull TasksRepository tasksRepository, @NonNull MainContract.View taskView) {

        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mTaskView = checkNotNull(taskView, "tasksView cannot be null!");

        mTaskView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        //如果任务成功添加，使用snackbar提示
//        if(A)

    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void addNewTask() {
        mTaskView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestTask) {
        checkNotNull(requestTask);
        mTaskView.showTaskDetailsUi(requestTask.getTaskId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask);
        mTasksRepository.completeTask(completedTask.getTaskId());
        mTaskView.showTaskMarkedComplete();
        loadTasks(false,false);
    }

    @Override
    public void activeTask(@NonNull Task activeTask) {
        checkNotNull(activeTask);
        mTasksRepository.activateTask(activeTask.getTaskId());
        mTaskView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompleteTask() {
        mTasksRepository.clearCompletedTasks();
        mTaskView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TasksFilterType type) {
        mCurrentFiltering = type;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TaskDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    public void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTaskView.setLoadingIndicator(true);
        }
        if(forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        mTasksRepository.getTasks(new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<Task>();
                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TYPE:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETE_TASK:
                            if (task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                if (!mTaskView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mTaskView.setLoadingIndicator(true);
                }
                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mTaskView.isActive()) {
                    return;
                }
                mTaskView.showLoadingTasksError();
            }
        });
    }


    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            processEmptyTasks();
        }else {
            mTaskView.showTasks(tasks);

            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTaskView.showActiveFilterLabel();
                break;
            case COMPLETE_TASK:
                mTaskView.showCompletedFilterLabel();
                break;
            default:
                mTaskView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTaskView.showNoActiveTasks();
                break;
            case COMPLETE_TASK:
                mTaskView.showNoCompletedTasks();
                break;
            default:
                mTaskView.showNoTasks();
                break;
        }
    }
}
