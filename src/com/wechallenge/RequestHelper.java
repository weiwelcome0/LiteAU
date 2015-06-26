package com.wechallenge;

import com.wechallenge.bean.MapRequestResult;
import com.wechallenge.bean.RequestParam;
import com.wechallenge.itf.IRequest;
import com.wechallenge.itf.IRequestResultConverter;
import com.wechallenge.tool.Logger;

/**
 * do the request;
 * @author weisir
 * 2015-4-21
 */
public class RequestHelper {
	private String serverUrl;

	private IRequest<String> requester;

	private IRequestResultConverter resultConverter;
	
	public RequestHelper(String serverUrl, IRequest<String> requester, IRequestResultConverter resultConverter){
		this.serverUrl = serverUrl;
		this.requester = requester;
		this.resultConverter = resultConverter;
	}

	public MapRequestResult requestServer(RequestParam rp) {
		return requestServer(rp, serverUrl);
	}

	public MapRequestResult requestServer(RequestParam rp, String url) {
		long t1 = System.currentTimeMillis();
		String str = requester.doRequest(rp, url);

		long t2 = System.currentTimeMillis();
		MapRequestResult result = resultConverter.convert(str);

		long t3 = System.currentTimeMillis();
		Logger.i("--requestServer--request timecost:" + (t2 - t1) + ";convert result timecost:" + (t3 - t2));

		return result;
	}
}
