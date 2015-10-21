package com.hutuchong.app_game;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.RequestDataEntity;

import mobi.domore.mcdonalds.R;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.util.BaseService;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;

/**
 * 
 * @author sunxml
 * 
 */
public class CheckVerDialog implements ActivityHandle {
	Context mContext;
	Dialog dialog;
	BindService bindService;
	ProgressBar progressBar;
	View btnOk;
	TextView tvTip;
	String mTitle;
	String mApk;
	String mDescription;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public CheckVerDialog(Context context, String title, String apk,
			String description) {
		mContext = context;
		dialog = Commond.createDialog(mContext, R.layout.dialog_confirm);
		progressBar = (ProgressBar) dialog.findViewById(R.id.progressImage);
		progressBar.setVisibility(View.VISIBLE);
		btnOk = dialog.findViewById(R.id.btn_ok);
		btnOk.setVisibility(View.GONE);
		tvTip = (TextView) dialog.findViewById(R.id.tv_tip);
		//
		TextView tvVer = (TextView) dialog.findViewById(R.id.tv_title);
		tvVer.setText("当前版本：" + Commond.verName);
		// 关闭按钮
		View btnClose = dialog.findViewById(R.id.btn_cancel);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mTitle = title;
		mApk = apk;
		mDescription = description;
		if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mApk)
				&& !TextUtils.isEmpty(mDescription)) {
			setUpdate();
		} else {
			bindService = new BindService(mContext, this, GameService.class);
		}

	}

	/**
	 * 
	 */
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 请求下载
			if (!TextUtils.isEmpty(mApk)) {
				if (Commond.downloadApk(mContext, mTitle,
						Commond.getCachePath(mContext, "ddgame"), mApk)) {
				}
			}
			//
			dialog.cancel();
		}
	};

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}

	/**
	 * 模块：操作句柄类 功能：在另一个线程中回调使用
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 取消网络状态条
			progressBar.setVisibility(View.GONE);
			if (msg.what == 1) {
				setUpdate();
			}
		}
	};

	private void setUpdate() {
		tvTip.setText(mDescription);
		if (!TextUtils.isEmpty(mApk)) {
			btnOk.setVisibility(View.VISIBLE);
			btnOk.setOnClickListener(listener);
		} else {
			if (TextUtils.isEmpty(mDescription))
				tvTip.setText(R.string.vercheck_failed_tip);
		}
	}

	@Override
	public void onBinddedService() {
		bindService.service = this.bindService.service;
		bindService.service.initService(Commond.userInfo.isProxy());
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				String verUrl = ContantValue.verCheckUrl;
				bindService.service.delFile(verUrl);
				String newUrl = bindService.service.requestChannel(verUrl);
				RSSChannel mChannel = bindService.service.getChannel(newUrl);
				if (mChannel != null) {
					mTitle = mChannel.getTitle();
					mApk = mChannel.getNextLink();
					mDescription = mChannel.getDescription();
				}
				mHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	@Override
	public void onUpdateRequest(BaseService service,
			RequestDataEntity dataEntity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartIntent(Intent i) {
		// TODO Auto-generated method stub

	}
}