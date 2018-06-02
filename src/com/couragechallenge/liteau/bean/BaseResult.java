package com.couragechallenge.liteau.bean;

import java.util.List;

public class BaseResult<T> {
	public int code;
	public String message;
	public List<T> dataList;
}
