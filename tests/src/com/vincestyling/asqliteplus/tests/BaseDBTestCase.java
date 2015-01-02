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
        assertSQLEquals("SELECT count(*) FROM Categories");
        assertGreatThan(0);

        Table.prepare(Products.class);
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        assertSQLEquals("SELECT count(*) FROM Products");
        assertGreatThan(0);
    }

    protected void assertGreatThan(int comparand) {
        assertGreatThan(MyDBOverseer.get().getInt(mStatement), comparand);
    }

    protected void assertGreatThan(int operand, int comparand) {
        if (operand > comparand) return;
        fail(String.format("great than not as expected. operand :<%d> comparand :<%d>", operand, comparand));
    }

    protected void assertSQLHasResult() {
        if (MyDBOverseer.get().checkIfExists(mStatement)) return;
        fail(String.format("Performing SQL <%s> has no data returned.", mStatement));
    }

    protected void assertSQLHasNotResult() {
        if (!MyDBOverseer.get().checkIfExists(mStatement)) return;
        fail(String.format("Performing SQL <%s> has data returned.", mStatement));
    }

    protected void assertSQLSuccessful() {
        if (MyDBOverseer.get().executeSql(mStatement) > 0) return;
        fail(String.format("Performing SQL <%s> is not successful.", mStatement));
    }

    protected void assertSQLEquals(String expected) {
        assertSQLEquals(expected, mStatement);
    }

    protected void assertSQLEquals(String expected, Statement actualStmt) {
        assertEquals("SQL not as expected.", expected, actualStmt.toString());
    }

    protected void assertResultSizeEquals(int expectedSize) {
        assertResultSizeEquals(expectedSize, mStatement);
    }

    protected void assertResultSizeEquals(int expectedSize, Statement stmt) {
        assertResultSizeEquals(expectedSize, MyDBOverseer.get().getList(stmt, new SimpleRowMapper()));
    }

    protected void assertResultSizeEquals(int expectedSize, List actualResults) {
        assertNotNull(actualResults);
        assertEquals("returned result amount not as expected.", expectedSize, actualResults.size());
    }
}
