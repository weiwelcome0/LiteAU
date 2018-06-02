/*
 * Copyright (c) 2010 北京数字政通科技股份有限公司
 * 版权所有
 *
 * 修改标识：赵冲20110303
 * 修改描述：创建
 */
package com.couragechallenge.liteau.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.widget.Toast;

public class MediaUtil {
	  public static int FILE_SIZE = 400;
	  public static int PHOTE_WIDTH = 1024;

	/**
	 * 压缩图片
	 * 
	 * @param srcFilePath
	 *            源图片文件路径
	 * @param toSize
	 *            目标大小
	 * @return Bitmap
	 */
	public static Bitmap resizePic(String srcFilePath, int toSize) {
		try {
			// 读取原始图片大小
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			// 读取图片的旋转角度
			int degree = getBitmapDegree(srcFilePath);

			BitmapFactory.decodeFile(srcFilePath, options);

			// 根据原始图像大小,计算需要载入的图像最接近toSize又不失真的大小
			int width = options.outWidth;
			int height = options.outHeight;
			options.inJustDecodeBounds = false;

			int toWidth = toSize;
			int toHeight = toSize;
			int be = 1;
			if (width > height) {
				toHeight = toSize * height / width;
				toHeight = toHeight % 2 == 0 ? toHeight : toHeight + 1;
				be = width * 10 / toSize;
			} else {
				toWidth = toSize * width / height;
				toWidth = toWidth % 2 == 0 ? toWidth : toWidth + 1;
				be = height * 10 / toSize;
			}
			options.inSampleSize = be / 10;

			// 按目标大小读取图片,最小化内存占用
			Bitmap bitmapOrg = null;
			FileInputStream fiStream = new FileInputStream(srcFilePath);
			try {
				// 使用decodeStream可更好的避免内存溢出
				bitmapOrg = BitmapFactory.decodeStream(fiStream, null, options);
				if (degree > 0) {
					// 根据旋转角度，生成旋转矩阵
					Matrix matrix = new Matrix();
					matrix.postRotate(degree);

					// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
					bitmapOrg = Bitmap.createBitmap(bitmapOrg, 0, 0,
							bitmapOrg.getWidth(), bitmapOrg.getHeight(),
							matrix, true);
					int temp = toWidth;
					toWidth = toHeight;
					toHeight = temp;

				}
				if (bitmapOrg.getWidth() > toWidth
						|| bitmapOrg.getHeight() > toHeight) {
					bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, toWidth,
							toHeight, true);
				}
				Logger.d("[resizePic]调整后图片大小为width=" + bitmapOrg.getWidth()
						+ "; height=" + bitmapOrg.getHeight());
			} catch (OutOfMemoryError er) {
				Logger.e("[resizePic]内存溢出，将使用默认大小[640]来调整图片，调整后的宽高将<=640。");
				if (null != bitmapOrg) {
					bitmapOrg.recycle();
					bitmapOrg = null;
				}
				be = (width > height ? width : height) / 640;
				be = be % 10 != 0 ? be + 1 : be;
				options.inSampleSize = be;
				try {
					bitmapOrg = BitmapFactory.decodeStream(fiStream, null,
							options);
				} catch (Error er2) {
					Logger.e("[resizePic]内存溢出，原始图片 srcFilePath=" + srcFilePath
							+ "太大。");
					return null;
				}
			}
			return bitmapOrg;
		} catch (Exception e) {
			Logger.e("[resizePic]调整图片大小失败, srcFilePath=" + srcFilePath
					+ "; toSize=" + toSize, e);
			return null;
		}
	}
	
	public static boolean save2File(Bitmap bitmap, String filePath) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			Logger.e("--保存失败;", e);
			return false;
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (Exception e) {
				Logger.e("--关闭流失败;", e);
			}
		}
		return true;
	}

	public static boolean checkFileSize(String srcFilePath, int sizeOfKB) {
		File file = new File(srcFilePath);
		if ((!file.exists()) || (file.isDirectory())){
			return false;
		}
		if (file.length() > sizeOfKB * 1024){
			return false;
		}
		return true;
	}

	public static int[] getPicSize(String srcFilePath) {
		try {
			int[] size = new int[2];

			// 读取原始图片大小
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(srcFilePath, options);
			size[0] = options.outWidth;
			size[1] = options.outHeight;

			return size;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 读取图片的旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 将content://形式的uri转为实际文件路径 如果是file://形式的就直接返回uri的路径
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressLint("NewApi") 
	public static String uriToPath(Context context, Uri uri) {
		return uriToPath(context, uri, null);
//		String filePath = "";
//		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){//4.4及以上
//			String wholeID = DocumentsContract.getDocumentId(uri);
//			String id = wholeID.split(":")[1];
//			String[] column = { MediaStore.Images.Media.DATA };
//			String sel = MediaStore.Images.Media._ID + "=?";
//			Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
//					sel, new String[] { id }, null);
//			int columnIndex = cursor.getColumnIndex(column[0]);
//			if (cursor.moveToFirst()) {
//				filePath = cursor.getString(columnIndex);
//			}
//			cursor.close();
//		}else{//4.4以下
//			String[] projection = { MediaStore.Images.Media.DATA };
//			Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			cursor.moveToFirst();
//			filePath = cursor.getString(column_index);
//		}
//		return filePath;
	}
	
	public static String uriToPath(Context context, Uri uri, String uriString) {
		Cursor cursor = null;
		String strPath = null;
		
	    if (Build.VERSION.SDK_INT >= 19) {
	    	try {
				return PathUtil.getPath(context, uri);

			} catch (Exception e) {
				return null;
			}
		}
		
		try {
			if (uri.getScheme().equalsIgnoreCase("file")) {
				return uri.getPath();
			}
			cursor = context.getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			strPath = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)); // 图片文件路径
			return strPath;
		} catch (Exception e) {
			Logger.e("uriToPath()[将content://形式的uri转为实际文件路径]异常.URI:" + uri);
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
			String path = null;
			try {
				if (null != uriString) {
					path = Uri.decode(uriString).substring(7, uriString.length());
				}
			} catch (Exception e2) {
				Logger.e("uriToPath()[将content://形式的uri转为实际文件路径]异常.URI:" + uri);
			}
			return path;
		}
		
		
	}
	
	/**   
	* @date 2015-4-14 
	* @Description: android 4.4以上版本 媒体路径获得
	*/
	private static class PathUtil{
	    final static boolean isKitKat = Build.VERSION.SDK_INT >= 19;
	    
	    final static String sisDocumentUri ="isDocumentUri";
	    final static  String sgetDocumentId ="getDocumentId";
	    
	     static Method misDocumentUri ;
	     static Method mgetDocumentId ;
	     
	     static boolean isInited = false;

	     static Class<?> DocumentsContract;
		static{
			if (isKitKat) {
				try {
					DocumentsContract = Class.forName("android.provider.DocumentsContract");
					misDocumentUri = DocumentsContract.getMethod(sisDocumentUri,new Class[]{Context.class,Uri.class});
					mgetDocumentId = DocumentsContract.getMethod(sgetDocumentId, new Class[]{Uri.class});
					isInited = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		static String getPath(final Context context, final Uri uri) throws Exception {

			if (!isInited) {
				return "";
			}
			
			boolean isDoc =  (Boolean) misDocumentUri.invoke(DocumentsContract, context, uri);

			
		    if (isKitKat && isDoc) {
		        // ExternalStorageProvider
		        if (isExternalStorageDocument(uri)) {
		            final String docId =  (String) mgetDocumentId.invoke(DocumentsContract, uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            if ("primary".equalsIgnoreCase(type)) {
		                return Environment.getExternalStorageDirectory() + "/" + split[1];
		            }

		            // TODO handle non-primary volumes
		        }
		        // DownloadsProvider
		        else if (isDownloadsDocument(uri)) {
		            final String id =  (String) mgetDocumentId.invoke(DocumentsContract, uri);
		            final Uri contentUri = ContentUris.withAppendedId(
		                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

		            return getDataColumn(context, contentUri, null, null);
		        }
		        // MediaProvider
		        else if (isMediaDocument(uri)) {
		            final String docId =  (String) mgetDocumentId.invoke(DocumentsContract, uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            Uri contentUri = null;
		            if ("image".equals(type)) {
		                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		            } else if ("video".equals(type)) {
		                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		            } else if ("audio".equals(type)) {
		                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		            }

		            final String selection = "_id=?";
		            final String[] selectionArgs = new String[] {
		                    split[1]
		            };

		            return getDataColumn(context, contentUri, selection, selectionArgs);
		        }
		    }
		    // MediaStore (and general)
		    else if ("content".equalsIgnoreCase(uri.getScheme())) {

		        // Return the remote address
		        if (isGooglePhotosUri(uri))
		            return uri.getLastPathSegment();

		        return getDataColumn(context, uri, null, null);
		    }
		    // File
		    else if ("file".equalsIgnoreCase(uri.getScheme())) {
		        return uri.getPath();
		    }

		    return null;
		}

		/**
		 * Get the value of the data column for this Uri. This is useful for
		 * MediaStore Uris, and other file-based ContentProviders.
		 *
		 * @param context The context.
		 * @param uri The Uri to query.
		 * @param selection (Optional) Filter used in the query.
		 * @param selectionArgs (Optional) Selection arguments used in the query.
		 * @return The value of the _data column, which is typically a file path.
		 */
		private static String getDataColumn(Context context, Uri uri, String selection,
		        String[] selectionArgs) {

		    Cursor cursor = null;
		    final String column = "_data";
		    final String[] projection = {
		            column
		    };

		    try {
		        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
		                null);
		        if (cursor != null && cursor.moveToFirst()) {
		            final int index = cursor.getColumnIndexOrThrow(column);
		            return cursor.getString(index);
		        }
		    } finally {
		        if (cursor != null)
		            cursor.close();
		    }
		    return null;
		}


		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is ExternalStorageProvider.
		 */
		private static boolean isExternalStorageDocument(Uri uri) {
		    return "com.android.externalstorage.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is DownloadsProvider.
		 */
		private static boolean isDownloadsDocument(Uri uri) {
		    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is MediaProvider.
		 */
		private static boolean isMediaDocument(Uri uri) {
		    return "com.android.providers.media.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is Google Photos.
		 */
		private static boolean isGooglePhotosUri(Uri uri) {
		    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
		}
		
		
	}

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}
	
	public static void refreshMediaFile(Context context,String filePath){
		if (filePath == null || "".equals(filePath) || !filePath.startsWith(FilePath.SDCARD+"")) {
			Logger.w("[refreshMediaFile]非法的刷新文件：filePath=" + filePath);
			return;
		}
		
		try {
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
		} catch(Exception ex) {
			Logger.e("[refreshMediaFile]刷新多媒体文件失败, filePath=" + filePath, ex);
		}  
	}
	
	public static void viewPhoto(Context context, File file) {
		Logger.i("Open Multimedia file");
		try {
			// 调用系统程序打开文件.
			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			context.startActivity(intent);
		} catch (Exception ex) {
			Toast.makeText(context, "打开失败.", Toast.LENGTH_SHORT).show();
			Logger.e("[viewPhoto]" + file, ex);
		}
	}
	
	
	/**
     * 打开视频文件.
     * @param file 视频文件
     */
	public static void playVideo(Context context, File file) {
		Logger.i( "Open Multimedia file");
		try {
			// 调用系统程序打开文件.
			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "video/*");
			context.startActivity(intent);
		} catch (Exception ex) {
			Toast.makeText(context, "打开失败.", Toast.LENGTH_SHORT).show();
			Logger.e( "[playVideo]" + file, ex);
		}
	}
	
	public static boolean isPhoto(String file){
		if(null != file){
			return file.toLowerCase().endsWith(".jpg") ;
		}
		return false;
	}
	
	public static boolean isVideo(String file){
		if(null != file){
			return file.toLowerCase().endsWith(".mp4") ;
		}
		return false;
	}

}
