/**
 * @author Douglas Cavalheiro (doug.cav@ig.com.br)
 */
package br.com.gourmetlibrary.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;



import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.gourmetlibrary.model.FieldModel;
import br.com.gourmetlibrary.util.DroidUtils;

public abstract class DroidDao<T, ID, COD extends Serializable> {

    private TableDefinition<T> tableDefinition;
    private String insertStatement;
    private String tableName;
    private String[] arrayColumns;
    private Field[] fieldDefinition;
    private SQLiteDatabase database;
    private SQLiteStatement statement;
    private String idColumn;
    private final Class<T> model;
    private List<FieldModel> listFieldModels;
    private String codColumn;
    private Integer sizeCloumn;
    private Class OBJECT;
    private Field[] FIELD_DEFINITION;

    /**
     * Create a instance of Dao class, setting the model, definition of model
     * and the database
     */
    public DroidDao(Class<T> model, TableDefinition<T> tableDefinition, SQLiteDatabase database) {
        this.model = model;
        this.database = database;

        try {
            this.tableDefinition = tableDefinition;
        } catch (Exception e) {
            e.printStackTrace();
        }
        setArrayColumns(getTableDefinition().getArrayColumns());
        setTableName(getTableDefinition().getTableName());
        setFieldDefinition(
                getTableDefinition().getFieldDefinition());
        createInsertStatement(getTableDefinition().getTableName(), getTableDefinition().getArrayColumns());
        setIdColumn(getTableDefinition().getPK());
        setListFieldModels(getTableDefinition().getLIST_FIELD_MODEL());
        setCodColumn(getTableDefinition().getCOD());
        setSizeCloumn(getTableDefinition().getSIZE());

//		if(getInsertStatement().trim() != ""){
//			statement = this.database.compileStatement(getInsertStatement());
//		}
    }

    /**
     * Delete object
     */
    public boolean delete(COD id) {
        boolean result = false;
        try {
            database.delete(getTableName(), getCodColumn() + " = ?",
                    new String[]{String.valueOf(id)});
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get a object by id
     */
    public T get(COD id) {
        T object = null;
        Cursor cursor = database.query(getTableName(), getArrayColumns(),
                getCodColumn() + " = ?", new String[]{String.valueOf(id)}, null, null, "1");
        if (cursor.moveToFirst()) {
            try {
                object = buildDataFromCursor(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return object;
    }

    /**
     * List all items
     */
    public List<T> getAll() {
        List<T> objectList = new ArrayList<T>();
        Cursor cursor = database.query(getTableName(), getArrayColumns(),
                null, null, null, null, "1");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                try {
                    do {
                        T object = buildDataFromCursor(cursor);
                        if (object != null) {
                            objectList.add(object);
                        }
                    } while (cursor.moveToNext());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return objectList;
    }

    /**
     * List items by clause
     */
    public List<T> getAllbyClause(String clause, String[] clauseArgs, String groupBy, String having, String orderBy) {
        List<T> objectList = new ArrayList<T>();
        Cursor cursor = database.query(getTableName(), getArrayColumns(),
                clause, clauseArgs, groupBy, having, orderBy);
        if (cursor.moveToFirst()) {
            try {
                do {
                    T object = buildDataFromCursor(cursor);
                    if (object != null) {
                        objectList.add(object);
                    }
                } while (cursor.moveToNext());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return objectList;
    }

    public List<T> getAllbySql(String sql) {
        List<T> objectList = new ArrayList<T>();
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    T object = buildDataFromCursor(cursor);
                    if (object != null) {
                        objectList.add(object);
                    }
                } while (cursor.moveToNext());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return objectList;
    }

    /**
     * Get an Object by clause
     */
    public T getByClause(String clause, String[] clauseArgs) {
        T object = null;
        Cursor cursor = database.query(getTableName(), getArrayColumns(),
                clause, clauseArgs, null, null, "1");
        if (cursor.moveToFirst()) {
            try {
                object = buildDataFromCursor(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return object;

    }


    /**
     * Saves the Object
     */
    public void merge(T object, COD codigo) throws Exception {
        if (get(codigo) == null) {
            save(object);

        } else {
            update(object, codigo);
        }
    }
    public List<T> getListAll(COD id) {
        List<T> objectList = new ArrayList<T>();
        Cursor cursor = database.query(getTableName(), getArrayColumns(),
                getCodColumn() + " = ?", new String[]{String.valueOf(id)}, null, null, "1");
        if (cursor.moveToFirst()) {
            try {
                //object = buildDataFromCursor(cursor);
                do {
                    T object = buildDataFromCursor(cursor);
                    if (object != null) {
                        objectList.add(object);
                    }
                } while (cursor.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return objectList;
    }

    public Integer count() {
        return getAll().size();
    }

    public Integer max() {
        Integer retorno = -1;

        Cursor c = database.rawQuery("select max(" + getCodColumn() + ") from " + getTableName() + "", null);
        if (c.moveToFirst()) {
            retorno = c.getInt(0);
        }
        c.close();
        return retorno;
    }
    public Long maxLong() {
        Long retorno = null;

        Cursor c = database.rawQuery("select coalesce( max(" + getCodColumn() + "),0) from " + getTableName() + "", null);
        if (c.moveToFirst()) {
            retorno = c.getLong(0);
        }
        c.close();
        return retorno;
    }
    public Integer  next(){
        int retorno = max();
        return retorno++;

    }
    public Long  nextLong(){
        Long retorno = maxLong();
        return retorno++;

    }
    public long save(T object) throws Exception {
        ////Log("afaLog","TAMANHO OBJETO"+object.getClass().toString());
        ////Log("afaLog","OBJETO"+object.toString());
        long result = 0;

        if (getTableDefinition().getPK() == "") {
            statement.clearBindings();

            for (int e = 0; e < getArrayColumns().length; e++) {
                for (int i = 0; i < object.getClass().getDeclaredMethods().length; i++) {
                    Method method = object.getClass().getDeclaredMethods()[i];
                    if (method.getName().equalsIgnoreCase("get" + getArrayColumns()[e])) {
                        i = object.getClass().getDeclaredMethods().length;
                        Type type = method.getReturnType();
                        try {
                            if (type == int.class) {
                                Integer output = (Integer) method.invoke(object);
                                statement.bindLong(e + 1, output.longValue());
                            } else if (type == Long.class || type == Short.class || type == long.class) {
                                Long output = (Long) method.invoke(object);
                                statement.bindLong(e + 1, output);
                            } else if (type == Double.class || type == double.class || type == float.class) {
                                Double output = (Double) method.invoke(object);
                                statement.bindDouble(e + 1, output);
                            } else if (type == String.class) {
                                String output = (String) method.invoke(object);
                                statement.bindString(e + 1, output);
                            } else if (type == Date.class) {
                                Date output = (Date) method.invoke(object);
                                statement.bindString(e + 1, DroidUtils.convertDateToString(output));
                            } else if (type == byte[].class) {
                                byte[] output = (byte[]) method.invoke(object);
                                statement.bindBlob(e + 1, output);
                            } else {
                                statement.bindNull(e + 1);
                            }

                        } catch (Exception ex) {
                            throw new Exception(" Failed to invoke the method " + method.getName() + ", cause:" + ex.getMessage());
                        }
                    }
                }

            }

            result = statement.executeInsert();
        } else {
            final ContentValues values = new ContentValues();
            String value = "";
            for (int e = 0; e < getListFieldModels().size(); e++) {
                ////Log("afaLog", "SAIDA1");
                FieldModel fieldModel = getListFieldModels().get(e);
                for (int i = 0; i < object.getClass().getDeclaredMethods().length; i++) {

                    Method method = object.getClass().getDeclaredMethods()[i];
                    ////Log("afaLog", "SAIDA2 "+method.getName().equalsIgnoreCase("get" + fieldModel.getFieldName()));
                    if (method.getName().equalsIgnoreCase("get" + fieldModel.getFieldName())) {
                        ////Log("afaLog", "SAIDA3");
                        i = object.getClass().getDeclaredMethods().length;
                        Object outputMethod = method.invoke(object);
                        Type type = method.getReturnType();
                        //helps if the return type of method is Date (java.Utils)
                        if (outputMethod == null) {
                            value = " ";
                        } else {
                            if (type == Date.class) {

                                Date date = (Date) outputMethod;

                                value = DroidUtils.convertDateToString(date);
                                //  //Log("afaLog", "Data FORMATO PARA GRAVAR " + value);
                            }
                            /*if(type == ArrayList.class){

                            }*/else {
                                value = outputMethod.toString();
                            }
                        }

                        ////Log("afaLog", "SAIDA4" + value.toString());
                        values.put(fieldModel.getColumnName(), value);
                    }

                }
            }
            // //Log("afaLog", "VALORES" + values.toString());
            result = database.insert(getTableName(), null, values);

        }

        return result;
    }

    /**
     * Update the Object by ID
     */
    public void update(T object, COD id) throws Exception {
        //Log("afaLog", "PARAMETROS" + object.toString() + id);
        final ContentValues values = new ContentValues();
        String value;
        for (int e = 0; e < getListFieldModels().size(); e++) {
            FieldModel fieldModel = getListFieldModels().get(e);
            for (int i = 0; i < object.getClass().getDeclaredMethods().length; i++) {
                Method method = object.getClass().getDeclaredMethods()[i];
                if (method.getName().equalsIgnoreCase("get" + fieldModel.getFieldName())) {
                    i = object.getClass().getDeclaredMethods().length;
                    Object outputMethod = method.invoke(object);
                    Type type = method.getReturnType();
                    //helps if the return type of method is Date (java.Utils)
                    if (outputMethod == null) {
                        value = " ";
                    } else {
                        if (type == Date.class) {
                            Date date = (Date) outputMethod;
                            value = DroidUtils.convertDateToString(date);
                        } else {
                            value = outputMethod.toString();
                        }
                    }
                    // //Log("afaLog", "VALOR V" + value);
                    values.put(fieldModel.getColumnName(), value);
                }
            }
        }

        database.update(getTableName(), values, getCodColumn() + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Update the Object by Clause
     */
    public void update(T object, String clause, String[] clauseArgs) throws Exception {
        final ContentValues values = new ContentValues();
        String value;
        for (int e = 0; e < getListFieldModels().size(); e++) {
            FieldModel fieldModel = getListFieldModels().get(e);
            for (int i = 0; i < object.getClass().getDeclaredMethods().length; i++) {
                Method method = object.getClass().getDeclaredMethods()[i];
                if (method.getName().equalsIgnoreCase("get" + fieldModel.getFieldName())) {
                    i = object.getClass().getDeclaredMethods().length;
                    Object outputMethod = method.invoke(object);
                    Type type = method.getReturnType();
                    //helps if the return type of method is Date (java.Utils)

                    if (type == Date.class) {
                        Date date = (Date) outputMethod;
                        value = DroidUtils.convertDateToString(date);
                    } else {
                        value = outputMethod.toString();
                    }

                    values.put(fieldModel.getColumnName(), value);
                }
            }
        }

        database.update(getTableName(), values, clause,
                clauseArgs);
    }

    public void deletarTudo(String tabela) {
        database.execSQL("delete from " + tabela);

    }

    public void executaSql(String sql) {
        database.execSQL(sql);

    }

    public String executaQueryString(String sql) {
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }

    public Double executaQueryDouble(String sql) {
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getDouble(0);
        }
        return 0.0;
    }
    public double executaQuerydouble(String sql) {
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getDouble(0);
        }
        return 0.0;
    }
    public int executaQueryInt(String sql) {
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        return 0;
    }

    public int executaQueryBollean(String sql) {
        int retorno = -1;
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            if (c.getString(0).equals(true)) {
                retorno = 1;
            } else {
                retorno = 0;
            }
        }
        return retorno;
    }
    public boolean executaQuerybollean(String sql) {
        boolean retorno = false;
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            if (c.getString(0).equals(true)) {
                retorno = true;
            }
        }
        return retorno;
    }
    public Cursor getCursor(String sql) {
        Cursor c = database.rawQuery(sql, null);
        return c;
    }

    public String getInsertStatement() {
        return insertStatement;
    }

    public void setInsertStatement(String insertStatement) {
        this.insertStatement = insertStatement;
    }

    public TableDefinition<T> getTableDefinition() {
        return tableDefinition;
    }

    public void setTableDefinition(TableDefinition<T> tableDefinition) {
        this.tableDefinition = tableDefinition;
    }

    /**
     * Build a insert statement to the model
     */
    private void createInsertStatement(String tableName, String[] columns) {
        StringBuffer values = new StringBuffer();
        StringBuffer tableColumns = new StringBuffer();

        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                if (i < columns.length) {
                    values.append(",");
                    tableColumns.append(",");
                }
            }
            values.append("?");
            tableColumns.append(columns[i]);
        }
        setInsertStatement("insert into " + tableName + "(" + tableColumns + ") " + "values ( " + values + ")");
    }

    /**
     * Transforms the row in a Object
     */
    public T buildDataFromCursor(Cursor cursor) throws Exception {
        T object = null;

        Field[] fields = getFieldDefinition();
        if (cursor != null) {
            object = this.model.newInstance();

            Method[] methods = object.getClass().getMethods();

            for (int i = 0; i < cursor.getColumnCount(); i++) {

                try {

                    for (int e = 0; e < methods.length; e++) {

                        if (methods[e].getName().trim().equalsIgnoreCase("set" + fields[i].getName())) {
                            Method method = methods[e];
                            e = methods.length;
                            Type type = method.getParameterTypes()[0];

                            if (type == int.class
                                    || type == Integer.class) {
                                method.invoke(object, Long.valueOf(cursor.getLong(i)).intValue());
                            } else if (type == Long.class
                                    || type == long.class) {
                                method.invoke(object, cursor.getLong(i));
                            } else if (type == Double.class
                                    || type == double.class) {
                                method.invoke(object, cursor.getDouble(i));
                            } else if (type == float.class) {
                                method.invoke(object, cursor.getFloat(i));
                            } else if (type == String.class) {
                                method.invoke(object, cursor.getString(i));
                            } else if (type == Date.class) {
                                method.invoke(object, DroidUtils.convertStringToDateBr(cursor.getString(i)));
                            } else if (type == java.sql.Time.class) {
                                method.invoke(object, java.sql.Time.valueOf(cursor.getString(i)));
                            } else if (type == java.sql.Timestamp.class) {
                                method.invoke(object, java.sql.Timestamp.valueOf(cursor.getString(i)));
                            } else if (type == Short.class) {
                                method.invoke(object, cursor.getShort(i));
                            } else if (type == Boolean.class || type == boolean.class) {
                                ////Log("afaLog", "SAIDA BOOLEAN" + cursor.getString(i));
                                //teste
                                if (cursor.getString(i)
                                        .equals("true")) {
                                    method.invoke(object, true);
                                } else {
                                    method.invoke(object, false);
                                }

                            } else if (type == BigDecimal.class) {
                                BigDecimal bg = new BigDecimal(cursor.getDouble(i));

                                method.invoke(object,
                                        (bg));
                            } else {
                                method.invoke(object, cursor.getBlob(i));
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new Exception(" Failed to cast a object, maybe a method not declared, cause:" + e.getMessage());
                }
            }
        }
        if (object.getClass().getDeclaredFields().length == 0) {
            throw new Exception("Cannot be cast a no field object!");
        }
        return (T) object;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getArrayColumns() {
        return arrayColumns;
    }

    public void setArrayColumns(String[] arrayColumns) {
        this.arrayColumns = arrayColumns;
    }

    public Field[] getFieldDefinition() {
        return fieldDefinition;
    }

    public void setFieldDefinition(Field[] fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public List<FieldModel> getListFieldModels() {
        return listFieldModels;
    }

    public void setListFieldModels(List<FieldModel> listFieldModels) {
        this.listFieldModels = listFieldModels;
    }

    public String getCodColumn() {
        return codColumn;
    }

    public void setCodColumn(String codColumn) {
        this.codColumn = codColumn;
    }

    public Integer getSizeCloumn() {
        return sizeCloumn;
    }

    public void setSizeCloumn(Integer sizeCloumn) {
        this.sizeCloumn = sizeCloumn;
    }
}
