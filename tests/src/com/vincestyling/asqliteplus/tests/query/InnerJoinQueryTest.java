package com.vincestyling.asqliteplus.tests.query;

import com.vincestyling.asqliteplus.statement.Alias;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Scoping;
import com.vincestyling.asqliteplus.statement.Statement;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.tests.BaseDBTestCase;
import com.vincestyling.asqliteplus.tests.DefRowMapper;

public class InnerJoinQueryTest extends BaseDBTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // creating and populating dummy data to tables.
        mDBOverseer.execSQL(Products.getCreateStatment());
        mDBOverseer.executeBatch(Products.INIT_DATAS, Products.CREATE_DBOPER);

        mDBOverseer.execSQL(Categories.getCreateStatment());
        mDBOverseer.executeBatch(Categories.INIT_DATAS, Categories.CREATE_DBOPER);

    }

    public void testBasically() {
        Statement stmt;

        stmt = QueryStatement.rowCount().from(Categories.TABLE_NAME);
        assertTrue(mDBOverseer.getInt(stmt) > 0);

        stmt = QueryStatement.rowCount().from(Products.TABLE_NAME);
        assertTrue(mDBOverseer.getInt(stmt) > 0);

        Alias podAlias = new Alias(Products.TABLE_NAME, "pod");
        Alias catAlias = new Alias(Categories.TABLE_NAME, "cat");
        stmt = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(podAlias, Products.PRICE),
                new Scoping(podAlias, Products.CATEGORY_ID),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias, catAlias)
                .where(new Scoping(podAlias, Products.CATEGORY_ID)).eq(new Scoping(catAlias, Categories.CATEGORY_ID))
                .and(new Scoping(podAlias, Products.PRICE)).gt(20)
                .groupBy(new Scoping(podAlias, Products.CATEGORY_ID))
                .having(new Scoping(podAlias, Products.PRICE)).gt(30)
                .orderBy(new Scoping(podAlias, Products.PRICE)).desc();

        assertEquals("SQL not as expected.", "SELECT pod.product_name, pod.price, pod.category_id, cat.category_name " +
                "FROM Products AS pod, Categories AS cat WHERE pod.category_id = cat.category_id AND pod.price > 20 " +
                "GROUP BY pod.category_id HAVING pod.price > 30 ORDER BY pod.price DESC", stmt.toString());

        assertEquals("returned result amount not as expected.", 5, mDBOverseer.getList(stmt, new DefRowMapper()).size());
    }
}
