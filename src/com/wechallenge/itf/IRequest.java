package com.wechallenge.itf;

import com.wechallenge.bean.RequestParam;

/**
 * the Generic Type request interface; the implemention can be various: http,soap or others
 * @author weisir
 * 2015-4-21
 * @param <T>
 */
public interface IRequest<T> {
	
	/**
	 * post a request which contains the rp parameters infomation to url
	 * @param rp	the parameters
	 * @param url	the url of server
	 * @return
	 */
	T doRequest(RequestParam rp,String url);
	
}