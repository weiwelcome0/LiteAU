package com.wechallenge.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wechallenge.itf.IMapProcessor;
import com.wechallenge.tool.Logger;


/**
 * GenericType result of a request; it contains 3 fields: return code, return message and the list of generic type items
 * @author weisir
 * 2015-4-21
 * @param <T>
 */
public class RequestResult<T> {
	public static int OK = 0;
	public static int ERROR_NET = 1;
	public static int ERROR_DATA = 2;
	
	public static String RET_CODE = "retCode";
	public static String RET_MSG = "retMsg";
	public static String RET_LIST = "retList";
	/**
	 * the return code
	 */
	private int retCode;
	/**
	 * the return message
	 */
	private String retMsg;
	/**
	 * the list of items
	 */
	private List<T> dataList;
	
	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public static <T> List<T> convert(List<Map<String, String>> listMap, IMapProcessor<T> processor) {
		int size = null == listMap ? 0 : listMap.size();
		ArrayList<T> list = new ArrayList<T>(size);
		if (size > 0 && null != processor) {
			try {
				for (Map<String, String> map : listMap) {
					T t = processor.convert(map);
					if (null != t) {
						list.add(t);
					}
				}
			} catch (Exception e) {
				Logger.e("[convert]--convert map to another object error;", e);
			}

		}
		return list;
		
	}
	
	public static <T> RequestResult<T> convert(RequestResult<Map<String,String>> mapResult, IMapProcessor<T> processor){
		RequestResult<T> result = new RequestResult<T>();
		result.setRetCode(mapResult.getRetCode());
		result.setRetMsg(mapResult.getRetMsg());
		result.setDataList(convert(mapResult.getDataList(),processor));
		return result;
		
	}

	
}