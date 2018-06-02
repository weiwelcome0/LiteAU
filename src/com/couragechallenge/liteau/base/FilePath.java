package com.couragechallenge.liteau.base;


public enum FilePath {
	PRODUCT("CommonServiceArea"), 

	SDCARD(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/"),
	
	/** 系统运行所需资源根目录. */
	ROOT(""+SDCARD + PRODUCT + "/"),
	
	/** 数据库文件路径. */
	FILE_DB(ROOT + "client.db"),
	
	/** 日志文件. */
	FILE_LOG(""+ROOT + PRODUCT +".log"),
		
	/** 多媒体目录. */
	CACHE(ROOT + "cache/"),
	
	/** 项目目录. */
	PROJECT(ROOT + "project/"),
	
	/** 项目目录. */
	DATA(ROOT + "data/"),
	
	/** config文件  */
	FILE_PROJECT_CONFIG(""+PROJECT + "project.conf");
	
	private String path = "";
	FilePath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return path;
	}

	private void setPath(String path) {
		this.path = path;
	}
	
}
