package com.vincestyling.asqliteplus.tests;

import android.test.AndroidTestCase;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Statement;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.table.Table;

import java.util.List;

public abstract class BaseDBTestCase extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

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

    protected void setupTables() throws Exception {
    }

    protected Statement mStatement;

    protected void assertGreatThan(int right) {
        assertGreatThan(MyDBOverseer.get().getInt(mStatement), right);
    }

    protected void assertGreatThan(int left, int right) {
        if (left > right) return;
        fail(String.format("great than not as expected. left :<%d> right:<%d>", left, right));
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
