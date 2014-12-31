package com.vincestyling.asqliteplus.tests;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import com.vincestyling.asqliteplus.DBOverseer;

public abstract class BaseDBTestCase extends AndroidTestCase {
    protected DBOverseer mDBOverseer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mDBOverseer = new DBOverseer(new SQLiteOpenHelper(getContext(), "asqliteplus.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        });
        mDBOverseer.setIsDebug(true);
    }
}
