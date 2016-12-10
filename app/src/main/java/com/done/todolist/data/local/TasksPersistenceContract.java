package com.done.todolist.data.local;

import android.provider.BaseColumns;

/**
 * Created by Done on 2016/12/6.
 */

public class TasksPersistenceContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TasksPersistenceContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
