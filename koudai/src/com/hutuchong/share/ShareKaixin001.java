/**
 * $id$
 */
package com.hutuchong.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kaixin.connect.Kaixin;
import com.kaixin.connect.exception.KaixinAuthError;
import com.kaixin.connect.listener.KaixinAuthListener;
import com.kaixin.demo.PostRecordActivity;

/**
 * 演示如何使用系统提供的widget
 * 
 * @author Shaofeng Wang (shaofeng.wang@renren-inc.com)
 */
public class ShareKaixin001 {
	static Kaixin kaixin;
	static Activity mActivity;
	//
	String mTitle;
	String mContent;
	String mLink;
	String mPicUrl;
	String mActionName;
	String mActionLink;
	String mPicPath;

	static ShareKaixin001 instance;

	static public ShareKaixin001 getInstance(Activity activity) {
		if (instance == null) {
			instance = new ShareKaixin001();
		}

		mActivity = activity;

		return instance;
	}

	KaixinAuthListener authListener = new KaixinAuthListener() {
		@Override
		public void onAuthCancel(Bundle values) {
			// Log.i(TAG, "onAuthCancel");
		}

		@Override
		public void onAuthCancelLogin() {
			// Log.i(TAG, "onAuthCancelLogin");
		}

		@Override
		public void onAuthComplete(Bundle values) {
			// Kaixin kaixin = Kaixin.getInstance();
			// try {
			// String response = kaixin.refreshAccessToken(AndroidExample.this,
			// null);
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (MalformedURLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			post();
		}

		@Override
		public void onAuthError(KaixinAuthError kaixinAuthError) {
			// Log.i(TAG, "onAuthError");
		}
	};

	public void share(String title, String content, String link, String picUrl,
			String actionName, String actionLink, String picPath) {
		mTitle = title;
		mContent = content;
		mLink = link;
		mPicUrl = picUrl;
		mActionName = actionName;
		mActionLink = actionLink;
		mPicPath = picPath;
		//
		kaixin = Kaixin.getInstance();
		if (kaixin.isSessionValid()) {
			post();
		} else {
			String[] permissions = { "basic", "create_records" };
			kaixin.authorize(mActivity, permissions, authListener);
		}
	}

	private void post() {
		Intent intent = new Intent(mActivity, PostRecordActivity.class);
		intent.putExtra("title", mTitle);
		intent.putExtra("content", mContent);
		intent.putExtra("link", mLink);
		intent.putExtra("picUrl", mPicUrl);
		intent.putExtra("actionName", mActionName);
		intent.putExtra("actionLink", mActionLink);
		intent.putExtra("picPath", mPicPath);
		mActivity.startActivity(intent);
	}
}
