package com.wechallenge.itf;

import android.database.Cursor;

/**
 * the sub abstract interface which specified convert a Cursor to other object
 * @author weisir
 * 2015-4-21
 * @param <T>
 */
public interface ICursorProcessor<T> extends IConvert<Cursor, T> {

}
