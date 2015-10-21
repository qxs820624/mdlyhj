package com.hutuchong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSParser;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import cn.coolworks.util.Debug;
import cn.coolworks.util.StringUtil;

import com.hutuchong.http.HotInterface;
import com.jw.http.ProgressListener;

public abstract class BaseService extends Service implements ServiceHandle {

	public String categoryUrl;
	public String favUrl;
	public String searchUrl;
	public String rootPath;
	public String adCategory = "20";
	private final IBinder binder = new MyBinder();
	public HotInterface inter;
	public String cacheDir = "ddgame";
	// private HashMap<String, RSSChannel> channels = new HashMap<String,
	// RSSChannel>(
	// 0);
	private HashMap<String, String> nameToTime = new HashMap<String, String>();
	WeakHashMap<String, WeakReference<Bitmap>> drawableMap = new WeakHashMap<String, WeakReference<Bitmap>>();

	public abstract void initService(boolean isProxy);

	/**
	 * 
	 * @param categoryUrl
	 * @param rootPath
	 */
	public void initService(String categoryUrl, String rootpath, boolean isProxy) {
		// Debug.d("BaseService initService isProxy=" + isProxy);
		if (this.inter != null)
			return;
		this.categoryUrl = categoryUrl;
		this.inter = new HotInterface(isProxy);
		cacheDir = rootpath;
		this.rootPath = Commond.getCachePath(this, cacheDir);
		//
		if (nameToTime.size() == 0) {
			File root = new File(rootPath);
			String[] filenames = root.list(null);
			if (filenames == null) {
				return;
			}
			for (String filename : filenames) {
				String[] tmp1 = filename.split("_");
				String time = null;
				if (tmp1.length >= 2)
					time = tmp1[1];
				nameToTime.put(rootPath + tmp1[0], time);
			}
		}
	}

	/**
	 * 
	 * @param categoryUrl
	 * @param favUrl
	 * @param searchUrl
	 */
	public void initUrl(String categoryUrl, String favUrl, String searchUrl) {
		this.categoryUrl = categoryUrl;
		this.favUrl = favUrl;
		this.searchUrl = searchUrl;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class MyBinder extends Binder {
		public BaseService getService() {
			return BaseService.this;
		}

	}

	public void onCreate() {
		// Debug.d("BaseService onCreate()...");
		super.onCreate();
	}

	/**
	 * 
	 */
	@Override
	public void onDestroy() {
		// Debug.d("BaseService onDestory()...");
		if (inter != null)
			inter.release();
		//
		super.onDestroy();
	}

	/**
	 * 
	 */
	private void clearCacheFile() {
		File root = this.getCacheDir();
		File[] files = root.listFiles();
		if (files == null)
			return;
		for (File file : files) {
			file.delete();
		}
		//
		clearWebviewCacheFile();
	}

	/**
	 * 
	 */
	private void clearWebviewCacheFile() {
		File root = this.getCacheDir();
		String path = root.getAbsolutePath() + "/webviewCache";
		// Debug.d("webviewCache:" + path);
		File webCache = new File(path);
		File[] files = webCache.listFiles();
		if (files == null)
			return;
		for (File file : files) {
			file.delete();
		}
		webCache = null;
	}

	/**
	 * 
	 * @param count
	 */
	public void clearSavedFile(int day) {
		Object[] keys = nameToTime.keySet().toArray();
		int size = keys.length;
		long m1 = day * 86400000;// 24 * 60 * 60 * 1000;
		long m2 = System.currentTimeMillis();
		long m = m2 - m1;
		for (int i = 0; i < size; i++) {
			String key = (String) keys[i];
			String value = nameToTime.get(key);
			// Debug.d("value:" + value);
			String filename = key;
			if (!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)) {
				long m3 = Long.parseLong(value);
				// Debug.d("m3:" + m3);
				if (m < m3) {
					// Debug.d("m < m3:");
					continue;
				}
				//
				filename = key + "_" + value;
			} else {
				// Debug.d("continue:");
				continue;
			}
			Debug.d("del:" + filename);
			File file = new File(filename);
			// file.deleteOnExit();
			file.delete();
			file = null;
			nameToTime.remove(key);
		}
	}

	/**
	 * 
	 * @param url
	 * @param data
	 */
	private String loadChannelData(String url, byte[] data) {
		String result = url;
		RSSChannel channel = null;

		try {
			RSSHandler hand = new RSSHandler();
			RSSParser.parseXml(data, hand, false);
			channel = hand.getRSSChannel();
			hand = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (channel == null)
			channel = new RSSChannel();
		// 保证跳转是同一个页面
		// System.out.println("channel.getLink() url:"+url);
		// System.out.println("channel.getLink():"+channel.getLink());
		if (TextUtils.isEmpty(channel.getLink())) {
			channel.setLink(url);
		}
		result = channel.getLink();
		data = null;
		// }
		return result;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String requestChannel(String url) {
		String prex = "file:///android_asset/";
		if (url.startsWith(prex)) {
			return url;
		}
		String filename = getExitFileName(url);
		if (!TextUtils.isEmpty(filename))
			return url;
		if (inter == null)
			return url;
		System.out.println("requestChannel url:" + url);
		byte[] data = inter.read(null, url);
		if (data != null && data.length > 0) {
			saveTempFile(url, data);
		}
		return url;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public RSSChannel getChannel(String url) {
		RSSChannel channel = null;
		String prex = "file:///android_asset/";
		if (url.startsWith(prex)) {
			try {
				AssetManager am = this.getAssets();
				InputStream is = am.open(url.substring(prex.length()));
				RSSHandler hand = new RSSHandler();
				RSSParser.parseXml(is, hand, false);
				channel = hand.getRSSChannel();
				if (channel != null && TextUtils.isEmpty(channel.getLink())) {
					channel.setLink(url);
				}
				hand = null;
				is.close();
				is = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String filename = getExitFileName(url);
			if (TextUtils.isEmpty(filename))
				return null;
			try {
				RSSHandler hand = new RSSHandler();
				RSSParser.parseXmlFile(filename, hand, false);
				channel = hand.getRSSChannel();
				if (channel != null && TextUtils.isEmpty(channel.getLink())) {
					channel.setLink(url);
				}
				hand = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				delFile(url);// 删除异常的文件
				e.printStackTrace();
			}
		}
		return channel;
	}

	/**
	 * 
	 * @param obj
	 * @param l
	 * @param url
	 * @param update
	 *            true:不检查本地是否存在，始终向服务器请求； false:首先检查本地是否存在，如果存在则直接使用本地文件
	 */
	public void requestFile(Object obj, ProgressListener l, String url) {
		String filename = getExitFileName(url);
		// Debug.d("requestFile url:" + filename);
		if (!TextUtils.isEmpty(filename))
			return;
		if (inter == null)
			return;
		String time = Long.toString(System.currentTimeMillis());
		filename = rootPath + Commond.getMd5Hash(url);
		byte[] data = inter.image(filename + "_" + time, obj, l, url);
		if (data != null && data.length > 1) {
			try {
				//
				nameToTime.put(filename, time);
				// Debug.d("filename:" + filename);
				// 网络请求返回的时候就进行保存了
				// saveTo(this, url, data);
				data = null;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public byte[] requestWebData(ProgressListener listener, String url,
			String data, String enctype, String referer) {
		if (!TextUtils.isEmpty(referer)) {
			url = url + "[referer]" + URLEncoder.encode(referer) + "[/referer]";
		}
		if ("multipart/form-data".equals(enctype)) {
			return inter.multipartForm(listener, url, data);
		} else {
			return inter.read(listener, url, data);
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public byte[] getCacheData(String url) {
		// Debug.d("getCacheData:" + url);
		byte[] data = null;
		String filename = getExitFileName(url);
		if (TextUtils.isEmpty(filename))
			return null;
		File file = new File(filename);
		if (!file.exists())
			return null;
		try {
			FileInputStream fis = new FileInputStream(file);
			int tlen = fis.available();
			data = new byte[tlen];
			fis.read(data, 0, data.length);
			fis.close();
			fis = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getCacheBitmap(String url) {
		// Debug.d("getCacheBitmap:" + url);
		String filename = getExitFileName(url);
		// Debug.d("getCacheBitmap filename:" + filename);
		if (TextUtils.isEmpty(filename))
			return null;
		try {
			InputStream stream = new FileInputStream(filename);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			// @@1.5
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			Bitmap bmp = BitmapFactory.decodeStream(stream, null, opts);
			stream.close();
			stream = null;
			return bmp;
		} catch (OutOfMemoryError error) {
			drawableMap.clear();
			error.printStackTrace();
			Debug.d(error.getMessage());
			// Commond.showToast(this, "显示图片失败1!");
		} catch (Exception ex) {
			ex.printStackTrace();
			Debug.d(ex.getMessage());
			// Commond.showToast(this, "显示图片失败!");
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap fetchBitmap(String url) {
		WeakReference<Bitmap> drawableRef = drawableMap.get(url);
		if (drawableRef != null) {
			Bitmap drawable = drawableRef.get();
			if (drawable != null && !drawable.isRecycled())
				return drawable;
			drawableMap.remove(url);
		}
		Bitmap bitmap = getCacheBitmap(url);
		drawableRef = new WeakReference<Bitmap>(bitmap);
		drawableMap.put(url, drawableRef);
		return drawableRef.get();
	}

	/**
	 * 
	 * @param url
	 * @param path
	 * @return
	 */
	private String saveTempFile(String url, byte[] data) {
		// 保存xml文件
		String time = "0";
		// String time = Long.toString(System.currentTimeMillis());
		String filename = rootPath + Commond.getMd5Hash(url);
		nameToTime.put(filename, time);

		// 创建文件
		File file = new File(filename + "_" + time);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			fos = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param path
	 * @return
	 */
	public String saveFile(String url, String path, String ex) {
		byte[] data = getCacheData(url);
		if (data == null)
			return null;
		String filename = Commond.getMd5Hash(url) + ex;
		if (!path.endsWith("/"))
			path += "/";
		filename = path + filename;
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				//
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close();
				fos = null;
				data = null;
				file = null;
				return filename;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String getExitFileName(String url) {
		if (TextUtils.isEmpty(url))
			return null;
		// Debug.d("getFileName:" + url);
		String filename = rootPath + Commond.getMd5Hash(url);
		if (!this.nameToTime.containsKey(filename)) {
			// Debug.d("not local file:" + filename);
			return null;
		} else {
			String time = this.nameToTime.get(filename);
			if (!TextUtils.isEmpty(time))
				filename += "_" + time;
			// Debug.d("local file:" + filename);
			return filename;
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String getTempFileName(String url) {
		if (TextUtils.isEmpty(url))
			return null;
		return rootPath + Commond.getMd5Hash(url) + "_0";
	}

	/**
	 * 
	 * @param url
	 */
	public boolean delFile(String url) {
		String filename = getExitFileName(url);
		if (!TextUtils.isEmpty(filename)) {
			//
			String[] tmp1 = filename.split("_");
			if (tmp1.length >= 2) {
				this.nameToTime.remove(tmp1[0]);
			}
			//
			File file = new File(filename);
			if (file.exists())
				file.delete();
			file = null;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public long getFileSize(String url) {
		// Debug.d("getFileSize:" + url);
		String filename = getExitFileName(url);
		if (TextUtils.isEmpty(filename))
			return 0;
		File f = new File(filename);
		long result = 0;
		if (f.exists()) {
			result = f.length();
		}
		f = null;
		return result;
	}

	/**
	 * 
	 * @param ret
	 * @return
	 */
	public String[] getAPKInfo(String ret) {
		String[] values = new String[3];
		try {
			JSONObject json = new JSONObject(ret);
			values[0] = json.getString("pkg");
			values[1] = json.getString("size");
			values[2] = json.getString("apk");
			return values;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public void requestHideAd(final ProgressListener l, final String url) {
		Thread test = new Thread(new Runnable() {
			public void run() {
				inter.image(l, url);
			}
		});
		test.start();
	}

	/**
	 * 
	 * @param uid
	 * @param ver
	 * @param width
	 * @param height
	 * @return
	 */
	public void requestLogin(ProgressListener l, String url, String adstr) {
		String newurl = StringUtil.appendNameValue(url, "adstr", adstr);
		byte[] data = inter.read(l, newurl);
		//
		if (data == null || data.length == 0) {
			data = null;
			return;
		} else {
			// loadChannelData(url, data);
			saveTempFile(url, data);
		}
	}

	/**
	 * 
	 * @param uid
	 * @param imei
	 * @param msg
	 * @return
	 */
	public String uploadImage(Object obj, ProgressListener l, String title,
			String filename) {
		byte[] temp = inter.postPhoto(obj, l, ContantValue.uploadFotoUrl,
				"title=" + title, filename);
		if (temp == null || temp.length == 0) {
			temp = null;
			return null;
		} else {
			String ret = new String(temp);
			// Debug.d("ret:" + ret);
			try {
				JSONObject json = new JSONObject(ret);
				String retmsg = json.getString("retmsg");
				ret = null;
				temp = null;
				return retmsg;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}
