package com.angeeks.market;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import mobi.domore.mcdonalds.R;
import com.hutuchong.app_game.adapter.ManageItemAdapter;
import com.hutuchong.app_game.util.MarketCommond;
import com.hutuchong.util.Commond;

public class ManageActivity extends Activity {
	Context mContext;
	// Use ArrayList to store the installed non-system apps
	ListView app_listView;
	ManageItemAdapter appAdapter;
	String pkg;
	ProgressBar progressImage;

	// ListView app_listView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.app_market_adminlist);

		// Populate data to listView
		app_listView = (ListView) findViewById(R.id.listview);
		app_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String key = (String) Commond.appList.keySet().toArray()[arg2];
				String pkg = Commond.appList.get(key).packageName;
				gotoAppInfoActivity(mContext, pkg);
			}
		});
		//
		progressImage = (ProgressBar) findViewById(R.id.progressImage);
		if (Commond.appList.size() == 0) {
			progressImage.setVisibility(View.VISIBLE);
			Commond.initAppList(mContext, mHandler);
		} else {
			loadListView();
		}
		//
		MarketCommond.loadHomeTab(this);
		MarketCommond.loadAdminTab(this, false);
		MarketCommond.loadSearchTab(this, true);
		MarketCommond.switchNavBar(this, -1);
	}

	/**
	 * 
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				loadListView();
			}
		}
	};

	private void loadListView() {
		progressImage.setVisibility(View.GONE);
		if (appAdapter == null)
			appAdapter = new ManageItemAdapter(mContext,
					R.layout.app_market_adminlist_row, Commond.appList,
					delListener);

		app_listView.setAdapter(appAdapter);
	}

	/**
	 * 
	 */
	OnClickListener delListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pkg = (String) v.getTag();
			Uri packageURI = Uri.parse("package:" + pkg);
			Intent i = new Intent(Intent.ACTION_DELETE, packageURI);
			startActivityForResult(i, 10);
		}

	};

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("requestCode:" + requestCode);
		System.out.println("resultCode:" + resultCode);
		// 不管是卸载了还是未卸载返回的resultCode都为0（RESULT_CANCELED）
		if (!Commond.apkInstalled(this, pkg, false)) {
			Commond.appList.remove(pkg);
			// app_listView.setAdapter(appAdapter);
			appAdapter.notifyDataSetChanged();
		}
	}

	private void gotoAppInfoActivity(Context context, String packageName) {
		String SCHEME = "package";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
		 */
		String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
		 */
		String APP_PKG_NAME_22 = "pkg";
		String ACTION_22 = "android.settings.APPLICATION_DETAILS_SETTINGS";
		/**
		 * InstalledAppDetails所在包名
		 */
		String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
		/**
		 * InstalledAppDetails类名
		 */
		String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
		/**
		 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
		 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
		 */
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(ACTION_22/*
									 * Settings.ACTION_APPLICATION_DETAILS_SETTINGS
									 */);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param packageName
	 */
	public void getPackageStats(final String packageName) {
		try {
			// 获取setting包的的Context
			Context mmsCtx = createPackageContext("com.android.settings",
					Context.CONTEXT_INCLUDE_CODE
							| Context.CONTEXT_IGNORE_SECURITY);
			// 使用setting的classloader加载com.android.settings.ManageApplications类
			Class<?> maClass = Class.forName(
					"com.android.settings.ManageApplications", true,
					mmsCtx.getClassLoader());
			// 创建它的一个对象
			Object maObject = maClass.newInstance();

			/*
			 * 将私有域mPm赋值。因为mPm在SizeObserver的invokeGetSize中用到了，
			 * 却因为没有执行onCreate而没有初始化，所以要在此处初始化。
			 */
			Field f_mPm = maClass.getDeclaredField("mPm");
			f_mPm.setAccessible(true);
			f_mPm.set(maObject, mmsCtx.getPackageManager());

			/*
			 * 给mHandler赋值为重新定义的Handler，以便接收SizeObserver的
			 * onGetStatsCompleted回调方法中dispatch的消息，从中取PackageStats对象。
			 */
			Field f_mHandler = maClass.getDeclaredField("mHandler");
			f_mHandler.setAccessible(true);
			f_mHandler.set(maObject, new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						// 此处获取到PackageStats对象
						PackageStats ps = (PackageStats) msg.getData()
								.getParcelable("ApplicationPackageStats");
						Log.d("", "" + ps.codeSize);
						Commond.appList.get(packageName).size = ps.codeSize;
					}
				}
			});

			// 加载内部类SizeObserver
			Class<?> sizeObserverClass = Class.forName(
					"com.android.settings.ManageApplications$SizeObserver",
					true, mmsCtx.getClassLoader());
			Constructor sizeObserverConstructor = sizeObserverClass
					.getDeclaredConstructors()[0];
			sizeObserverConstructor.setAccessible(true);
			/*
			 * 创建SizeObserver对象，两个参数，第一个是外部类的对象，
			 * 也就是ManageApplications对象，第二个是msgId，也就是 分发消息的id，跟Handler接收的msgId一样。
			 */
			Object soObject = sizeObserverConstructor.newInstance(maObject, 1);
			// 执行invokeGetSize方法
			sizeObserverClass.getMethod("invokeGetSize", String.class,
					CountDownLatch.class).invoke(soObject, packageName,
					new CountDownLatch(1));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}