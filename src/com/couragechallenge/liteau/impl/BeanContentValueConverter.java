package com.couragechallenge.liteau.impl;
import java.lang.reflect.Field;

import android.content.ContentValues;

import com.couragechallenge.liteau.bean.Fields;
import com.couragechallenge.liteau.itf.IContentValuesConverter;
import com.couragechallenge.liteau.tool.Logger;

/**
 * convert a Bean to contentvalue which always used in database "insert" operations
 * @author weisir
 * 2015-4-21
 */
public class BeanContentValueConverter<F> implements
		IContentValuesConverter<F> {

	private Fields fields;

	public BeanContentValueConverter(Fields fields) {
		this.fields = fields;
	}

	@Override
	public ContentValues convert(F f) {

		ContentValues values = new ContentValues();
		for(Field field:fields.getAllFields()){
			Object value = null;
			try {
				value = field.get(f);
			} catch (Exception e) {
				Logger.e("--convert; reflect bean get field failed; cls,field,type,value:"+f.getClass()+","+field.getName()+","+field.getType()+","+value, e);
			}
			if(value != null){
				values.put(field.getName(), ""+value);
			}
		}

		return values;
	}

}
