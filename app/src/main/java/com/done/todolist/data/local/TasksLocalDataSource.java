package com.done.todolist.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.done.todolist.data.TaskDataSource;
import com.done.todolist.data.local.TasksPersistenceContract.TaskEntry;
import com.done.todolist.entity.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Done on 2016/12/6.
 * 以数据库的数据源来实现。
 */

public class TasksLocalDataSource implements TaskDataSource {

    private static TasksLocalDataSource INSTANCE;

    private TaskDbHelper mDbHelper;

    private TasksLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new TaskDbHelper(context);
    }

    public static TasksLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context);
        }
        return INSTANCE;
    }
    String[] projection = {TaskEntry.COLUMN_NAME_ENTRY_ID,
            TaskEntry.COLUMN_NAME_TITLE,
            TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskEntry.COLUMN_NAME_COMPLETED};
    /**
     *  如果数据库不存在或表是空的,则调用{@link LoadTasksCallback#onDataNotAvailable()}
     * @param callback
     */
    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        Cursor cursor = db.query(TaskEntry.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String itemId = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                Task task = new Task(itemId, title, description, completed);
                tasks.add(task);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        if (tasks.isEmpty()) {
            callback.onDataNotAvailable();
        }else {
            callback.onTasksLoaded(tasks);
        }

    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        Task task = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            task = new Task(itemId, title, description, completed);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        if (task == null) {
            callback.onDataNotAvailable();
        }else {
            callback.onTaskLoaded(task);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getTaskId());
        contentValues.put(TaskEntry.COLUMN_NAME_TITLE, task.getTaskTitle());
        contentValues.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getTaskDescription());
        contentValues.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TaskEntry.TABLE_NAME, null, contentValues);

        db.close();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getTaskId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};

        db.update(TaskEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        db.close();
    }

    @Override
    public void activateTask(@NonNull Task task) {
       activateTask(task.getTaskId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};

        db.update(TaskEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        db.close();
    }

    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }
}
