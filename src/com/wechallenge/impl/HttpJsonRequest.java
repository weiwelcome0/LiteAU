package com.wechallenge.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import com.wechallenge.bean.RequestParam;
import com.wechallenge.itf.IRequest;
import com.wechallenge.tool.HttpUtil;
import com.wechallenge.tool.Logger;
import com.wechallenge.tool.StringUtils;

/**
 * post a http request to url which json string returned by server
 * @author weisir
 * 2015-4-21
 */
public class HttpJsonRequest implements IRequest<String> {

	/* (non-Javadoc)
	 * @see com.wechallenge.itf.IRequest#doRequest(com.wechallenge.bean.RequestParam, java.lang.String)
	 */
	@Override
	public String doRequest(RequestParam rp, String url) {
		String params = null;
		byte[] result = null;
		InputStream stream = null;
		if (RequestParam.FUNC_UPLOAD.equals(rp.functionName)) {
			params = getUploadParams(rp);
			try {
				stream = new FileInputStream(rp.paramMap.get(RequestParam.PARAM_FILE));
			} catch (Exception e) {
				Logger.e("--httpClient.doRequest();uploadFile open FileInputStream exception:spath="
								+ rp.paramMap.get(RequestParam.PARAM_FILE), e);
			}
			result = HttpUtil.doPost(url, params, stream, 300*1000);
		} else {
			params = "sfunc=" + rp.functionName;
			stream = new ByteArrayInputStream(getBody(rp.paramMap).getBytes());
			result = HttpUtil.doPost(url, params, stream);
		}

		if (null == result) {
			return null;
		}

		String str = null;
		try {
			// 指定编码
			str = new String(result, HttpUtil.ENCODING);
		} catch (UnsupportedEncodingException e) {
			str = new String(result);
			Logger.e("[doRequest]结果编码异常.", e);
		}
		return str;
	}
	
	private String getUploadParams(RequestParam rp) {
		StringBuilder buf = new StringBuilder();
		buf.append("sfunc=").append(rp.functionName);
		for(Entry<String,String> entry:rp.paramMap.entrySet()){
			String value = entry.getValue();
			if(StringUtils.isNotEmpty(value)){
				buf.append("&").append(entry.getKey())
				.append("=").append(value);
			}
		}
//		buf.append("&sid_bill=").append(rp.paramMap.get("sid_bill"))
//		.append("&sid_busistatus=").append(rp.paramMap.get("sid_busistatus"))
//		.append("&scode=").append(rp.paramMap.get("scode"))
//		.append("&sname=").append(rp.paramMap.get("sname"));		
		return buf.toString();
	}

	private String getBody(Map<String, String> map) {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");

		if (null != map) {
			for (Entry<String, String> entry : map.entrySet()) {
				if(StringUtils.isNotEmpty(entry.getValue())){
					buf.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
				}
			}
		}
		buf.setLength(buf.length() - 1);
		buf.append("}");

		return buf.toString();
	}
	

}
