/**
 * Copyright (C) 2015 Vince Styling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
