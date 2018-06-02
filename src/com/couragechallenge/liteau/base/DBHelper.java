package com.couragechallenge.liteau.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.couragechallenge.liteau.bean.CaseInsensitiveMap;
import com.couragechallenge.liteau.itf.IContentValuesConverter;
import com.couragechallenge.liteau.itf.ICursorProcessor;

/**
 * sqlite的dao操作帮助类
 * 
 * @author weisir
 * 
 */

public class DBHelper {
	public static final String TAG = "[DBHelper]";

	public static final MapRowProcessor MAPROWPROCESSOR = new MapRowProcessor();
	
	private static SQLiteDatabase database = null;
	
	/**
	 * 取得可读写数据库实例.
	 * @return 数据库
	 */
	public static synchronized SQLiteDatabase getWritableDatabase() {
		if (database == null || !database.isOpen()) {
			database = SQLiteDatabase.openDatabase(""+FilePath.FILE_DB, null, SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}

	/**
	 * 查询得到列表
	 * 
	 * @param sql 　完整的select语句，可包含?，但不能用;结尾
	 * @param sqlWhereArgs 　查询参数
	 * @param rp 　　每行的处理，使用DAOHelper.MAPROWPROCESSOR
	 * @return
	 */
	public static List<Map<String, String>> query(String sql) {
		return query(sql, null, MAPROWPROCESSOR);
	}
	
	/**
	 * 查询得到列表
	 * 
	 * @param sql 　完整的select语句，可包含?，但不能用;结尾
	 * @param sqlWhereArgs 　查询参数
	 * @param rp 　　每行的处理，使用DAOHelper.MAPROWPROCESSOR
	 * @return
	 */
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
			Logger.e("[query]--查询失败" , e);

		} finally {
			database = null;
			if(null != c) {
				c.close();
			}
		}

		long end = System.currentTimeMillis();

		Logger.d("[query]--参数：sql:" + sql + ",sqlWhereArgs:" + Arrays.toString(sqlWhereArgs) + ";耗时：" + (end - begin) + "ms;");

		return list;
	}

	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, MAPROWPROCESSOR, null);
	}
	
	public static <T> List<T> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, ICursorProcessor<T> rp) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, rp, null);
	}
	
	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, String strOrderBy) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, MAPROWPROCESSOR, strOrderBy, null);
	}

	public static <T> List<T> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, ICursorProcessor<T> rp, String strOrderBy) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, rp, strOrderBy, null);
	}
	
	public static List<Map<String, String>> query(String tableName, String[] columns, String sqlWhere, String[] sqlWhereArgs, String strOrderBy, String limit) {
		return query(tableName, columns, sqlWhere, sqlWhereArgs, MAPROWPROCESSOR, strOrderBy, limit);
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
			Logger.e( "[query]--查询失败", e);
		} finally {
			database = null;
			if(null != cursor) {
				cursor.close();
			}
		}

		long end = System.currentTimeMillis();

		Logger.d("[query]--参数：table:" + tableName + ",cols:" + Arrays.toString(columns) + ",where:" + sqlWhere + ";耗时：" + (end - begin) + "ms;");

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
			// 打开数据库
			database.beginTransaction();
			if(values != null) {
				rowNum = database.insert(table, nullColumnHack, values);
			}
			// 设置事务成功.
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[insert]--插入失败",e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		
		Logger.d("[insert]--参数：table:" + table + ",nullColumnHack:" + nullColumnHack +",values:" + values + ";耗时：" + (end - begin) + "ms;");
		
		return rowNum > 0 ? 1 : 0;
	}

	public static <T> int insert(String tableName, List<T> list, IContentValuesConverter<T> converter) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = 0;
		
		long begin = System.currentTimeMillis();
		
		try {
			// 打开数据库
			database.beginTransaction();
			if(null != list){
				for (T t : list) {
					ContentValues v = converter.convert(t);
					if(v != null) {
						if(database.insert(tableName, null, v) >=0){
							rowCount ++;
						}
						v.clear();
						v = null;
					}
				}				
			}
			// 设置事务成功.
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[insert]--插入失败", e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		
		Logger.d("[insert]--参数：table:" + tableName + ",list:" + (list==null?null:list.size()) +",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
		return rowCount;

	}

	public static int delete(String tableName, String sqlWhere) {
		return delete(tableName, sqlWhere, null);
	}

	public static int delete(String tableName, String sqlWhere, String[] whereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = -1;
		long begin = System.currentTimeMillis();
		
		try {
			// 打开数据库
			database.beginTransaction();
			// 设置事务成功.
			rowCount = database.delete(tableName, sqlWhere, whereArgs);
			database.setTransactionSuccessful();
			return rowCount;
		} catch (Exception e) {
			Logger.e("[delete]--删除失败",e);
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[delete]--参数：table:" + tableName + ",where:" + sqlWhere + ",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
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
			// 打开数据库
			database.beginTransaction();	
			for(String[] args:listArgs){
				database.execSQL(sql, args);
			}
			// 设置事务成功.
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--更新失败",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		long end = System.currentTimeMillis();
		Logger.d("[update]--参数：sql:" + sql + "listArgs.size():"+listArgs.size()+",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
		return rowCount;
	}

	public static int update(String sql, String[] args) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		try {
			// 打开数据库
			database.beginTransaction();
			// 设置事务成功.
			database.execSQL(sql, args);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--更新失败",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[delete]--参数：sql:" + sql + ",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
		return rowCount;
	}
	
	public static int update(String table, ContentValues values, String whereClause) {
		return update(table, values, whereClause, null);
	}
	
	/**
	 * 更新
	 * @param tableName
	 * @param values
	 * @param where
	 * @return -1表示异常，0-表示不存在，1-有
	 */
	public static int update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int rowCount = 0;
		long begin = System.currentTimeMillis();
		
		try {
			// 打开数据库
			database.beginTransaction();
			// 设置事务成功.
			rowCount = database.update(tableName, values, whereClause, whereArgs);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e("[update]--更新失败",e);
			rowCount = -1;
		} finally {
			database.endTransaction();
		}
		
		long end = System.currentTimeMillis();
		Logger.d("[update]--参数：tableName:" + tableName +",whereClause:"+whereClause + ",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
		return rowCount;
	}

	// private ContentValues mapToContentValues(Map<String, String> map) {
	//
	// ContentValues values = new ContentValues();
	// for (String col : cols) {
	// values.put(col, map.get(col));
	// }
	//
	// return values;
	// }

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
			Logger.e("[count]--查询失败" ,e);
			rowCount = -1;
		} finally {
			database = null;
			if(null != c) {
				c.close();
			}
		}

		long end = System.currentTimeMillis();
		Logger.d("[count]--参数：sql:" + sql +","+ Arrays.toString(sqlWhereArgs)+ ",rowCount:" + rowCount + ";耗时：" + (end - begin) + "ms;");
		
		return rowCount;
	}

	/** 将每行处理成Map<String,String>结构 */
	public static class MapRowProcessor implements ICursorProcessor<Map<String, String>> {

		@Override
		public Map<String, String> convert(Cursor c) {
			Map<String, String> map = new CaseInsensitiveMap<String>();

			String[] columns = c.getColumnNames();

			for (String col : columns) {
				map.put(col, c.getString(c.getColumnIndex(col)));
			}

			return map;
		}
	}
	
	/** 将每行处理成BaseVO结构  */
	public static class BaseVOProcessor<T> implements ICursorProcessor<T> {
		public Class<?> clazz = null;
		public BaseVOProcessor( Class<?> clazz){
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T convert(Cursor c) {
			T obj=null;
			try {
				obj = (T)clazz.newInstance();

				Field[] flds = ClassUtils.getAllFields(clazz);
				for(Field f:flds){
					String name = f.getName();
					int index = c.getColumnIndex(name);
					if(index < 0){
						String[] cols = c.getColumnNames();
						for(int i =0; i < cols.length; i ++){
							if(cols[i].equalsIgnoreCase(name)){
								index = i ;
								break;
							}
						}
					}
					if(index < 0){
						continue;
					}
					f.setAccessible(true);
					if(name.startsWith("s") || name.startsWith("v")){
						f.set(obj, c.getString(index));
					}else if(name.startsWith("i")){
						f.set(obj, c.getInt(index));
					}else if(name.startsWith("n")){
						f.set(obj, c.getDouble(index));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return obj;
		}

	}

	// 将每行处理成String结构
	public static class StringRowProcessor implements ICursorProcessor<String> {
		private String[] fields;
		private String joinner;
		private volatile int[] fldIdx = null;

		public StringRowProcessor() {
			this.fields = null;
			this.joinner = ",";
		}

		public StringRowProcessor(String[] fields, String joinner) {
			this.fields = fields;
			if(null != joinner) {
				this.joinner = joinner;
			}
		}

		public StringRowProcessor(int[] fieldIndex, String joinner) {
			fldIdx = fieldIndex;
			if(null != joinner) {
				this.joinner = joinner;
			}
		}

		@Override
		public String convert(Cursor c) {
			if(null == fldIdx) {
				initFldIdx(c);
			}

			StringBuilder builder = new StringBuilder();
			for (int i = 0, n = fldIdx.length; i < n; i++) {
				builder.append(c.getString(fldIdx[i]));
				if(i < n - 1) {
					builder.append(joinner);
				}
			}

			return builder.toString();
		}

		private void initFldIdx(Cursor c) {
			if(null == fields) {
				fldIdx = new int[c.getColumnCount()];
				for (int i = 0, n = c.getColumnCount(); i < n; i++) {
					fldIdx[i] = i;
				}
			} else {
				fldIdx = new int[fields.length];
				for (int i = 0, n = fields.length; i < n; i++) {
					fldIdx[i] = c.getColumnIndex(fields[i]);
				}
			}
		}

	}
}