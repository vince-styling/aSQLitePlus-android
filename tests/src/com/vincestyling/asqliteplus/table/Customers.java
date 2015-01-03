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
package com.vincestyling.asqliteplus.table;

import com.vincestyling.asqliteplus.DBOperator;
import com.vincestyling.asqliteplus.entity.Customer;
import com.vincestyling.asqliteplus.statement.CreateStatement;

import java.util.ArrayList;
import java.util.List;

public class Customers extends Table {
    public static final String TABLE_NAME = "Customers";
    public static final String CUST_CODE = "cust_code";
    public static final String CUST_NAME = "cust_name";
    public static final String CUST_CITY = "cust_city";
    public static final String WORKING_AREA = "working_area";
    public static final String CUST_COUNTRY = "cust_country";

    public static CharSequence buildColumnDeclarations() {
        return concatColumns(CUST_CODE + " VARCHAR(255) NOT NULL PRIMARY KEY",
                CUST_NAME + " VARCHAR(255)", CUST_CITY + " VARCHAR(255)",
                WORKING_AREA + " VARCHAR(255)", CUST_COUNTRY + " VARCHAR(255)");
    }

    public final static List<Customer> INIT_DATAS = new ArrayList<Customer>();

    static {
        INIT_DATAS.add(new Customer("C00013", "Holmes", "London", "London", "UK"));
        INIT_DATAS.add(new Customer("C00001", "Micheal", "New York", "New York", "USA"));
        INIT_DATAS.add(new Customer("C00020", "Albert", "New York", "New York", "USA"));
        INIT_DATAS.add(new Customer("C00025", "Ravindran", "Bangalore", "Bangalore", "India"));
        INIT_DATAS.add(new Customer("C00024", "Cook", "London", "London", "UK"));
        INIT_DATAS.add(new Customer("C00015", "Stuart", "London", "London", "UK"));
        INIT_DATAS.add(new Customer("C00002", "Bolt", "New York", "New York", "USA"));
        INIT_DATAS.add(new Customer("C00018", "Fleming", "Brisban", "Brisban", "Australia"));
        INIT_DATAS.add(new Customer("C00021", "Jacks", "Brisban", "Brisban", "Australia"));
        INIT_DATAS.add(new Customer("C00019", "Yearannaidu", "Chennai", "Chennai", "India"));
        INIT_DATAS.add(new Customer("C00005", "Sasikant", "Mumbai", "Mumbai", "India"));
        INIT_DATAS.add(new Customer("C00007", "Ramanathan", "Chennai", "Chennai", "India"));
        INIT_DATAS.add(new Customer("C00022", "Avinash", "Mumbai", "Mumbai", "India"));
        INIT_DATAS.add(new Customer("C00004", "Winston", "Brisban", "Brisban", "Australia"));
        INIT_DATAS.add(new Customer("C00023", "Karl", "London", "London", "UK"));
        INIT_DATAS.add(new Customer("C00006", "Shilton", "Torento", "Torento", "Canada"));
        INIT_DATAS.add(new Customer("C00010", "Charles", "Hampshair", "Hampshair", "UK"));
        INIT_DATAS.add(new Customer("C00017", "Srinivas", "Bangalore", "Bangalore", "India"));
        INIT_DATAS.add(new Customer("C00012", "Steven", "San Jose", "San Jose", "USA"));
        INIT_DATAS.add(new Customer("C00008", "Karolina", "Torento", "Torento", "Canada"));
        INIT_DATAS.add(new Customer("C00003", "Martin", "Torento", "Torento", "Canada"));
    }

    public final static DBOperator<Customer> CREATE_DBOPER = new DBOperator<Customer>() {
        @Override
        public Object produce(Customer entity) {
            return CreateStatement.produce(TABLE_NAME)
                    .put(CUST_CODE, entity.getCustCode())
                    .put(CUST_NAME, entity.getCustName())
                    .put(CUST_CITY, entity.getCustCity())
                    .put(WORKING_AREA, entity.getWorkingArea())
                    .put(CUST_COUNTRY, entity.getCustCountry());
        }
    };
}
