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
package com.vincestyling.asqliteplus.statement;

/**
 * A statement producer that use to producing <b>UPDATE</b> command of SQL language. The completed SQL
 * statement would be sent to {@link android.database.sqlite.SQLiteDatabase} objects to execute.
 *
 * @see com.vincestyling.asqliteplus.DBOverseer
 */
public class UpdateStatement extends Statement {
    /**
     * Producing an "UPDATE OR ROLLBACK ..." statement.
     * <p/>
     * ROLLBACK resolution algorithm aborts the current SQL statement when an applicable
     * constraint violation occurs and rolls back the current transaction if within.
     * <p/>
     * If no transaction is active, this algorithm works the same as the ABORT algorithm.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ROLLBACK
     */
    public static UpdateStatement orRollback(CharSequence table) {
        return produce(table, "ROLLBACK");
    }

    /**
     * Producing an "UPDATE OR REPLACE ..." statement.
     * <p/>
     * When an UNIQUE or PRIMARY KEY constraint violation occurs, the REPLACE algorithm silently deletes
     * pre-existing rows that are causing the constraint violation prior to updating the current row.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_REPLACE
     */
    public static UpdateStatement orReplace(CharSequence table) {
        return produce(table, "REPLACE");
    }

    /**
     * Producing an "UPDATE OR IGNORE ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the IGNORE resolution algorithm skips the one row that contains
     * the constraint violation and continues processing subsequent rows of the SQL statement as if nothing went wrong.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_IGNORE
     */
    public static UpdateStatement orIgnore(CharSequence table) {
        return produce(table, "IGNORE");
    }

    /**
     * Producing an "UPDATE OR ABORT ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the ABORT resolution algorithm aborts the current SQL statement
     * with an SQLITE_CONSTRAINT error and backs out any changes made by the current SQL statement, but changes caused
     * by prior SQL statements within the same transaction are preserved and the transaction remains active.
     * <p/>
     * This is the default behavior and the behavior specified by the SQL standard.
     * That means "UPDATE OR ABORT tbl_name SET ..." equivalent to "UPDATE tbl_name SET ...".
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ABORT
     */
    public static UpdateStatement orAbort(CharSequence table) {
        return produce(table, "ABORT");
    }

    /**
     * Producing an "UPDATE OR FAIL ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the FAIL resolution algorithm aborts the current SQL statement
     * with an SQLITE_CONSTRAINT error. But the FAIL resolution does not back out prior changes of the SQL statement
     * that failed nor does it end the transaction.
     * <p/>
     * For example, if an UPDATE statement encountered a constraint violation on the 100th row that it attempts to update,
     * then the first 99 row changes are preserved but changes to rows 100 and beyond never occur.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_FAIL
     */
    public static UpdateStatement orFail(CharSequence table) {
        return produce(table, "FAIL");
    }

    /**
     * Producing an UPDATE statement with a clause. This method should be
     * work with orXXX() which in this Statement, don't call it directly.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     */
    protected static UpdateStatement produce(CharSequence table, CharSequence clause) {
        UpdateStatement updateStmt = new UpdateStatement();
        updateStmt.statement.append("UPDATE");
        if (clause != null) updateStmt.statement.append(" OR ").append(clause);
        updateStmt.statement.append(' ').append(table).append(" SET");
        return updateStmt;
    }

    /**
     * Producing an UPDATE statement without clause.
     *
     * @param table the name of the table to updating.
     * @return the created statement.
     */
    public static UpdateStatement produce(CharSequence table) {
        return produce(table, null);
    }

    private short columnCount;

    /**
     * Put a single field pair to constructing this statement.
     *
     * @param column column name.
     * @param value  column value, will auto quoting if instance of {@link CharSequence}.
     * @return this statement.
     */
    public UpdateStatement set(CharSequence column, Object value) {
        if (++columnCount > 1) statement.append(',');
        statement.append(' ').append(column);
        eq(value);
        return this;
    }
}
