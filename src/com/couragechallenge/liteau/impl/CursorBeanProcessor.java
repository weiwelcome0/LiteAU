package com.couragechallenge.liteau.impl;

import java.lang.reflect.Field;

import android.database.Cursor;

import com.couragechallenge.liteau.bean.Fields;
import com.couragechallenge.liteau.itf.ICursorProcessor;
import com.couragechallenge.liteau.tool.ClassUtils;
import com.couragechallenge.liteau.tool.Logger;

/**
 * convert the cursor row of sqlite to a java bean
 * @author weisir
 * 2015-4-21
 */
public class CursorBeanProcessor<T> implements ICursorProcessor<T> {
	public Class<T> cls;
	private Fields fields;
	
	public CursorBeanProcessor(Class<T> cls,Fields fields){
		this.cls = cls;
		this.fields = fields;
	}
	
	@Override
	public T convert(Cursor c) {
		T t = null;
		try {
			t = cls.newInstance();
		} catch (Exception e) {
			Logger.e("--convert; reflect newInstance failed; cls:"+cls, e);
		}
		
		if(null != t){
			for (int i = 0, n = c.getColumnCount(); i < n; i++) {
				Field f = fields.getField(c.getColumnName(i));
				if(null == f){
					continue;
				}
				Object value = ClassUtils.convertCompatibleType(c.getString(i), f.getType());
				try {
					f.set(t, value);
				}  catch (Exception e) {
					Logger.e("--convert; reflect bean set field failed; cls,field,type,value:"+cls+","+f.getName()+","+f.getType()+","+value, e);
				}
			}
		}

		return t;
	}

}
