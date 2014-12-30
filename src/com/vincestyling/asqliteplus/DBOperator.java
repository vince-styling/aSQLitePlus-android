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

/**
 * This class used to producing a SQL statement by the given entity.
 * Usually an INSERT||UPDATE||DELETE statement in a batch operation context.
 *
 * @param <T> the produced SQL statement. The caller would invoke its {@link #toString()}
 *            to take the final statement. Can be either a normal String which just the SQL
 *            or represented the SQL's {@link com.vincestyling.asqliteplus.statement.Statement} object.
 * @see com.vincestyling.asqliteplus.DBOverseer#executeBatch(java.util.List, DBOperator)
 */
public interface DBOperator<T> {
    /**
     * producing a SQL statement by the given entity, usually an INSERT||UPDATE||DELETE statement.
     * @param entity the operating entity.
     * @return the produced SQL statement.
     */
    Object produce(T entity);
}
