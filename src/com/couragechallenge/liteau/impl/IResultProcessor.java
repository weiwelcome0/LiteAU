package com.couragechallenge.liteau.impl;

import com.couragechallenge.liteau.bean.RequestResult;
import com.couragechallenge.liteau.itf.IConvert;



/**
 * 结果解析接口
 * 
 * @author 胡威
 * @param <T>
 * @param <T>
 * @date 2013-10-15
 */
public interface IResultProcessor<T> extends IConvert<CharSequence,RequestResult<T>> {
	
}