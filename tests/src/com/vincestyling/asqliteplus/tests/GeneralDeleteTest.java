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
import com.vincestyling.asqliteplus.statement.DeleteStatement;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.table.Customers;
import com.vincestyling.asqliteplus.table.Table;

public class GeneralDeleteTest extends BaseDBTestCase {
    @Override
    protected void setUpDB() throws Exception {
        Table.prepare(Customers.class);
    }

    public void testDeleteExplicitly() {
        Customer customer = Customers.INIT_DATAS.get(0);

        mStatement = DeleteStatement.produce(Customers.TABLE_NAME)
                .where(Customers.CUST_CODE).eq(customer.getCustCode());

        assertSQLSuccessful("DELETE FROM Customers WHERE cust_code = 'C00013'");

        mStatement = QueryStatement.produce().from(Customers.TABLE_NAME)
                .where(Customers.CUST_CODE).eq(customer.getCustCode());

        assertSQLHasNotResult("SELECT * FROM Customers WHERE cust_code = 'C00013'");
    }

    public void testDeleteAllRows() {
        mStatement = DeleteStatement.produce(Customers.TABLE_NAME);

        assertSQLSuccessful("DELETE FROM Customers");

        mStatement = QueryStatement.produce().from(Customers.TABLE_NAME);

        assertSQLHasNotResult("SELECT * FROM Customers");
    }

}
