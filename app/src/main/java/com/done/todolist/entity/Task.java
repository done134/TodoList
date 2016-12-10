package com.done.todolist.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Task Entity
 * @author Done
 * @time 2016/11/25 21:33
 */
public final class Task {

    @NonNull
    private final String taskId;

    @Nullable
    private final String taskTitle;

    @Nullable
    private final String taskDescription;

    private final boolean completed;

    /**
     * 如果任务已经有了一个id，则使用此构造函数来指定一个已存在的任务。
     * @param taskId
     * @param taskTitle
     * @param TaskDescription
     * @param completed
     */
    public Task(@NonNull String taskId, @Nullable String taskTitle, @Nullable String TaskDescription, boolean completed) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = TaskDescription;
        this.completed = completed;
    }

    /**
     * 使用该构造函数创建一个新的Active Task
     * @param taskTitle
     * @param taskDescription
     */
    public Task(@Nullable String taskTitle, @Nullable String taskDescription) {
        this(UUID.randomUUID().toString(),taskTitle, taskDescription, false);
    }

    /**
     *
     * @param taskTitle
     * @param taskDescription
     * @param completed
     */
    public Task(@Nullable String taskTitle, @Nullable String taskDescription, boolean completed) {
        this(UUID.randomUUID().toString(), taskTitle, taskDescription, completed);
    }

    public Task(@NonNull String taskId, String taskTitle, String taskDescription) {
        this(taskId, taskTitle, taskDescription, false);
    }


    @NonNull
    public String getTaskId() {
        return taskId;
    }

    @Nullable
    public String getTaskTitle() {
        return taskTitle;
    }

    @Nullable
    public String getTaskDescription() {
        return taskDescription;
    }


    public String getTitleForList() {
        if (Strings.isNullOrEmpty(taskTitle)) {
            return taskDescription;
        }else {
            return taskTitle;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return !completed;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(taskTitle) &&
                Strings.isNullOrEmpty(taskDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equal(taskId, task.taskId) &&
                Objects.equal(taskTitle, task.taskTitle) &&
                Objects.equal(taskDescription, task.taskDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId, taskTitle, taskDescription);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", completed=" + completed +
                '}';
    }
}
