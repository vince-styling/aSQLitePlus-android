package com.vincestyling.asqliteplus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.vincestyling.asqliteplus.statement.Function;
import com.vincestyling.asqliteplus.statement.QueryStatement;

public class QueryStatementTest extends BaseDBTest {
    @Override
    protected void onCreateDB(SQLiteDatabase db) {
    }

    public void testBasically() {
        assertNotNull(mDBHelper);

        String version;
        version = getString("select sqlite_version()");
        assertNotNull(version);

        version = new DBOverseer(mDBHelper).getString(QueryStatement.produce(Function.sqliteVersion()));
        assertNull(version);
    }

    public String getString(String sql) {
        Cursor cursor = null;
        try {
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToFirst()) return cursor.getString(0);
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }
}
