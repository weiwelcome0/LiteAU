package com.wechallenge.liteau;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wechallenge.liteau.bean.CaseInsensitiveMap;
import com.wechallenge.liteau.bean.Fields;
import com.wechallenge.liteau.impl.BeanContentValueConverter;
import com.wechallenge.liteau.impl.CursorBeanProcessor;
import com.wechallenge.liteau.impl.CursorMapProcessor;
import com.wechallenge.liteau.impl.CursorStringProcessor;
import com.wechallenge.liteau.itf.IContentValuesConverter;
import com.wechallenge.liteau.itf.ICursorProcessor;
import com.wechallenge.liteau.tool.Logger;

/**
 * sqlite db utils; Attention: must call the initialize() before sql operation
 * @author weisir
 * 2015-4-21
 */
public class DBHelper {
	private static String sqliteDbFile = null;
	private static SQLiteDatabase database = null;
	private static CaseInsensitiveMap<Fields> classFieldMap = new CaseInsensitiveMap<Fields>();	//cache the fields of java bean
	private static CaseInsensitiveMap<Boolean> tableCheckedMap = new CaseInsensitiveMap<Boolean>();	//check the table columns is the same as the bean
	
	public static void initialize(String sqliteDbFilePath){
		if(!new File(sqliteDbFilePath).exists()){
			Logger.e("[initialize]--sqliteDbFile not exists; please confirm the path:"+sqliteDbFilePath);
			return;
		}
		sqliteDbFile = sqliteDbFilePath;
	}
	
	public static void close(){
		if(null != database){
			database.close();
		}
	}
	
	private static synchronized SQLiteDatabase getWritableDatabase() {
		if (database == null || !database.isOpen()) {
			database = SQLiteDatabase.openDatabase(sqliteDbFile, null, SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}
	
	public static <T> List<T> query(Class<T> cls) {
		String sql = "select * from "+cls.getSimpleName();
		return query(sql, null, cls);
	}
	
	public static <T> List<T> query(String sql, Class<T> cls) {
		return query(sql, null, cls);
	}
	
	public static <T> List<T> query(String sql, String[] sqlWhereArgs, Class<T> cls) {
		return query(sql, sqlWhereArgs, new CursorBeanProcessor<T>(cls, getFields(cls)));
	}
	
	public static int insert(Object obj) {
		if(null == obj){
			return 0;
		}
		return insert(obj.getClass().getSimpleName(),obj);
	}
	
	public static int insert(String tableName, Object obj) {
		if(null == obj){
			return 0;
		}
		ArrayList<Object> list = new ArrayList<Object>(1);
		list.add(obj);
		return insert(tableName, list);
	}
	
	public static <T> int insert(List<T> list) {
		if(null == list || list.size() <= 0){
			return 0;
		}
		Class<?> cls = list.get(0).getClass();
		return insert(cls.getSimpleName(),list);
	}
	
	public static <T> int insert(String tableName, List<T> list) {
		if(null == list || list.size() <= 0){
			return 0;
		}
		Class<?> cls = list.get(0).getClass();
		checkTable(tableName,getFields(cls));
		IContentValuesConverter<T> converter = new BeanContentValueConverter<T>(getFields(cls));
		
		return insert(tableName, list, converter);
	}
	
	public static <T> int update(List<T> list, String[] keyFields, String[] valueFields) {
		if(null == list || list.size() <= 0
				|| null == keyFields || keyFields.length <= 0
				|| null == valueFields || valueFields.length <= 0){
			return 0;
		}
		String tableName = list.get(0).getClass().getSimpleName();
		return update(tableName,keyFields,list,keyFields,valueFields);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> int update(String tableName, String[] keyColumns, List<T> list, String[] keyFields, String[] valueFields) {
		if(null == keyColumns || keyColumns.length <= 0
				|| null == list || list.size() <= 0
				|| null == keyFields || keyFields.length <= 0){
			return 0;
		}
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		String whereClause = "1=1";
		for(String col : keyColumns){
			whereClause = whereClause+ " AND "+col+ "=?";
		}
		Fields fields = getFields(list.get(0).getClass());
		
		SQLiteDatabase database = getWritableDatabase();
		
		try {
			Fields updateFields = new Fields(valueFields.length);
			for(String name:valueFields){
				updateFields.addField(name, fields.getField(name));
			}
			
			IContentValuesConverter<T> converter = new BeanContentValueConverter<T>(updateFields);
			
			database.beginTransaction();
			
			for(Object obj: list){
				int i = 0;
				String[] whereArgs = new String[keyFields.length];
				for(String name:keyFields){
					whereArgs[i ++] = ""+fields.getField(name).get(obj);
				}
				rowCount += database.update(tableName, converter.convert((T) obj), whereClause, whereArgs);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--failed",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[update]--params：whereClause:" + whereClause + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	public static <T> int delete(List<T> list, String[] keyFields) {
		if(null == list || list.size() <= 0
				|| null == keyFields || keyFields.length <= 0){
			return 0;
		}
		String tableName = list.get(0).getClass().getSimpleName();
		return delete(tableName,keyFields,list,keyFields);
	}
	
	public static <T> int delete(String tableName, String[] keyColumns, List<T> list, String[] keyFields) {
		if(null == keyColumns || keyColumns.length <= 0
				|| null == list || list.size() <= 0
				|| null == keyFields || keyFields.length <= 0){
			return 0;
		}
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		StringBuilder buf = new StringBuilder();
		buf.append("DELETE FROM ").append(tableName).append(" WHERE 1=1");
		for(String col : keyColumns){
			buf.append(" AND ").append(col).append("=?");
		}
		String sql = buf.toString();
		int fieldSize = keyFields.length;
		Fields fields = getFields(list.get(0).getClass());
		List<String[]> listValues = new ArrayList<String[]>(list.size());
		
		SQLiteDatabase database = getWritableDatabase();
		
		try {
			for(Object obj: list){
				int i = 0;
				String[] values = new String[fieldSize];
				for(String name:keyFields){
					values[i ++] = ""+fields.getField(name).get(obj);
				}
				listValues.add(values);
			}
			
			database.beginTransaction();
			for(String[] sqlWhereArgs:listValues){
				database.execSQL(sql, sqlWhereArgs);
				rowCount ++;
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[delete]--failed",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[delete]--params：sql:" + sql + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	public static List<Map<String, String>> query(String sql) {
		return query(sql, null, CursorMapProcessor.CURSOR_MAP_PROCESSOR);
	}	
	
	public static <T> List<T> query(String sql, String[] sqlWhereArgs, ICursorProcessor<T> rp) {
		ArrayList<T> list = new ArrayList<T>(0);

		long begin = System.currentTimeMillis();
		SQLiteDatabase database = null;
		Cursor c = null;
		try {

			database = getWritableDatabase();

			c = database.rawQuery(sql, sqlWhereArgs);

			list.ensureCapacity(c.getCount());

			while (c.moveToNext()) {
				T t = rp.convert(c);
				if(null != t) {
					list.add(t);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("[query]--failed",e);

		} finally {
			database = null;
			if(null != c) {
				c.close();
			}
		}

		long end = System.currentTimeMillis();

		Logger.d("[query]--params：sql:" + sql + ",sqlWhereArgs:" + Arrays.toString(sqlWhereArgs) + ",count:"+list.size()+ ";timecost：" + (end - begin) + "ms;");

		return list;
	}

	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, CursorMapProcessor.CURSOR_MAP_PROCESSOR, null);
	}
	
	public static <T> List<T> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, ICursorProcessor<T> rp) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, rp, null);
	}
	
	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, String strOrderBy) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, CursorMapProcessor.CURSOR_MAP_PROCESSOR, strOrderBy, null);
	}

	public static <T> List<T> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, ICursorProcessor<T> rp, String strOrderBy) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, rp, strOrderBy, null);
	}
	
	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, String strOrderBy, String limit) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, CursorMapProcessor.CURSOR_MAP_PROCESSOR, strOrderBy, limit);
	}
	
	public static <T> List<T> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, ICursorProcessor<T> rp, String strOrderBy, String limit) {
		ArrayList<T> list = new ArrayList<T>(0);

		long begin = System.currentTimeMillis();
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = getWritableDatabase();
			cursor = database.query(tableName, columns, sqlWhere, sqlWhereArgs, null, null, strOrderBy, limit);

			list.ensureCapacity(cursor.getCount());

			while (cursor.moveToNext()) {
				T t = rp.convert(cursor);
				if(null != t) {
					list.add(t);
				}
			}
		} catch (Exception e) {
			Logger.e( "[query]--failed", e);
		} finally {
			database = null;
			if(null != cursor) {
				cursor.close();
			}
		}

		long end = System.currentTimeMillis();

		Logger.d("[query]--params：table:" + tableName + ",cols:" + Arrays.toString(columns) + ",sqlWhere:" + sqlWhere + ",count:"+list.size()+ ";timecost：" + (end - begin) + "ms;");

		return list;
	}
	
    public static int insert(String table, ContentValues values) {
		return insert(table, null, values);
	}

	public static int insert(String table, String nullColumnHack, ContentValues values) {
		SQLiteDatabase database = getWritableDatabase();
        long rowNum = -1;
		
		long begin = System.currentTimeMillis();
		
		try {
			database.beginTransaction();
			if(values != null) {
				rowNum = database.insert(table, nullColumnHack, values);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[insert]--failed",e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		
		Logger.d("[insert]--params：table:" + table + ",nullColumnHack:" + nullColumnHack +",values:" + values + ";timecost：" + (end - begin) + "ms;");
		
		return rowNum > 0 ? 1 : 0;
	}

	public static <T> int insert(String tableName, List<T> list, IContentValuesConverter<T> converter) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = -1;
		
		long begin = System.currentTimeMillis();
		
		try {
			database.beginTransaction();
			if(null != list){
				for (T t : list) {
					ContentValues v = converter.convert(t);
					if(v != null) {
						database.insert(tableName, null, v);
						v.clear();
						v = null;
					}
				}				
			}
			database.setTransactionSuccessful();
			rowCount = list.size();
		} catch (Exception e) {
			Logger.e("[insert]--failed",e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		
		Logger.d("[insert]--params：table:" + tableName + ",list:" + (list==null?null:list.size()) +",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;

	}

	public static int delete(String tableName, String sqlWhere) {
		return delete(tableName, sqlWhere, null);
	}

	public static int delete(String tableName, String sqlWhere, String[] sqlWhereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = -1;
		long begin = System.currentTimeMillis();
		
		try {
			database.beginTransaction();
			rowCount = database.delete(tableName, sqlWhere, sqlWhereArgs);
			database.setTransactionSuccessful();
			return rowCount;
		} catch (Exception e) {
			Logger.e("[delete]--failed",e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[delete]--params：table:" + tableName + ",sqlWhere:" + sqlWhere + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}

	public static int update(String sql, String[] sqlWhereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		try {
			database.beginTransaction();
			database.execSQL(sql, sqlWhereArgs);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--failed",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[update]--params：sql:" + sql + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	public static int updateBatch(String sql, List<String[]> listArgs) {
		if(null == listArgs || listArgs.size() <=0){
			return 0;
		}
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		SQLiteDatabase database = getWritableDatabase();
		try {
			database.beginTransaction();	
			for(String[] args:listArgs){
				database.execSQL(sql, args);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--failed",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		long end = System.currentTimeMillis();
		Logger.d("[update]--params：sql:" + sql + "listArgs.size():"+listArgs.size()+",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	public static int update(String table, ContentValues values, String sqlWhereClause) {
		return update(table, values, sqlWhereClause, null);
	}
	
	public static int update(String tableName, ContentValues values, String sqlWhereClause, String[] sqlWhereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		try {
			database.beginTransaction();
			rowCount = database.update(tableName, values, sqlWhereClause, sqlWhereArgs);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--failed");
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[update]--params：tableName:" + tableName +",sqlWhere:"+sqlWhereClause + "sqlWhereArgs:"+Arrays.toString(sqlWhereArgs) + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	private static int checkTable(String tableName,Fields fields){
		Boolean b = tableCheckedMap.get(tableName);
		if(b != null){
			return 0;
		}

		synchronized (tableCheckedMap) {
			String sql = "PRAGMA table_info(["+tableName+"])";
			List<String> columnList = query(sql,new String[0],new CursorStringProcessor(new int[]{1},null));
			if(columnList.size() <= 0){
				createTable(tableName, fields);
			}else{
				alterTable(tableName,fields,columnList);
			}
			tableCheckedMap.put(tableName, Boolean.TRUE);
		}
		return 1;
	}
	
	private static int createTable(String tableName, Fields fields) {
		StringBuilder buf = new StringBuilder();
		buf.append("CREATE TABLE ").append(tableName).append("( ");
		for(Field f:fields.getAllFields()){
			buf.append("\t").append(f.getName()).append(" ").append("Varchar,");
		}
		buf.setLength(buf.length()-1);
		buf.append(");");
		return update(buf.toString(), new String[0]);
	}
	
	private static int alterTable(String tableName, Fields fields,
			List<String> columnList) {
		StringBuilder buf = new StringBuilder();
		boolean notExists = true; 
		for(Field f:fields.getAllFields()){
			notExists = true;
			buf.setLength(0);
			for(String col:columnList){
				if(f.getName().equalsIgnoreCase(col)){
					notExists = false;
					break;
				}
			}
			
			if(notExists){
				buf.append("ALTER TABLE ").append(tableName).append(" ADD ").append(f.getName()).append(" Varchar;\r\n");
				update(buf.toString(), new String[0]);
			}
			
		}
		return 1;
	}

	public static Fields getFields(Class<?> cls){
		Fields fields = classFieldMap.get(cls.getName());
		if(null == fields){
			synchronized (classFieldMap) {
				fields = new Fields(cls);
				classFieldMap.put(cls.getName(), fields);
			}
		}
		return fields;
	}
	
	
	public static void clear(List<Map<String, String>> list) {
		if(null == list) {
			return;
		}
		for (Map<String, String> map : list) {
			if(null != map) {
				map.clear();
			}
		}
		list.clear();
	}
	
	public static Map<String,String> list2Map(List<Map<String, String>> list,String[] keyFields,String valueField) {
		CaseInsensitiveMap<String> map =new CaseInsensitiveMap<String>();
		if(null != list) {
			StringBuilder buf=new StringBuilder(); 
			for (Map<String, String> m : list) {
				buf=new StringBuilder();
				for(String key:keyFields){
					buf.append(m.get(key)).append(","); 
				}
				int len = buf.length();
				if(len>0){
					buf.setLength(len-1);
				}
				map.put(buf.toString(), m.get(valueField));
			}	
			list.clear();
		}
		
		return map;
	}

	public static int count(String sql, String[] sqlWhereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		Cursor c = null;
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		try {
			c = database.rawQuery(sql, sqlWhereArgs);
			rowCount = c.getCount();

		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("[count]--failed",e);
			rowCount = -1;
		} finally {
			database = null;
			if(null != c) {
				c.close();
			}
		}

		long end = System.currentTimeMillis();
		Logger.d("[count]--params：sql:" + sql +","+"sqlWhereArgs:"+Arrays.toString(sqlWhereArgs)+ ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
		return rowCount;
	}

}