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

import com.vincestyling.asqliteplus.statement.*;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.tests.BaseDBTestCase;

public class GeneralQueryTest extends BaseDBTestCase {

    public void testDistinctClause() {
        mStatement = QueryStatement.distinct(Products.CATEGORY_ID).from(Products.TABLE_NAME);

        assertResultSizeEquals("SELECT DISTINCT category_id FROM Products", 8);

        mStatement = QueryStatement.distinct(Products.SUPPLIER_ID,
                Products.PRODUCT_ID, Products.PRODUCT_NAME).from(Products.TABLE_NAME);

        assertResultSizeEquals("SELECT DISTINCT supplier_id, product_id, product_name FROM Products", 77);
    }

    public void testRegexpClause() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.PRODUCT_NAME).regexp("Ch.+");

        assertSQLEquals("SELECT * FROM Products WHERE product_name REGEXP 'Ch.+'");

        // Because SQLite does not provide an implementation for REGEXP, so no longer examine its result size here.
        // assertResultSizeEquals(6);
    }

    public void testSQLInjection() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.PRODUCT_NAME).eq("Chais' or '1' = '1");

        assertResultSizeEquals("SELECT * FROM Products WHERE product_name = 'Chais'' or ''1'' = ''1'", 0);
    }

    public void testExistsClause() {
        mStatement = QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME, Products.UNIT)
                .from(Products.TABLE_NAME).where(new Exists(QueryStatement.produce()
                        .from(Products.TABLE_NAME).where(Products.PRICE).elt(20)));

        assertResultSizeEquals("SELECT product_id, product_name, unit FROM Products " +
                "WHERE EXISTS (SELECT * FROM Products WHERE price <= 20)", 77);

        mStatement = QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME, Products.UNIT)
                .from(Products.TABLE_NAME).where(new NotExists(QueryStatement.produce()
                        .from(Products.TABLE_NAME).where(Products.PRICE).lt(0)));

        assertResultSizeEquals("SELECT product_id, product_name, unit FROM Products " +
                "WHERE NOT EXISTS (SELECT * FROM Products WHERE price < 0)", 77);

        mStatement = QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME, Products.UNIT)
                .from(Products.TABLE_NAME).where(Products.SUPPLIER_ID).egt(3).and(new Exists(
                        QueryStatement.produce().from(Products.TABLE_NAME).where(Products.UNIT).isNull()));

        assertResultSizeEquals("SELECT product_id, product_name, unit FROM Products WHERE supplier_id >= 3 " +
                "AND EXISTS (SELECT * FROM Products WHERE unit IS NULL)", 0);
    }

    public void testRangeTermQuery() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.SUPPLIER_ID).in(1).and(Function.length(Products.PRODUCT_NAME)).egt(6);

        assertResultSizeEquals("SELECT * FROM Products WHERE supplier_id IN (1) AND length(product_name) >= 6", 1);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.SUPPLIER_ID).notIn(1, 2, 3);

        assertResultSizeEquals("SELECT * FROM Products WHERE supplier_id NOT IN (1, 2, 3)", 67);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.UNIT).in("500 ml", "12 boxes", "48 pies");

        assertResultSizeEquals("SELECT * FROM Products WHERE unit IN ('500 ml', '12 boxes', '48 pies')", 3);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME).where(Products.CATEGORY_ID)
                .in(QueryStatement.produce(Categories.CATEGORY_ID).from(Categories.TABLE_NAME)
                        .where(Categories.CATEGORY_ID).neq(1));

        assertResultSizeEquals("SELECT * FROM Products WHERE category_id IN " +
                "(SELECT category_id FROM Categories WHERE category_id <> 1)", 65);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME).where(Products.CATEGORY_ID)
                .notIn(QueryStatement.produce(Categories.CATEGORY_ID).from(Categories.TABLE_NAME)
                        .where(Categories.DESCRIPTION).likeContains("re")
                        .orderBy(Categories.CATEGORY_NAME).desc().limit(1));

        assertResultSizeEquals("SELECT * FROM Products WHERE category_id NOT IN (SELECT category_id FROM " +
                "Categories WHERE description LIKE '%re%' ORDER BY category_name DESC LIMIT 1)", 71);
    }

    public void testGroupByClause() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .groupBy(Products.SUPPLIER_ID).orderBy(Function.avg(Products.PRICE)).desc();

        assertResultSizeEquals("SELECT * FROM Products GROUP BY supplier_id ORDER BY avg(price) DESC", 29);

        mStatement = QueryStatement.produce(
                Products.CATEGORY_ID, new Alias(Function.sum(Products.PRICE), "total_price"))
                .from(Products.TABLE_NAME).groupBy(Products.CATEGORY_ID);

        assertResultSizeEquals("SELECT category_id, sum(price) AS total_price FROM Products GROUP BY category_id", 8);

        Alias alias = new Alias(Function.length(Products.PRODUCT_NAME), "leng");
        mStatement = QueryStatement.produce(alias).from(Products.TABLE_NAME)
                .groupBy(alias.getAlias()).orderBy(alias.getAlias()).asc();

        assertResultSizeEquals("SELECT length(product_name) AS leng FROM Products GROUP BY leng ORDER BY leng ASC", 25);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .groupBy(Function.length(Products.PRODUCT_NAME), Products.PRICE).limit(10).offset(11);

        assertResultSizeEquals("SELECT * FROM Products GROUP BY length(product_name), price LIMIT 10 OFFSET 11", 10);

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.CATEGORY_ID).eq(1).or(Products.SUPPLIER_ID).eq(2)
                .groupBy(Products.SUPPLIER_ID, Products.CATEGORY_ID)
                .having(Function.sum(Products.PRICE)).egt(30);

        assertResultSizeEquals("SELECT * FROM Products WHERE category_id = 1 OR supplier_id = 2 " +
                "GROUP BY supplier_id, category_id HAVING sum(price) >= 30", 5);
    }

    public void testUnionQuery() {
        mStatement = QueryStatement.union(true,
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.CATEGORY_ID).between(1, 3),
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.SUPPLIER_ID).in(3, 5, 6),
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.CATEGORY_ID).notIn(6));

        assertResultSizeEquals(
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE category_id BETWEEN 1 AND 3" +
                " UNION " +
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE supplier_id IN (3, 5, 6)" +
                " UNION " +
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE category_id NOT IN (6)", 71);


        mStatement = QueryStatement.union(false,
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.PRODUCT_NAME).likeContains("Ch"),
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.UNIT).likeEndsWith("bottles"),
                QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
                        Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
                        .from(Products.TABLE_NAME).where(Products.UNIT).likeStartsWith("24"));

        assertResultSizeEquals(
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE product_name LIKE '%Ch%'" +
                " UNION ALL " +
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE unit LIKE '%bottles'" +
                " UNION ALL " +
                "SELECT product_id, product_name, price, category_id, supplier_id FROM Products WHERE unit LIKE '24%'", 46);
    }
}
