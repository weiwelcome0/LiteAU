package com.wechallenge.impl;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;

import com.wechallenge.itf.IContentValuesConverter;

/**
 * convert a map to contentvalue which always used in "insert" operations
 * @author weisir
 * 2015-4-21
 */
public class MapContentValueConverter implements
		IContentValuesConverter<Map<String, String>> {

	private String[] cols = null;

	public MapContentValueConverter() {

	}

	public MapContentValueConverter(String[] columns) {
		this.cols = columns;
	}

	@Override
	public ContentValues convert(Map<String, String> f) {

		ContentValues values = new ContentValues();
		if (null != cols) {
			for (String col : cols) {
				values.put(col, f.get(col));
			}
		} else {
			for (Entry<String, String> e : f.entrySet()) {
				values.put(e.getKey(), e.getValue());
			}
		}

		return values;
	}

}
