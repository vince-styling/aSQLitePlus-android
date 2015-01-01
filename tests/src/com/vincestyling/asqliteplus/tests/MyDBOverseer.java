package com.vincestyling.asqliteplus.tests;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vincestyling.asqliteplus.DBOverseer;

public class MyDBOverseer extends DBOverseer {
    private MyDBOverseer(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static MyDBOverseer mInstance;

    public static MyDBOverseer get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        if (mInstance == null) mInstance = new MyDBOverseer(new SQLiteOpenHelper(ctx, "asqliteplus.db", null, 1) {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

            @Override
            public void onCreate(SQLiteDatabase db) {}
        });
    }
}
