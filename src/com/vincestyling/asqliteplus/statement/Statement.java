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

import static android.database.DatabaseUtils.appendEscapedSQLString;

/**
 * A base class for constructing SQL statement. Internal holding a {@link StringBuilder} to representing
 * the SQL statement. Exposed many elegant methods to concatenating those acceptable clauses finally
 * complete the entire SQL statement.
 * <p/>
 * A Statement look as a String, only its toString() matter, just make sure that the {@link #toString()}
 * method would return the final SQL statement you want to be executing.
 * <p/>
 * <strong>Note:</strong> 'Cause this series of statement producer would never understand about the SQL
 * syntax, therefore this assumes you have a basic knowledge of SQL programming, you need to decide
 * which clause can be join together without syntax errors while performing and which cannot.
 *
 * @see com.vincestyling.asqliteplus.DBOverseer
 */
public class Statement {
    /**
     * the StringBuilder to representing the SQL statement.
     */
    protected StringBuilder statement = new StringBuilder(512);

    /**
     * Appending the FROM clause with one or more table name.
     *
     * @param tables the names of the table to operating. Each name can be either a normal String
     *               which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement from(Object... tables) {
        statement.append(" FROM ");
        appendClauses(tables);
        return this;
    }

    /**
     * Appending the WHERE clause.
     *
     * @param operand the condition operand. Usually be a normal String which just the column name or
     *                wrapped the column name's {@link Function}||{@link Scoping} object. It can also
     *                be an {@link Exists}||{@link NotExists} object which carry the (NOT)EXISTS clause.
     * @return this statement.
     */
    public Statement where(Object operand) {
        statement.append(" WHERE ").append(operand);
        return this;
    }

    /**
     * Appending the AND clause.
     *
     * @param operand the condition operand. Usually be a normal String which just the column name or
     *                wrapped the column name's {@link Function}||{@link Scoping} object. It can also
     *                be an {@link Exists}||{@link NotExists} object which carry the (NOT)EXISTS clause.
     * @return this statement.
     */
    public Statement and(Object operand) {
        statement.append(" AND ").append(operand);
        return this;
    }

    /**
     * Appending the OR clause.
     *
     * @param operand the condition operand. Usually be a normal String which just the column name or
     *                wrapped the column name's {@link Function}||{@link Scoping} object. It can also
     *                be an {@link Exists}||{@link NotExists} object which carry the (NOT)EXISTS clause.
     * @return this statement.
     */
    public Statement or(Object operand) {
        statement.append(" OR ").append(operand);
        return this;
    }

    /**
     * Appending the ORDER BY clause.
     *
     * @param columns the column list to be sorting. Each name can be either a normal String which just
     *                the column name or wrapped the column name's {@link Function}||{@link Scoping} object.
     * @return this statement.
     */
    public Statement orderBy(Object... columns) {
        statement.append(" ORDER BY ");
        appendClauses(columns);
        return this;
    }

    /**
     * Appending the ASCENDING order clause.
     * <p/>
     * <strong>Notice</strong>: This clause often be omitted because the
     * ORDER BY keyword sorts the records in ASCENDING order by default.
     *
     * @return this statement.
     */
    public Statement asc() {
        statement.append(" ASC");
        return this;
    }

    /**
     * Appending the DESCENDING order clause.
     *
     * @return this statement.
     */
    public Statement desc() {
        statement.append(" DESC");
        return this;
    }

    /**
     * Appending the GROUP BY clause.
     *
     * @param columns the column list to be grouping. Each name can be either a normal String which just
     *                the column name or wrapped the column name's {@link Function}||{@link Scoping} object.
     * @return this statement.
     */
    public Statement groupBy(Object... columns) {
        statement.append(" GROUP BY ");
        appendClauses(columns);
        return this;
    }

    /**
     * Appending the HAVING clause.
     *
     * @param column the condition column name. Can be either a normal String which just
     *               the column name or wrapped the column name's {@link Function}||{@link Scoping} object.
     * @return this statement.
     */
    public Statement having(Object column) {
        statement.append(" HAVING ").append(column);
        return this;
    }

    /**
     * Appending the OFFSET clause.
     *
     * @param offset the offset index.
     * @return this statement.
     */
    public Statement offset(int offset) {
        statement.append(" OFFSET ").append(offset);
        return this;
    }

    /**
     * Appending the LIMIT clause.
     *
     * @param rowAmount the limit row amount.
     * @return this statement.
     */
    public Statement limit(int rowAmount) {
        statement.append(" LIMIT ").append(rowAmount);
        return this;
    }

    /**
     * Appending the LIMIT-range clause.
     * <p/>
     * About the limit-offset clause,
     * Checking the <a href="http://sqlite.org/lang_select.html#limitoffset">SQLite documentation</a> for more details.
     *
     * @param rowAmount the limit row amount.
     * @param offset    the offset index.
     * @return this statement.
     */
    public Statement limit(int rowAmount, int offset) {
        limit(rowAmount);
        offset(offset);
        return this;
    }

    /**
     * Appending those comparison operators clause.
     *
     * @param op    the operator.
     * @param value the comparand.
     * @return this statement.
     */
    protected Statement compare(String op, Object value) {
        statement.append(' ').append(op).append(' ');
        append(value);
        return this;
    }

    /**
     * Appending the equals comparison clause.
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement eq(Object value) {
        return compare("=", value);
    }

    /**
     * Appending the non-equals comparison clause.
     * <p/>
     * <strong>Note:</strong> The non-equals operator can be either "!=" or "<>", here used "<>".
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement neq(Object value) {
        return compare("<>", value);
    }

    /**
     * Appending the greater than comparison clause.
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement gt(Object value) {
        return compare(">", value);
    }

    /**
     * Appending the equals or greater than comparison clause.
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement egt(Object value) {
        return compare(">=", value);
    }

    /**
     * Appending the less than comparison clause.
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement lt(Object value) {
        return compare("<", value);
    }

    /**
     * Appending the equals or less than comparison clause.
     *
     * @param value the term's value, will auto quoting if instance of {@link String}.
     * @return this statement.
     */
    public Statement elt(Object value) {
        return compare("<=", value);
    }

    /**
     * Appending the IS NULL operator clause.
     *
     * @return this statement.
     */
    public Statement isNull() {
        statement.append(" IS NULL");
        return this;
    }

    /**
     * Appending the IS NOT NULL operator clause.
     *
     * @return this statement.
     */
    public Statement isNotNull() {
        statement.append(" IS NOT NULL");
        return this;
    }

    /**
     * Appending the IN operator clause.
     *
     * @param values apply an explicit list as terms.
     * @return this statement.
     */
    public Statement in(Object... values) {
        statement.append(" IN (");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) statement.append(", ");
            append(values[i]);
        }
        statement.append(')');
        return this;
    }

    /**
     * Appending the IN operator clause by a single sub-query.
     *
     * @param stmt apply a single sub-query as terms, the sub-query must have a single result column.
     * @return this statement.
     */
    public Statement in(Statement stmt) {
        statement.append(" IN (").append(stmt).append(')');
        return this;
    }

    /**
     * Appending the NOT IN operator clause.
     *
     * @param values apply an explicit list as terms.
     * @return this statement.
     */
    public Statement notIn(Object... values) {
        statement.append(" NOT");
        return in(values);
    }

    /**
     * Appending the NOT IN operator clause by a single sub-query.
     *
     * @param stmt apply a single sub-query as terms, the sub-query must have a single result column.
     * @return this statement.
     */
    public Statement notIn(Statement stmt) {
        statement.append(" NOT");
        return in(stmt);
    }

    /**
     * Appending the BETWEEN operator clause.
     *
     * @param former the former operand.
     * @param later  the later operand.
     * @return this statement.
     */
    public Statement between(Object former, Object later) {
        statement.append(" BETWEEN ");
        append(former);
        statement.append(" AND ");
        append(later);
        return this;
    }

    /**
     * Appending the LIKE operator clause.
     *
     * @param expr the former operand.
     * @return this statement.
     */
    public Statement like(String expr) {
        statement.append(" LIKE ");
        appendEscapedSQLString(statement, expr);
        return this;
    }

    /**
     * Appending the LIKE operator clause by contains form.
     *
     * @param keyword the search text.
     * @return this statement.
     */
    public Statement likeContains(String keyword) {
        return like('%' + keyword + '%');
    }

    /**
     * Appending the LIKE operator clause by starts-with form.
     *
     * @param keyword the search text.
     * @return this statement.
     */
    public Statement likeStartsWith(String keyword) {
        return like(keyword + '%');
    }

    /**
     * Appending the LIKE operator clause by ends-with form.
     *
     * @param keyword the search text.
     * @return this statement.
     */
    public Statement likeEndsWith(String keyword) {
        return like('%' + keyword);
    }

    /**
     * Appending the REGEXP operator clause.
     * <p/>
     * The REGEXP operator is a special syntax for the regexp() user function.
     * Checking the <a href="https://www.sqlite.org/lang_expr.html">SQLite documentation</a> for more details.
     *
     * @param regexp the regular expression.
     * @return this statement.
     */
    public Statement regexp(CharSequence regexp) {
        statement.append(" REGEXP '").append(regexp).append('\'');
        return this;
    }

    /**
     * Appending the INNER JOIN clause.
     * <p/>
     * <strong>Note:</strong> In this produced statement, we have omitted the INNER keyword.
     *
     * @param table the name of the table to operating. Can be either a normal String
     *              which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement join(Object table) {
        statement.append(" JOIN ").append(table);
        return this;
    }

    /**
     * Appending the NATURAL INNER JOIN clause.
     * <p/>
     * <strong>Note:</strong> In this produced statement, we have omitted the INNER keyword.
     *
     * @param table the name of the table to operating. Can be either a normal String
     *              which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement naturalJoin(Object table) {
        statement.append(" NATURAL JOIN ").append(table);
        return this;
    }

    /**
     * Appending the CROSS INNER JOIN clause.
     * <p/>
     * <strong>Note:</strong> In this produced statement, we have omitted the INNER keyword.
     *
     * @param table the name of the table to operating. Can be either a normal String
     *              which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement crossJoin(Object table) {
        statement.append(" CROSS JOIN ").append(table);
        return this;
    }

    /**
     * Appending the LEFT OUTER JOIN clause.
     * <p/>
     * <strong>Note:</strong> In this produced statement, we have omitted the OUTER keyword.
     *
     * @param table the name of the table to operating. Can be either a normal String
     *              which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement leftJoin(Object table) {
        statement.append(" LEFT JOIN ").append(table);
        return this;
    }

    /**
     * Appending the NATURAL LEFT OUTER JOIN clause.
     * <p/>
     * <strong>Note:</strong> In this produced statement, we have omitted the OUTER keyword.
     *
     * @param table the name of the table to operating. Can be either a normal String
     *              which just the table name or wrapped the table name's {@link Alias} object.
     * @return this statement.
     */
    public Statement leftNaturalJoin(Object table) {
        statement.append(" NATURAL LEFT JOIN ").append(table);
        return this;
    }

    /**
     * Appending the ON sub-clause which follow the JOIN clause.
     *
     * @param column the condition column name. Can be either a normal String which just
     *               the column name or wrapped the column name's {@link Function}||{@link Scoping} object.
     * @return this statement.
     */
    public Statement on(Object column) {
        statement.append(" ON ").append(column);
        return this;
    }

    /**
     * Appending the USING sub-clause which follow the JOIN clause.
     *
     * @param columns the column list to be matching.
     * @return this statement.
     */
    public Statement using(Object... columns) {
        statement.append(" USING (");
        appendClauses(columns);
        statement.append(')');
        return this;
    }

    /**
     * A safe way to appending a value if instance of {@link String}, include quoting the String value.
     * In order to preventing SQL injection, also escaping that String value which may contain single quotes.
     * <p/>
     * <strong>Note:</strong> you can wrapping a String as {@link UnescapeString} to avoiding this behaviors.
     *
     * @param value the raw value to be append, would translate to String["null"] if being <code>null</code>.
     * @return this statement.
     */
    public Statement append(Object value) {
        append(statement, value);
        return this;
    }

    /**
     * A safe way to appending a value if instance of {@link String}, include quoting the String value.
     * In order to preventing SQL injection, also escaping that String value which may contain single quotes.
     * <p/>
     * <strong>Note:</strong> you can wrapping a String as {@link UnescapeString} to avoiding this behaviors.
     *
     * @param statement the StringBuilder that the SQL statement will be appended to.
     * @param value     the raw value to be append, would translate to String["null"] if being <code>null</code>.
     */
    protected void append(StringBuilder statement, Object value) {
        if (value instanceof String) {
            appendEscapedSQLString(statement, value.toString());
        } else {
            statement.append(value);
        }
    }

    /**
     * Concatenates all the given clauses, separating them with commas.
     *
     * @param clauses the list of clause. Each clause usually a table name or column name, and each
     *                name can be either a normal String which just the name or wrapped the
     *                name's {@link Alias}||{@link Function}||{@link Scoping} object.
     */
    protected void appendClauses(Object... clauses) {
        for (int i = 0; i < clauses.length; i++) {
            if (i > 0) statement.append(", ");
            statement.append(clauses[i]);
        }
    }

    /**
     * Simply clone this Statement by create a new Statement which apply the original statement.
     *
     * @return the new statement.
     */
    public Statement copy() {
        Statement stmt = new Statement();
        stmt.statement.append(statement);
        return stmt;
    }

    @Override
    public String toString() {
        return statement.toString();
    }
}
