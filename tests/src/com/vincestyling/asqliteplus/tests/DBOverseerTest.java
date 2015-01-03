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

import com.vincestyling.asqliteplus.entity.Customer;
import com.vincestyling.asqliteplus.statement.CreateStatement;
import com.vincestyling.asqliteplus.table.Customers;
import com.vincestyling.asqliteplus.table.Table;

public class DBOverseerTest extends BaseDBTestCase {

    @Override
    protected void setUpDB() throws Exception {
        Table.prepare(Customers.class);
    }

    public void testExecuteInsertButNotAutoincrementKeyWillReturn() {
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

}
