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
package com.vincestyling.asqliteplus.statement;

import static java.lang.String.format;

/**
 * This class used to producing an ALIAS clause which temporary renaming the
 * object(could be table||column||function) as a given name in a particular SQL statement.
 * <p/>
 * For example:
 * <p/>
 * new Alias("tbl_name", "tmp_name") would produce "tbl_name AS tmp_name".
 * <p/>
 * new Alias({@link Function}.sum("price"), "total_price") would produce "sum(price) AS total_price".
 * <p/>
 * <strong>Note:</strong> {@link Statement} would calls the {@link #toString()} method to take the final clause.
 */
public class Alias {
    /**
     * The object to be renaming.
     */
    private Object object;

    /**
     * The object's alias.
     */
    private CharSequence alias;

    /**
     * Constructing the ALIAS clause.
     *
     * @param object the object to be renaming for. Usually a table name or column name,
     *               can be either a normal String which just the name
     *               or wrapped the name's {@link Function} object.
     * @param alias  the newly name.
     */
    public Alias(Object object, CharSequence alias) {
        this.object = object;
        this.alias = alias;
    }

    /**
     * Return the object's alias.
     *
     * @return The object's alias.
     */
    public CharSequence getAlias() {
        return alias;
    }

    /**
     * Taking the final ALIAS clause via this method.
     *
     * @return the final ALIAS clause.
     */
    @Override
    public String toString() {
        return format("%s AS %s", object, alias);
    }
}
