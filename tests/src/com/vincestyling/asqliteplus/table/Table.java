package com.vincestyling.asqliteplus.table;

import com.vincestyling.asqliteplus.DBOperator;
import com.vincestyling.asqliteplus.tests.MyDBOverseer;

import java.util.List;

public abstract class Table {
    private static final String DROP_STATMENT = "DROP TABLE IF EXISTS ";
    private static final String CREATE_STATMENT = "CREATE TABLE ";

    protected static CharSequence concatColumns(String... columnDeclarations) {
        StringBuilder columns = new StringBuilder();
        for (int i = 0; i < columnDeclarations.length; i++) {
            if (i > 0) columns.append(",");
            columns.append('\n').append(columnDeclarations[i]);
        }
        return columns;
    }

    public static void prepare(Class<? extends Table> clazz) throws Exception {
        Object tableName = clazz.getField("TABLE_NAME").get(null);

        MyDBOverseer.get().execSQL(DROP_STATMENT + tableName);
        MyDBOverseer.get().execSQL(String.format("%s%s(%s)", CREATE_STATMENT,
                tableName, clazz.getMethod("buildColumnDeclarations").invoke(null)));

        MyDBOverseer.get().executeBatch(
                (List) clazz.getField("INIT_DATAS").get(null),
                (DBOperator) clazz.getField("CREATE_DBOPER").get(null));
    }
}
