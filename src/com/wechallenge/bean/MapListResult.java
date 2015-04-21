/**
 * author weisir
 * 2015-4-21
 */
package com.wechallenge.bean;

import java.util.List;
import java.util.Map;

import com.wechallenge.itf.IMapProcessor;

/**
 * @author weisir
 * 2015-4-21
 */
public class MapListResult extends RequestResult<Map<String, String>> {
	
	public <T> List<T> getDataList(IMapProcessor<T> processor){
		
		return convert(getDataList(), processor);		
	}
}
