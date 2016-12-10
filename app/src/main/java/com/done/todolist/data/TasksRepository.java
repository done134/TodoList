package com.done.todolist.data;

import android.support.annotation.NonNull;

import com.done.todolist.entity.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Done on 2016/11/26.
 */

public class TasksRepository implements TaskDataSource {

    private static TasksRepository INSTANCE = null;
    private final TaskDataSource mTaskRemoteDataSource;
    private final TaskDataSource mTaskLocalDataSource;

    Map<String,Task> mCahceTasks;

    boolean mCacheIsDirty = false;

    /**
     * 防止直接实例化
     * @param mTaskRemoteDataSource
     * @param mTaskLocalDataSource
     */
    private TasksRepository(@NonNull TaskDataSource mTaskRemoteDataSource, @NonNull TaskDataSource mTaskLocalDataSource) {
        this.mTaskRemoteDataSource = checkNotNull(mTaskRemoteDataSource);
        this.mTaskLocalDataSource = checkNotNull(mTaskLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TaskDataSource tasksRemoteDataSource,
                                              TaskDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TaskDataSource, TaskDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);
        if (mCahceTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<Task>(mCahceTasks.values()));
            return;
        }

        if (mCacheIsDirty) {
            //如果缓存已失效则从网络获取数据
            getTaskFromRemoteDataSource(callback);
        }else {
            mTaskLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<Task>(mCahceTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTaskFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskLocalDataSource.saveTask(task);
        mTaskRemoteDataSource.saveTask(task);
        if (mCahceTasks == null) {
            mCahceTasks = new LinkedHashMap<>();
        }
        mCahceTasks.put(task.getTaskId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskLocalDataSource.completeTask(task);
        mTaskRemoteDataSource.completeTask(task);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskRemoteDataSource.activateTask(task);
        mTaskLocalDataSource.activateTask(task);
        Task activeTask = new Task(task.getTaskId(), task.getTaskTitle(), task.getTaskDescription());
        if (mCahceTasks == null) {
            mCahceTasks = new LinkedHashMap<>();
        }
        mCahceTasks.put(task.getTaskId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTaskRemoteDataSource.clearCompletedTasks();
        mTaskLocalDataSource.clearCompletedTasks();

        if (mCahceTasks == null) {
            mCahceTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> iterator = mCahceTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Task> entry = iterator.next();
            if (entry.getValue().isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTaskRemoteDataSource.deleteAllTasks();
        mTaskLocalDataSource.deleteAllTasks();
        if (mCahceTasks == null) {
            mCahceTasks = new LinkedHashMap<>();
        }
        mCahceTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTaskLocalDataSource.deleteTask(checkNotNull(taskId));
        mTaskRemoteDataSource.deleteTask(checkNotNull(taskId));
        mCahceTasks.remove(taskId);
    }

    private void getTaskFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTaskRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<Task>(mCahceTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if (mCahceTasks == null) {
            mCahceTasks = new LinkedHashMap<>();
        }
        mCahceTasks.clear();
        for (Task task: tasks) {
            mCahceTasks.put(task.getTaskId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTaskLocalDataSource.deleteAllTasks();
        for (Task task: tasks) {
            mTaskLocalDataSource.saveTask(task);
        }
    }

    @NonNull
    private Task getTaskWithId(String taskId) {
        checkNotNull(taskId);
        if (mCahceTasks == null || mCahceTasks.isEmpty()) {
            return null;
        }else {
            return mCahceTasks.get(taskId);
        }
    }


}
