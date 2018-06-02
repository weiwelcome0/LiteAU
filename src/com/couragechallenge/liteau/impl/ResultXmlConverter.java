package com.couragechallenge.liteau.impl;

import com.couragechallenge.liteau.bean.MapRequestResult;
import com.couragechallenge.liteau.itf.IRequestResultConverter;

/** convert the xml String to the MapListResult; 
 * it's strongly recommended that form the xml json String like this:
 * <result><retCode>0</retCode><retMsg>25#successful#some other infomation</retMsg><dataList><row><a>1</a><b>2</b></row><row><a>2</a><b>3</b></row></dataList></result>
 * and the failed json String much more like this:
 * <result><retCode>-1</retCode><retMsg>the database is closed</retMsg><dataList></dataList></result>
 * as also you can defined the xml string format in you own way
 * @author weisir
 * 2015-4-21
 */
public class ResultXmlConverter implements IRequestResultConverter {

	/* (non-Javadoc)
	 * @see com.wechallenge.itf.IConvert#convert(java.lang.Object)
	 */
	@Override
	public MapRequestResult convert(CharSequence f) {
		// TODO Auto-generated method stub
		return null;
	}

}
