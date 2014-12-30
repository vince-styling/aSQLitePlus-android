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
 * A statement producer that use to producing <b>INSERT</b> command of SQL language. The completed SQL
 * statement would be sent to {@link android.database.sqlite.SQLiteDatabase} objects to execute.
 *
 * @see com.vincestyling.asqliteplus.DBOverseer
 */
public class CreateStatement extends Statement {
    /**
     * Producing an "INSERT OR ROLLBACK INTO ..." statement.
     * <p/>
     * ROLLBACK resolution algorithm aborts the current SQL statement when an applicable
     * constraint violation occurs and rolls back the current transaction if within.
     * <p/>
     * If no transaction is active, this algorithm works the same as the ABORT algorithm.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ROLLBACK
     */
    public static CreateStatement orRollback(CharSequence table) {
        return produce(table, "ROLLBACK");
    }

    /**
     * Producing an "INSERT OR REPLACE INTO ..."(equivalent to "REPLACE INTO ...") statement.
     * <p/>
     * When an UNIQUE or PRIMARY KEY constraint violation occurs, the REPLACE algorithm silently deletes
     * pre-existing rows that are causing the constraint violation prior to inserting the current row.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     * <p/>
     * <strong>Note:</strong> For compatibility with MySQL, SQLite allows us use the single keyword REPLACE
     * as an alias for "INSERT OR REPLACE". So if you're looking for a method which building a statement
     * like "REPLACE INTO ...", then this would be equivalent.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_REPLACE
     */
    public static CreateStatement orReplace(CharSequence table) {
        return produce(table, "REPLACE");
    }

    /**
     * Producing an "INSERT OR IGNORE INTO ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the IGNORE resolution algorithm skips the one row that contains
     * the constraint violation and continues processing subsequent rows of the SQL statement as if nothing went wrong.
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_IGNORE
     */
    public static CreateStatement orIgnore(CharSequence table) {
        return produce(table, "IGNORE");
    }

    /**
     * Producing an "INSERT OR ABORT INTO ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the ABORT resolution algorithm aborts the current SQL statement
     * with an SQLITE_CONSTRAINT error and backs out any changes made by the current SQL statement, but changes caused
     * by prior SQL statements within the same transaction are preserved and the transaction remains active.
     * <p/>
     * This is the default behavior and the behavior specified by the SQL standard.
     * That means "INSERT OR ABORT INTO ..." equivalent to "INSERT INTO ...".
     * <p/>
     * Checking the <a href="http://sqlite.org/lang_conflict.html">SQLite documentation</a> for more details.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ABORT
     */
    public static CreateStatement orAbort(CharSequence table) {
        return produce(table, "ABORT");
    }

    /**
     * Producing an "INSERT OR FAIL INTO ..." statement.
     * <p/>
     * When an applicable constraint violation occurs, the FAIL resolution algorithm aborts the current SQL statement
     * with an SQLITE_CONSTRAINT error. But the FAIL resolution does not back out prior changes of the SQL statement
     * that failed nor does it end the transaction.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteDatabase#CONFLICT_FAIL
     */
    public static CreateStatement orFail(CharSequence table) {
        return produce(table, "FAIL");
    }

    /**
     * Producing an INSERT statement with a clause. This method should be
     * work with orXXX() which in this Statement, don't call it directly.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     */
    protected static CreateStatement produce(CharSequence table, CharSequence clause) {
        CreateStatement createStmt = new CreateStatement();
        createStmt.statement.append("INSERT");
        if (clause != null) createStmt.statement.append(" OR ").append(clause);
        createStmt.statement.append(" INTO ").append(table);
        return createStmt;
    }

    /**
     * Producing an "INSERT INTO ..." statement without clause.
     *
     * @param table the name of the table to inserting.
     * @return the created statement.
     */
    public static CreateStatement produce(CharSequence table) {
        return produce(table, null);
    }

    /**
     * The "INSERT ... DEFAULT VALUES" statement inserts a single new row into the given table.
     * Each column of the new row is populated with its default value.
     * <p/>
     * A correct form of the produced SQL would like "insert into tbl_name(column-1, column-2) default values".
     * <p/>
     * <strong>Notice:</strong> The {@link #columns(Object...)} method which used to specifying table columns
     * must have already have been called before enable this clause.
     * <p/>
     * Interesting to know more details about "DEFAULT VALUES"? check the
     * <a href="http://www.sqlite.org/lang_insert.html">SQLite documentation</a>'s
     * describe about "The third form of the INSERT statement".
     *
     * @return this statement.
     */
    public CreateStatement defaultValues() {
        statement.append(" DEFAULT VALUES");
        return this;
    }

    /**
     * Using a SELECT statement instead of a VALUES clause. A new entry is inserted into
     * the table for each row of data returned by executing the SELECT statement.
     * <p/>
     * A correct form of the produced SQL would like
     * "insert into first_tbl_name(column-1, column-2) (select column-1, column-2 from second_tbl_name)".
     * <p/>
     * <strong>Notice:</strong> The {@link #columns(Object...)} method which used to specifying table columns
     * must have already have been called before enable this clause.
     * <p/>
     * Interesting to know more details about "INSERT with SELECT"? check the
     * <a href="http://www.sqlite.org/lang_insert.html">SQLite documentation</a>'s
     * describe about "The second form of the INSERT statement".
     *
     * @param stmt the subquery statement.
     * @return this statement.
     */
    public CreateStatement entry(QueryStatement stmt) {
        statement.append(" (").append(stmt).append(')');
        return this;
    }

    /**
     * Concatenates all the given column names. Used for cooperating with
     * {@link #defaultValues()} or {@link #entry(QueryStatement)} methods.
     *
     * @param columns the column names.
     * @return this statement.
     */
    public CreateStatement columns(Object... columns) {
        statement.append('(');
        appendClauses(columns);
        statement.append(')');
        return this;
    }

    private StringBuilder columns = new StringBuilder(128);
    private StringBuilder values = new StringBuilder(128);
    private short columnCount;

    /**
     * Put a single field pair to constructing a conventional format's INSERT statement.
     *
     * @param column column name.
     * @param value  column value, will auto quoting if instance of {@link CharSequence}.
     * @return this statement.
     */
    public CreateStatement put(CharSequence column, Object value) {
        if (++columnCount > 1) {
            columns.append(", ");
            values.append(", ");
        }
        columns.append(column);
        append(values, value);
        return this;
    }

    @Override
    public String toString() {
        // complete the statement if any column presented.
        if (columnCount > 0) {
            statement.append('(').append(columns).append(')');
            statement.append(" VALUES(").append(values).append(')');

            // prevent completing twice and more.
            columns = values = null;
            columnCount = 0;
        }
        return statement.toString();
    }
}
