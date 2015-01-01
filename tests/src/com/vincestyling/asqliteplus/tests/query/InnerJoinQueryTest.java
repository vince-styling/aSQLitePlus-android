package com.vincestyling.asqliteplus.tests.query;

import com.vincestyling.asqliteplus.statement.Alias;
import com.vincestyling.asqliteplus.statement.Function;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Scoping;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.tests.BaseDBTestCase;

public final class InnerJoinQueryTest extends BaseDBTestCase {

    public void testOmitted_JOINON_KeywordsAndScoping() {
        Alias podAlias = new Alias(Products.TABLE_NAME, "pod");
        Alias catAlias = new Alias(Categories.TABLE_NAME, "cat");
        mStatement = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(podAlias, Products.PRICE),
                new Scoping(podAlias, Products.CATEGORY_ID),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias, catAlias)
                .where(new Scoping(podAlias, Products.CATEGORY_ID))
                .eq(new Scoping(catAlias, Categories.CATEGORY_ID))
                .and(new Scoping(podAlias, Products.PRICE)).gt(20)
                .groupBy(new Scoping(podAlias, Products.CATEGORY_ID))
                .having(new Scoping(podAlias, Products.PRICE)).gt(30)
                .orderBy(new Scoping(podAlias, Products.PRICE)).desc();

        assertSQLEquals("SELECT pod.product_name, pod.price, pod.category_id, cat.category_name FROM " +
                "Products AS pod, Categories AS cat WHERE pod.category_id = cat.category_id AND " +
                "pod.price > 20 GROUP BY pod.category_id HAVING pod.price > 30 ORDER BY pod.price DESC");

        assertResultSizeEquals(5);
    }

    public void testFullTableNameScoping() {
        mStatement = QueryStatement.produce(
                new Scoping(Products.TABLE_NAME, Products.PRODUCT_NAME),
                new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID),
                new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_NAME)
        ).from(Products.TABLE_NAME, Categories.TABLE_NAME)
                .where(new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID))
                .eq(new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_ID))
                .and(new Scoping(Products.TABLE_NAME, Products.PRICE)).gt(10)
                .orderBy(new Scoping(Products.TABLE_NAME, Products.PRICE));

        assertSQLEquals("SELECT Products.product_name, Products.category_id, Categories.category_name " +
                "FROM Products, Categories WHERE Products.category_id = Categories.category_id AND " +
                "Products.price > 10 ORDER BY Products.price");

        assertResultSizeEquals(63);
    }

    public void testFullTableNameButColumnNameWithoutScoping() {
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
                .from(Products.TABLE_NAME, Categories.TABLE_NAME)
                .where(new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID))
                .eq(new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_ID));

        assertSQLEquals("SELECT product_name, category_name FROM " +
                "Products, Categories WHERE Products.category_id = Categories.category_id");

        assertResultSizeEquals(77);
    }

    public void testFullTableNameButColumnNameWithoutScopingAndAShorterStatementByUsingClause() {
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
                .from(Products.TABLE_NAME, Categories.TABLE_NAME)
                .using(Products.CATEGORY_ID);

        assertSQLEquals("SELECT product_name, category_name FROM Products, Categories USING (category_id)");

        assertResultSizeEquals(77);
    }

    public void testStandardSyntax() {
        Alias podAlias = new Alias(Products.TABLE_NAME, "pod");
        Alias catAlias = new Alias(Categories.TABLE_NAME, "cat");
        mStatement = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(podAlias, Products.CATEGORY_ID),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias).join(catAlias)
                .on(new Scoping(podAlias, Products.CATEGORY_ID))
                .eq(new Scoping(catAlias, Categories.CATEGORY_ID));

        assertSQLEquals("SELECT pod.product_name, pod.category_id, cat.category_name FROM " +
                "Products AS pod JOIN Categories AS cat ON pod.category_id = cat.category_id");

        assertResultSizeEquals(77);
    }

    public void testNaturalJoin() {
        mStatement = QueryStatement.produce()
                .from(Categories.TABLE_NAME).naturalJoin(Products.TABLE_NAME)
                .where(Function.length(Categories.CATEGORY_NAME)).elt(9);

        assertSQLEquals("SELECT * FROM Categories NATURAL JOIN Products WHERE length(category_name) <= 9");

        assertResultSizeEquals(29);
    }

    public void testCrossJoin() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME).crossJoin(Categories.TABLE_NAME);

        assertSQLEquals("SELECT * FROM Products CROSS JOIN Categories");

        assertResultSizeEquals(616);
    }
}
