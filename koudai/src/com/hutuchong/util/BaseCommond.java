package com.hutuchong.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import cn.duome.fotoshare.entity.UserInfo;
import cn.duome.fotoshare.utils.StringUtil;

public class BaseCommond {
	public static UserInfo userInfo;
	public static String verName;
	public static String pkg;
	public static String imei;
	public static String imsi;
	public static int osver;
	public static String phone;
	public static String sim;
	public static String model;
	public static int sW;
	public static int sH;
	static WeakHashMap<String, WeakReference<Bitmap>> bmpCacheMap = new WeakHashMap<String, WeakReference<Bitmap>>();

	/**
	 * 
	 * @param a
	 */
	public static void getUserInfo(Activity a) {
		// 加载用户信息
		if (userInfo == null) {
			userInfo = new UserInfo(a);
			if (TextUtils.isEmpty(userInfo.getUid())) {
				TelephonyManager tm = (TelephonyManager) a
						.getSystemService(Context.TELEPHONY_SERVICE);

				userInfo.setUid(tm.getDeviceId() + "_"
						+ Long.toString(System.currentTimeMillis()));
			}
			userInfo.saveData();
		}
	}

	/**
	 * 
	 * @param a
	 */
	public static void getDeviceInfo(Activity a) {
		if (TextUtils.isEmpty(verName)) {
			PackageManager pm = a.getPackageManager();
			try {
				PackageInfo info = pm.getPackageInfo(a.getPackageName(), 0);
				pkg = info.applicationInfo.packageName;
				verName = info.versionName;
				TelephonyManager tm = (TelephonyManager) a
						.getSystemService(Context.TELEPHONY_SERVICE);
				imei = tm.getDeviceId();
				imsi = tm.getSubscriberId();
				phone = tm.getLine1Number();
				sim = tm.getSimSerialNumber();
				osver = android.os.Build.VERSION.SDK_INT;
				model = android.os.Build.MODEL;
				System.out.println("model:" + model);
				DisplayMetrics dm = new DisplayMetrics();
				a.getWindowManager().getDefaultDisplay().getMetrics(dm);

				sW = dm.widthPixels;
				sH = dm.heightPixels;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param context
	 * @param dir
	 * @return
	 */
	public static String getCachePath(Context context, String dir) {
		String path = null;
		if (TextUtils.isEmpty(dir))
			dir = context.getPackageName();
		String tmp = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(tmp)/*
															 * || android.os.
															 * Environment
															 * .MEDIA_SHARED
															 * .equals(tmp)
															 */) {
			path = "/sdcard/" + dir + "/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			file = null;
			//
		} else {
			path = context.getCacheDir().getAbsolutePath() + File.separator;
		}
		return path;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String appendUrl(String url) {
		String newurl = StringUtil.appendNameValue(url, "uid",
				userInfo.getUid());
		newurl = StringUtil.appendNameValue(newurl, "lang",
				StringUtil.getLocaleLanguage());
		newurl = StringUtil
				.appendNameValue(newurl, "width", String.valueOf(sW));
		newurl = StringUtil.appendNameValue(newurl, "height",
				String.valueOf(sH));
		newurl = StringUtil.appendNameValue(newurl, "ver", verName);
		newurl = StringUtil.appendNameValue(newurl, "imei", imei);
		newurl = StringUtil.appendNameValue(newurl, "imsi", imsi);
		newurl = StringUtil.appendNameValue(newurl, "osver",
				String.valueOf(osver));
		newurl = StringUtil.appendNameValue(newurl, "pkg", pkg);
		newurl = StringUtil.appendNameValue(newurl, "model",
				URLEncoder.encode(model));
		newurl = StringUtil.appendNameValue(newurl, "email",
				URLEncoder.encode(userInfo.getEmail()));
		newurl = StringUtil.appendNameValue(newurl, "phone", phone == null ? ""
				: URLEncoder.encode(phone));
		newurl = StringUtil.appendNameValue(newurl, "sim", sim == null ? ""
				: URLEncoder.encode(sim));

		return newurl;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String getMd5Hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);
			//
			// while (md5.length() < 32)
			// md5 = "0" + md5;
			return md5;
		} catch (NoSuchAlgorithmException e) {
			// Log.e("MD5", e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void sendProcessingMessage(Handler handler) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt(BaseContant.MESSAGE_DATA_RESULT,
				BaseContant.MESSAGE_PROCESSINGFLAG);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	/**
	 * 
	 * @param handler
	 */
	public static void sendProcessedMessage(Handler handler, int pageid,
			String url, boolean isClear, boolean isRefresh, boolean isRemote) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt(BaseContant.MESSAGE_DATA_RESULT,
				BaseContant.MESSAGE_PROCESSEDFLAG);
		b.putString(BaseContant.MESSAGE_URL_RESULT, url);
		b.putInt(BaseContant.MESSAGE_PAGEID_RESULT, pageid);
		b.putBoolean(BaseContant.MESSAGE_ISCLEAR_RESULT, isClear);
		b.putBoolean(BaseContant.MESSAGE_ISREFRESH_RESULT, isRefresh);
		b.putBoolean(BaseContant.MESSAGE_ISREMOTE_RESULT, isRemote);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	/**
	 * 清理早于指定的时间的问题
	 * 
	 * @param count
	 */
	public static void clearSavedFile(String rootPath, int day) {
		File dir = new File(rootPath);
		String[] filenames = dir.list();
		// System.out.println("clearSavedFile 111:");
		long m1 = day * 86400000;// 24 * 60 * 60 * 1000;
		long m2 = System.currentTimeMillis();
		long m = m2 - m1;
		// System.out.println("filenames.length:" + filenames.length);
		for (String filename : filenames) {
			// System.out.println("filename:" + filename);
			String[] tmp = filename.split("_");
			if (tmp.length == 2 && TextUtils.isDigitsOnly(tmp[1])) {
				long m3 = Long.parseLong(tmp[1]);
				// Debug.d("m3:" + m3);
				if (m > m3) {
					// System.out.println("del:" + filename);
					File file = new File(rootPath + filename);
					// file.deleteOnExit();
					file.delete();
					file = null;
				}
			} else if (!filename.toLowerCase().endsWith(".apk")
					&& !filename.toLowerCase().endsWith(".ap0")) {
				File file = new File(rootPath + filename);
				// file.deleteOnExit();
				file.delete();
			}
		}
	}

	/**
	 * 删除下载在手机中（非SDCARD）的安装包
	 */
	public static void clearDownedTempFile(Context context) {
		File file = context.getFilesDir();
		for (String filename : file.list()) {
			if (filename.startsWith("sharetemp_")) {
				// System.out.println("clearDownedTempFile:" + filename);
				context.deleteFile(filename);
			}
		}
	}

}
