/**
 * author weisir
 * 2015-4-21
 */
package com.wechallenge.liteau.bean;

import java.util.List;
import java.util.Map;

import com.wechallenge.liteau.itf.IMapProcessor;

/**
 * @author weisir
 * 2015-4-21
 */
public class MapRequestResult extends RequestResult<Map<String, String>> {
	
	public <T> List<T> getDataList(IMapProcessor<T> processor){
		
		return convert(getDataList(), processor);		
	}
}
