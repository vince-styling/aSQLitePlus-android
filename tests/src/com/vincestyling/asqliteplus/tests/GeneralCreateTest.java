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

import com.vincestyling.asqliteplus.entity.Product;
import com.vincestyling.asqliteplus.statement.CreateStatement;
import com.vincestyling.asqliteplus.statement.Parenthesize;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Statement;
import com.vincestyling.asqliteplus.table.Products;

public class GeneralCreateTest extends BaseDBTestCase {

    public void testStandardSyntax() {
        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        Product product = Products.INIT_DATAS.get(0);
        mStatement = CreateStatement.produce(Products.TABLE_NAME)
                .put(Products.PRODUCT_NAME, product.getProductName())
                .put(Products.SUPPLIER_ID, product.getSupplierId())
                .put(Products.CATEGORY_ID, product.getCategoryId())
                .put(Products.UNIT, product.getUnit())
                .put(Products.PRICE, product.getPrice());

        assertSQLEquals("INSERT INTO Products(product_name, supplier_id, category_id, " +
                "unit, price) VALUES('Chais', 1, 1, '10 boxes x 20 bags', 18.0)");

        // assert the create-by-entry operations successful.
        int newProductId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertGreatThan(newProductId, 0);

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // comparing if the current rows count great than before.
        assertGreatThan(afterRowCount, beforeRowCount);
    }

    public void testCreateOrReplace() {
        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        // taking a sample entity then modify some fields.
        Product product = Products.INIT_DATAS.get(2);
        product.setProductName("DeAniseed");
        product.setPrice(102.132);

        mStatement = CreateStatement.orReplace(Products.TABLE_NAME)
                .put(Products.PRODUCT_ID, product.getProductId())
                .put(Products.PRODUCT_NAME, product.getProductName())
                .put(Products.SUPPLIER_ID, product.getSupplierId())
                .put(Products.CATEGORY_ID, product.getCategoryId())
                .put(Products.UNIT, product.getUnit())
                .put(Products.PRICE, product.getPrice());

        assertSQLEquals("INSERT OR REPLACE INTO Products(product_id, product_name, supplier_id, category_id, " +
                "unit, price) VALUES(3, 'DeAniseed', 1, 2, '12 - 550 ml bottles', 102.132)");

        // perform and assert the returned product id still been before.
        int returnedProductId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertEquals(returnedProductId, product.getProductId());

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // assert no newly row effected.
        assertEquals(afterRowCount, beforeRowCount);

        // taking the replaced row as entity then comparing the two values which
        // modified before to verify the "INSERT OR REPLACE" command successful.
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME)
                .where(Products.PRODUCT_ID).eq(product.getProductId());
        Product freshProduct = MyDBOverseer.get().getEntity(mStatement, Product.class);

        assertNotNull(freshProduct);
        assertEquals(freshProduct.getPrice(), product.getPrice());
        assertEquals(freshProduct.getProductName(), product.getProductName());
    }

    public void testCreateByEntry() {
        // create the sub-query SQL statement.
        Statement queryStmt = QueryStatement.produce(Products.PRODUCT_NAME, Products.SUPPLIER_ID,
                Products.CATEGORY_ID, Products.UNIT, Products.PRICE).from(Products.TABLE_NAME)
                .where(Products.PRICE).elt(20).limit(10, 20);

        // counting the sub-query SQL statement rows.
        Statement countStmt = QueryStatement.rowCount().from(new Parenthesize(queryStmt));

        assertSQLEquals("SELECT count(*) FROM (SELECT product_name, supplier_id, category_id, " +
                "unit, price FROM Products WHERE price <= 20 LIMIT 10 OFFSET 20)", countStmt);

        int comingRowCount = MyDBOverseer.get().getInt(countStmt);

        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        mStatement = CreateStatement.produce(Products.TABLE_NAME).columns(Products.PRODUCT_NAME,
                Products.SUPPLIER_ID, Products.CATEGORY_ID, Products.UNIT, Products.PRICE).entry(queryStmt);

        assertSQLEquals("INSERT INTO Products(product_name, supplier_id, category_id, unit, price) " +
                "SELECT product_name, supplier_id, category_id, unit, price " +
                "FROM Products WHERE price <= 20 LIMIT 10 OFFSET 20");

        // assert the create-by-entry operations successful.
        int lastProductId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertGreatThan(lastProductId, 0);

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Products.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // comparing if the new rows count equal as expected.
        assertEquals(beforeRowCount + comingRowCount, afterRowCount);
    }

}
