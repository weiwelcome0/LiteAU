package com.couragechallenge.liteau.itf;

/**
 * the root GennericType interface, convert F object to T object
 * @author weisir
 * 2015-4-21
 * @param <F> the original object, means "from_object"
 * @param <T> the destination object, means "to_object"
 */
public interface IConvert<F, T> {
	T convert(F f);
}
