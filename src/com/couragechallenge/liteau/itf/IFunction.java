package com.couragechallenge.liteau.itf;

import com.couragechallenge.liteau.bean.RequestResult;

public interface IFunction<T> {
	RequestResult<T> execute();
}
