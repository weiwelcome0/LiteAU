package com.couragechallenge.liteau.impl;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;

import com.couragechallenge.liteau.itf.IContentValuesConverter;

/**
 * convert a map to contentvalue which always used in "insert" operations
 * @author weisir
 * 2015-4-21
 */
public class MapContentValueConverter implements
		IContentValuesConverter<Map<String, String>> {

	private String[] fields = null;

	public MapContentValueConverter() {

	}

	public MapContentValueConverter(String[] fields) {
		this.fields = fields;
	}

	@Override
	public ContentValues convert(Map<String, String> f) {
		return convert(f,this.fields);
	}
	
	public static ContentValues convert(Map<String, String> f, String[] fields) {
		
		ContentValues values = new ContentValues();
		if (null != fields) {
			for (String fld : fields) {
				values.put(fld, f.get(fld));
			}
		} else {
			for (Entry<String, String> e : f.entrySet()) {
				values.put(e.getKey(), e.getValue());
			}
		}
		
		return values;
	}
	

}
