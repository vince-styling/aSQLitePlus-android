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
import com.vincestyling.asqliteplus.entity.Category;
import com.vincestyling.asqliteplus.statement.CreateStatement;

import java.util.ArrayList;
import java.util.List;

public class Categories extends Table {
    public static final String TABLE_NAME = "Categories";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String DESCRIPTION = "description";

    public static CharSequence buildColumnDeclarations() {
        return concatColumns(
                CATEGORY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT",
                CATEGORY_NAME + " VARCHAR(255)", DESCRIPTION + " VARCHAR(255)");
    }

    public final static List<Category> INIT_DATAS = new ArrayList<Category>();

    static {
        INIT_DATAS.add(new Category("Beverages", "Soft drinks, coffees, teas, beers, and ales"));
        INIT_DATAS.add(new Category("Condiments", "Sweet and savory sauces, relishes, spreads, and seasonings"));
        INIT_DATAS.add(new Category("Confections", "Desserts, candies, and sweet breads"));
        INIT_DATAS.add(new Category("Dairy Products", "Cheeses"));
        INIT_DATAS.add(new Category("Grains/Cereals", "Breads, crackers, pasta, and cereal"));
        INIT_DATAS.add(new Category("Meat/Poultry", "Prepared meats"));
        INIT_DATAS.add(new Category("Produce", "Dried fruit and bean curd"));
        INIT_DATAS.add(new Category("Seafood", "Seaweed and fish"));
    }

    public final static DBOperator<Category> CREATE_DBOPER = new DBOperator<Category>() {
        @Override
        public Object produce(Category entity) {
            return CreateStatement.produce(TABLE_NAME)
                    .put(DESCRIPTION, entity.getDescription())
                    .put(CATEGORY_NAME, entity.getCategoryName());
        }
    };
}
