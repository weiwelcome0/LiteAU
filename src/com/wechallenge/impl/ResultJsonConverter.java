/**
 * author weisir
 * 2015-4-21
 */
package com.wechallenge.impl;

import com.wechallenge.bean.MapRequestResult;
import com.wechallenge.itf.IRequestResultConverter;

/** convert the Json String to the MapListResult; 
 * it's strongly recommended that form the successful json String like this:
 * { retCode:0,retMsg:"25#successful#some other infomation",dataList:[{a:1,b:2},{a:2,b:3}]}
 * and the failed json String much more like this:
 * { retCode:-1,retMsg:"this database is closed",dataList:[] }
 * as also you can defined the json string format in you own format
 * @author weisir
 * 2015-4-21
 */
public class ResultJsonConverter implements IRequestResultConverter {

	/* (non-Javadoc)
	 * @see com.wechallenge.itf.IConvert#convert(java.lang.Object)
	 */
	@Override
	public MapRequestResult convert(CharSequence f) {
		// TODO Auto-generated method stub
		return null;
	}

}
