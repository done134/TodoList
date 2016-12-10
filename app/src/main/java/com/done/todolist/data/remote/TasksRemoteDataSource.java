package com.done.todolist.data.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.done.todolist.data.TaskDataSource;
import com.done.todolist.entity.Task;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Done on 2016/12/6.
 */

public class TasksRemoteDataSource implements TaskDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<String, Task> TASK_SERVICE_DATA;

    static {
        TASK_SERVICE_DATA = new LinkedHashMap<>();
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASK_SERVICE_DATA.put(newTask.getTaskId(), newTask);
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private TasksRemoteDataSource() {}

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        //通过延迟执行模拟网络。
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTasksLoaded(Lists.newArrayList(TASK_SERVICE_DATA.values()));
            }
        },SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallback callback) {
        final Task task = TASK_SERVICE_DATA.get(taskId);
        //通过延迟执行模拟网络。
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTaskLoaded(task);
            }
        },SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASK_SERVICE_DATA.put(task.getTaskId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completeTask = new Task(task.getTaskId(), task.getTaskTitle(), task.getTaskDescription(), true);
        TASK_SERVICE_DATA.put(task.getTaskId(), completeTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
//        Task task = TASK_SERVICE_DATA.get(taskId);
//        Task completeTask = new Task(task.getTaskId(), task.getTaskTitle(), task.getTaskDescription(), true);
//        TASK_SERVICE_DATA.put(task.getTaskId(), completeTask);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTaskId(), task.getTaskTitle(), task.getTaskDescription());
        TASK_SERVICE_DATA.put(task.getTaskId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
//        Task task = TASK_SERVICE_DATA.get(taskId);
//        Task activeTask = new Task(task.getTaskId(), task.getTaskTitle(), task.getTaskDescription());
//        TASK_SERVICE_DATA.put(task.getTaskId(), activeTask);

    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASK_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        TASK_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASK_SERVICE_DATA.remove(taskId);
    }
}
