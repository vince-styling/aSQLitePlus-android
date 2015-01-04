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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.vincestyling.asqliteplus.statement.Parenthesize;
import com.vincestyling.asqliteplus.statement.QueryStatement;
import com.vincestyling.asqliteplus.statement.Statement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class exposes numerous handy methods to performing SQL statements, to be enable them,
 * create a subclass and pass the {@link SQLiteOpenHelper} instance in.
 * <p/>
 * All methods accepts the SQL as an Object, only its toString() matter. In other words,
 * developer can constructing any Object such as {@link String}, {@link StringBuilder},
 * {@link Statement} to carrying the SQL statement, just make sure that the toString()
 * method would return the final SQL statement you want to be executing.
 */
public class DBOverseer {
    protected static final String TAG = "DBOverseer";

    /**
     * true indicates enable debug, would output the executing SQL statement.
     */
    protected boolean mIsDebug;

    public DBOverseer(SQLiteOpenHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    /**
     * Having a single SQLiteOpenHelper instance is benefit in threading cases throughout the Application's entire life
     * cycle, Read more details in <a href="http://stackoverflow.com/a/8888606/1294681">StackOverflow Question</a>.
     */
    protected SQLiteOpenHelper mDBHelper;

    /**
     * Fetches the newly inserted row ID of the specified table. Return 0 if the given table without an INTEGER primary key.
     * <p/>
     * e.g. "select seq from sqlite_sequence where name = 'tbl_name'".
     *
     * @param table the name of the table.
     * @return the row ID of the newly inserted row of the table.
     */
    public int getLastInsertRowId(CharSequence table) {
        return getInt(QueryStatement.produce("seq").from("sqlite_sequence").where("name").eq(table));
    }

    /**
     * Performs the query and take the INTEGER value in the first column of the first row.
     * Return 0 as default either not result presented or error occurred.
     *
     * @param sql the SQL in querying form.
     * @return the INTEGER value.
     */
    public int getInt(Object sql) {
        return getInt(sql, 0);
    }

    /**
     * Performs the query and take the INTEGER value in the first column of the first row.
     * Return the given value as default either not result presented or error occurred.
     *
     * @param sql the SQL in querying form.
     * @param def the default value.
     * @return the INTEGER value.
     */
    public int getInt(Object sql, int def) {
        try {
            return getInts(sql)[0];
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return def;
    }

    /**
     * Performs the query and retrieve all columns as INTEGER value in the first row.
     *
     * @param sql the multiple-columns SQL in querying form.
     * @return the INTEGER values.
     */
    public int[] getInts(Object sql) {
        Cursor cursor = null;
        try {
            debugSql(sql);
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                int columnCount = cursor.getColumnCount();
                int[] result = new int[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    result[i] = cursor.getInt(i);
                }
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * Performs the query and take the STRING value in the first column of the first row.
     * Return null as default either not result presented or error occurred.
     *
     * @param sql the SQL in querying form.
     * @return the STRING value.
     */
    public String getString(Object sql) {
        return getString(sql, null);
    }

    /**
     * Performs the query and take the STRING value in the first column of the first row.
     * Return the given value as default either not result presented or error occurred.
     *
     * @param sql the SQL in querying form.
     * @param def the default value.
     * @return the STRING value.
     */
    public String getString(Object sql, String def) {
        try {
            return getStrings(sql)[0];
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return def;
    }

    /**
     * Performs the query and retrieve all columns as STRING value in the first row.
     *
     * @param sql the multiple-columns SQL in querying form.
     * @return the STRING values.
     */
    public String[] getStrings(Object sql) {
        Cursor cursor = null;
        try {
            debugSql(sql);
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                int columnCount = cursor.getColumnCount();
                String[] result = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    result[i] = cursor.getString(i);
                }
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * Performs the query and checking if has result.
     *
     * @param sql the query statement.
     * @return true if result presented.
     */
    public boolean checkIfExists(Object sql) {
        Cursor cursor = null;
        try {
            debugSql(sql);
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql.toString(), null);
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    /**
     * A convenience way to expose the {@link SQLiteStatement#executeInsert()} method.
     * <p/>
     * Performs the SQL statement and return the ID of the row inserted due to this call.
     * The SQL statement should be an INSERT for this to be a useful call.
     * <p/>
     * Sometime, an INSERT could be creating more than one rows, e.g.
     * "INSERT INTO tbl_name(...) SELECT ... FROM an_tbl_name",
     * that the last created row ID would be returned.
     *
     * @param sql the INSERT SQL statement.
     * @return the row ID of the newly inserted row if this insert is successful, -1 if an error occurred.
     * Note as long as insert was successful, this execution will always return a positive value, even if
     * the operating table did not creating as a table that holding an auto increment INTEGER primary key.
     * @see android.database.sqlite.SQLiteDatabase#insert(String, String, android.content.ContentValues)
     */
    public long executeInsert(Object sql) {
        SQLiteDatabase dataBase = null;
        SQLiteStatement statement = null;
        try {
            dataBase = mDBHelper.getWritableDatabase();
            statement = dataBase.compileStatement(sql.toString());
            debugSql(sql);
            return statement.executeInsert();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (statement != null) statement.close();
            if (dataBase != null) dataBase.close();
        }
        return -1;
    }

    /**
     * A convenience way to expose the {@link SQLiteStatement#executeUpdateDelete()} method.
     * <p/>
     * Performs a single SQL statement that is NOT a <b>SELECT</b> or any other SQL statement
     * that returns data. Would return the number of rows affected by execution of this SQL
     * statement, thus usually used to perform a update kind's statement, including INSERT,
     * UPDATE, DELETE, CREATE-TABLE, DROP-TABLE etc.
     *
     * @param sql the SQL statement.
     * @return the number of rows affected by this SQL statement execution.
     * @see android.database.sqlite.SQLiteDatabase#update(String, android.content.ContentValues, String, String[])
     * @see android.database.sqlite.SQLiteDatabase#delete(String, String, String[])
     * @see android.database.sqlite.SQLiteDatabase#execSQL(String, Object[])
     * @see android.database.sqlite.SQLiteDatabase#execSQL(String)
     */
    public int executeSql(Object sql) {
        SQLiteDatabase dataBase = null;
        SQLiteStatement statement = null;
        try {
            dataBase = mDBHelper.getWritableDatabase();
            statement = dataBase.compileStatement(sql.toString());
            debugSql(sql);
            return statement.executeUpdateDelete();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (statement != null) statement.close();
            if (dataBase != null) dataBase.close();
        }
        return 0;
    }

    /**
     * Performs the query and populate the given list by the returning resultset.
     * Every row's fetch principle was defined in the {@link RowMapper} instance.
     *
     * @param sql    the SELECT SQL statement.
     * @param list   the list to be filling.
     * @param mapper the fetching principle for one row.
     * @param <T>    the generic entity which represent one row.
     */
    public <T> void getList(Object sql, List<T> list, RowMapper<T> mapper) {
        Cursor cursor = null;
        try {
            debugSql(sql);
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(mapper.mapRow(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Performs the query and fetching the resultset to an {@link ArrayList}.
     * Every row's fetch principle was defined in the {@link RowMapper} instance.
     *
     * @param sql    the SELECT SQL statement.
     * @param mapper the fetching principle for one row.
     * @param <T>    the generic entity which represent one row.
     * @return the resultset list.
     */
    public <T> List<T> getList(Object sql, RowMapper<T> mapper) {
        List<T> list = new ArrayList<T>();
        getList(sql, list, mapper);
        return list;
    }

    /**
     * Performs the query and fetching the resultset to an {@link ArrayList}.
     * <p/>
     * Advantage with the powerful Java Reflection, this approach accepted an entity class, mapping all the selected
     * columns of row into that entity instance via invoke its setXXX methods at runtime. Which means we needn't
     * hard-coding to perform those setter methods, just make the entity class obey the naming principle.
     *
     * @param sql   the SELECT SQL statement.
     * @param clazz the row's entity class.
     * @param <T>   the generic entity which represent one row.
     * @return the resultset list.
     * @see #getEntity(android.database.Cursor, Class)
     */
    public <T> List<T> getList(Object sql, final Class<T> clazz) {
        return getList(sql, new RowMapper<T>() {
            public T mapRow(Cursor cursor) {
                return getEntity(cursor, clazz);
            }
        });
    }

    /**
     * Performs the query with two pagination arguments, computing and returning
     * the desired PaginationList which contained four pagination informations.
     * <p/>
     * First perform a query like "SELECT count(*) FROM (the original SQL statement lie here)" to
     * determine how many rows of data are available. This allows the number of pages to be calculated.
     * <p/>
     * Second, plus the pagination clauses to the original SQL statement, which would make it change
     * like "[original SQL] LIMIT 10 OFFSET 20", finally perform that changed SQL to take the resultset.
     *
     * @param sql           the SELECT SQL statement.
     * @param pageNo        the current page number.
     * @param pageItemCount how many item a page have.
     * @param clazz         the row's entity class.
     * @param <T>           the generic entity which represent one row.
     * @return the resultset list.
     */
    public <T> PaginationList<T> getPaginationList(Statement sql, int pageNo, int pageItemCount, final Class<T> clazz) {
        // determine how many row are available.
        int totalItemCount = getInt(QueryStatement.rowCount().from(new Parenthesize(sql)));
        if (totalItemCount > 0) {
            // fetch a single page of results.
            PaginationList<T> records = new PaginationList<T>();
            getList(sql.copy().limit(pageItemCount, (pageNo - 1) * pageItemCount), records, new RowMapper<T>() {
                public T mapRow(Cursor cursor) {
                    return getEntity(cursor, clazz);
                }
            });

            return records.setPagination(pageNo, pageItemCount, totalItemCount);
        }
        return new PaginationList<T>(pageNo, pageItemCount, totalItemCount);
    }

    /**
     * Looping the given List, put every item of that into the {@link DBOperator} to producing an individual SQL statement to performing.
     * <p/>
     * Normally used to batch both SQL inserts, updates, and deletes, it does not make sense to batch select statements.
     * <p/>
     * <strong>Note:</strong> All the batch update would be inside a transaction, which make us possible to avoiding
     * data inconsistent by declare "INSERT||UPDATE OR ROLLBACK ..." form's statement whenever necessary.
     *
     * @param list     the batch datasource.
     * @param operator the SQL producer by each item of the datasource.
     * @param <T>      the datasource generic type.
     * @return true if without any errors.
     */
    public <T> boolean executeBatch(List<T> list, DBOperator<T> operator) {
        if (list == null || list.size() == 0) return false;
        SQLiteDatabase dataBase = null;
        try {
            dataBase = mDBHelper.getWritableDatabase();
            // TODO : It appeared SQLite would begin a transaction for UPDATE statement automatically,
            // TODO : see {@link android.database.sqlite.SQLiteStatement#acquireAndLock()}.
            dataBase.beginTransaction();
            for (T entity : list) {
                Object sql = operator.produce(entity);
                debugSql(sql);
                dataBase.execSQL(sql.toString());
            }
            dataBase.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (dataBase != null) {
                dataBase.endTransaction();
                dataBase.close();
            }
        }
        return false;
    }

    /**
     * @see #executeBatch(java.util.List, DBOperator)
     */
    public <T> boolean executeBatch(T[] array, DBOperator<T> operator) {
        return executeBatch(Arrays.asList(array), operator);
    }

    /**
     * Performs the query and return the first row as an entity via {@link RowMapper}.
     *
     * @param sql    the SELECT SQL statement.
     * @param mapper the fetching principle for one row.
     * @param <T>    the generic entity which represent one row.
     * @return the row's entity.
     */
    public <T> T getEntity(Object sql, RowMapper<T> mapper) {
        Cursor cursor = null;
        try {
            debugSql(sql);
            cursor = mDBHelper.getReadableDatabase().rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) return mapper.mapRow(cursor);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * Performs the query and return the first row as an entity which instancing of the specified Class.
     *
     * @param sql   the SELECT SQL statement.
     * @param clazz the entity class.
     * @param <T>   the generic entity which represent one row.
     * @return the instantiated row entity.
     * @see #getEntity(android.database.Cursor, Class)
     */
    public <T> T getEntity(Object sql, final Class<T> clazz) {
        return getEntity(sql, new RowMapper<T>() {
            public T mapRow(Cursor cursor) {
                return getEntity(cursor, clazz);
            }
        });
    }

    public static final String METHOD_PREFIX = "set";

    /**
     * Fetching current row from the {@link Cursor}, instancing the specified Class and mapping
     * each presented column's value associated with its name to the entity instance.
     * <p/>
     * The names of columns in SQL statement played a big role here. For example:
     * say the current row contained "nick_name, age, login_time" three columns,
     * then the Class instance's setNickName(...), setAge(...), setLoginTime(...)
     * would be invoke accordingly to mapping their values in.
     * <p/>
     * Default naming principle accepted used underline(_) to separated each word in SQLite column naming;
     * For Java, we followed the traditional naming which remove the underline character and Upper Case
     * the first letter of each word. But that isn't fixed, in order to flexibility, the column name's
     * translation pattern can be customize by overwrite the {@link #translateColumnName(String)} method.
     * <p/>
     * <strong>Notice:</strong> Because this approach was powered by Java Reflection, in order to
     * prevent name mapping problems, it is important to tell <b>ProGuard</b> that which classes
     * and members you wish not be obfuscated when you build your application in release mode.
     *
     * @param cursor the Cursor to map.
     * @param clazz  the row's entity class.
     * @param <T>    the generic entity which represent one row.
     * @return the instantiated row entity.
     * @see #translateColumnName(String)
     */
    protected <T> T getEntity(Cursor cursor, Class<T> clazz) {
        try {
            // instancing the generic entity.
            T entity = clazz.newInstance();

            // obtain all the presented column names.
            String[] columnNames = cursor.getColumnNames();

            for (int index = 0; index < columnNames.length; index++) {
                // searching for a method named "set[ColumnName]" such as setLoginTime, setAge(case-insensitive).
                for (Method method : clazz.getMethods()) {
                    // picking the matched method by name.
                    if (method.getName().equalsIgnoreCase(METHOD_PREFIX + translateColumnName(columnNames[index]))) {
                        // checking the parameter type.
                        Class paramType = method.getParameterTypes()[0];

                        // invoke the proper getter method of Cursor to putting the value in.
                        if (paramType == String.class)
                            method.invoke(entity, cursor.getString(index));
                        else if (paramType == int.class)
                            method.invoke(entity, cursor.getInt(index));
                        else if (paramType == short.class)
                            method.invoke(entity, cursor.getShort(index));
                        else if (paramType == long.class)
                            method.invoke(entity, cursor.getLong(index));
                        else if (paramType == float.class)
                            method.invoke(entity, cursor.getFloat(index));
                        else if (paramType == double.class)
                            method.invoke(entity, cursor.getDouble(index));
                        else if (paramType == byte[].class)
                            method.invoke(entity, cursor.getBlob(index));

                        break;
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Simply remove all the underline(_) characters to translating the name which
     * used underline(_) to separated each word in SQLite column naming.
     *
     * @param columnName the original column name.
     * @return the new name with all underline(_) removed.
     */
    protected String translateColumnName(String columnName) {
        return columnName.replaceAll("_", "");
    }

    /**
     * Informing the SQL to be performing, by default, just print it to
     * DEBUG level Logger when {@link #mIsDebug} is true.
     * <p/>
     * Implementations should overwrite this method to define other
     * activities with the SQLs whenever necessary.
     *
     * @param sql the SQL to be perform.
     */
    protected void debugSql(Object sql) {
        if (mIsDebug) Log.d(TAG, String.format("Performing: %s", sql));
    }

    /**
     * Mark whether current in debugging mode or not.
     *
     * @param isDebug true indicate in debugging.
     * @see #debugSql(Object)
     */
    public void setIsDebug(boolean isDebug) {
        mIsDebug = isDebug;
    }
}
