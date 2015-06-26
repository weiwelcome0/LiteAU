/**
 * author weisir
 * 2015-4-21
 */
package com.wechallenge.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wechallenge.bean.CaseInsensitiveMap;
import com.wechallenge.bean.MapRequestResult;
import com.wechallenge.bean.RequestResult;
import com.wechallenge.itf.IRequestResultConverter;
import com.wechallenge.tool.Logger;

/** convert the Json String to the MapListResult; 
 * it's strongly recommended that form the successful json String like this:
 * { retCode:0,retMsg:"25#successful#some other infomation",retList:[{a:1,b:2},{a:2,b:3}]}
 * and the failed json String much more like this:
 * { retCode:-1,retMsg:"this database is closed",retList:[] }
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
		MapRequestResult result = new MapRequestResult();
		if (f == null) {
			result.setRetCode(RequestResult.ERROR_NET);
			result.setRetMsg("ÍøÂç´íÎó");
			return result;
		}

		int errCode = RequestResult.ERROR_DATA;
		String errMsg = "Êý¾Ý´íÎó";
		result.setRetCode(errCode);
		result.setRetMsg(errMsg);

		// s = "{\"errcode\":\"-2\",\"errmsg\":\"ÃÜÂë²»ÕýÈ·\"}";

		try {
			int k = 0;
			String key = null, value = null;
			Iterator<?> iterator1, iterator2;
			ArrayList<Map<String, String>> list = null;

			JSONObject jsonObj = new JSONObject(f.toString());
			iterator1 = jsonObj.keys();
			while (iterator1.hasNext() && k < 3) {

				key = (String) iterator1.next();
				if (RequestResult.RET_CODE.equalsIgnoreCase(key)) {
					k ++;
					errCode = jsonObj.getInt(key);
				}

				if (RequestResult.RET_MSG.equalsIgnoreCase(key)) {
					k ++;
					errMsg = jsonObj.getString(key);
				}

				if (RequestResult.RET_LIST.equalsIgnoreCase(key)) {
					k++;
					list = new ArrayList<Map<String, String>>();
					JSONArray jsonArr = null;
					try{
						jsonArr = jsonObj.getJSONArray(key);
					}catch(JSONException e1){
						Logger.e("--convert;½âÎödatalist´íÎó:", e1);
					}
					int len = null == jsonArr ? 0 : jsonArr.length();

					for (int i = 0; i < len; i++) {
						CaseInsensitiveMap<String> map = new CaseInsensitiveMap<String>();
						JSONObject obj = jsonArr.getJSONObject(i);
						iterator2 = obj.keys();

						while (iterator2.hasNext()) {
							key = (String) iterator2.next();
							value = obj.getString(key);
							map.put(key, value);
						}

						list.add(map);
					}

				}

			}

			result.setRetCode(errCode);
			result.setRetMsg(errMsg);
			result.setDataList(list);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("--convert½âÎöjson´íÎó;json=" + f, e);
		}

		return result;
	}

}
