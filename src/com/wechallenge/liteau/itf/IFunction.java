package com.wechallenge.liteau.itf;

import com.wechallenge.liteau.bean.RequestResult;

public interface IFunction<T> {
	RequestResult<T> execute();
}
