package com.wechallenge.impl;

import java.util.Map;

import android.database.Cursor;

import com.wechallenge.bean.CaseInsensitiveMap;
import com.wechallenge.itf.ICursorProcessor;

/**
 * convert the cursor row of sqlite to map
 * @author weisir
 * 2015-4-21
 */
public class CursorMapProcessor implements ICursorProcessor<Map<String, String>> {
	
	public static final CursorMapProcessor CURSOR_MAP_PROCESSOR = new CursorMapProcessor();

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
