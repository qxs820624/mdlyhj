package com.hutuchong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import cn.duome.fotoshare.utils.Debug;
import cn.duome.fotoshare.utils.StringUtil;

import com.jw.http.HotInterface;
import com.jw.http.ProgressListener;

public class BaseContent {

	Context mContext;
	public String rootPath;
	public HotInterface inter;
	static BaseContent instance;

	/**
	 * 
	 * @param categoryUrl
	 * @param rootPath
	 */
	public static BaseContent getInstance(Context context) {
		if (instance == null)
			instance = new BaseContent();
		instance.mContext = context;
		if (instance.inter == null)
			instance.inter = new HotInterface(BaseCommond.userInfo.isProxy());
		if (instance.rootPath == null)
			instance.rootPath = BaseCommond.getCachePath(context,
			/* context.getPackageName() */"borpor");
		return instance;
	}

	/**
	 * 
	 */
	public void clearCacheFile() {
		File root = mContext.getCacheDir();
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
		File root = mContext.getCacheDir();
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
	 * @param url
	 * @param refresh
	 * @return false:表示从本地获取数据的
	 */
	public boolean requestChannel(String url, boolean refresh, String lasttime) {
		String newUrl = url;
		if (!TextUtils.isEmpty(lasttime))
			StringUtil.appendNameValue(url, "pb", lasttime);
		// System.out.println("requestChannel url 2:" + url);
		if (refresh) {
			byte[] data = inter.read(null, newUrl);
			if (data != null && data.length > 0) {
				saveFile(url, data);
			}
		} else {
			String prex = "file:///android_asset/";
			if (url.startsWith(prex)) {
				return false;
			}
			String filename = getExitFileName(url);
			if (!TextUtils.isEmpty(filename))
				return false;
			if (inter != null) {
				byte[] data = inter.read(null, newUrl);
				if (data != null && data.length > 0) {
					saveFile(url, data);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param url
	 * @param refresh
	 * @return false:表示从本地获取数据的
	 */
	public boolean requestChannel(String url, String referer, String enctype,
			ProgressListener listener, String data, boolean isRefresh) {
		//
		if (!isRefresh) {
			String filename = getExitFileName(url);
			if (!TextUtils.isEmpty(filename))
				return false;
		}
		// System.out.println("requestChannel 1:" + url);
		if (!TextUtils.isEmpty(referer)) {
			url = url + "[referer]" + URLEncoder.encode(referer) + "[/referer]";
		}
		byte[] result = null;
		if ("multipart/form-data".equals(enctype)) {
			result = inter.multipartForm(listener, url, data);
		} else {
			result = inter.read(listener, url, data);
		}
		if (result != null && result.length > 0) {
			// try {
			System.out.println("result:" + new String(result));
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			saveFile(url, result);
		}
		return true;
	}

	/**
	 * 
	 * @param uid
	 * @param imei
	 * @param msg
	 * @return
	 */
	public void uploadFile(Object obj, String url, ProgressListener l,
			String params, String filename) {
		byte[] temp = inter.postPhoto(obj, l, url, params, filename);
		if (temp == null || temp.length == 0) {
			temp = null;
			delFile(url);
		} else {
			saveFile(url, temp);
			String ret = new String(temp);
			// System.out.println("ret:" + ret);
			// try {
			// JSONObject json = new JSONObject(ret);
			// String retmsg = json.getString("retmsg");
			// ret = null;
			// temp = null;
			// return retmsg;
			// } catch (Exception e) {
			// e.printStackTrace();
			// return null;
			// }
		}
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
				AssetManager am = mContext.getAssets();
				InputStream is = am.open(url.substring(prex.length()));
				RSSHandler hand = new RSSHandler();
				RSSParser.parseXml(is, hand, false);
				channel = hand.getRSSChannel();
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
				hand = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// try {
				// FileInputStream fis = new FileInputStream(filename);
				// byte[] buffer = new byte[fis.available()];
				// fis.read(buffer);
				// System.out.println("delfile:" + new String(buffer));
				// } catch (Exception e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// System.out.println("rss ex:" + filename);
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
	public boolean requestFile(Object obj, ProgressListener l, String url) {
		String filename = getExitFileName(url);
		// Debug.d("requestFile url:" + filename);
		if (!TextUtils.isEmpty(filename))
			return true;
		if (inter == null)
			return false;
		String time = Long.toString(System.currentTimeMillis());
		filename = rootPath + BaseCommond.getMd5Hash(url);
		byte[] data = inter.image(filename + "_" + time, obj, l, url);
		if (data != null && data.length > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param url
	 */
	public void clearCacheBitmap(String url) {
		WeakReference<Bitmap> bitmapRef = BaseCommond.bmpCacheMap.get(url);
		if (bitmapRef != null) {
			Bitmap bitmap = bitmapRef.get();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		BaseCommond.bmpCacheMap.remove(url);
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getCacheBitmap(String url, int width, int height) {
		Bitmap bmp = null;
		WeakReference<Bitmap> drawableRef = BaseCommond.bmpCacheMap.get(url);
		if (drawableRef != null) {
			bmp = drawableRef.get();
			if (bmp == null || bmp.isRecycled()) {
				BaseCommond.bmpCacheMap.remove(url);
				bmp = null;
			}
		}
		if (bmp == null || bmp.getWidth() != width || bmp.getHeight() != height) {
			bmp = getBitmap(url);
			// bmp = getBitmap(url, width, height);
			// sdk2.2之后才有
			// bmp = ThumbnailUtils.extractThumbnail(bmp, width, height);
			if (bmp != null)
				bmp = resizeImage(bmp, width, height);
		}
		if (bmp != null) {
			drawableRef = new WeakReference<Bitmap>(bmp);
			BaseCommond.bmpCacheMap.put(url, drawableRef);
		}
		return bmp;
	}

	/**
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w == 0 ? width : w;
		int newHeight = h == 0 ? height : h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		//
		float scale = scaleWidth;
		if (scaleWidth > scaleHeight) {
			scale = scaleHeight;
		}

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scale, scale);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return resizedBitmap;

	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmap(String url) {
		String filename = getExitFileName(url);
		// System.out.println("getCacheBitmap filename:" + filename);
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
			//
			return bmp;
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			Debug.d(error.getMessage());
			// Commond.showToast(this, "显示图片失败1!");
		} catch (Exception ex) {
			delFile(url);
			ex.printStackTrace();
			Debug.d(ex.getMessage());
			// Commond.showToast(this, "显示图片失败!");
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param path
	 * @return
	 */
	private String saveFile(String url, byte[] data) {
		String filename = rootPath + BaseCommond.getMd5Hash(url);
		// 创建文件
		// filename += "_0";
		File file = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			fos = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filename;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	private String getExitFileName(String url) {
		if (TextUtils.isEmpty(url))
			return null;
		// Debug.d("getFileName:" + url);
		// url = Commond.appendUrl(url);
		if (url.startsWith("http://")) {
			String filename1 = BaseCommond.getMd5Hash(url);
			File dir = new File(rootPath);
			String[] filenames = dir.list();
			if (filenames == null)
				return null;
			for (String filename : filenames) {
				if (filename.startsWith(filename1)) {
					return rootPath + filename;
				}
			}
			return null;
		} else
			return url;
	}

	/**
	 * 
	 * @param url
	 */
	public boolean delFile(String url) {
		String filename = getExitFileName(url);
		System.out.println("delFile filename:" + filename);
		if (!TextUtils.isEmpty(filename)) {
			//
			File file = new File(filename);
			if (file.exists())
				file.delete();
			file = null;
			return true;
		}
		return false;
	}
}
