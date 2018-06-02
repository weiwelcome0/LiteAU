/**
 * 
 */
package com.couragechallenge.liteau.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

public class AppTool {

	/**
	 * 隐藏手机键盘
	 */
	public static void hideInputKeyboard(Activity activity) {
		try {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			Logger.e("--hideInputKeyboard隐藏输入法异常!", e);
		}
	}

	/**
	 * 显示手机键盘
	 */
	public static void showInputKeyboard(Context context) {
		try {
			InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			Logger.e("--showInputKeyboard显示输入法异常!", e);
		}
	}

	/**
	 * 获取IMEI号
	 */
	public static String getIMEI(Context context) {
		if(null == context) {
			return "";
		}
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	/**
	 * 获取SIM卡串号
	 */
	public static String getSIM(Context context) {
		if(null == context) {
			return "";
		}
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = telephonyManager.getSimSerialNumber();
		return sim==null?"":sim;
	}

	/**
	 * 获取版本号
	 */
	public static int getAPPVersionCode(Context context) {
		int currentVersionCode = -1;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			currentVersionCode = info.versionCode; // 版本号
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("--getAPPVersionCode错误;",e);
		}
		return currentVersionCode;
	}

	/**
	 * 获取版本名称
	 */
	public static String getAPPVersionName(Context context) {
		String currentVersionName = "1.0";
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			currentVersionName = info.versionName; // 版本号
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("--getAPPVersionCode错误;",e);
		}
		return currentVersionName;
	}
}
