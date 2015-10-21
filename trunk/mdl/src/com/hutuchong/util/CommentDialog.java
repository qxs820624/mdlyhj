package com.hutuchong.util;

import java.net.URLEncoder;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import mobi.domore.mcdonalds.R;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.RequestDataEntity;
import cn.coolworks.util.StringUtil;

import com.hutuchong.app_game.service.GameService;

/**
 * 
 * @author sunxml
 * 
 */
public class CommentDialog implements ActivityHandle {
	Context mContext;
	Dialog dialog;
	BindService bindService;
	ProgressBar progressBar;
	String mInput;
	EditText etInput;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public CommentDialog(Context context) {
		mContext = context;
		dialog = Commond.createDialog(mContext, R.layout.dialog_comment);
		dialog.setOnCancelListener(cancelListener);
		progressBar = (ProgressBar) dialog.findViewById(R.id.progressImage);
		progressBar.setVisibility(View.GONE);
		//
		etInput = (EditText) dialog.findViewById(R.id.et_input);
		etInput.setText(Commond.mTempInput);
		// 关闭按钮
		View btnClose = dialog.findViewById(R.id.btn_cancel);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mInput = etInput.getText().toString();
				Commond.mTempInput = mInput;
				dialog.dismiss();
			}
		});
		// 提交按钮
		View btnOk = dialog.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mInput = etInput.getText().toString();
				if (TextUtils.isEmpty(mInput)) {
					Commond.showToast(mContext, "输入不能为空!");
					return;
				}
				submitMessage();
			}

		});
		//
		bindService = new BindService(mContext, this, GameService.class);
	}

	@Override
	public void onBinddedService() {
		bindService.service = this.bindService.service;
		bindService.service.initService(Commond.userInfo.isProxy());
	}

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
			String err = msg.getData().getString("err");
			if (!TextUtils.isEmpty(err)) {
				Commond.showToast(mContext, err);
				return;
			}
			String tip = msg.getData().getString("tip");
			if (!TextUtils.isEmpty(tip)) {
				Commond.showToast(mContext, tip);
			}
			Commond.mTempInput = "";// 清空
			dialog.dismiss();
		}
	};

	/**
	 * 
	 */
	private void submitMessage() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				String commentUrl = ContantValue.commentUrl;
				commentUrl = StringUtil.appendNameValue(commentUrl, "input",
						URLEncoder.encode(mInput));
				bindService.service.delFile(commentUrl);
				String newUrl = bindService.service.requestChannel(commentUrl);
				RSSChannel mChannel = bindService.service.getChannel(newUrl);
				Bundle data = new Bundle();
				if (mChannel != null) {
					//
					data.putString("tip", mChannel.getTitle());
				} else {
					data.putString("err", "很抱歉！提交失败，请重试！");
				}
				Message msg = new Message();
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 
	 */
	OnCancelListener cancelListener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			mInput = etInput.getText().toString();
			Commond.mTempInput = mInput;
		}
	};

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