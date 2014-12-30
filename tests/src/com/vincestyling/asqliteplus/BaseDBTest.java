package com.vincestyling.asqliteplus;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

public abstract class BaseDBTest extends AndroidTestCase {
    protected SQLiteOpenHelper mDBHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mDBHelper = new SQLiteOpenHelper(getContext(), null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                onCreateDB(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };
    }

    protected abstract void onCreateDB(SQLiteDatabase db);
}
