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
package com.vincestyling.asqliteplus.tests.query;

import com.vincestyling.asqliteplus.statement.Function;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.UnescapeString;
import com.vincestyling.asqliteplus.tests.BaseDBTestCase;
import com.vincestyling.asqliteplus.tests.MyDBOverseer;

public class OtherwiseTest extends BaseDBTestCase {

    @Override
    protected void setUpDB() throws Exception {
    }

    public void testUnescapeString() {
        mStatement = QueryStatement.produce(new UnescapeString("datetime('now', 'localtime')"));
        String currentTime = MyDBOverseer.get().getString(mStatement);

        assertNotNull(currentTime);
        assertGreatThan(currentTime.length(), 8);
    }

    public void testSqliteVersionFetching() {
        mStatement = QueryStatement.produce(Function.sqliteVersion());
        String sqliteVersion = MyDBOverseer.get().getString(mStatement);

        assertNotNull(sqliteVersion);
        assertGreatThan(sqliteVersion.length(), 4);
    }

}
