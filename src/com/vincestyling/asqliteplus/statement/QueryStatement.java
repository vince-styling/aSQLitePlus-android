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
 * A statement producer that use to producing <b>SELECT</b> command of SQL language. The completed SQL
 * statement would be sent to {@link android.database.sqlite.SQLiteDatabase} objects to execute.
 *
 * @see com.vincestyling.asqliteplus.DBOverseer
 */
public class QueryStatement extends Statement {
    /**
     * Producing a normal SELECT statement.
     *
     * @param columns A list of which columns you desired to return, leave it empty will return all columns.
     *                Each column can be either a normal String which just the column name or wrapped
     *                the column name's {@link Function}||{@link Alias}||{@link Scoping} object.
     * @return the created statement.
     */
    public static Statement produce(Object... columns) {
        QueryStatement queryStmt = new QueryStatement();
        queryStmt.statement.append("SELECT");
        return queryStmt.processColumns(columns);
    }

    /**
     * Producing a SELECT statement by DISTINCT clause.
     *
     * @param columns A list of which columns you desired to return, leave it empty will return all columns.
     *                Each column can be either a normal String which just the column name or wrapped
     *                the column name's {@link Function}||{@link Alias}||{@link Scoping} object.
     * @return the created statement.
     */
    public static Statement distinct(Object... columns) {
        QueryStatement queryStmt = new QueryStatement();
        queryStmt.statement.append("SELECT DISTINCT");
        return queryStmt.processColumns(columns);
    }

    /**
     * Processing the column list, append to statement.
     *
     * @param columns A list of which columns you desired to return, leave it empty will return all columns.
     *                Each column can be either a normal String which just the column name or wrapped
     *                the column name's {@link Function}||{@link Alias}||{@link Scoping} object.
     * @return the created statement.
     */
    public Statement processColumns(Object... columns) {
        statement.append(' ');
        if (columns != null && columns.length > 0) {
            appendClauses(columns);
        } else {
            statement.append('*');
        }
        return this;
    }

    /**
     * Producing a "SELECT count(*) ..." statement.
     *
     * @return the created statement.
     */
    public static Statement rowCount() {
        return produce(Function.count());
    }

    /**
     * Producing an UNION statement that combine the results of two or more SELECT statements.
     *
     * @param distinct   true if you want the results without returning any duplicate rows.
     *                   otherwise would including duplicate rows.
     * @param subQueries an array of SQL SELECT statements, all of which must have
     *                   the same columns as the same positions in their results.
     * @return the created statement.
     * @see android.database.sqlite.SQLiteQueryBuilder#buildUnionQuery(String[], String, String)
     */
    public static Statement union(boolean distinct, Statement... subQueries) {
        QueryStatement stmt = new QueryStatement();

        String unionOperator = distinct ? " UNION " : " UNION ALL ";

        for (int i = 0; i < subQueries.length; i++) {
            if (i > 0) stmt.statement.append(unionOperator);
            stmt.statement.append(subQueries[i]);
        }

        return stmt;
    }
}
