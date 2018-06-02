package com.couragechallenge.liteau.base;

/***
Copyright (c) 2009 
Author: Stefan Klumpp <stefan.klumpp@gmail.com>
Web: http://stefanklumpp.com

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.couragechallenge.liteau.bean.RequestParam;
import com.couragechallenge.liteau.itf.IRequest;

public class HttpClient implements IRequest<String> {
	private static final int TIMEOUT = 30 * 1000;

	static final int BUFFER_SIZE = 8 * 1024;

	public static final String ENCODING = "utf-8";

	public static final HttpClient instance = new HttpClient();

	@Override
	public String doRequest(RequestParam rp, String serverURL) {
		String params = null;
		byte[] result = null;
		InputStream stream = null;
		
//		rp.paramMap.put("sid_user", MainApp.user().sid);

		if (RequestParam.FUNC_UPLOAD.equals(rp.functionName)) {
			params = getUploadParams(rp);
			try {
				stream = new FileInputStream(rp.paramMap.get("spath"));
			} catch (Exception e) {
				Logger.e("--httpClient.doRequest();uploadFile open FileInputStream exception:spath="
								+ rp.paramMap.get("spath"), e);
			}
			result = doPost(params, stream, serverURL);
		} else {
			params = "sfunc=" + rp.functionName;
			stream = new ByteArrayInputStream(getBody(rp.paramMap).getBytes());
			result = doPost(params, stream, serverURL);
		}

		if (null == result) {
			return null;
		}

		String str = null;
		try {
			// 指定编码
			str = new String(result, HttpClient.ENCODING);
		} catch (UnsupportedEncodingException e) {
			str = new String(result);
			Logger.e("[requestServer]结果编码异常.", e);
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

	public byte[] doPost(String params, InputStream stream, String serverURL) {
		HttpURLConnection objConn = null;
		BufferedOutputStream outputStream = null;
		BufferedInputStream inputStream = null;
		try {

			URL objURL = new URL(serverURL + "?" + params);
			Logger.d("[doPost]--URL:" + objURL.toString());

			// 打开连接;
			objConn = (HttpURLConnection) objURL.openConnection();
			objConn.setRequestMethod("POST");
			// objConn.setRequestProperty("Content-Type", "text/plain");

			// 设置连接和读取超时时间
			objConn.setConnectTimeout(TIMEOUT);
			objConn.setReadTimeout(TIMEOUT);
			objConn.setDoInput(true);

			byte[] data = new byte[BUFFER_SIZE];
			int c = 0;

			// 如果有输入数据时则写入输出流
			if (stream != null) {
				// 设置传输长度
				objConn.setChunkedStreamingMode(stream.available());
				objConn.setUseCaches(false);

				// 设置URLConnection允许输出
				objConn.setDoOutput(true);
				outputStream = new BufferedOutputStream(objConn.getOutputStream());

				while ((c = stream.read(data)) != -1) {
					outputStream.write(data, 0, c);
					outputStream.flush();
				}
				stream.close();

			} else {
				objConn.setDoOutput(false);
			}

			// 读取返回字节数组
			inputStream = new BufferedInputStream(objConn.getInputStream());
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			byte[] bufferByte = new byte[BUFFER_SIZE];
			for (int l = -1; (l = inputStream.read(bufferByte)) > -1;) {
				bout.write(bufferByte, 0, l);
				bout.flush();
			}

			byte[] dataReturn = bout.toByteArray();
			Logger.d("[doPost]--http请求成功;获得数据长度:" + (null == dataReturn ? "null" : "" + dataReturn.length));

			return dataReturn;

		} catch (Exception ex) {
			// 记录错误信息
			Logger.e("[doPost]--http请求异常;", ex);

		} finally {
			// 最后关闭连接和数据流
			if (objConn != null) {
				objConn.disconnect();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					Logger.e("[doPost]--输出流关闭异常;", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Logger.e("[doPost]--输入流关闭异常;", e);
				}
			}
		}

		// 如果中间出现错误,则返回null
		return null;
	}


}
