package com.wechallenge.liteau.impl;

import java.util.Map;

import android.database.Cursor;

import com.wechallenge.liteau.bean.CaseInsensitiveMap;
import com.wechallenge.liteau.itf.ICursorProcessor;

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

		for (int i = 0, n = c.getColumnCount(); i < n; i++) {
			map.put(c.getColumnName(i), c.getString(i));
		}

		return map;
	}

}
