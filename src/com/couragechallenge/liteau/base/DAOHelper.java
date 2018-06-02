package com.couragechallenge.liteau.base;

import java.util.List;
import java.util.Map;

import com.couragechallenge.liteau.bean.RequestParam;
import com.couragechallenge.liteau.bean.RequestResult;
import com.couragechallenge.liteau.impl.IResultProcessor;
import com.couragechallenge.liteau.impl.JsonResultProcessor;
import com.couragechallenge.liteau.itf.IRequest;

public class DAOHelper {
	public String serverUrl;

	private IRequest<String> requester;

	private IResultProcessor<Map<String, String>> resultProcessor;

	public static final DAOHelper instance = new DAOHelper();

	private DAOHelper() {
		initServerUrl();
		// serverUrl = "http://192.168.1.3/mobile/";
		requester = HttpClient.instance;
		resultProcessor = new JsonResultProcessor();
	}

	private void initServerUrl() {
		String sql = "select svalue from dic_tUser where istatus >=0 and scode='sserverurl'";
		List<String> list = DBHelper.query(sql, null, new DBHelper.StringRowProcessor());
		if (list != null && list.size() > 0) {
			serverUrl = new String(list.get(0));
			list.clear();
		}

	}

	public RequestResult<Map<String, String>> requestServer(RequestParam rp) {
		return requestServer(rp, serverUrl);
	}

	public RequestResult<Map<String, String>> requestServer(RequestParam rp, String url) {
		long t1 = System.currentTimeMillis();
		String str = requester.doRequest(rp, url);

		long t2 = System.currentTimeMillis();
		RequestResult<Map<String, String>> result = resultProcessor.convert(str);

		long t3 = System.currentTimeMillis();
		Logger.i("--requestServer--请求耗时:" + (t2 - t1) + ";解析耗时:" + (t3 - t2));

		return result;
	}
}
