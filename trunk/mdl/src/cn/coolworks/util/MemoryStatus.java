package cn.coolworks.util;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.os.StatFs;

public class MemoryStatus {

	static final int ERROR = -1;

	static public boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	static public long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	static public long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	static public long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	static public long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	static public String formatSize(long size) {
		String suffix = null;
		long mod = 0;
		if (size >= 1000) {
			suffix = "K";
			size /= 1000;
			mod = size % 1000;
			if (size >= 1000) {
				suffix = "M";
				size /= 1000;
				mod = size % 1000;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}
		resultBuffer.append(".");
		resultBuffer.append(mod);

		if (suffix != null)
			resultBuffer.append(suffix);

		return resultBuffer.toString();
	}

	static long oldMemory;
	static long usedMemory;

	public static void initMemroy(Activity activity) {
		oldMemory = 0;
		usedMemory = 0;
		// final ActivityManager activityManager = (ActivityManager) activity
		// .getSystemService(Activity.ACTIVITY_SERVICE);
		// ActivityManager.MemoryInfo outInfo = new
		// ActivityManager.MemoryInfo();
		// activityManager.getMemoryInfo(outInfo);
		// oldMemory = outInfo.availMem;
		oldMemory = Runtime.getRuntime().freeMemory();
	}

	/**
	 * 
	 * @param activity
	 * @param tag
	 * @return
	 */
	public static String displayAvailMemory(String title) {
		StringBuffer sbf = new StringBuffer();
		// final ActivityManager activityManager = (ActivityManager) activity
		// .getSystemService(Activity.ACTIVITY_SERVICE);
		// ActivityManager.MemoryInfo outInfo = new
		// ActivityManager.MemoryInfo();
		// activityManager.getMemoryInfo(outInfo);
		sbf = sbf.append("========" + title + "===========");
		sbf.append("\n=====maxMemory:")
				.append(Runtime.getRuntime().maxMemory());
		sbf.append("\n=====totalMemory:").append(
				Runtime.getRuntime().totalMemory());
		sbf.append("\n=====oldMemory:").append(oldMemory);
		sbf.append("\n=====availMem:")
				.append(Runtime.getRuntime().freeMemory());
		long tmp = oldMemory - Runtime.getRuntime().freeMemory();
		sbf.append("\n=====add memory:").append(tmp);
		oldMemory = Runtime.getRuntime().freeMemory();
		usedMemory += tmp;
		sbf.append("\n=====usedMemory:").append(usedMemory);
		sbf.append("\n========================");
		//Debug.d(sbf.toString());
		return sbf.toString();
	}
}