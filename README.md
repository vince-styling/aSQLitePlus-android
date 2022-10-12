aSQLite+ Android [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-aSQLite%2B-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1636)
================

As the name **aSQLite+(Android SQLite plus)** meant, this library aim to attaching some practical functionality to Android SQLite. It offered numerous handy classes and methods to help us concatenating and performing those CURD SQLs.

You probably already know that Android also provided many methods for us to helping producing SQL statement. Well, let's discuss with an example, saying we are going to producing such SQL statement :

```sql
SELECT product_id, product_name FROM Products WHERE price >= 10 ORDER BY price DESC LIMIT 10
```

With Android libcore on our hands currently, we probably coding like following to achieve it :

```java
String sql = android.database.sqlite.SQLiteQueryBuilder.buildQueryString(false,
        "Products", new String[]{"product_id", "product_name"}, "price >= 10",
        null, null, "price DESC", "10");
```

See? it's a bit of complication, quite a number of arguments needs us paying attention to. Also difficult to maintain for subsequent development. And, what about aSQLite+ ?

```java
Statement statement = QueryStatement.produce("product_id", "product_name")
        .from("Products").where("price").egt(10).orderBy("price").desc().limit(10);
String sql = statement.toString();
```

aSQLite+ bring you more accurate method naming and less parameters method signature, make it clear and more readable, also understandable and maintainable. Every method representing one [SQL keyword](https://www.scaler.com/topics/sql/keywords-in-sql/) or clause, this way of separation make you not easy to make mistakes during writing SQL statement. You would only apply them in the producing SQL statement whenever necessary. Which **greatly simplifying** the SQL statement constructing against using Android libcore even using `+` to tearing those statements to pieces.



## Statements

In aSQLite+'s context, `Statement`s acting a statement producer, be responsible for concatenating the desired SQL statement via those known syntax methods successively. They're pretty elegant, internal holding a StringBuilder to carrying the SQL statement, overriden the toString() method to output the final statement. Thus a Statement look as a String, only its toString() matter for caller.

By default, aSQLite+ only focus on simplifying the CURD commands in SQL language, offered an individual Statement object for every commands :

| name | description |
| ---- | ----------- |
| CreateStatement | A statement producer that use to producing **INSERT** command of SQL language. |
| UpdateStatement | A statement producer that use to producing **UPDATE** command of SQL language. |
| DeleteStatement | A statement producer that use to producing **DELETE** command of SQL language. |
| QueryStatement  | A statement producer that use to producing **SELECT** command of SQL language. |


### Statement Boilerplates

It's more straightforward to show you many boilerplates for such library, now I'm going to walk you that, you'll learn further about how aSQLite+ works with many typical cases. Note we omitted the entity classes definition here.

Consider you want to producing following SQL statement.

```sql
SELECT product_id, product_name, price, category_id,
    supplier_id FROM Products WHERE product_name LIKE '%Ch%'
```

then you can doing this :

```java
QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
    Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
    .from(Products.TABLE_NAME).where(Products.PRODUCT_NAME).likeContains("Ch");
```

Consider you want to producing following SQL statement.

```sql
SELECT product_id, product_name, price, category_id,
    supplier_id FROM Products WHERE category_id BETWEEN 1 AND 3
```

then you can doing this :

```java
QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME,
    Products.PRICE, Products.CATEGORY_ID, Products.SUPPLIER_ID)
    .from(Products.TABLE_NAME).where(Products.CATEGORY_ID).between(1, 3);
```

Consider you want to producing following SQL statement.

```sql
SELECT DISTINCT category_id FROM Products
```

then you can doing this :

```java
QueryStatement.distinct(Products.CATEGORY_ID).from(Products.TABLE_NAME);
```

Consider you want to producing following SQL statement.

```sql
SELECT product_name, category_name FROM Products, Categories USING (category_id)
```

then you can doing this :

```java
QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
    .from(Products.TABLE_NAME, Categories.TABLE_NAME).using(Products.CATEGORY_ID);
```

Consider you want to producing following SQL statement.

```sql
SELECT * FROM Products WHERE product_name = 'Chais'' or ''1'' = ''1'
```

then you can doing this :

```java
QueryStatement.produce().from(Products.TABLE_NAME)
    .where(Products.PRODUCT_NAME).eq("Chais' or '1' = '1");	// prevent SQL Injection.
```

Consider you want to producing following SQL statement.

```sql
SELECT product_id, product_name, unit FROM Products
    WHERE EXISTS (SELECT * FROM Products WHERE price <= 20)
```

then you can doing this :

```java
QueryStatement.produce(Products.PRODUCT_ID, Products.PRODUCT_NAME, Products.UNIT)
    .from(Products.TABLE_NAME).where(
    	new Exists(QueryStatement.produce().from(Products.TABLE_NAME)
    	.where(Products.PRICE).elt(20))
    );
```

Consider you want to producing following SQL statement.

```sql
SELECT * FROM Products WHERE unit IN ('500 ml', '12 boxes', '48 pies')
```

then you can doing this :

```java
QueryStatement.produce().from(Products.TABLE_NAME)
    .where(Products.UNIT).in("500 ml", "12 boxes", "48 pies");
```

Consider you want to producing following SQL statement.

```sql
SELECT category_id, sum(price) AS total_price FROM Products GROUP BY category_id
```

then you can doing this :

```java
QueryStatement.produce(Products.CATEGORY_ID,
    new Alias(Function.sum(Products.PRICE), "total_price"))
    .from(Products.TABLE_NAME).groupBy(Products.CATEGORY_ID);
```

Consider you want to producing following SQL statement.

```sql
SELECT Products.product_name, Products.category_id, Categories.category_name
    FROM Products, Categories WHERE Products.category_id = Categories.category_id
    AND Products.price > 10 ORDER BY Products.price
```

then you can doing this :

```java
QueryStatement.produce(
    new Scoping(Products.TABLE_NAME, Products.PRODUCT_NAME),
    new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID),
    new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_NAME)
).from(Products.TABLE_NAME, Categories.TABLE_NAME)
    .where(new Scoping(Products.TABLE_NAME, Products.CATEGORY_ID))
    .eq(new Scoping(Categories.TABLE_NAME, Categories.CATEGORY_ID))
    .and(new Scoping(Products.TABLE_NAME, Products.PRICE)).gt(10)
    .orderBy(new Scoping(Products.TABLE_NAME, Products.PRICE));
```

Consider you want to producing following SQL statement.

```sql
SELECT product_name, category_name FROM Products
    NATURAL LEFT JOIN Categories WHERE category_name IS NOT NULL AND price <> 10
```

then you can doing this :

```java
QueryStatement.produce(Products.PRODUCT_NAME, Categories.CATEGORY_NAME)
    .from(Products.TABLE_NAME).leftNaturalJoin(Categories.TABLE_NAME)
    .where(Categories.CATEGORY_NAME).isNotNull()
    .and(Products.PRICE).neq(10);
```

Consider you want to producing following SQL statement.

```sql
SELECT datetime('now', 'localtime')
```

then you can doing this :

```java
QueryStatement.produce(new UnescapeString("datetime('now', 'localtime')"));
```

Consider you want to producing following SQL statement.

```sql
INSERT INTO Suppliers(supplier_name, contact_name, address, city, postal_code,
    country, phone) VALUES('Exotic Liquid', 'Charlotte Cooper',
    '49 Gilbert St.', 'Londona', 'EC1 4SD', 'UK', '(171) 555-2222')
```

then you can doing this :

```java
Supplier supplier = Suppliers.INIT_DATAS.get(0);
CreateStatement.produce(Suppliers.TABLE_NAME)
    .put(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
    .put(Suppliers.CONTACT_NAME, supplier.getContactName())
    .put(Suppliers.ADDRESS, supplier.getAddress())
    .put(Suppliers.CITY, supplier.getCity())
    .put(Suppliers.POSTAL_CODE, supplier.getPostalCode())
    .put(Suppliers.COUNTRY, supplier.getCountry())
    .put(Suppliers.PHONE, supplier.getPhone());
```

Consider you want to producing following SQL statement.

```sql
INSERT INTO Suppliers(supplier_name, contact_name, address, city, postal_code,
    country, phone) SELECT supplier_name, contact_name, address, city, postal_code,
    country, phone FROM Suppliers WHERE country = 'China' LIMIT 2 OFFSET 1
```

then you can doing this :

```java
Statement queryStmt = QueryStatement.produce(Suppliers.SUPPLIER_NAME, Suppliers.CONTACT_NAME,
    Suppliers.ADDRESS, Suppliers.CITY, Suppliers.POSTAL_CODE, Suppliers.COUNTRY,
    Suppliers.PHONE).from(Suppliers.TABLE_NAME).where(Suppliers.COUNTRY).eq("China")
    .limit(2, 1);

CreateStatement.produce(Suppliers.TABLE_NAME).columns(Suppliers.SUPPLIER_NAME,
    Suppliers.CONTACT_NAME, Suppliers.ADDRESS, Suppliers.CITY, Suppliers.POSTAL_CODE,
    Suppliers.COUNTRY, Suppliers.PHONE).entry(queryStmt);
```

Consider you want to producing following SQL statement.

```sql
DELETE FROM Customers WHERE cust_code = 'C00013'
```

then you can doing this :

```java
Customer customer = Customers.INIT_DATAS.get(0);

DeleteStatement.produce(Customers.TABLE_NAME)
    .where(Customers.CUST_CODE).eq(customer.getCustCode());
```

Consider you want to producing following SQL statement.

```sql
UPDATE Suppliers SET supplier_name = 'New Name', contact_name = 'Small Wood',
    address = 'Middle Street of States', city = 'ZhuHai', postal_code = '510665',
    country = 'China', phone = '+86 13800 138000' WHERE supplier_id = 1
```

then you can doing this :

```java
Supplier supplier = Suppliers.INIT_DATAS.get(0);
supplier.setSupplierName("New Name");
supplier.setContactName("Small Wood");
supplier.setAddress("Middle Street of States");
supplier.setCity("ZhuHai");
supplier.setPostalCode("510665");
supplier.setCountry("China");
supplier.setPhone("+86 13800 138000");

UpdateStatement.produce(Suppliers.TABLE_NAME)
    .set(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
    .set(Suppliers.CONTACT_NAME, supplier.getContactName())
    .set(Suppliers.ADDRESS, supplier.getAddress())
    .set(Suppliers.CITY, supplier.getCity())
    .set(Suppliers.POSTAL_CODE, supplier.getPostalCode())
    .set(Suppliers.COUNTRY, supplier.getCountry())
    .set(Suppliers.PHONE, supplier.getPhone())
    .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
```

Consider you want to producing following SQL statement.

```sql
UPDATE OR REPLACE Suppliers SET supplier_id = 5, supplier_name = 'Tokyo Traders',
    contact_name = 'Yoshi Nagase', address = '9-8 Sekimai Musashino-shi', city = 'Tokyo',
    postal_code = '100', country = 'Japan', phone = '(03) 3555-5011' WHERE supplier_id = 4
```

then you can doing this :

```java
Supplier supplier = Suppliers.INIT_DATAS.get(3);
Supplier targetSupplier = Suppliers.INIT_DATAS.get(4);

UpdateStatement.orReplace(Suppliers.TABLE_NAME)
    // update supplier_id to another supplier_id which already populated by another row.
    .set(Suppliers.SUPPLIER_ID, targetSupplier.getSupplierId())
    .set(Suppliers.SUPPLIER_NAME, supplier.getSupplierName())
    .set(Suppliers.CONTACT_NAME, supplier.getContactName())
    .set(Suppliers.ADDRESS, supplier.getAddress())
    .set(Suppliers.CITY, supplier.getCity())
    .set(Suppliers.POSTAL_CODE, supplier.getPostalCode())
    .set(Suppliers.COUNTRY, supplier.getCountry())
    .set(Suppliers.PHONE, supplier.getPhone())
    .where(Suppliers.SUPPLIER_ID).eq(supplier.getSupplierId());
```

Once we done with SQL statement producing, we're able to send it to SQLiteDatabase or **DBOverseer**(describe in subsequent) to execute and take the result due to this call.

These shown cases were lay in the `tests` folder which contained all test cases, they're covering all functionality of aSQLite+. Note below description to checking how to execute them.

**Note:** 'Cause this series of statement producer would never understand about the SQL syntax, therefore this assumes you have a basic knowledge of SQL programming, you need to decide which clause can be join together without syntax errors while performing and which cannot.



## DBOverseer

As mentioned earlier, we implemented a class named `DBOverseer` which exposes numerous handy methods to performing SQL statements. It's the best choice to work with those **Statement**s.

DBOverseer claims a `SQLiteOpenHelper` to instancing, and should be stand as singleton throughout the Application's entire life cycle. Following list the main methods with signatures.

```java
public class DBOverseer {
    /** Fetches the newly inserted row ID of the specified table. */
    public int getLastInsertRowId(CharSequence table)...;

    /** Performs the query and take the INTEGER value in the first column of the first row. */
    public int getInt(Object sql)...;

    /** Performs the query and retrieve all columns as INTEGER value in the first row. */
    public int[] getInts(Object sql)...;

    /** Performs the query and take the STRING value in the first column of the first row. */
    public String getString(Object sql)...;

    /** Performs the query and retrieve all columns as STRING value in the first row. */
    public String[] getStrings(Object sql)...;

    /** Performs the query and checking if has result. */
    public boolean checkIfExists(Object sql)...;

    /** Performs the SQL statement and return the ID of the row inserted due to this call. */
    public long executeInsert(Object sql)...;

    /** Performs a single SQL statement that is NOT a SELECT
    or any other SQL statement that returns data. */
    public int executeSql(Object sql)...;

    /** Performs the query and fetching the resultset to an ArrayList. */
    public <T> List<T> getList(Object sql, RowMapper<T> mapper)...;

    /** Performs the query with two pagination arguments, computing and returning
    the desired PaginationList which contained four pagination informations. */
    public <T> PaginationList<T> getPaginationList(Statement sql,
    	int pageNo, int pageItemCount, final Class<T> clazz)...;

    /** Looping the given List, put every item of that into the DBOperator
    to producing an individual SQL statement to performing. */
    public <T> boolean executeBatch(List<T> list, DBOperator<T> operator)...;

    /** Performs the query and return the first row as
    an entity which instancing of the specified Class. */
    public <T> T getEntity(Object sql, final Class<T> clazz)...;
}
```

All methods of DBOverseer accepts the [SQL](https://www.interviewbit.com/courses/databases/sql-queries/sql-introduction/) as an Object, only its `toString()` matter. In other words, developer can constructing any Object such as String, StringBuilder, Statement to carrying the SQL statement, just make sure that the **toString()** method would return the final SQL statement you want to be executing.

You can checking the test cases of DBOverseer for more details.


### wielding Java Reflection in DBOverseer

The `getEntity(..., Class)`, in particular, DBOverseer will instancing the specified Class via its empty constructor and mapping each presented column's value associated with its name to the entity instance. So the names of columns in SQL statement played a big role here. For example: say the current row contained "nick_name, age, login_time" three columns, then the Class instance's **setNickName(...)**, **setAge(...)**, **setLoginTime(...)** would be invoke accordingly to mapping their values in.

Default naming principle accepted used underline(_) to separated each word in SQLite column naming; For Java, we followed the traditional naming which remove the underline character and Upper Case the first letter of each word. But that isn't fixed, in order to flexibility, the column name's translation pattern can be customize by overwrite the **DBOverseer.translateColumnName(String)** method in implementation.

**Notice:** Because this approach was powered by Java Reflection, in order to prevent name mapping problems, it is important to tell `ProGuard` that which classes and members you wish not be obfuscated when you build your application in release mode.



## Test Cases

I value those projects which afford many test cases. I believe the reason of why a project is qualified would be the tests first, the wild implementation later. Certainly, I try to make this project in accordance with that specification. Following procedure would show you how to execute the whole test cases of aSQLite+.

```bash
# enter the project's root directory
~ $ cd PROJECT_ROOT_DIR

# Optional, to ensure the local.properties generated.
~ $ android update project -p . -s

# enter the test module.
~ $ cd tests

# execute the integrate test of aSQLite+.
~ $ ant integrateTest
```

Check the logcat for informations when test errors occurred :

```bash
~ $ adb logcat -v time DBOverseer:E *:S
```

If wish to checking the performed SQLs :

```bash
~ $ adb logcat DBOverseer:D *:S
```



JavaDoc
=======

The JavaDoc of aSQLite+ was generated and hosted on [https://vince-styling.github.io/aSQLitePlus-android](https://vince-styling.github.io/aSQLitePlus-android).



Integration
===========

Download the [latest JAR](http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.vincestyling.asqliteplus&a=asqliteplus&v=LATEST)
or grab via **Maven** :

```xml
<dependency>
  <groupId>com.vincestyling.asqliteplus</groupId>
  <artifactId>asqliteplus</artifactId>
  <version>0.2</version>
</dependency>
```

For **Gradle** projects use :

```groovy
compile 'com.vincestyling.asqliteplus:asqliteplus:0.2'
```

If you have some changes for the source code, you can use `ant` to packaging a SNAPSHOT jar for test.

```bash
# enter the project's root directory
~ $ cd PROJECT_ROOT_DIR

# packaging a new jar, it would place to "PROJECT_ROOT_DIR/bin/asqliteplus-0.2.jar".
~ $ ant jar
```

Also I will gladly accept pull requests for fixes and feature enhancements.



Apps
====

There is [ixiaoshuo-android](https://github.com/vince-styling/ixiaoshuo-android) already using aSQLite+. If you have an App which be the same, let me know.



Acknowledgments
===============

- [SQL Tryit Editor v1.2](http://www.w3schools.com/sql/trysql.asp?filename=trysql_select_all) : My test cases data source.
- [SQLite official Documentation](http://sqlite.org/lang.html) : Straightforward syntax diagrams helping me in deep to understand some clauses I never know.
- [TutorialSpoint SQL Documentation](http://www.tutorialspoint.com/sqlite/sqlite_syntax.htm) : Show me all the SQL syntax and usage which supported by SQLite.
- [Testing Fundamentals | Android Developer](http://developer.android.com/tools/testing/testing_android.html) : The brilliant documentation of how to testing the Android application.



License
=======

```
Copyright 2015 Vince Styling

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
