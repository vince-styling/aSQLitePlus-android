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
package com.vincestyling.asqliteplus;

import android.database.Cursor;

/**
 * An interface used for mapping each row of a {@link Cursor} to a result object. Typically used to query
 * a list datasource, it's an ideal choice for implementing row-mapping logic in a single place.
 *
 * @param <T> the mapped entity.
 * @see com.vincestyling.asqliteplus.DBOverseer#getList(Object, RowMapper)
 */
public interface RowMapper<T> {
    /**
     * Implementations must implement this method to map each row of data in the {@link Cursor}.
     * This method should not call any non-column operation such as move();
     * it is only supposed to map values of the current row.
     *
     * @param cursor the Cursor to map (pre-moved for the current row)
     * @return the result object for the current row
     */
    T mapRow(Cursor cursor);
}
