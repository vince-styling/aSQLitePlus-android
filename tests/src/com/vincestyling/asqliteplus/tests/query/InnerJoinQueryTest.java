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

        assertResultSizeEquals("SELECT pod.product_name, pod.price, pod.category_id, cat.category_name FROM " +
                "Products AS pod, Categories AS cat WHERE pod.category_id = cat.category_id AND " +
                "pod.price > 20 GROUP BY pod.category_id HAVING pod.price > 30 ORDER BY pod.price DESC", 5);
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

        assertResultSizeEquals("SELECT Products.product_name, Products.category_id, Categories.category_name " +
                "FROM Products, Categories WHERE Products.category_id = Categories.category_id AND " +
                "Products.price > 10 ORDER BY Products.price", 63);
    }

    public void testFullTableNameButColumnNameWithoutScoping() {
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
                .from(Products.TABLE_NAME, Categories.TABLE_NAME)
                .where(new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID))
                .eq(new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_ID));

        assertResultSizeEquals("SELECT product_name, category_name FROM Products, Categories " +
                "WHERE Products.category_id = Categories.category_id", 77);
    }

    public void testFullTableNameButColumnNameWithoutScopingAndAShorterStatementByUsingClause() {
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
                .from(Products.TABLE_NAME, Categories.TABLE_NAME)
                .using(Products.CATEGORY_ID);

        assertResultSizeEquals("SELECT product_name, category_name FROM Products, Categories USING (category_id)", 77);
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

        assertResultSizeEquals("SELECT pod.product_name, pod.category_id, cat.category_name FROM " +
                "Products AS pod JOIN Categories AS cat ON pod.category_id = cat.category_id", 77);

        mStatement = QueryStatement.produce(
                new Scoping(podAlias, Products.PRODUCT_NAME),
                new Scoping(podAlias, Products.CATEGORY_ID),
                new Scoping(catAlias, Categories.CATEGORY_NAME)
        ).from(podAlias).join(catAlias)
                .on(Function.abs(new Scoping(podAlias, Products.CATEGORY_ID)))
                .eq(Function.abs(new Scoping(catAlias, Categories.CATEGORY_ID)));

        assertResultSizeEquals("SELECT pod.product_name, pod.category_id, cat.category_name FROM " +
                "Products AS pod JOIN Categories AS cat ON abs(pod.category_id) = abs(cat.category_id)", 77);
    }

    public void testNaturalJoin() {
        mStatement = QueryStatement.produce()
                .from(Categories.TABLE_NAME).naturalJoin(Products.TABLE_NAME)
                .where(Function.length(Categories.CATEGORY_NAME)).elt(9);

        assertResultSizeEquals("SELECT * FROM Categories NATURAL JOIN Products WHERE length(category_name) <= 9", 29);
    }

    public void testCrossJoin() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME).crossJoin(Categories.TABLE_NAME);

        assertResultSizeEquals("SELECT * FROM Products CROSS JOIN Categories", 616);
    }
}
