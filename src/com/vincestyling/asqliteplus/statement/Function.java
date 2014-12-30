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

import static java.lang.String.format;

/**
 * This class provides many static utility methods for wrapping those useful built-in
 * functions of SQLite during constructing SQL statement.
 * <p/>
 * For example:
 * <p/>
 * Function.max("price") would produce a Function object which carried "max(price)" clause.
 * <p/>
 * <strong>Note:</strong> {@link Statement} would calls the {@link #toString()} method to take the final clause.
 */
public class Function {
    /**
     * The final function expression.
     */
    private String expression;

    public Function(String expression) {
        this.expression = expression;
    }

    /**
     * The SQLite COUNT function is used to count the number of rows for returned result set.
     *
     * @return the created function.
     */
    public static Function count() {
        return new Function("count(*)");
    }

    /**
     * The SQLite MAX function allows us to select the highest(maximum) value for a certain column.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function max(String column) {
        return new Function(format("max(%s)", column));
    }

    /**
     * The SQLite MIN function allows us to select the lowest(minimum) value for a certain column.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function min(String column) {
        return new Function(format("min(%s)", column));
    }

    /**
     * The SQLite AVG function selects the average value for certain table column.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function avg(String column) {
        return new Function(format("avg(%s)", column));
    }

    /**
     * The SQLite SUM function allows selecting the total for a numeric column.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function sum(String column) {
        return new Function(format("sum(%s)", column));
    }

    /**
     * The SQLite ABS function returns the absolute value of the numeric argument.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function abs(String column) {
        return new Function(format("abs(%s)", column));
    }

    /**
     * The SQLite UPPER function converts a string into upper-case letters.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function upper(String column) {
        return new Function(format("upper(%s)", column));
    }

    /**
     * The SQLite LOWER function converts a string into lower-case letters.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function lower(String column) {
        return new Function(format("lower(%s)", column));
    }

    /**
     * The SQLite LENGTH function returns the length of a string.
     *
     * @param column the desired column you want to evaluating.
     * @return the created function.
     */
    public static Function length(String column) {
        return new Function(format("length(%s)", column));
    }

    /**
     * The SQLite LAST_INSERT_ROWID function returns the last auto-increment primary key.
     *
     * @return the created function.
     */
    public static Function lastInsertRowid() {
        return new Function("last_insert_rowid()");
    }

    /**
     * The SQLite SQLITE_VERSION function returns the version of the SQLite library.
     *
     * @return the created function.
     */
    public static Function sqliteVersion() {
        return new Function("sqlite_version()");
    }

    /**
     * The SQLite RANDOM function returns a pseudo-random integer between -9223372036854775808 and +9223372036854775807.
     *
     * @return the created function.
     */
    public static Function random() {
        return new Function("random()");
    }

    /**
     * Taking the final function expression via this method.
     *
     * @return the final function expression.
     */
    @Override
    public String toString() {
        return expression;
    }
}
