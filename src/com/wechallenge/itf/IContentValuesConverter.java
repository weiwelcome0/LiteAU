package com.wechallenge.itf;

import android.content.ContentValues;

/**
 * the sub abstract interface which specified converting other object to ContentValues
 * @author weisir
 * 2015-4-21
 * @param <F> the original object
 */
public interface IContentValuesConverter<F> extends IConvert<F, ContentValues> {

}
