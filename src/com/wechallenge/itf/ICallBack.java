package com.wechallenge.itf;

/**
 * the callback interface
 * @author weisir
 * 2015-4-21
 * @param <T>
 */
public interface ICallBack<T> {
	/**
	 * 获取数据、处理结束时回调
	 * @return
	 */
	public int call(T t);
}
