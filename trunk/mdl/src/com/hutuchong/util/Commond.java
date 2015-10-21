package com.hutuchong.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.coolworks.entity.UserInfo;
import cn.coolworks.util.StringUtil;

import com.hutuchong.adapter.MenuAdapter;
import com.hutuchong.app_game.AppInfo;
import com.hutuchong.app_game.service.UpdateService;

public class Commond {
	public static final int PAGE_LOGIN = 2;
	public static final int PAGE_CATEGORY = 3;
	public static final int PAGE_CHANNEL = 4;
	public static final int PAGE_FEED = 5;
	public static final int PAGE_IMAGE = 6;
	public static final int PAGE_ITEM = 7;
	public static final int PAGE_FULL_IMAGE = 8;
	public static final int PAGE_ICON = 9;
	public static final int PAGE_FILE = 10;
	public static final int PAGE_UPLOAD_FOTO = 11;
	public static final int PAGE_UPDATE = 12;
	public static final int PAGE_SEND = 13;
	public static final int PAGE_FAV = 14;
	public static final int PAGE_VOTE = 15;
	public static final int PAGE_DOWN = 16;

	public static String INTENT_SEND_IMAGE = "image/*";
	public static String INTENT_SEND_TEXT = "text/plain";
	public static String INTENT_SEND_URL = "text/plain";
	public static String INTENT_SEND_AUDIO = "audio/*";

	public static int mBgColor = Color.TRANSPARENT;// 0xFFB6B9BE;//
	public static String copyright = null;
	public static boolean isHidePanel = false;
	//
	public static UserInfo userInfo;
	//
	public static String verName;
	public static String pkg;
	public static String imei;
	public static String imsi;
	public static String osver;
	public static int sW;
	public static int sH;

	//
	public static String mTempInput;
	//
	public static HashMap<String, AppInfo> appList = new HashMap<String, AppInfo>();
	//
	public static ArrayList<RSSItem> showItemListRef = null;

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
				// String osver = System.getProperty("os.version");
				osver = android.os.Build.VERSION.SDK;
				DisplayMetrics dm = new DisplayMetrics();
				a.getWindowManager().getDefaultDisplay().getMetrics(dm);
				sW = dm.widthPixels;
				sH = dm.heightPixels;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//
			// Intent i = new Intent(a, MessageService.class);
			// a.startService(i);
		}

	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static String getImei(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String appendUrl(String url) {
		String newurl = StringUtil.appendNameValue(url, "uid",
				Commond.userInfo.getUid());
		newurl = StringUtil.appendNameValue(newurl, "lang",
				StringUtil.getLocaleLanguage());
		newurl = StringUtil.appendNameValue(newurl, "width",
				String.valueOf(Commond.sW));
		newurl = StringUtil.appendNameValue(newurl, "height",
				String.valueOf(Commond.sH));
		newurl = StringUtil.appendNameValue(newurl, "ver", Commond.verName);
		newurl = StringUtil.appendNameValue(newurl, "imei", Commond.imei);
		newurl = StringUtil.appendNameValue(newurl, "imsi", Commond.imsi);
		newurl = StringUtil.appendNameValue(newurl, "osver", Commond.osver);
		newurl = StringUtil.appendNameValue(newurl, "pkg", Commond.pkg);
		newurl = StringUtil.appendNameValue(newurl, "email",
				Commond.userInfo.getEmail());
		newurl = StringUtil.appendNameValue(newurl, "skin",
				String.valueOf(Commond.userInfo.getSkin()));

		return newurl;
	}

	/**
	 * 
	 * @param pkg
	 * @return true:表示已经下载或已经安装 false:未安装
	 */
	public static boolean apkInstalled(Context context, String pkg,
			boolean needRun) {
		// 判断应用程序是否被安装了
		try {
			// 安装了则直接运行
			context.getPackageManager().getPackageInfo(pkg,
					PackageManager.SIGNATURE_MATCH);
			//
			if (needRun) {
				Intent intent = context.getPackageManager()
						.getLaunchIntentForPackage(pkg);
				context.startActivity(intent);
			}
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @param pkg
	 * @return
	 */
	public static boolean apkInstalled(Context context, String pkg) {
		if (appList.size() == 0) {
			return apkInstalled(context, pkg, false);
		}
		return appList.containsKey(pkg);
	}

	/**
	 * 
	 * @param c
	 * @param handler
	 */
	public static void initAppList(final Context c, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				List<PackageInfo> packages = c
						.getPackageManager()
						.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
				appList.clear();
				for (PackageInfo packageInfo : packages) {
					AppInfo tmpInfo = new AppInfo();
					tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
							c.getPackageManager()).toString();
					tmpInfo.packageName = packageInfo.packageName;
					tmpInfo.versionName = packageInfo.versionName;
					tmpInfo.versionCode = packageInfo.versionCode;
					tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(c
							.getPackageManager());
					// Only display the non-system app info
					if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						appList.put(tmpInfo.packageName, tmpInfo);
						pkg = tmpInfo.packageName;
						// getPackageStats(tmpInfo.packageName);
					}
				}
				if (handler != null) {
					Message msg = new Message();
					msg.what = 1;
					msg.setTarget(handler);
					msg.sendToTarget();
				}
			}
		}.start();
	}

	/**
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
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
			Log.e("MD5", e.getMessage());
			return null;
		}
	}

	/**
	 * 显示提示消息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastRoot = vi.inflate(R.layout.tip_toast, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setView(toastRoot);
		TextView tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
		if (tv != null)
			tv.setText(msg);
		// System.out.println("toastRoot.getWidth():" + toastRoot.getWidth());
		// System.out.println("toastRoot.getHeight():" + toastRoot.getHeight());
		// toast.setGravity(Gravity.LEFT | Gravity.TOP,
		// (sW - toastRoot.getWidth()) / 2,
		// (sH - toastRoot.getHeight()) / 2);
		toast.show();
	}

	/**
	 * 
	 * @param imageViewId
	 */
	public static void showProcessing(View view) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * @param imageViewId
	 */
	public static void hideProcessing(View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	public final static String MESSAGE_PROGRESS_RESULT = "progress";
	public final static String MESSAGE_PAGE_RESULT = "page";
	public final static String MESSAGE_DATA_RESULT = "result";
	public final static String MESSAGE_MESSAGE_RESULT = "message";
	public final static String MESSAGE_URL_RESULT = "url";
	public final static String MESSAGE_PAGEID_RESULT = "pageid";
	public final static String MESSAGE_ISCLEAR_RESULT = "isclear";

	public final static int MESSAGE_PROCESSINGFLAG = 0;
	public final static int MESSAGE_PROCESSEDFLAG = 1;

	/**
	 * 
	 * @param handler
	 */
	public static void sendProcessingMessage(Handler handler) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_DATA_RESULT, MESSAGE_PROCESSINGFLAG);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	/**
	 * 
	 * @param handler
	 */
	public static void sendProcessedMessage(Handler handler, String message,
			int pageid, String url, boolean isClear) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_DATA_RESULT, MESSAGE_PROCESSEDFLAG);
		b.putString(MESSAGE_MESSAGE_RESULT, message);
		b.putString(MESSAGE_URL_RESULT, url);
		b.putInt(MESSAGE_PAGEID_RESULT, pageid);
		b.putBoolean(MESSAGE_ISCLEAR_RESULT, isClear);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	/**
	 * 
	 * @param activity
	 * @param resId
	 * @return
	 */
	public static boolean hidePanel(final Activity activity, int resId) {
		View v = activity.findViewById(resId);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.INVISIBLE);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param e
	 * @param activity
	 * @return
	 */
	public static boolean hideOptionMenuPanel(MotionEvent e, Activity activity,
			int resGridId) {
		int x = (int) e.getRawX();
		int y = (int) e.getRawY();
		//
		View v = activity.findViewById(resGridId/* R.id.optionmenu_grid */);
		if (v.getVisibility() == View.VISIBLE) {
			int[] loc = new int[2];
			v.getLocationInWindow(loc);
			// v.getLocationOnScreen(loc);
			Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight() + loc[0],
					v.getBottom() + loc[1]);
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP) {
					Commond.showOptionMenu(activity, v);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param e
	 * @param activity
	 * @return
	 */
	public static boolean hideChannelSearchPanel(MotionEvent e,
			Activity activity) {
		int x = (int) e.getRawX();
		int y = (int) e.getRawY();
		View v = activity.findViewById(R.id.nav_panel);
		if (v == null)
			return false;
		Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		if (r.contains(x, y)) {
			return false;
		}
		//
		v = activity.findViewById(R.id.optionmenu_popup_grid);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			int[] loc = new int[2];
			v.getLocationInWindow(loc);
			// v.getLocationOnScreen(loc);
			r.set(v.getLeft() + loc[0], v.getTop() + loc[1], v.getRight()
					+ loc[0], v.getBottom() + loc[1]);
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP) {
					Commond.showOptionMenu(activity, false);
				}
				return true;
			}
		}
		//
		v = activity.findViewById(R.id.search_panel);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			int[] loc = new int[2];
			v.getLocationInWindow(loc);
			// v.getLocationOnScreen(loc);
			r.set(v.getLeft() + loc[0], v.getTop() + loc[1], v.getRight()
					+ loc[0], v.getBottom() + loc[1]);
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP) {
					Commond.showSearchInput(activity, false);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param e
	 * @param activity
	 * @return
	 */
	public static boolean hidePopPanel(MotionEvent e, Activity activity) {

		View v = activity.findViewById(R.id.nav_panel);
		Rect r = new Rect();
		int x = (int) e.getRawX();
		int y = (int) e.getRawY();
		if (v != null) {
			r.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
			if (r.contains(x, y)) {
				return false;
			}
		}
		v = activity.findViewById(R.id.popup_share_panel);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			View vv = activity.findViewById(R.id.popup_share);
			int[] location = new int[2];
			vv.getLocationOnScreen(location);
			r.set(location[0], location[1], location[0] + vv.getWidth(),
					location[1] + vv.getHeight());
			location = null;
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP)
					v.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		v = activity.findViewById(R.id.popup_comment_panel);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			View vv = activity.findViewById(R.id.popup_comment);
			r.set(vv.getLeft(), vv.getTop(), vv.getRight(), vv.getBottom());
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP)
					v.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		v = activity.findViewById(R.id.popup_more_panel);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			View vv = activity.findViewById(R.id.popup_more);
			r.set(vv.getLeft(), vv.getTop(), vv.getRight(), vv.getBottom());
			if (!r.contains(x, y)) {
				if (e.getAction() != MotionEvent.ACTION_UP)
					v.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param activity
	 * @param popids
	 * @return
	 */
	public static boolean isPopShow(Activity activity, int[] popids) {
		for (int i = 0; i < popids.length; i++) {
			View v = activity.findViewById(popids[i]);
			if (v != null) {
				if (v.getVisibility() == View.VISIBLE)
					return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public static void loadMenuShare(final BaseActivity activity,
			final String type) {
		//
		GridView lv = (GridView) activity.findViewById(R.id.popup_share);
		if (lv == null)
			return;
		// String[] titles = activity.getResources().getStringArray(
		// R.array.view_image_menu_popup_share);
		// int[] resids = { android.R.drawable.stat_sys_data_bluetooth,
		// android.R.drawable.sym_action_email,
		// android.R.drawable.sym_action_email };

		final Intent i = new Intent(Intent.ACTION_SEND);
		// File file = new File("/sdcard/MyScreen/20100528163140.jpg");
		// i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		i.setType(type/* "image/*" *//* "text/plain" */);
		// activity.startActivity(Intent.createChooser(i,
		// "Share via..."));
		PackageManager pm = activity.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(i,
				PackageManager.MATCH_DEFAULT_ONLY);
		String[] titles = new String[list.size()];
		Drawable[] resids = new Drawable[list.size()];
		final String[] cls = new String[list.size()];
		final String[] pkg = new String[list.size()];
		int index = 0;
		for (ResolveInfo ri : list) {
			titles[index] = ri.activityInfo.applicationInfo.loadLabel(pm)
					.toString();
			resids[index] = ri.activityInfo.applicationInfo.loadIcon(pm);
			cls[index] = ri.activityInfo.name;
			pkg[index] = ri.activityInfo.packageName;
			// Debug.d("ri.activityInfo.name:"
			// + ri.activityInfo.loadLabel(pm).toString());
			index++;
		}
		//
		lv.setAdapter(new MenuAdapter(activity, R.layout.menu_popup_item,
				resids, titles));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				View vShare = activity.findViewById(R.id.popup_share_panel);
				vShare.setVisibility(View.INVISIBLE);
				//
				i.setClassName(pkg[arg2], cls[arg2]);
				activity.onStartIntent(i);
			}
		});
	}

	/**
	 * 
	 */
	public static void loadMenuComment(final BaseActivity activity,
			final WebView webView) {
		//
		GridView lv = (GridView) activity.findViewById(R.id.popup_comment);
		if (lv == null)
			return;
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				View vComment = (View) activity
						.findViewById(R.id.popup_comment_panel);
				vComment.setVisibility(View.INVISIBLE);
				//
				TextView v = (TextView) arg1.findViewById(R.id.item_menu_text);
				String url = (String) v.getTag();
				activity.popupMenuRequest(v.getText().toString(), url, webView,
						Commond.PAGE_VOTE);
			}
		});
	}

	/**
	 * 
	 */
	public static void loadMenuMore(final BaseActivity activity,
			final WebView webView) {
		//
		GridView lv = (GridView) activity.findViewById(R.id.popup_more);
		if (lv == null)
			return;
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				View vMore = (View) activity
						.findViewById(R.id.popup_more_panel);
				vMore.setVisibility(View.INVISIBLE);
				//
				TextView v = (TextView) arg1.findViewById(R.id.item_menu_text);
				String url = (String) v.getTag();
				activity.popupMenuRequest(v.getText().toString(), url, webView,
						Commond.PAGE_FAV);
			}
		});
	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isSAapterEmpty(Activity activity, int resId) {
		GridView gv = (GridView) activity.findViewById(resId);
		if (gv == null || gv.getAdapter() == null
				|| gv.getAdapter().getCount() < 1)
			return true;
		return false;
	}

	/**
	 * 
	 */
	public static void loadMenuAndPop(final Activity activity, int shareId,
			final int popShareId, int commentId, final int popCommentId,
			int moreId, final int popMoreId) {
		// TODO Auto-generated method stub
		View vNavShare = activity.findViewById(shareId);
		if (vNavShare != null)
			vNavShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					loadMenuAndPopClick(activity, popShareId);
				}
			});
		View vNavComment = activity.findViewById(commentId);
		if (vNavComment != null)
			vNavComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					loadMenuAndPopClick(activity, popCommentId);
				}
			});
		View vNavMore = activity.findViewById(moreId);
		if (vNavMore != null)
			vNavMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					loadMenuAndPopClick(activity, popMoreId);
				}
			});
	}

	//
	private static void loadMenuAndPopClick(final Activity activity, int resId) {
		//
		View vShare = activity.findViewById(R.id.popup_share_panel);
		View vComment = activity.findViewById(R.id.popup_comment_panel);
		View vMore = activity.findViewById(R.id.popup_more_panel);
		//
		// vTitleMenu.setTag(Integer.valueOf(-1));
		Animation anim = AnimationUtils.loadAnimation(activity,
				R.anim.popup_alpha_scale);
		switch (resId) {
		case R.id.popup_share:
			if (isSAapterEmpty(activity, R.id.popup_share))
				return;
			if (vShare.getVisibility() == View.VISIBLE) {
				vShare.setVisibility(View.INVISIBLE);
				// vTitleMenu.setTag(Integer.valueOf(0));
			} else {
				vShare.setVisibility(View.VISIBLE);
				vShare.startAnimation(anim);
			}
			if (vComment != null)
				vComment.setVisibility(View.INVISIBLE);
			if (vMore != null)
				vMore.setVisibility(View.INVISIBLE);
			break;
		case R.id.popup_comment:
			if (isSAapterEmpty(activity, R.id.popup_comment))
				return;
			if (vComment.getVisibility() == View.VISIBLE) {
				vComment.setVisibility(View.INVISIBLE);
				// vTitleMenu.setTag(Integer.valueOf(0));
			} else {
				vComment.setVisibility(View.VISIBLE);
				vComment.startAnimation(anim);
			}
			vShare.setVisibility(View.INVISIBLE);
			vMore.setVisibility(View.INVISIBLE);
			break;
		case R.id.popup_more:
			if (isSAapterEmpty(activity, R.id.popup_more))
				return;
			if (vMore.getVisibility() == View.VISIBLE) {
				vMore.setVisibility(View.INVISIBLE);
				// vTitleMenu.setTag(Integer.valueOf(0));
			} else {
				vMore.setVisibility(View.VISIBLE);
				vMore.startAnimation(anim);
			}
			vComment.setVisibility(View.INVISIBLE);
			vShare.setVisibility(View.INVISIBLE);
			break;
		}
	}

	// /**
	// *
	// * @param activity
	// */
	//
	// public static void loadWebView(final Activity activity, int resid) {
	// //
	// WebView mWebView = (WebView) activity.findViewById(resid);
	// if (mWebView == null)
	// return;
	// // You have to use webView.setBackgroundColor(...) or it won't actually
	// // apply.
	// mWebView.setBackgroundColor(mBgColor);
	// // Looks like you were dead on with
	// // android:scrollbarStyle="insideOverlay", except that because of some
	// // bug it doesn't function right when defined in XML. It works right if
	// // you use:
	// mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	// WebSettings webSettings = mWebView.getSettings();
	// webSettings.setJavaScriptEnabled(true);
	// // mWebView.setBackgroundResource(R.drawable.list_normal);
	// // mWebView.setInitialScale((int) (userInfo.getZoomValue() * 100));
	// final TextView vText = (TextView) activity
	// .findViewById(R.id.progressText);
	// final View progressImage = activity.findViewById(R.id.progressImage);
	// mWebView.setWebChromeClient(new WebChromeClient() {
	// public void onProgressChanged(WebView view, int progress) {
	// //
	// // if (vText != null) {
	// // vText.setText(progress + "%");
	// // }
	// }
	//
	// @Override
	// public void onReceivedTitle(WebView view, String title) {
	// if (TextUtils.isEmpty(title))
	// return;
	// TextView titleView = (TextView) activity
	// .findViewById(R.id.title_text);
	// if (titleView != null)
	// titleView.setText(title);
	// }
	// });
	// mWebView.setPictureListener(new WebView.PictureListener() {
	// public void onNewPicture(WebView view, Picture picture) {
	// // view.pageDown(true);
	// // Debug.d("onNewPicture url:");
	// }
	// });
	// mWebView.setWebViewClient(new WebViewClient() {
	// @Override
	// public void onReceivedError(WebView view, int errorCode,
	// String description, String failingUrl) {
	// super.onReceivedError(view, errorCode, description, failingUrl);
	// }
	//
	// //
	// @Override
	// public void onPageFinished(WebView view, String url) {
	// super.onPageFinished(view, url);
	// if (progressImage != null)
	// progressImage.setVisibility(View.GONE);
	// }
	//
	// //
	// public void onPageStarted(final WebView view, String url,
	// Bitmap favicon) {
	// if (progressImage != null)
	// progressImage.setVisibility(View.VISIBLE);
	// // Animation anim = AnimationUtils.loadAnimation(activity,
	// // R.anim.tovisible_1s);
	// // view.startAnimation(anim);
	// }
	//
	// @Override
	// public boolean shouldOverrideUrlLoading(final WebView view,
	// final String url) {
	// // Debug.d("shouldOverrideUrlLoading url:" + url);
	// if (!preUrl(activity, url)) {
	// // Animation anim = AnimationUtils.loadAnimation(activity,
	// // R.anim.togone_1s);
	// // view.startAnimation(anim);
	// view.clearView();
	// view.loadUrl(url);
	// }
	// return true;
	// }
	// });
	// }

	/**
	 * 
	 * @param activity
	 */
	public static void loadWebView(final Activity activity, WebView webView,
			final TextView titleView, final String downloadPath,
			final View progress_bg, final View progress_fg,
			final ImageView vPrev, final ImageView vNext,final View progressImage) {
		//
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(Commond.userInfo.isBlockImg());
		// 设置允许Gears插件来实现网页中的Flash动画显示。
		//webSettings.setPluginsEnabled(true);
		// webSettings.setUseWideViewPort(true); // 让浏览器支持用户自定义view
		webSettings.setSupportZoom(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// LayoutAlgorithm是一个枚举，用来控制html的布局，总共有三种类型：
		// NORMAL：正常显示，没有渲染变化。
		// SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
		// NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		//
		// webSettings.setUseWideViewPort(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// mWebView.setScrollbarFadingEnabled(true);

		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// You have to use webView.setBackgroundColor(...) or it won't actually
		// apply.
		webView.setBackgroundColor(Commond.mBgColor);
		// Looks like you were dead on with
		// android:scrollbarStyle="insideOverlay", except that because of some
		// bug it doesn't function right when defined in XML. It works right if
		// you use:
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// mWebView.setBackgroundResource(R.drawable.list_normal);
		// Debug.d("Commond.userInfo.getZoomValue():"
		// + Commond.userInfo.getZoomValue());

		// float aa = Commond.userInfo.getZoomValue();
		// int scale = (int) (aa * 100);
		// float bb = webView.getScale();
		// Commond.showToast(activity, "aa:scale:bb=" + aa + ":" + scale + ":"
		// + bb);
		// webView.setInitialScale(scale);

		//
		// this.javaScript = new MyJavaScript();
		// mWebView.addJavascriptInterface(javaScript, "HTMLOUT");
		//
		if (progress_fg != null)
			webView.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					if (progress_fg != null) {
						int width = (progress * progress_bg.getWidth()) / 100;
						int l = 0;
						int t = progress_fg.getTop();
						int r = width;
						int b = progress_fg.getBottom();
						progress_fg.layout(l, t, r, b);
					}
					// Debug.d("progress:" + progress + "  width:" + width);
				}

				@Override
				public void onReceivedTitle(WebView view, String title) {
					if (titleView != null)
						titleView.setText(title);
				}
			});
		webView.setPictureListener(new WebView.PictureListener() {
			public void onNewPicture(WebView view, Picture picture) {
				// view.pageDown(true);
				// Debug.d("onNewPicture url:");
				// Debug.d("onNewPicture:");
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				// Debug.d("errorCode:" + errorCode);
			}

			//
			@Override
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				Commond.userInfo.setZoomValue(newScale);
				Commond.userInfo.saveData();
				// view.setInitialScale((int) (Commond.userInfo.getZoomValue() *
				// 100));
			}

			//
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// Debug.d("onPageFinished:" + url);

				view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				if (progress_bg != null)
					progress_bg.setVisibility(View.GONE);
				if (progressImage != null)
					progressImage.setVisibility(View.GONE);
				//
				if (vPrev != null) {
					if (view.canGoBack()) {
						vPrev.setEnabled(true);
						vPrev.setClickable(true);
						vPrev.setImageResource(R.drawable.nav_item_prev);
					} else {
						vPrev.setEnabled(false);
						vPrev.setClickable(false);
						vPrev.setImageResource(R.drawable.nav_item_prev_disable);
					}
				}
				//
				if (vNext != null) {
					if (view.canGoForward()) {
						vNext.setEnabled(true);
						vNext.setClickable(true);
						vNext.setImageResource(R.drawable.nav_item_next);
					} else {
						vNext.setEnabled(false);
						vNext.setClickable(false);
						vNext.setImageResource(R.drawable.nav_item_next_disable);
					}
				}
			}

			//
			@Override
			public void onPageStarted(final WebView view, String url,
					Bitmap favicon) {
				// Debug.d("onPageStarted:" + url);
				if (progress_bg != null) {
					int l = 0;
					int t = progress_fg.getTop();
					int r = 8;
					int b = progress_fg.getBottom();
					progress_fg.layout(l, t, r, b);
					progress_bg.setVisibility(View.VISIBLE);
				}
				if (progressImage != null)
					progressImage.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				// Debug.d("shouldOverrideUrlLoading url:" + url);
				if (!preUrl(activity, url)) {
					// Animation anim = AnimationUtils.loadAnimation(activity,
					// R.anim.togone_1s);
					// view.startAnimation(anim);
					view.clearView();
					view.loadUrl(url);
				}
				return true;
			}

		});
		//
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String arg0, String arg1, String arg2,
					String arg3, long arg4) {
				// TODO Auto-generated method stub
				// 请求下载
				if (!TextUtils.isEmpty(downloadPath))
					downloadApk(activity, null, downloadPath, arg0);
			}
		});
	}

	/**
	 * 请求下载
	 * 
	 * @param url
	 */
	public static boolean downloadApk(Context context, String title,
			String savePath, String url) {
		// 请求下载
		int pos = url.lastIndexOf('/');
		if (pos > 0 && pos < url.length() - 1) {
			String name = url.substring(pos + 1);
			if (name.contains(".apk")) {
				Intent i = new Intent(context, UpdateService.class);
				i.putExtra("url", url);
				if (TextUtils.isEmpty(title))
					i.putExtra("title", name);
				else
					i.putExtra("title", title);
				// String savePath = service.rootPath;
				if (savePath.endsWith("/") || savePath.endsWith("\\"))
					savePath += "//";
				savePath += name;
				i.putExtra("filepath", savePath);
				context.startService(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param webView
	 * @param data
	 */
	public static void loadHtml(WebView webView, String data) {
		if (!TextUtils.isEmpty(data)) {
			// Debug.d("data:" + data);
			// data = data.replaceFirst("background-color: #f0f0f0;",
			// "background-color: transparent;");
			// data = data.replaceFirst("background-color: #ffffff;",
			// "background-color: transparent;");
			// data = data.replaceFirst("color: #000000;", "color: #F0F0F0;");
			//
			// webView.setVisibility(View.GONE);
			// Debug.d("loadHtml data:" + data);
			if (data.startsWith("http://")) {
				webView.loadUrl(data);
			} else {
				String mimeType = "text/html";
				String encoding = "utf-8";
				webView.loadDataWithBaseURL(null, data, mimeType, encoding,
						null);
			}
		}
	}

	/**
	 * 
	 * @param activity
	 * @param url
	 * @return
	 */
	public static boolean preUrl(final Activity activity, String url) {
		String pre = "http://market:";
		String pre2 = "http://web:";
		String action = "android.intent.action.VIEW";
		if (url.startsWith(pre)) {
			Intent i = new Intent(action);
			i.setData(Uri.parse("market://search?q=pname:"
					+ url.substring(pre.length())));
			activity.startActivity(i);
			return true;
		} else if (url.startsWith(pre2)) {
			// ///////////////////////////
			// 请求下载
			String newurl = "http://" + url.substring(pre2.length());
			if (downloadApk(activity, null, getCachePath(activity, "ddgame"),
					newurl))
				return true;
			// ///////////////////////////
			Intent i = new Intent(action);
			i.setData(Uri.parse("http://" + url.substring(pre2.length())));
			activity.startActivity(i);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 * @param dir
	 * @return
	 */
	public static String getCachePath(Context context, String dir) {
		String path = null;
		String tmp = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(tmp)
				|| android.os.Environment.MEDIA_SHARED.equals(tmp)) {
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
	 */
	public static void setAbout(final Activity activity, int resPhoneBtn,
			final int resPhoneStr, int resSmsBtn) {
		//
		View vPhone = (View) activity.findViewById(resPhoneBtn);
		OnClickListener lPhone = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri data = Uri.parse("tel:" + activity.getText(resPhoneStr));
				Intent i = new Intent(Intent.ACTION_DIAL, data);
				activity.startActivity(i);
			}

		};
		vPhone.setOnClickListener(lPhone);
		//
		View vSms = (View) activity.findViewById(resSmsBtn);
		OnClickListener lSms = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri smsToUri = Uri.parse("smsto:"
						+ activity.getString(resPhoneStr));
				Intent i = new Intent(Intent.ACTION_SENDTO, smsToUri);
				i.putExtra("sms_body", "");
				activity.startActivity(i);
			}
		};
		vSms.setOnClickListener(lSms);
	}

	/**
	 * 
	 * @param activity
	 */
	public static void showOptionMenu(Activity activity, final View v) {
		if (v == null)
			return;
		if (v.getVisibility() == View.VISIBLE) {
			float fromYDelta = 0;
			float toYDelta = v.getHeight();
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					//
					v.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

			});
			v.startAnimation(anim);
		} else {
			v.setVisibility(View.VISIBLE);
			float fromYDelta = v.getHeight();
			float toYDelta = 0;
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {

				}

			});
			v.startAnimation(anim);
		}
	}

	/**
	 * 
	 * @param activity
	 */
	public static void showSearchInput(final Activity activity,
			final boolean isHideCategory) {
		final View v = activity.findViewById(R.id.search_panel);
		final ImageView vStatus = (ImageView) activity
				.findViewById(R.id.nav_search_icon);
		if (v.getVisibility() == View.VISIBLE) {
			// Animation anim = AnimationUtils.loadAnimation(activity,
			// R.anim.slide_bottom_to_top);
			float fromYDelta = 0;
			float toYDelta = v.getHeight();
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					//
					v.setVisibility(View.INVISIBLE);
					if (isHideCategory) {
						showOptionMenu(activity, false);
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

			});
			v.startAnimation(anim);
		} else /* if(!needHide) */{
			// Animation anim = AnimationUtils.loadAnimation(activity,
			// R.anim.slide_top_to_bottom);
			v.setVisibility(View.VISIBLE);
			float fromYDelta = v.getHeight();
			float toYDelta = 0;
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					v.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {
				}

			});
			v.startAnimation(anim);
		}
	}

	/**
	 * 
	 * @param activity
	 */
	public static void showOptionMenu(final Activity activity,
			final boolean isHideSearch) {
		if (isSAapterEmpty(activity, R.id.optionmenu_popup_grid))
			return;
		//
		final ImageView vStatus = (ImageView) activity
				.findViewById(R.id.nav_menu_icon);
		final GridView gv = (GridView) activity
				.findViewById(R.id.optionmenu_popup_grid);
		int y = gv.getHeight();
		if (gv.getVisibility() == View.VISIBLE) {
			// Animation anim = AnimationUtils.loadAnimation(activity,
			// R.anim.slide_top_to_bottom/* popup_alpha_scale */);
			float fromYDelta = 0;
			float toYDelta = y;
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					//
					if (vStatus != null)
						vStatus.setBackgroundResource(R.drawable.nav_menu_normal);
					//
					gv.setVisibility(View.INVISIBLE);
					//
					if (isHideSearch) {
						showSearchInput(activity, false);
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

			});
			gv.startAnimation(anim);
		} else /* if(!needHide) */{
			// Animation anim = AnimationUtils.loadAnimation(activity,
			// R.anim.slide_bottom_to_top/* popup_alpha_scale */);
			//
			gv.setVisibility(View.VISIBLE);
			float fromYDelta = y;
			float toYDelta = 0;
			TranslateAnimation anim = new TranslateAnimation(0, 0, fromYDelta,
					toYDelta);
			anim.setDuration(500);
			anim.setInterpolator(activity,
					android.R.anim.accelerate_interpolator);
			// anim.setZAdjustment(1);
			// anim.setFillBefore(true);
			// anim.setFillAfter(true);
			// anim.setFillEnabled(true);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					// View v = activity
					// .findViewById(R.id.popup_channellist_status);
					// v.setBackgroundResource(R.drawable.channel_down_bg);
					//
					gv.setVisibility(View.VISIBLE);
					//
					// if (needHide) {
					// setAlphaAnim(activity, gv);
					// }
					// gv.layout(0, 0, gv.getWidth(), gv.getHeight());
					if (vStatus != null)
						vStatus.setBackgroundResource(R.drawable.nav_menu_selected);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

			});
			gv.startAnimation(anim);

		}
	}

	/**
	 * 
	 * @param activity
	 * @param v
	 */
	private static void setAlphaAnim(final Activity activity, final View v) {
		//
		Animation animEnd = AnimationUtils.loadAnimation(activity,
				R.anim.popup_alpha_in);
		long startTimeMillis = AnimationUtils.currentAnimationTimeMillis() + 3000;
		animEnd.setStartTime(startTimeMillis);
		animEnd.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				// v.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
			}

		});
		v.setAnimation(animEnd);
	}

	/**
	 * 
	 */
	public static void loadScroller(final BaseActivity activity, final int width) {
		//
		// ImageButton btnScroller = (ImageButton) activity
		// .findViewById(R.id.scroller_button);
		// btnScroller.setOnTouchListener(new OnTouchListener() {
		// int[] temp = new int[] { 0, 0 };
		//
		// public boolean onTouch(final View v, MotionEvent event) {
		// int eventaction = event.getAction();
		// int x = (int) event.getRawX();
		// switch (eventaction) {
		// case MotionEvent.ACTION_DOWN: // touch down so check if the
		// temp[0] = x;
		// break;
		// case MotionEvent.ACTION_MOVE: // touch drag with the ball
		// int left = v.getLeft() + (x - temp[0]);
		// temp[0] = x;
		// v.layout(left, v.getTop(), left + v.getWidth(), v.getTop()
		// + v.getHeight());
		// // v.invalidate();
		// break;
		//
		// case MotionEvent.ACTION_UP:
		// int xx = (width - v.getWidth()) / 2;
		// int offset = xx - v.getLeft();
		// if (Math.abs(offset) > 5) {
		// //
		// if (offset > 5) {
		// activity.onScroller(true);
		// } else if (offset < -5) {
		// activity.onScroller(false);
		// }
		// //
		// TranslateAnimation anim = new TranslateAnimation(0,
		// offset, 0, 0);
		// anim.setDuration(500);
		// anim.setInterpolator(activity,
		// android.R.anim.accelerate_interpolator);
		// anim.setZAdjustment(1);
		// anim.setFillBefore(true);
		// // anim.setFillAfter(true);
		// anim.setFillEnabled(true);
		// anim.setAnimationListener(new AnimationListener() {
		// @Override
		// public void onAnimationEnd(Animation arg0) {
		// // TODO Auto-generated method stub
		// int xx = (width - v.getWidth()) / 2;
		// v.layout(xx, v.getTop(), xx + v.getWidth(), v
		// .getBottom());
		// // v.invalidate();
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onAnimationStart(Animation arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// v.startAnimation(anim);
		// } else {
		// v.layout(xx, v.getTop(), xx + v.getWidth(), v.getTop()
		// + v.getHeight());
		// // v.invalidate();
		// }
		// break;
		// }
		//
		// return false;
		// }
		//
		// });
	}

	/**
	 * 
	 * @param actity
	 * @param lIndex
	 * @param rIndex
	 */
	public static void setDotView(Activity actity, int lIndex, int rIndex) {
		//
		// DotsView ldv = (DotsView)
		// actity.findViewById(R.id.scroller_left_dot);
		// ldv.setCount(lIndex);
		// ldv.invalidate();
		// DotsView rdv = (DotsView)
		// actity.findViewById(R.id.scroller_right_dot);
		// //
		// rdv.setCount(rIndex);
	}

	/**
	 * 
	 * @param filename
	 * @param width
	 * @param height
	 * @param exact
	 * @return
	 */
	public static Bitmap loadResizedBitmap(String filename, int width,
			int height, boolean exact) {
		if (TextUtils.isEmpty(filename))
			return null;
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		if (options.outHeight > 0 && options.outWidth > 0) {
			options.inJustDecodeBounds = false;
			options.inSampleSize = 2;
			while (options.outWidth / options.inSampleSize >= width
					&& options.outHeight / options.inSampleSize >= height) {
				options.inSampleSize++;
			}
			options.inSampleSize--;
			try {
				bitmap = BitmapFactory.decodeFile(filename, options);
				if (bitmap != null && exact) {
					bitmap = Bitmap.createScaledBitmap(bitmap, width, height,
							false);
				}
				// Debug.d("bitmap.width:" + bitmap.getWidth());
				// Debug.d("bitmap.height:" + bitmap.getHeight());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean loginOrReg(Activity activity) {

		if (TextUtils.isEmpty(Commond.userInfo.getEmail())) {
			Commond.showToast(activity,
					activity.getString(R.string.login_reg_tip));
			Intent ii = new Intent();
			ii.setClassName(activity.getPackageName(),
					"com.hutuchong.jpmm.UserLoginActivity");
			activity.startActivity(ii);
			return false;
		}
		return true;
	}

	/**
	 * 创建对话框
	 * 
	 * @param context
	 * @param layoutID
	 * @return
	 */
	static public Dialog createDialog(Context context, int layoutID) {
		Dialog dialog = new Dialog(context, R.style.Theme_DuomeDialog);
		dialog.setContentView(layoutID);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
		lp.dimAmount = 0.7f;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		return dialog;
	}

	/**
	 * 
	 * @param activity
	 * @param vid
	 * @param assetsFileName
	 */
	public static Bitmap setImgeViewSrc(Activity activity, int vid,
			String assetsFileName) {
		Bitmap bitmap = null;
		ImageView v = (ImageView) activity.findViewById(vid);
		if (v != null) {
			try {
				// int densityDpi =
				// activity.getResources().getDisplayMetrics().densityDpi;
				// if (densityDpi == DisplayMetrics.DENSITY_HIGH)
				// assetsFileName += "_h";
				// assetsFileName += ".png";
				InputStream is = activity.getResources().getAssets()
						.open(assetsFileName);
				bitmap = BitmapFactory.decodeStream(is);
				v.setImageBitmap(bitmap);
				is.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
}
