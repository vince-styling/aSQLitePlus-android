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

import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.asqliteplus.entity.Category;
import com.vincestyling.asqliteplus.entity.Customer;
import com.vincestyling.asqliteplus.entity.Product;
import com.vincestyling.asqliteplus.statement.CreateStatement;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Customers;
import com.vincestyling.asqliteplus.table.Products;
import com.vincestyling.asqliteplus.table.Table;

import java.util.List;

public class DBOverseerTest extends BaseDBTestCase {

    @Override
    protected void setUpDB() throws Exception {
        Table.prepare(Products.class);
        Table.prepare(Customers.class);
        Table.prepare(Categories.class);
    }

    public void testExecuteInsertButNotAutoincrementKeyWillGenerate() {
        Customer customer = new Customer("C00014", "Vince", "ZhuHai", "GuangDong", "China");
        mStatement = CreateStatement.produce(Customers.TABLE_NAME)
                .put(Customers.CUST_CODE, customer.getCustCode())
                .put(Customers.CUST_NAME, customer.getCustName())
                .put(Customers.CUST_CITY, customer.getCustCity())
                .put(Customers.WORKING_AREA, customer.getWorkingArea())
                .put(Customers.CUST_COUNTRY, customer.getCustCountry());

        assertSQLEquals("INSERT INTO Customers(cust_code, cust_name, cust_city, " +
                "working_area, cust_country) VALUES('C00014', 'Vince', 'ZhuHai', 'GuangDong', 'China')");

        int returnIdentifier = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertGreatThan(returnIdentifier, 0);
    }

    public void testGetLastInsertRowId() {
        int lastRowId = MyDBOverseer.get().getLastInsertRowId(Customers.TABLE_NAME);
        assertEquals(0, lastRowId);

        lastRowId = MyDBOverseer.get().getLastInsertRowId(Categories.TABLE_NAME);
        assertGreatThan(lastRowId, 0);
    }

    public void testGetMultipleInts() {
        int columnCount = 3;
        mStatement = QueryStatement.produce(Products.PRODUCT_ID, Products.SUPPLIER_ID, Products.CATEGORY_ID).from(Products.TABLE_NAME);
        int[] columnValues = MyDBOverseer.get().getInts(mStatement);
        assertNotNull(columnValues);
        assertEquals(columnCount, columnValues.length);
        for (int columnValue : columnValues) {
            assertGreatThan(columnValue, 0);
        }
    }

    public void testGetMultipleStrings() {
        int columnCount = 3;
        mStatement = QueryStatement.produce(Products.PRODUCT_NAME, Products.UNIT, Products.PRICE).from(Products.TABLE_NAME);
        String[] columnValues = MyDBOverseer.get().getStrings(mStatement);
        assertNotNull(columnValues);
        assertEquals(columnCount, columnValues.length);
        for (String columnValue : columnValues) {
            assertNotNull(columnValue);
            assertGreatThan(columnValue.length(), 0);
        }
    }

    public void testGetListBySpecifyTheEntityClass() {
        mStatement = QueryStatement.produce().from(Categories.TABLE_NAME);

        List<Category> categoryList = MyDBOverseer.get().getList(mStatement, Category.class);
        assertNotNull(categoryList);

        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            Category cpaCategory = Categories.INIT_DATAS.get(i);

            assertGreatThan(category.getCategoryId(), 0);
            assertEquals(category.getDescription(), cpaCategory.getDescription());
            assertEquals(category.getCategoryName(), cpaCategory.getCategoryName());
        }
    }

    public void testGetPaginationList() {
        mStatement = QueryStatement.produce().from(Products.TABLE_NAME);

        int pageNo = 0;
        int pageItemCount = 10;
        PaginationList<Product> productList;
        do {
            productList = MyDBOverseer.get().getPaginationList(
                    mStatement, ++pageNo, pageItemCount, Product.class);
            assertNotNull(productList);

            if (productList.hasNextPage()) {
                assertEquals(pageItemCount, productList.size());
            } else {
                assertGreatThan(productList.size(), 0);
            }

            for (int i = 0; i < productList.size(); i++) {
                Product product = productList.get(i);
                Product cpaProduct = Products.INIT_DATAS.get((pageNo - 1) * pageItemCount + i);

                assertEquals(product.getProductId(), cpaProduct.getProductId());
                assertEquals(product.getProductName(), cpaProduct.getProductName());
                assertEquals(product.getSupplierId(), cpaProduct.getSupplierId());
                assertEquals(product.getCategoryId(), cpaProduct.getCategoryId());
                assertEquals(product.getUnit(), cpaProduct.getUnit());
                assertEquals(product.getPrice(), cpaProduct.getPrice());
            }
        } while (productList.hasNextPage());
    }

}
