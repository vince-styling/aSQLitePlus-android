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
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.UpdateStatement;
import com.vincestyling.asqliteplus.table.Suppliers;
import com.vincestyling.asqliteplus.table.Table;

public final class GeneralUpdateTest extends BaseDBTestCase {
    @Override
    protected void setUpDB() throws Exception {
        Table.prepare(Suppliers.class);
    }

    public void testStandardSyntax() {
        Supplier supplier = Suppliers.INIT_DATAS.get(0);
        supplier.setSupplierName("New Name");
        supplier.setContactName("Small Wood");
        supplier.setAddress("Middle Street of States");
        supplier.setCity("ZhuHai");
        supplier.setPostalCode("510665");
        supplier.setCountry("China");
        supplier.setPhone("+86 13800 138000");

        mStatement = UpdateStatement.produce(Suppliers.TABLE_NAME)
                .set(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
                .set(Suppliers.CONTACT_NAME, supplier.getContactName())
                .set(Suppliers.ADDRESS, supplier.getAddress())
                .set(Suppliers.CITY, supplier.getCity())
                .set(Suppliers.POSTAL_CODE, supplier.getPostalCode())
                .set(Suppliers.COUNTRY, supplier.getCountry())
                .set(Suppliers.PHONE, supplier.getPhone())
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());

        assertSQLEquals("UPDATE Suppliers SET supplier_name = 'New Name', contact_name = 'Small Wood', " +
                "address = 'Middle Street of States', city = 'ZhuHai', postal_code = '510665', " +
                "country = 'China', phone = '+86 13800 138000' WHERE supplier_id = 1");

        assertEquals(1, MyDBOverseer.get().executeSql(mStatement));

        // taking the updated row as entity then comparing the values
        // which modified before to verify the UPDATE was persisted.
        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
        Supplier freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);

        GeneralCreateTest.assertSuppliersEquals(freshSupplier, supplier);
    }

    public void testUpdateOrIgnore() {
        Supplier supplier = Suppliers.INIT_DATAS.get(1);
        Supplier targetSupplier = Suppliers.INIT_DATAS.get(2);

        mStatement = UpdateStatement.orIgnore(Suppliers.TABLE_NAME)
                // update supplier_id to another supplier_id which already populated by another row.
                .set(Suppliers.SUPPLIER_ID, targetSupplier.getSupplierId())
                .set(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
                .set(Suppliers.CONTACT_NAME, supplier.getContactName())
                .set(Suppliers.ADDRESS, supplier.getAddress())
                .set(Suppliers.CITY, supplier.getCity())
                .set(Suppliers.POSTAL_CODE, supplier.getPostalCode())
                .set(Suppliers.COUNTRY, supplier.getCountry())
                .set(Suppliers.PHONE, supplier.getPhone())
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());

        assertSQLEquals("UPDATE OR IGNORE Suppliers SET supplier_id = 3, supplier_name = 'New Orleans Cajun Delights', " +
                "contact_name = 'Shelley Burke', address = 'P.O. Box 78934', city = 'New Orleans', postal_code = '70117', " +
                "country = 'USA', phone = '(100) 555-4822' WHERE supplier_id = 2");

        // so this update does not affect anything.
        assertEquals(0, MyDBOverseer.get().executeSql(mStatement));

        // verify two Suppliers has not change.
        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
        Supplier freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);
        GeneralCreateTest.assertSuppliersEquals(freshSupplier, supplier);

        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(targetSupplier.getSupplierId());
        freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);
        GeneralCreateTest.assertSuppliersEquals(freshSupplier, targetSupplier);
    }

    public void testUpdateOrReplace() {
        Supplier supplier = Suppliers.INIT_DATAS.get(3);
        Supplier targetSupplier = Suppliers.INIT_DATAS.get(4);

        mStatement = UpdateStatement.orReplace(Suppliers.TABLE_NAME)
                // update supplier_id to another supplier_id which already populated by another row.
                .set(Suppliers.SUPPLIER_ID, targetSupplier.getSupplierId())
                .set(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
                .set(Suppliers.CONTACT_NAME, supplier.getContactName())
                .set(Suppliers.ADDRESS, supplier.getAddress())
                .set(Suppliers.CITY, supplier.getCity())
                .set(Suppliers.POSTAL_CODE, supplier.getPostalCode())
                .set(Suppliers.COUNTRY, supplier.getCountry())
                .set(Suppliers.PHONE, supplier.getPhone())
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());

        assertSQLEquals("UPDATE OR REPLACE Suppliers SET supplier_id = 5, supplier_name = 'Tokyo Traders', " +
                "contact_name = 'Yoshi Nagase', address = '9-8 Sekimai Musashino-shi', city = 'Tokyo', " +
                "postal_code = '100', country = 'Japan', phone = '(03) 3555-5011' WHERE supplier_id = 4");

        // update will delete "supplier_id = 5"'s row and occupy it's primary key with "supplier_id = 4"'s datas.
        assertEquals(1, MyDBOverseer.get().executeSql(mStatement));

        // assert "supplier_id = 4"'s row has gone.
        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
        Supplier freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);
        assertNull(freshSupplier);

        // verify "supplier_id = 5"'s datas were "supplier_id = 4"'s.
        mStatement = QueryStatement.produce().from(Suppliers.TABLE_NAME)
                .where(Suppliers.SUPPLIER_ID).eq(targetSupplier.getSupplierId());
        freshSupplier = MyDBOverseer.get().getEntity(mStatement, Supplier.class);
        GeneralCreateTest.assertSuppliersEquals(freshSupplier, supplier);
    }

}
