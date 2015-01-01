package com.vincestyling.asqliteplus.tests;

import android.database.Cursor;
import com.vincestyling.asqliteplus.RowMapper;

public class SimpleRowMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(Cursor cursor) {
        return cursor.getColumnCount();
    }
}
