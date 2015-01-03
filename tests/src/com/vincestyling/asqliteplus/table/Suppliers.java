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
import com.vincestyling.asqliteplus.entity.Supplier;
import com.vincestyling.asqliteplus.statement.CreateStatement;

import java.util.ArrayList;
import java.util.List;

public class Suppliers extends Table {
    public static final String TABLE_NAME = "Suppliers";
    public static final String SUPPLIER_ID = "supplier_id";
    public static final String SUPPLIER_NAME = "supplier_name";
    public static final String CONTACT_NAME = "contact_name";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String POSTAL_CODE = "postal_code";
    public static final String COUNTRY = "country";
    public static final String PHONE = "phone";

    public static CharSequence buildColumnDeclarations() {
        return concatColumns(
                SUPPLIER_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT",
                SUPPLIER_NAME + " VARCHAR(255)",
                CONTACT_NAME + " VARCHAR(255)",
                ADDRESS + " VARCHAR(255)",
                CITY + " VARCHAR(255)",
                POSTAL_CODE + " VARCHAR(255)",
                COUNTRY + " VARCHAR(255)",
                PHONE + " VARCHAR(255)");
    }

    public final static List<Supplier> INIT_DATAS = new ArrayList<Supplier>();

    static {
        INIT_DATAS.add(new Supplier(1, "Exotic Liquid", "Charlotte Cooper", "49 Gilbert St.", "Londona", "EC1 4SD", "UK", "(171) 555-2222"));
        INIT_DATAS.add(new Supplier(2, "New Orleans Cajun Delights", "Shelley Burke", "P.O. Box 78934", "New Orleans", "70117", "USA", "(100) 555-4822"));
        INIT_DATAS.add(new Supplier(3, "Grandma Kelly\'s Homestead", "Regina Murphy", "707 Oxford Rd.", "Ann Arbor", "48104", "USA", "(313) 555-5735"));
        INIT_DATAS.add(new Supplier(4, "Tokyo Traders", "Yoshi Nagase", "9-8 Sekimai Musashino-shi", "Tokyo", "100", "Japan", "(03) 3555-5011"));
        INIT_DATAS.add(new Supplier(5, "Cooperativa de Quesos \'Las Cabras\'", "Antonio del Valle Saavedra ", "Calle del Rosal 4", "Oviedo", "33007", "Spain", "(98) 598 76 54"));
        INIT_DATAS.add(new Supplier(6, "Mayumi\'s", "Mayumi Ohno", "92 Setsuko Chuo-ku", "Osaka", "545", "Japan", "(06) 431-7877"));
        INIT_DATAS.add(new Supplier(7, "Pavlova, Ltd.", "Ian Devling", "74 Rose St. Moonie Ponds", "Melbourne", "3058", "Australia", "(03) 444-2343"));
        INIT_DATAS.add(new Supplier(8, "Specialty Biscuits, Ltd.", "Peter Wilson", "29 King\'s Way", "Manchester", "M14 GSD", "UK", "(161) 555-4448"));
        INIT_DATAS.add(new Supplier(9, "PB Knäckebröd AB", "Lars Peterson", "Kaloadagatan 13", "Göteborg", "S-345 67", "Sweden ", "031-987 65 43"));
        INIT_DATAS.add(new Supplier(10, "Refrescos Americanas LTDA", "Carlos Diaz", "Av. das Americanas 12.890", "São Paulo", "5442", "Brazil", "(11) 555 4640"));
        INIT_DATAS.add(new Supplier(11, "Heli Süßwaren GmbH &amp; Co. KG", "Petra Winkler", "Tiergartenstraße 5", "Berlin", "10785", "Germany", "(010) 9984510"));
        INIT_DATAS.add(new Supplier(12, "Plutzer Lebensmittelgroßmärkte AG", "Martin Bein", "Bogenallee 51", "Frankfurt", "60439", "Germany", "(069) 992755"));
        INIT_DATAS.add(new Supplier(13, "Nord-Ost-Fisch Handelsgesellschaft mbH", "Sven Petersen", "Frahmredder 112a", "Cuxhaven", "27478", "Germany", "(04721) 8713"));
        INIT_DATAS.add(new Supplier(14, "Formaggi Fortini s.r.l.", "Elio Rossi", "Viale Dante, 75", "Ravenna", "48100", "Italy", "(0544) 60323"));
        INIT_DATAS.add(new Supplier(15, "Norske Meierier", "Beate Vileid", "Hatlevegen 5", "Sandvika", "1320", "Norway", "(0)2-953010"));
        INIT_DATAS.add(new Supplier(16, "Bigfoot Breweries", "Cheryl Saylor", "3400 - 8th Avenue Suite 210", "Bend", "97101", "USA", "(503) 555-9931"));
        INIT_DATAS.add(new Supplier(17, "Svensk Sjöföda AB", "Michael Björn", "Brovallavägen 231", "Stockholm", "S-123 45", "Sweden", "08-123 45 67"));
        INIT_DATAS.add(new Supplier(18, "Aux joyeux ecclésiastiques", "Guylène Nodier", "203, Rue des Francs-Bourgeois", "Paris", "75004", "France", "(1) 03.83.00.68"));
        INIT_DATAS.add(new Supplier(19, "New England Seafood Cannery", "Robb Merchant", "Order Processing Dept. 2100 Paul Revere Blvd.", "Boston", "02134", "USA", "(617) 555-3267"));
        INIT_DATAS.add(new Supplier(20, "Leka Trading", "Chandra Leka", "471 Serangoon Loop, Suite #402", "Singapore", "0512", "Singapore", "555-8787"));
        INIT_DATAS.add(new Supplier(21, "Lyngbysild", "Niels Petersen", "Lyngbysild Fiskebakken 10", "Lyngby", "2800", "Denmark", "43844108"));
        INIT_DATAS.add(new Supplier(22, "Zaanse Snoepfabriek", "Dirk Luchte", "Verkoop Rijnweg 22", "Zaandam", "9999 ZZ", "Netherlands", "(12345) 1212"));
        INIT_DATAS.add(new Supplier(23, "Karkki Oy", "Anne Heikkonen", "Valtakatu 12", "Lappeenranta", "53120", "Finland", "(953) 10956"));
        INIT_DATAS.add(new Supplier(24, "G\'day, Mate", "Wendy Mackenzie", "170 Prince Edward Parade Hunter\'s Hill", "Sydney", "2042", "Australia", "(02) 555-5914"));
        INIT_DATAS.add(new Supplier(25, "Ma Maison", "Jean-Guy Lauzon", "2960 Rue St. Laurent", "Montréal", "H1J 1C3", "Canada", "(514) 555-9022"));
        INIT_DATAS.add(new Supplier(26, "Pasta Buttini s.r.l.", "Giovanni Giudici", "Via dei Gelsomini, 153", "Salerno", "84100", "Italy", "(089) 6547665"));
        INIT_DATAS.add(new Supplier(27, "Escargots Nouveaux", "Marie Delamare", "22, rue H. Voiron", "Montceau", "71300", "France", "85.57.00.07"));
        INIT_DATAS.add(new Supplier(28, "Gai pâturage", "Eliane Noz", "Bat. B 3, rue des Alpes", "Annecy", "74000", "France", "38.76.98.06"));
        INIT_DATAS.add(new Supplier(29, "Forêts d\'érables", "Chantal Goulet", "148 rue Chasseur", "Ste-Hyacinthe", "J2S 7S8", "Canada", "(514) 555-2955"));
    }

    public final static DBOperator<Supplier> CREATE_DBOPER = new DBOperator<Supplier>() {
        @Override
        public Object produce(Supplier entity) {
            return CreateStatement.produce(TABLE_NAME)
                    .put(SUPPLIER_ID, entity.getSupplierId())
                    .put(SUPPLIER_NAME, entity.getSupplierName())
                    .put(CONTACT_NAME, entity.getContactName())
                    .put(ADDRESS, entity.getAddress())
                    .put(CITY, entity.getCity())
                    .put(POSTAL_CODE, entity.getPostalCode())
                    .put(COUNTRY, entity.getCountry())
                    .put(PHONE, entity.getPhone());
        }
    };
}
