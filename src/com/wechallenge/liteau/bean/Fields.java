/**
 * author weisir
 * 2015-4-26
 */
package com.wechallenge.liteau.bean;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import com.wechallenge.liteau.tool.ClassUtils;

/**
 * @author weisir 2015-4-26
 */
public class Fields {
	final CaseInsensitiveMap<Field> fieldsMap;

	public Fields(int fieldCount) {
		fieldsMap = new CaseInsensitiveMap<Field>(fieldCount);
	}
	
	public Fields(Class<?> cls){
		List<Field> list = ClassUtils.getPublicFields(cls);
		fieldsMap = new CaseInsensitiveMap<Field>(list.size());
		addAllField(list);
	}
	
	public void addField(String name, Field field) {
		fieldsMap.put(name, field);
	}
	
	public void addAllField(Collection<Field> fields){
		if(null != fields){
			for(Field f:fields){
				fieldsMap.put(f.getName(), f);
			}
		}
	}

	public Field getField(String name) {
		return fieldsMap.get(name);
	}

	public Collection<Field> getAllFields() {
		return fieldsMap.values();
	}
	
	public int getCount() {
		return fieldsMap.size();
	}

}
