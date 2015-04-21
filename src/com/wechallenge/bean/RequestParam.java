package com.wechallenge.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * the universal data struct of request
 * @author weisir
 * 2015-4-21
 */
public class RequestParam {
	public static final String PARAM_UPLOAD_FILE = "uploadFilePath";

	public Map<String, String> paramMap = new HashMap<String, String>();
	public String functionName;
}