package com.vincestyling.asqliteplus.tests;

import com.vincestyling.asqliteplus.statement.DeleteStatement;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.table.Categories;
import com.vincestyling.asqliteplus.table.Products;

public class GeneralDeleteTest extends BaseDBTestCase {

    public void testDeleteExplicitly() {
        mStatement = DeleteStatement.produce(Products.TABLE_NAME).where(Products.PRODUCT_ID).eq(1);

        assertSQLSuccessful("DELETE FROM Products WHERE product_id = 1");

        mStatement = QueryStatement.produce().from(Products.TABLE_NAME).where(Products.PRODUCT_ID).eq(1);

        assertSQLHasNotResult("SELECT * FROM Products WHERE product_id = 1");
    }

    public void testDeleteAllRows() {
        mStatement = DeleteStatement.produce(Categories.TABLE_NAME);

        assertSQLSuccessful("DELETE FROM Categories");

        mStatement = QueryStatement.produce().from(Categories.TABLE_NAME);

        assertSQLHasNotResult("SELECT * FROM Categories");
    }

}
