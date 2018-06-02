/*
 * Zip.java		20091207
 * 
 * Copyright (c) 2009 北京数字政通科技股份有限公司
 * 版权所有
 * 
 * 文件功能描述：Zip文件压缩解压类
 *
 * 修改标识：史先方20091207
 * 修改描述：创建
 */
package com.couragechallenge.liteau.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩解压Zip数据.
 * 
 */
public class Zip {
	
	/** buffer size. */
	private static final int BUFF_SIZE = 1024 * 256; // 128K Byte
	
	/**
	 * 解压zip文件,将文件到目标路径:defaultUnzipDir.
	 * @param zipFilePath zip文件完整路径
	 * @param defaultUnzipDir 解压目标路径 
	 * @param isUnzipWithPath 是否解压压缩文件的相对路径
	 * @return 解压是否成功
	 */
	public static boolean unZip(String zipFilePath, String defaultUnzipDir, boolean isUnzipWithPath) {
		return unZip(zipFilePath, defaultUnzipDir, isUnzipWithPath, null, null);
	}
	
	/**
	 * 解压zip文件,将指定扩展名的文件解压到指定路径,其余文件到目标路径:defaultUnzipDir.
	 * @param zipFilePath zip文件完整路径
	 * @param defaultUnzipDir 解压目标路径 
	 * @param isUnzipWithPath 是否解压压缩文件的相对路径
	 * @param specificSuffix 指定的文件扩展名
	 * @param specificUnzipDir 符合指定的文件扩展名的文件解压到的目标路径
	 * @return 解压是否成功
	 */
	public static boolean unZip(String zipFilePath, String defaultUnzipDir, boolean isUnzipWithPath,
								String specificSuffix, String specificUnzipDir) {
		
		InputStream in = null;
		FileOutputStream out = null;
		
		try {
			if (!new File(zipFilePath).exists()) {
				Logger.w("unZip文件不存在:" + zipFilePath);
				return false;
			}
			
			Logger.i("unZip:" + zipFilePath);
			
			ZipFile zipFile = new ZipFile(zipFilePath);
			// 遍历压缩文件
			Enumeration<? extends ZipEntry> enumer = zipFile.entries();
			while (enumer.hasMoreElements()) {
				ZipEntry entry = enumer.nextElement();
				// 实体类型和实体名
				boolean isDir = entry.isDirectory();
				String name = new String(entry.getName().getBytes("8859_1"),"GB2312");
				// 如果不是路径,则解压文件
				if (!isDir) {
					String filePath = "";
					// 如果指定特定扩展名和特定目录,且文件符合扩展名,则解压到特定目录下,不包括压缩文件的相对路径.
					if (specificSuffix != null && specificUnzipDir != null
							&& name.toLowerCase().endsWith(specificSuffix)) {
						filePath = specificUnzipDir + File.separator
								+ name.substring(name.lastIndexOf(File.separator) + 1);
					} else if (!isUnzipWithPath) {
						// 如果未指定或不符合指定扩展名,并且解压时不包括相对路径,则解压到目标路径
						filePath = defaultUnzipDir + File.separator
								+ name.substring(name.lastIndexOf(File.separator) + 1);
					} else {
						// 如果未指定或不符合指定扩展名,并且解压时包括相对路径,则解压到目标路径+相对路径
						filePath = defaultUnzipDir + File.separator + name;
					}
					
					// 创建目标解压路径的目录和文件
					File file = new File(filePath);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();//创建所有的父目录
					}
					file.createNewFile();
					
					// 读取压缩文件内容并写入文件
					in = zipFile.getInputStream(entry);
                    out = new FileOutputStream(file);

                    byte[] by = new byte[BUFF_SIZE];
                    int c;
                    while ((c = in.read(by)) != -1) {
                        out.write(by, 0, c);
                        out.flush();
                    }
                    
    				Logger.d( "Unzip File:" + filePath);
				}
			}
			zipFile.close();
		} catch (IOException e) {
			Logger.e("unZip IOException", e);
			return false;
		} catch (Exception ex) {
			Logger.e("unZip", ex);
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		
		return true;
	}
	
	public static boolean unZip(String zipFilePath, String defaultUnzipDir) 
	{
		byte[] buf=new byte[1024*128];
		BufferedOutputStream os= null;
		InputStream is = null; 
		try {
			if (!new File(zipFilePath).exists()) {
				Logger.w( "unZip文件不存在:" + zipFilePath);
				return false;
			}

			Logger.i("unZip:" + zipFilePath);

			ZipFile zipFile = new ZipFile(zipFilePath);
			// 遍历压缩文件
			Enumeration<? extends ZipEntry> enumer = zipFile.entries();
			while (enumer.hasMoreElements()) {
				ZipEntry entry = enumer.nextElement();
				// 实体类型和实体名
				boolean isDir = entry.isDirectory();
				String name = new String(entry.getName().getBytes("8859_1"),"GB2312");
				// 如果不是路径,则解压文件
				if (isDir) {
					continue;
				}

					// 读取压缩文件内容并写入文件
					os = new BufferedOutputStream(new FileOutputStream(getRealFileName(defaultUnzipDir, name)));
					is=new BufferedInputStream(zipFile.getInputStream(entry));

					int readLen=0;
					while ((readLen=is.read(buf, 0, 1024))!=-1) {
						os.write(buf, 0, readLen);
						}
					is.close();
					os.close(); 
			}
			zipFile.close();
		} catch (IOException e) {
			Logger.e("unZip IOException", e);
			return false;
		} catch (Exception ex) {
			Logger.e("unZip", ex);
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}

		return true;
	}

	private static File getRealFileName(String baseDir, String absFileName) {
		   String[] dirs = absFileName.split("/");
		   File ret = new File(baseDir);
		   // 创建目录结构
		   if (dirs.length > 1) {
		    for (int i = 1; i < dirs.length - 1; i++) {		//i=0时包含文件目录，i=1开始就不包含文件目录
		     ret = new File(ret, dirs[i]);
		    }
		   }
		   if (!ret.exists()) {
		    ret.mkdirs();
		   }
		   ret = new File(ret, dirs[dirs.length - 1]);
		   return ret;
		}

	
	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFiles(File[] resFileList, File zipFile) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.close();
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException,
			IOException {
		rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	} 
	
}
