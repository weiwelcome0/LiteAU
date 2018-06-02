package com.couragechallenge.liteau.itf;

import java.util.Map;

/**
 * the sub abstract interface which specified convert a map to other object
 * @author weisir
 * 2015-4-21
 * @param <T>
 */
public interface IMapProcessor<T> extends IConvert<Map<String,String>, T> {

	public static final IMapProcessor<Map<String, String>> BASIC_MAP_PROCESSOR = new IMapProcessor<Map<String, String>>() {
		@Override
		public Map<String, String> convert(Map<String, String> map) {
			return map;
		}
	};
}
