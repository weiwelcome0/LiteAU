package com.couragechallenge.liteau.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.couragechallenge.liteau.base.Logger;
import com.couragechallenge.liteau.bean.CaseInsensitiveMap;
import com.couragechallenge.liteau.bean.RequestResult;

/**
 * json结果解析
 */
public class JsonResultProcessor implements IResultProcessor<Map<String, String>> {

	@Override
	public RequestResult<Map<String, String>> convert(CharSequence s) {

		RequestResult<Map<String, String>> result = new RequestResult<Map<String, String>>();
		if (s == null) {
			result.setRetCode(RequestResult.ERROR_NET);
			result.setRetMsg("网络错误");
			return result;
		}

		int errCode = RequestResult.ERROR_DATA;
		String errMsg = "数据错误";
		result.setRetCode(errCode);
		result.setRetMsg(errMsg);

		// s = "{\"errcode\":\"-2\",\"errmsg\":\"密码不正确\"}";

		try {
			int k = 0;
			String key = null, value = null;
			Iterator<?> iterator1, iterator2;
			ArrayList<Map<String, String>> list = null;

			JSONObject jsonObj = new JSONObject(s.toString());
			iterator1 = jsonObj.keys();
			while (iterator1.hasNext() && k < 3) {

				key = (String) iterator1.next();
				if ("errcode".equalsIgnoreCase(key)) {
					k ++;
					errCode = jsonObj.getInt(key);
				}

				if ("errmsg".equalsIgnoreCase(key)) {
					k ++;
					errMsg = jsonObj.getString(key);
				}

				if ("datalist".equalsIgnoreCase(key)) {
					k++;
					list = new ArrayList<Map<String, String>>();
					JSONArray jsonArr = null;
					try{
						jsonArr = jsonObj.getJSONArray("datalist");
					}catch(JSONException e1){
						Logger.e("--convert;解析datalist错误:", e1);
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
			Logger.e("--convert解析json错误;json=" + s, e);
		}

		return result;
	}
}