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
package com.vincestyling.asqliteplus.tests;

import android.test.AndroidTestCase;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Statement;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.table.Table;

import java.util.List;

public abstract class BaseDBTestCase extends AndroidTestCase {
    protected Statement mStatement;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpDB();
    }

    protected void setUpDB() throws Exception {
        // creating and populating dummy data to tables.
        Table.prepare(Categories.class);
        mStatement = QueryStatement.rowCount().from(Categories.TABLE_NAME);
        assertGreatThan("SELECT count(*) FROM Categories", 0);

        Table.prepare(Products.class);
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        assertGreatThan("SELECT count(*) FROM Products", 0);
    }

    protected void assertGreatThan(String expectedSQL, int comparand) {
        assertSQLEquals(expectedSQL);
        assertGreatThan(MyDBOverseer.get().getInt(mStatement), comparand);
    }

    protected void assertGreatThan(int operand, int comparand) {
        if (operand > comparand) return;
        fail(String.format("great than not as expected. operand :<%d> comparand :<%d>", operand, comparand));
    }

    protected void assertSQLHasResult(String expectedSQL) {
        assertSQLEquals(expectedSQL);
        if (MyDBOverseer.get().checkIfExists(mStatement)) return;
        fail(String.format("Performing SQL <%s> has no data returned.", mStatement));
    }

    protected void assertSQLHasNotResult(String expectedSQL) {
        assertSQLEquals(expectedSQL);
        if (!MyDBOverseer.get().checkIfExists(mStatement)) return;
        fail(String.format("Performing SQL <%s> has data returned.", mStatement));
    }

    protected void assertSQLSuccessful(String expectedSQL) {
        assertSQLEquals(expectedSQL);
        if (MyDBOverseer.get().executeSql(mStatement) > 0) return;
        fail(String.format("Performing SQL <%s> is not successful.", mStatement));
    }

    protected void assertResultSizeEquals(String expectedSQL, int expectedSize) {
        assertSQLEquals(expectedSQL);
        List actualResults = MyDBOverseer.get().getList(mStatement, new SimpleRowMapper());
        assertNotNull(actualResults);
        assertEquals("returned result amount not as expected.", expectedSize, actualResults.size());
    }

    protected void assertSQLEquals(String expectedSQL) {
        assertSQLEquals(expectedSQL, mStatement);
    }

    protected void assertSQLEquals(String expectedSQL, Statement stmt) {
        assertEquals("SQL not as expected.", expectedSQL, stmt.toString());
    }
}
