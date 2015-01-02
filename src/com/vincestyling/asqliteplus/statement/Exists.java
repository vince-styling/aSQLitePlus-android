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
 * This class used to producing an EXISTS operator clause which
 * used to tests whether a sub-query fetches at least one row.
 * <p/>
 * For example:
 * <p/>
 * new Exists(QueryStatement.produce().from("tbl_name"))
 * would produce "EXISTS (SELECT * FROM tbl_name)".
 */
public class Exists extends ClauseWrapper {
    /**
     * Constructing the clause by a single sub-query.
     *
     * @param stmt apply a single sub-query as term.
     */
    public Exists(Statement stmt) {
        clause = format("EXISTS (%s)", stmt);
    }
}
