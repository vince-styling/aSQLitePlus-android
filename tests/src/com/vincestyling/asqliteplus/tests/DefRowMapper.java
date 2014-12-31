package com.vincestyling.asqliteplus.tests;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import com.vincestyling.asqliteplus.RowMapper;

public class DefRowMapper implements RowMapper<ContentValues> {
    @Override
    public ContentValues mapRow(Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        return contentValues;
    }
}
