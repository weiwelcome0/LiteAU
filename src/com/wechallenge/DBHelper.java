package com.wechallenge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wechallenge.bean.CaseInsensitiveMap;
import com.wechallenge.impl.CursorMapProcessor;
import com.wechallenge.itf.IContentValuesConverter;
import com.wechallenge.itf.ICursorProcessor;
import com.wechallenge.tool.Logger;

/**
 * sqlite db utils; Attention: must call the initialize() before sql operation
 * @author weisir
 * 2015-4-21
 */
public class DBHelper {
	private static String sqliteDbFile = null;
	private static SQLiteDatabase database = null;
	
	public static void initialize(String sqliteDbFilePath){
		if(!new File(sqliteDbFilePath).exists()){
			Logger.e("[init]--sqliteDbFile not exists; please confirm the path:"+sqliteDbFilePath);
			return;
		}
		sqliteDbFile = sqliteDbFilePath;
	}
	
	private static synchronized SQLiteDatabase getWritableDatabase() {
		if (database == null || !database.isOpen()) {
			database = SQLiteDatabase.openDatabase(sqliteDbFile, null, SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
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

		Logger.d("[query]--params：sql:" + sql + ",sqlWhereArgs:" + Arrays.toString(sqlWhereArgs) + ";timecost：" + (end - begin) + "ms;");

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

		Logger.d("[query]--params：table:" + tableName + ",cols:" + Arrays.toString(columns) + ",sqlWhere:" + sqlWhere + ";timecost：" + (end - begin) + "ms;");

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
	
	public static int update(String sql, List<String[]> listArgs) {
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
		Logger.d("[delete]--params：sql:" + sql + ",rowCount:" + rowCount + ";timecost：" + (end - begin) + "ms;");
		
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