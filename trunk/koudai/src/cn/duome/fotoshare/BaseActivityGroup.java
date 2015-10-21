package cn.duome.fotoshare;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.duome.fotoshare.utils.Constant;
import cn.duome.fotoshare.utils.FotoCommond;

import com.chuangyu.music.R;
import com.cmsc.cmmusic.init.InitCmmInterface;
import com.example.mymusic.MusicChartListActivity;
import com.hutuchong.util.BaseCommond;
import com.hutuchong.util.BaseContant;

/**
 * 主界面：主要用于底部导航栏切换
 * 
 * @author dky
 * 
 */
public class BaseActivityGroup extends ActivityGroup {
	Activity activity;
	// 底部导航栏ID
	int[] resTabIds = { R.id.nav_home_panel,
			R.id.nav_icon_camera/* nav_camera_panel */, R.id.nav_local_panel };
	// 底部导航栏默认图片ID
	int[] resIconNormalIds = { R.drawable.nav_btn_home_background,
			R.drawable.nav_btn_camera_background,
			R.drawable.nav_btn_local_background };
	// 底部导航栏选中是图片ID
	int[] resIconSelectedIds = { R.drawable.nav_btn_home_pressed,
			R.drawable.nav_btn_camera_pressed, R.drawable.nav_btn_local_pressed };
	// 选中的导航栏ID
	int mSelectedTabId = 0;
	// 拍照时图片保存的文件名
	String cameraFileName;
	// ActivityGroup包含Activity的容器
	private FrameLayout container = null;
	/**
	 * Activity第一次启动时候调用的函数
	 */

	LinearLayout musicly;
	private UIHandler mUIHandler = new UIHandler();
	private ProgressDialog dialog;
	private long requestTime;
	private ProgressDialog mProgress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;

		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置视图
		setContentView(R.layout.activitygroup);
		dialog = ProgressDialog.show(BaseActivityGroup.this, null, "请稍候……",
				true, false);
		requestTime = System.currentTimeMillis();
		new Thread(new LoadData()).start();
		container = (FrameLayout) findViewById(R.id.body);
		//
		for (int i = 0; i < resTabIds.length; i++) {
			findViewById(resTabIds[i]).setOnClickListener(listener);
		}
		musicly = (LinearLayout) findViewById(R.id.nav_music_panel);
		musicly.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MusicChartListActivity.class);
				startActivity(intent);
			}
		});
		//
		gotoTab(resTabIds[0]);
		// 皮肤按钮处理
		View btnSkin = this.findViewById(R.id.btn_skin);
		// 点击侦听
		btnSkin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.btn_skin:
					Intent i = new Intent(activity, SkinSettingActivity.class);
					startActivityForResult(i, Constant.REQUEST_CODE_SETTING);
					break;
				}
			}
		});
		//
		View btn_share = findViewById(R.id.btn_share);
		btn_share.setVisibility(View.VISIBLE);
		btn_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(activity, UserCenterActivity.class);
				startActivity(i);
			}
		});
	}

	class LoadData extends Thread {
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			// if (!InitCmmInterface.initCheck(MainActivity.this)) {
			Hashtable<String, String> b = InitCmmInterface
					.initCmmEnv(BaseActivityGroup.this);
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
					Toast.makeText(BaseActivityGroup.this, "结果 = null",
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

	/**
	 * 切换导航栏
	 * 
	 * @param tabId
	 *            导航栏ID
	 */
	private void gotoTab(int tabId) {
		if (tabId == R.id.nav_icon_camera/* nav_camera_panel */) {
			gotoCamera();
			return;
		}
		if (tabId == mSelectedTabId)
			return;
		mSelectedTabId = tabId;
		tabSelect();
		TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
		switch (tabId) {
		case R.id.nav_home_panel:
			container.removeAllViews();
			if (this.getCurrentActivity() instanceof FotoShareActivity)
				((FotoShareActivity) this.getCurrentActivity()).clearCache();
			Intent intent1 = new Intent(activity, HomeActivity.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			container.addView(getLocalActivityManager().startActivity(
					"nav_tab_1", intent1).getDecorView());

			// 设置标题
			if (tvTitle != null) {
				tvTitle.setText(getText(R.string.app_name).toString());
			}
			break;
		case R.id.nav_camera_panel:
			gotoCamera();
			break;
		case R.id.nav_local_panel:
			container.removeAllViews();
			if (this.getCurrentActivity() instanceof HomeActivity)
				((HomeActivity) this.getCurrentActivity()).clearCache();
			Intent intent3 = new Intent(activity, FotoShareActivity.class);
			intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			container.addView(getLocalActivityManager().startActivity(
					"nav_tab_3", intent3).getDecorView());
			// 设置标题
			if (tvTitle != null) {
				tvTitle.setText(getText(R.string.local_title).toString());
			}
			break;
		}
	}

	/**
	 * 导航栏点击侦听器
	 */
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			gotoTab(arg0.getId());
		}
	};

	/**
	 * 导航栏选中时的处理
	 */
	public void tabSelect() {
		//
		int count = resTabIds.length;
		for (int i = 0; i < count; i++) {
			View vTab = findViewById(resTabIds[i]);
			if (vTab == null)
				continue;
			vTab.setTag(i);
			View vIcon = vTab.findViewById(R.id.nav_icon);
			if (vIcon == null)
				return;
			if (resTabIds[i] == mSelectedTabId) {
				vIcon.setBackgroundResource(resIconSelectedIds[i]);
			} else {
				vIcon.setBackgroundResource(resIconNormalIds[i]);
			}
		}
	}

	/**
	 * 调用摄像头进行拍照
	 */
	public void gotoCamera() {
		// android.media.action.IMAGE_CAPTURE
		Intent ii = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// //////////////////////////////////////////////
		// //////////////////////////////////////////////
		// //////////////////////////////////////////////
		PackageManager pm = getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(ii,
				PackageManager.MATCH_DEFAULT_ONLY);
		String pkg = null;
		String classname = null;
		for (ResolveInfo ri : list) {
			pkg = ri.activityInfo.packageName;
			classname = ri.activityInfo.name;
			// System.out.println("pkg:" + pkg + " classname:" + classname);
			break;
		}
		// ii.setClassName("com.sec.android.app.camera",
		// "com.sec.android.app.camera.Camera");
		ii.setClassName(pkg, classname);
		// //////////////////////////////////////////////
		// //////////////////////////////////////////////
		// //////////////////////////////////////////////
		// startActivityForResult(intent, 0);
		cameraFileName = FotoCommond.sharePath + System.currentTimeMillis()
				+ ".jpg";
		File out = new File(cameraFileName);
		Uri uri = Uri.fromFile(out);
		ii.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		ii.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(ii, Constant.REQUEST_CODE_CAMERA);
	}

	/**
	 * 拍照后返回处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == Constant.REQUEST_CODE_CAMERA) {
				Intent i = new Intent(activity, UploadActivity.class);
				i.putExtra(BaseContant.EXTRA_FILENAME, cameraFileName);
				i.putExtra(BaseContant.EXTRA_URL, cameraFileName);
				overridePendingTransition(R.anim.gotonextin, R.anim.gotonextout);
				startActivityForResult(i, Constant.REQUEST_CODE_EDIT);
			}
		}
	}

	/**
	 * 恢复显示时调用函数
	 */
	@Override
	public void onResume() {
		// 设置背景
		BaseCommond.getUserInfo(this);
		FotoCommond.setImgeViewSrc(this, R.id.iv_bg, FotoCommond.mBg);
		if (FotoCommond.mBg == null)
			FotoCommond.setBgBitmap(this, BaseCommond.userInfo.getSkin());
		super.onResume();
	}

}