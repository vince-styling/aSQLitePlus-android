package com.vincestyling.asqliteplus.tests.query;

import com.vincestyling.asqliteplus.statement.Alias;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Scoping;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.tests.BaseDBTestCase;

public final class OuterJoinQueryTest extends BaseDBTestCase {

    public void testStandardSyntax() {
        Alias podAlias = new Alias(Products.TABLE_NAME, "pod");
        Alias catAlias = new Alias(Categories.TABLE_NAME, "cat");
        mStatement = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(podAlias, Products.SUPPLIER_ID),
                new Scoping(podAlias, Products.CATEGORY_ID),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias).leftJoin(catAlias)
                .on(new Scoping(podAlias, Products.CATEGORY_ID))
                .eq(new Scoping(catAlias, Categories.CATEGORY_ID));

        assertSQLEquals("SELECT pod.product_name, pod.supplier_id, pod.category_id, cat.category_name FROM " +
                "Products AS pod LEFT JOIN Categories AS cat ON pod.category_id = cat.category_id");

        assertResultSizeEquals(77);
    }

    public void testStandardSyntaxWithAShorterStatementByUsingClause() {
        Alias podAlias = new Alias(Products.TABLE_NAME, "pod");
        Alias catAlias = new Alias(Categories.TABLE_NAME, "cat");
        mStatement = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias).leftJoin(catAlias).using(Products.CATEGORY_ID);

        assertSQLEquals("SELECT pod.product_name, cat.category_name FROM " +
                "Products AS pod LEFT JOIN Categories AS cat USING (category_id)");

        assertResultSizeEquals(77);
    }

    public void testNaturalJoin() {
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
                .from(Products.TABLE_NAME).leftNaturalJoin(Categories.TABLE_NAME)
                .where(Categories.CATEGORY_NAME).isNotNull()
                .and(Products.PRICE).neq(10);

        assertSQLEquals("SELECT product_name, category_name FROM Products " +
                "NATURAL LEFT JOIN Categories WHERE category_name IS NOT NULL AND price <> 10");

        assertResultSizeEquals(74);
    }

}
