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
 * This class used to producing a clause which included column name and table scope.
 * <p/>
 * For example:
 * <p/>
 * new Scoping("tbl_name", "column_name") would produce "tbl_name.column_name".
 * <p/>
 * new Scoping(new Alias("tbl_name", "tbl"), "column_name") would produce "tbl.column_name".
 */
public class Scoping extends ClauseWrapper {
    /**
     * The scoping pattern.
     */
    private static final String PATTERN = "%s.%s";

    /**
     * Constructing the clause by a table with Alias.
     *
     * @param alias  the object which wrapped the table name and alias.
     * @param column the column name.
     */
    public Scoping(Alias alias, CharSequence column) {
        clause = format(PATTERN, alias.getAlias(), column);
    }

    /**
     * Constructing the clause by an entire table name.
     *
     * @param table  the entire table name.
     * @param column the column name.
     */
    public Scoping(String table, CharSequence column) {
        clause = format(PATTERN, table, column);
    }
}
