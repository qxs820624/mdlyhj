package cn.duome.fotoshare.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.Ablum;
import cn.duome.fotoshare.entity.Item;

public class FotoCommond {
	public static Bitmap mBg;
	public static ArrayList<Ablum> albums = new ArrayList<Ablum>();
	public static String rootPath = "/sdcard/DCIM/";
	public static String shareName = "口袋图片分享";
	public static String sharePath = rootPath + shareName + "/";
	public static String thumbPath = rootPath + ".thumb/";
	public static int sW;
	public static int sH;
	public static RSSChannel mChannel;

	/**
	 * 设置是否全屏显示
	 */
	public static void setFullScreen(Activity activity, boolean isFullScreen) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		sW = dm.widthPixels;
		sH = dm.heightPixels;
		if (isFullScreen) {
			// 全屏设置
			// activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// activity.getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_FULLSCREEN);
			// activity.getWindow().clearFlags(
			// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	/**
	 * 
	 * @param activity
	 * @param preName
	 * @return
	 */
	public static String getAdapterName(Activity activity, String preName) {
		String name = preName;
		//
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		int sW = dm.widthPixels;
		// int sH = dm.heightPixels;
		if (sW <= 320) {
			name += "_320.png";
		} else if (sW <= 480) {
			if (sW - 320 < 480 - sW) {
				name += "_320.png";
			} else
				name += "_480.png";
		} else if (sW <= 600) {
			if (sW - 480 < 600 - sW) {
				name += "_480.png";
			} else
				name += "_600.png";
		} else
			name += "_600.png";
		return name;
	}

	/**
	 * 
	 * @param activity
	 * @param vid
	 * @param assetsFileName
	 * @return
	 */
	public static void setBgBitmap(Activity activity, int skinIndex) {
		try {
			String skinName = "skin1";
			switch (skinIndex) {
			case 0:
				skinName = "skin1";
				break;
			case 1:
				skinName = "skin2";
				break;
			case 2:
				skinName = "skin3";
				break;
			case 3:
				skinName = "skin4";
				break;
			}
			//
			skinName = getAdapterName(activity, skinName);
			if (mBg != null) {
				// mBg.recycle();
				mBg = null;
			}
			InputStream is = activity.getResources().getAssets().open(skinName);
			mBg = BitmapFactory.decodeStream(is);
			is.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param activity
	 * @param vid
	 * @param assetsFileName
	 * @return
	 */
	public static void setImgeViewSrc(Activity activity, int vid, Bitmap bitmap) {
		ImageView v = (ImageView) activity.findViewById(vid);
		if (v != null) {
			v.setImageBitmap(bitmap);
		}
	}

	/**
	 * 
	 * @param activity
	 * @param vid
	 * @param assetsFileName
	 * @return
	 */
	public static void setImgeViewSrc(Activity activity, int vid,
			String assetsFileName) {
		ImageView v = (ImageView) activity.findViewById(vid);
		if (v != null) {
			InputStream is;
			try {
				is = activity.getResources().getAssets().open(assetsFileName);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				v.setImageBitmap(bitmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public static void loadAblum() {
		System.out.println("loadAblum........");
		if (albums.size() > 0)
			return;
		// albums.clear();
		// //////////////////////
		File shareFile = new File(sharePath);
		if (!shareFile.exists()) {
			shareFile.mkdirs();
		}
		// //////////////////
		File thumb = new File(thumbPath);
		if (!thumb.exists()) {
			if (thumb.mkdirs()) {
				File nomedia = new File(thumbPath + ".nomedia");
				try {
					nomedia.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// ///////////////////////////////
		File file = new File(rootPath);
		File[] files = file.listFiles();
		if (files == null)
			return;
		for (File f : files) {
			if (f.getName().startsWith("."))
				continue;
			if (f.isDirectory()) {
				File nomedia = new File(file.getAbsolutePath() + "/.nomedia");
				if (nomedia.exists())
					continue;
			}
			Ablum ablum = new Ablum();
			loadItem(f, ablum);
			if (ablum.getItems().size() > 0) {
				if (f.isFile())
					ablum.getItem().setName(
							f.getName().substring(0, f.getName().length() - 4));
				else
					ablum.getItem().setName(f.getName());
				ablum.getItem().setPath(ablum.getItems().get(0).getPath());
				ablum.getItem().setThumb(ablum.getItems().get(0).getThumb());
				albums.add(ablum);
			}
		}
	}

	/**
	 * 
	 * @param path
	 * @param filename
	 * @return
	 */
	public static int[] refreshCameraAlbum(String filename) {
		int[] index = { -1, -1 };
		Ablum shareAblum = null;
		for (Ablum ablum : albums) {
			if (shareName.equals(ablum.getItem().getName())) {
				shareAblum = ablum;
				index[0]++;
				break;
			}
		}
		if (shareAblum == null) {
			shareAblum = new Ablum();
			albums.add(shareAblum);
			index[0] = albums.size() - 1;
			index[1] = 0;
		}
		File file = new File(sharePath);
		loadItem(file, shareAblum);
		//
		if (shareAblum.items.size() == 0) {
			albums.remove(shareAblum);
			return null;
		} else {
			shareAblum.getItem().setName(shareName);
			shareAblum.getItem()
					.setPath(shareAblum.getItems().get(0).getPath());
			shareAblum.getItem().setThumb(
					shareAblum.getItems().get(0).getThumb());
		}
		// 找到图片文件对应的位置索引
		for (Item item : shareAblum.items) {
			if (filename.equals(item.getPath())) {
				index[1]++;
				break;
			}
		}
		return index;
	}

	/**
	 * 
	 * @param ablum
	 */
	private static void loadItem(File file, Ablum ablum) {
		ablum.getItems().clear();
		File[] files = file.listFiles();
		if (files == null)
			return;
		File nomedia = new File(file.getAbsolutePath() + "/.nomedia");
		if (nomedia.exists())
			return;
		for (File f : files) {
			//
			if (!f.isHidden() && !f.getName().startsWith(".")) {
				if (f.isFile()) {
					String name = f.getName();
					if (name.endsWith(".jpg") || name.endsWith(".png")) {
						Item item = new Item();
						item.setName(name.substring(0, name.length() - 4));
						System.out.println("name:" + f.getAbsolutePath());
						item.setPath(f.getAbsolutePath());

						ablum.getItems().add(item);
						String thumbname = thumbPath + FotoCommond.md5(name);
						File thumbfile = new File(thumbname);
						if (!thumbfile.exists()) {
							// 创建缩略图
							createThumb(f.getAbsolutePath(), thumbname);
						}
						item.setThumb(thumbname);
					}
				} else if (f.isDirectory()) {
					File nextPath = new File(f.getPath());
					loadItem(nextPath, ablum);
				}
			}
		}
	}

	/**
	 * 
	 * @param filename
	 * @param thumbFileName
	 */
	public static void createThumb(String filename, String thumbFileName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		options.outHeight = 200;
		Bitmap bitmap = BitmapFactory.decodeFile(filename, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;
		// 计算缩放比
		int be = options.outHeight / 20;
		if (be % 10 != 0)
			be += 10;
		be = be / 10;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap = BitmapFactory.decodeFile(filename, options);
		if (bitmap == null)
			return;
		//
		File file = new File(thumbFileName);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param bitmap
	 * @param filename
	 * @return
	 */
	public static boolean saveBitmapToFile(Bitmap bitmap, String filename) {
		File file = new File(filename);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
				out.flush();
				out.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
		if (string == null || string.trim().length() < 1) {
			return null;
		}
		try {
			return getMD5(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	private static String getMD5(byte[] source) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	public static void hideViewAnim(Activity activity, final View v) {
		if (v == null)
			return;
		Animation anim;
		if (v.getVisibility() == View.GONE) {
			anim = AnimationUtils.loadAnimation(activity, R.anim.tovisible_1s);
		} else {
			anim = AnimationUtils.loadAnimation(activity, R.anim.togone_1s);
		}
		//
		AnimationListener listener = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (v.getVisibility() != View.VISIBLE) {
					v.setVisibility(View.VISIBLE);
					// 开始定时器检测显示了多长时间
				} else {
					// 结束定时器
					v.setVisibility(View.GONE);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				if (v.getVisibility() == View.GONE) {
					v.setVisibility(View.INVISIBLE);
				}
			}
		};
		anim.setAnimationListener(listener);
		v.startAnimation(anim);
	}

	/**
	 * 缩放、翻转和旋转图片
	 * 
	 * @param bmpOrg
	 * @param rotate
	 * @return
	 */
	public static Bitmap gerZoomRotateBitmap(Bitmap bmpOrg, int rotate) {
		// 获取图片的原始的大小
		int width = bmpOrg.getWidth();
		int height = bmpOrg.getHeight();

		int newWidth = 300;
		int newheight = 300;
		// 定义缩放的高和宽的比例
		float sw = ((float) newWidth) / width;
		float sh = ((float) newheight) / height;
		// 创建操作图片的用的Matrix对象
		android.graphics.Matrix matrix = new android.graphics.Matrix();
		// 缩放翻转图片的动作
		// sw sh的绝对值为绽放宽高的比例，sw为负数表示X方向翻转，sh为负数表示Y方向翻转
		matrix.postScale(1, 1);
		// 旋转30*
		matrix.postRotate(rotate);
		// 创建一个新的图片
		android.graphics.Bitmap resizeBitmap = android.graphics.Bitmap
				.createBitmap(bmpOrg, 0, 0, width, height, matrix, true);
		return resizeBitmap;
	}
}