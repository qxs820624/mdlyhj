package com.duom.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.example.mymusic.MusicChartListActivity;
import com.example.uitl.GetPublicKey;
import com.jingxunnet.cmusic.R;

public class Task1Activity extends TabActivity implements OnClickListener {
	/**
	 * TabHost控件
	 */
	private TabHost tabHost;
	/**
	 * TabWidget控件
	 */
	public static Button bnt, create, login, bianji, tianjialuxian;
	private boolean hasMeasured = false;
	private LinearLayout layout_left, layout_right;
	static LinearLayout layuser;
	private LinearLayout shoucang, wodexiangce, wodepinglun, wodexianlu,
			changenumb, exit;
	static boolean info = false;
	static boolean iswait;
	private ImageView qidong, header;
	public ImageView showhead;
	public static TextView tv0, tv, tv3, tv2, name, sex;
	public static TextView subject;
	private int layout_left_width, layout_right_width = 0;
	/** 每次自动展开/收缩的范围 */
	private int MAX_WIDTH = 0;
	/** 每次自动展开/收缩的速度 */
	private final static int SPEED = 5;
	private int staus = 0;
	static boolean fromshouye;
	URL myurl;
	static Bitmap bit;
	static boolean registinfo = false;
	static int uid, mark;
	static String nick, header1, sex1, humor;
	// 用来控制启动页面是否显示
	static boolean qidongyemian = true;
	//
	static boolean headerfresh = false;
	//
	private SharedPreferences prefs;
	// 用handler传值 ，设置启动页面的显示和消失
	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 当传过的值为2时，启动页面消失。
			case 2:
				qidong.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private UIHandler mUIHandler = new UIHandler();
	private ProgressDialog dialog;
	private long requestTime;
	private ProgressDialog mProgress = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_activity);
		Log.d("key", GetPublicKey.getSignInfo(this));
		dialog = ProgressDialog.show(Task1Activity.this, null, "请稍候……", true,
				false);
		requestTime = System.currentTimeMillis();
		new Thread(new LoadData()).start();
		// 左侧推出菜单，里面的控件
		fromshouye = true;
		// 显示标题
		subject = (TextView) findViewById(R.id.subject);
		// 用户名
		name = (TextView) findViewById(R.id.name);
		// 性别
		sex = (TextView) findViewById(R.id.sex);
		// 登录
		login = (Button) findViewById(R.id.login);
		// 启动页面
		qidong = (ImageView) findViewById(R.id.qidong);
		// 头像
		header = (ImageView) findViewById(R.id.head);
		// 小头像
		showhead = (ImageView) findViewById(R.id.showhead);
		// 登录后显示的，用户信息和头像的布局
		layuser = (LinearLayout) findViewById(R.id.layuser);
		// 当没有登录的时候，用户信息设置为隐藏
		if (info == false) {
			layuser.setVisibility(View.GONE);
		}
		// 当登录的时候，用户信息设置为可见
		else if (info == true) {
			layuser.setVisibility(View.VISIBLE);
		}
		// 设置为没有登录 ；
		info = false;
		// 进入软件， 运行启动页面，运行3000毫秒，然后隐藏
		if (qidongyemian == true) {
			Message msg = new Message();
			msg.what = 2;
			// 设置发送的值，和间隔启动的时间
			splashHandler.sendMessageDelayed(msg, 1500);
		} else {
			qidong.setVisibility(View.GONE);
			qidongyemian = true;
		}
		readDate();
		if (uid != 0) {
			if (sex1 != null) {
				sex.setText(sex1.toString());
				if (sex1.equalsIgnoreCase("0")) {
					sex.setText("男");
				} else if (sex1.equalsIgnoreCase("1")) {
					sex.setText("女");
				} else {
					sex.setText("无");
				}
				name.setText(nick.toString());
				showhead.setVisibility(View.VISIBLE);
				login.setVisibility(View.GONE);
				layuser.setVisibility(View.VISIBLE);
				Bitmap bitmap = null;
				if (header1 != null) {
					bitmap = makeBitmap(header1);
					BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
					header.setBackgroundDrawable(bitmapDrawable);
					Info.bitinfo = bitmap;
					showhead.setImageBitmap(bitmap);
				} else {
					name.setText("无");
					header.setBackgroundResource(R.drawable.defaultgravater);
					showhead.setImageResource(R.drawable.defaultgravater);
					showhead.setScaleType(ScaleType.CENTER);
				}
			}
		}
		// 获取tabhost实例
		tabHost = getTabHost();
		// 设置背景
		tabHost.setBackgroundResource(R.drawable.fristpagebackground);

		// outTab1选项卡 ，添加一个页面卡
		Intent musicIntent = new Intent(this, MusicChartListActivity.class);
		// 点击跳转到MostNew 页面
		TabHost.TabSpec musicTabSpec = tabHost.newTabSpec("tab0");
		musicTabSpec.setIndicator(getString(R.string.out_tab0), null);
		musicTabSpec.setContent(musicIntent);
		// startActivity(musicIntent);
		// 自定义下面的小标签，背景，内容
		tv0 = new TextView(this);
		tv0.setBackgroundResource(R.drawable.nav_btn_music_background);
		// 将tv控件添加到tabHost中
		tabHost.addTab(musicTabSpec.setIndicator(tv0));

		// outTab1选项卡 ，添加一个页面卡
		Intent oneIntent = new Intent();
		// 点击跳转到MostNew 页面
		oneIntent.setClass(this, MostNew.class);
		TabHost.TabSpec oneTabSpec = tabHost.newTabSpec("tab1");
		oneTabSpec.setIndicator(getString(R.string.out_tab1), null);
		oneTabSpec.setContent(oneIntent);
		// 自定义下面的小标签，背景，内容
		tv = new TextView(this);
		tv.setBackgroundResource(R.drawable.zhuye1);
		// 将tv控件添加到tabHost中
		tabHost.addTab(oneTabSpec.setIndicator(tv));

		// outTab2选项卡
		Intent twoIntent = new Intent();
		twoIntent.setClass(this, MostHost.class);
		TabHost.TabSpec twoTabSpec = tabHost.newTabSpec("tab2");
		twoTabSpec.setIndicator(getString(R.string.out_tab2), null);
		twoTabSpec.setContent(twoIntent);
		tv2 = new TextView(this);
		tv2.setBackgroundResource(R.drawable.daohang1);

		tabHost.addTab(twoTabSpec.setIndicator(tv2));

		// outTab3选项卡
		Intent threeIntent = new Intent();
		threeIntent.setClass(this, Area.class);
		TabHost.TabSpec threeTabSpec = tabHost.newTabSpec("tab3");
		threeTabSpec.setIndicator(getString(R.string.out_tab3), null);
		threeTabSpec.setContent(threeIntent);
		tv3 = new TextView(this);
		tv3.setBackgroundResource(R.drawable.xiangce1);
		tabHost.addTab(threeTabSpec.setIndicator(tv3));

		// 初始化控件
		layout_left = (LinearLayout) findViewById(R.id.layout_left);
		layout_right = (LinearLayout) findViewById(R.id.layout_right);
		shoucang = (LinearLayout) findViewById(R.id.wodeshoucang);
		wodexiangce = (LinearLayout) findViewById(R.id.pinglunwode);
		wodepinglun = (LinearLayout) findViewById(R.id.wodepinglun);
		wodexianlu = (LinearLayout) findViewById(R.id.wodexianlu);
		changenumb = (LinearLayout) findViewById(R.id.changenumb);
		exit = (LinearLayout) findViewById(R.id.tuichu);
		tianjialuxian = MostNew.tianjialuxian;
		bnt = (Button) findViewById(R.id.bnt);
		bianji = (Button) findViewById(R.id.bianji);
		// 菜单按钮监听，点击，右侧推出菜单页面
		bnt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取右边页面的布局
				LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) layout_right
						.getLayoutParams();
				if (lp.leftMargin >= MAX_WIDTH) {
					new AsynMove().execute(new Integer[] { -SPEED });// 负数展开
					Log.e("lp.left", lp.leftMargin + "");
				} else if (lp.leftMargin >= 0) {
					new AsynMove().execute(new Integer[] { SPEED });// 正数收缩
					Log.e("lp.left", lp.leftMargin + "");
				}
			}
		});
		// 登录按钮监听
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent login = new Intent();
				// 跳转登录页面
				login.setClass(Task1Activity.this, Login.class);
				startActivity(login);
			}
		});
		// 菜单里面的编辑按钮监听
		bianji.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent login = new Intent();
				// 跳转个人信息页面
				login.setClass(Task1Activity.this, Info.class);
				startActivity(login);
				// finish();
			}
		});

		ViewTreeObserver observer = layout_right.getViewTreeObserver();
		// 为了取得控件的宽，用来确定，菜单推出的距离和位置
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					layout_right_width = layout_right.getMeasuredWidth();
					layout_left_width = layout_left.getMeasuredWidth();
					MAX_WIDTH = layout_left_width - layout_right_width - 80;
					hasMeasured = true;
				}
				return true;
			}
		});
		// 设置监听
		shoucang.setOnClickListener(this);
		wodexiangce.setOnClickListener(this);
		wodepinglun.setOnClickListener(this);
		wodexianlu.setOnClickListener(this);
		changenumb.setOnClickListener(this);
		exit.setOnClickListener(this);
		// tianjialuxian.setOnClickListener(this);
	}

	class LoadData extends Thread {
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			// if (!InitCmmInterface.initCheck(MainActivity.this)) {
			Hashtable<String, String> b = InitCmmInterface
					.initCmmEnv(Task1Activity.this);
			Message m = new Message();
			m.what = 0;
			m.obj = b;
			mUIHandler.sendMessage(m);
			// } else {
			// if (null != dialog) {
			// dialog.dismiss();
			// }
			//
			// Toast.makeText(MainActivity.this, "已初始化过", Toast.LENGTH_LONG)
			// .show();
			// }
			Looper.loop();
		}
	}

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long responseTime = System.currentTimeMillis() - requestTime;

			switch (msg.what) {
			case 0:
				if (msg.obj == null) {
					hideProgressBar();
					Toast.makeText(Task1Activity.this, "结果 = null",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// new AlertDialog.Builder(MainActivity.this).setTitle("结果")
				// .setMessage((msg.obj).toString())
				// .setPositiveButton("确认", null).show();

				break;
			}
			hideProgressBar();
			if (null != dialog) {
				dialog.dismiss();
			}
		}
	}

	void hideProgressBar() {

		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mProgress != null) {
					mProgress.dismiss();
					mProgress = null;
				}
			}
		});
	}

	// 异步处理
	class AsynMove extends AsyncTask<Integer, Integer, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			int times;
			// 计算完成推动需要多长时间
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数

			for (int i = 0; i < times; i++) {
				publishProgress(params);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... params) {
			LayoutParams lf = (LayoutParams) layout_left.getLayoutParams();
			LayoutParams lp = (LayoutParams) layout_right.getLayoutParams();
			if (params[0] < 0) {
				lf.leftMargin = Math.max(lf.leftMargin + params[0], -MAX_WIDTH);
				lp.leftMargin = Math.max(lp.leftMargin + params[0], -MAX_WIDTH);
			} else {
				lf.leftMargin = Math.min(lf.leftMargin + params[0], 480);
				lp.leftMargin = Math.min(lp.leftMargin + params[0], 480);
			}
			layout_right.setLayoutParams(lp);
			layout_left.setLayoutParams(lf);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (headerfresh == true) {
			if (Login.nick != null) {
				name.setText(Login.nick);
				if (Login.sex.equalsIgnoreCase("0")) {
					sex.setText("男");
				} else if (Login.sex.equalsIgnoreCase("1")) {
					sex.setText("女");
				} else {
					sex.setText("无");
				}
				if (Login.header1 == null) {
					name.setText("无");
					header.setBackgroundResource(R.drawable.defaultgravater);
					showhead.setImageResource(R.drawable.defaultgravater);
					showhead.setScaleType(ScaleType.FIT_CENTER);
					Info.bitinfo = null;
				} else {
					Bitmap bitmap = makeBitmap(Login.header1);
					BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
					header.setBackgroundDrawable(bitmapDrawable);
					Info.bitinfo = bitmap;
					showhead.setImageBitmap(bitmap);
				}
			}
			headerfresh = false;
			if (layuser.getVisibility() == View.VISIBLE) {
				login.setVisibility(View.GONE);
				showhead.setVisibility(View.VISIBLE);
				if (Login.header1 == null) {
					// showhead.setBackgroundResource(R.drawable.defaultgravater);
				}
			}
		}
		Log.e("10010100101", Login.nick + "  " + Login.sex + "  "
				+ Login.header1 + "  ");
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 点击菜单的收藏，监听
		if (v == shoucang) {
			Intent it = new Intent();
			it.setClass(Task1Activity.this, Mysave.class);
			startActivity(it);
		}
		// 点击菜单的收藏，监听
		if (v == wodepinglun) {
			Intent it = new Intent();
			it.setClass(Task1Activity.this, Mycommented.class);
			startActivity(it);
		}
		// 点击菜单的收藏，监听
		if (v == wodexiangce) {
			Intent it = new Intent();
			it.setClass(Task1Activity.this, Photo.class);
			startActivity(it);
		}
		// 点击菜单的我的路线，监听
		if (v == wodexianlu) {
			Intent it = new Intent();
			it.setClass(Task1Activity.this, Myroute.class);
			startActivity(it);
		}
		// 点击菜单的收藏，监听
		if (v == changenumb) {
			Intent it = new Intent();
			it.setClass(Task1Activity.this, Login.class);
			startActivity(it);
			layuser.setVisibility(View.GONE);
			login.setVisibility(View.VISIBLE);
			showhead.setVisibility(View.GONE);
		}
		// 点击菜单的退出，监听
		if (v == exit) {
			new AlertDialog.Builder(Task1Activity.this)
					.setTitle("友情提示：")
					.setMessage("确定退出？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									saveDate();
								}
							}).show();
		}
		// 点击添加路线按钮
		if (v == tianjialuxian) {
			Intent it = new Intent();
			it.setClass(this, Navigation.class);
			startActivity(it);
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "GB2312"),// 防止模拟器上的乱码
					512 * 1024);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			Log.e("DataProvier convertStreamToString", e.getLocalizedMessage(),
					e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public Bitmap makeBitmap(String headerUrl) {
		try {
			myurl = new URL(headerUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bit = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bit;
	}

	private void readDate() {
		SharedPreferences prefs = getSharedPreferences("data",
				Context.MODE_PRIVATE);
		uid = prefs.getInt("uid", 1 * 103);
		nick = prefs.getString("nick", null);
		sex1 = prefs.getString("sex", null);
		header1 = prefs.getString("header1", null);
		humor = prefs.getString("humor", null);
	}

	public void saveDate() {
		// String ss= paCalendar.getInstance();
		prefs = getSharedPreferences("data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (login.getVisibility() == View.VISIBLE) {
			editor.putInt("uid", 0);
			editor.putString("nick", "");
			editor.putString("sex", "");
			editor.putString("header1", "");
			editor.putString("humor", "");
		} else {
			editor.putInt("uid", Login.uid);
			editor.putString("nick", Login.nick);
			editor.putString("sex", Login.sex);
			editor.putString("header1", Login.header1);
			editor.putString("humor", Login.humor);
		}
		editor.commit();
	}
}