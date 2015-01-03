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

import com.vincestyling.asqliteplus.entity.Supplier;
import com.vincestyling.asqliteplus.statement.CreateStatement;
import com.vincestyling.asqliteplus.statement.Parenthesize;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Statement;
import com.vincestyling.asqliteplus.table.Suppliers;
import com.vincestyling.asqliteplus.table.Table;

public class GeneralCreateTest extends BaseDBTestCase {
    @Override
    protected void setUpDB() throws Exception {
        Table.prepare(Suppliers.class);
    }

    public void testStandardSyntax() {
        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        Supplier supplier = Suppliers.INIT_DATAS.get(0);
        mStatement = CreateStatement.produce(Suppliers.TABLE_NAME)
                .put(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
                .put(Suppliers.CONTACT_NAME, supplier.getContactName())
                .put(Suppliers.ADDRESS, supplier.getAddress())
                .put(Suppliers.CITY, supplier.getCity())
                .put(Suppliers.POSTAL_CODE, supplier.getPostalCode())
                .put(Suppliers.COUNTRY, supplier.getCountry())
                .put(Suppliers.PHONE, supplier.getPhone());

        assertSQLEquals("INSERT INTO Suppliers(supplier_name, contact_name, address, city, " +
                "postal_code, country, phone) VALUES('Exotic Liquid', 'Charlotte Cooper', " +
                "'49 Gilbert St.', 'Londona', 'EC1 4SD', 'UK', '(171) 555-2222')");

        // assert the create-by-entry operations successful.
        int newProductId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertGreatThan(newProductId, 0);

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // comparing if the current rows count great than before.
        assertGreatThan(afterRowCount, beforeRowCount);
    }

    public void testCreateOrReplace() {
        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        // taking a sample entity then modify some fields.
        Supplier supplier = Suppliers.INIT_DATAS.get(2);
        supplier.setSupplierName("Grandma Kelly's Cap");
        supplier.setCity("Houston");

        mStatement = CreateStatement.orReplace(Suppliers.TABLE_NAME)
                .put(Suppliers.SUPPLIER_ID, supplier.getSupplierId())
                .put(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
                .put(Suppliers.CONTACT_NAME, supplier.getContactName())
                .put(Suppliers.ADDRESS, supplier.getAddress())
                .put(Suppliers.CITY, supplier.getCity())
                .put(Suppliers.POSTAL_CODE, supplier.getPostalCode())
                .put(Suppliers.COUNTRY, supplier.getCountry())
                .put(Suppliers.PHONE, supplier.getPhone());

        assertSQLEquals("INSERT OR REPLACE INTO Suppliers(supplier_id, supplier_name, contact_name, address, " +
                "city, postal_code, country, phone) VALUES(3, 'Grandma Kelly''s Cap', 'Regina Murphy', " +
                "'707 Oxford Rd.', 'Houston', '48104', 'USA', '(313) 555-5735')");

        // perform and assert the returned product id still been before.
        int returnedSupplierId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertEquals(returnedSupplierId, supplier.getSupplierId());

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // assert no newly row effected.
        assertEquals(afterRowCount, beforeRowCount);

        // taking the replaced row as entity then comparing the two values which
        // modified before to verify the "INSERT OR REPLACE" command successful.
        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
        Supplier freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);

        assertNotNull(freshSupplier);
        assertEquals(freshSupplier.getCity(), supplier.getCity());
        assertEquals(freshSupplier.getSupplierName(), supplier.getSupplierName());
    }

    public void testCreateByEntry() {
        // create the sub-query SQL statement.
        Statement queryStmt = QueryStatement.produce(Suppliers.SUPPLIER_NAME, Suppliers.CONTACT_NAME,
                Suppliers.ADDRESS, Suppliers.CITY, Suppliers.POSTAL_CODE, Suppliers.COUNTRY,
                Suppliers.PHONE).from(Suppliers.TABLE_NAME).where(Suppliers.COUNTRY).eq("USA").limit(2, 1);

        // counting the sub-query SQL statement rows.
        Statement countStmt = QueryStatement.rowCount().from(new Parenthesize(queryStmt));

        assertSQLEquals("SELECT count(*) FROM (SELECT supplier_name, contact_name, address, city, postal_code, " +
                "country, phone FROM Suppliers WHERE country = 'USA' LIMIT 2 OFFSET 1)", countStmt);

        int comingRowCount = MyDBOverseer.get().getInt(countStmt);

        // counting the row amount of table BEFORE perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int beforeRowCount = MyDBOverseer.get().getInt(mStatement);

        mStatement = CreateStatement.produce(Suppliers.TABLE_NAME).columns(Suppliers.SUPPLIER_NAME,
                Suppliers.CONTACT_NAME, Suppliers.ADDRESS, Suppliers.CITY, Suppliers.POSTAL_CODE,
                Suppliers.COUNTRY, Suppliers.PHONE).entry(queryStmt);

        assertSQLEquals("INSERT INTO Suppliers(supplier_name, contact_name, address, city, postal_code, " +
                "country, phone) SELECT supplier_name, contact_name, address, city, postal_code, " +
                "country, phone FROM Suppliers WHERE country = 'USA' LIMIT 2 OFFSET 1");

        // assert the create-by-entry operations successful.
        int lastSupplierId = (int) MyDBOverseer.get().executeInsert(mStatement);
        assertGreatThan(lastSupplierId, 0);

        // counting the row amount of table AFTER perform the target SQL statement.
        mStatement = QueryStatement.rowCount().from(Suppliers.TABLE_NAME);
        int afterRowCount = MyDBOverseer.get().getInt(mStatement);

        // comparing if the new rows count equal as expected.
        assertEquals(beforeRowCount + comingRowCount, afterRowCount);
    }

}
