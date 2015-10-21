/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hutuchong.share;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.ShareActivity;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

/**
 * Sample code for testing weibo APIs.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class ShareWeibo {
	/** Called when the activity is first created. */
	private Button mLogin;
	private static TextView mToken;

	private static final String URL_ACTIVITY_CALLBACK = "weiboandroidsdk://TimeLineActivity";
	private static final String FROM = "xweibo";

	// 设置appkey及appsecret，如何获取新浪微博appkey和appsecret请另外查询相关信息，此处不作介绍
	private static final String CONSUMER_KEY = "883111804";// 替换为开发者的appkey，例如"1646212960";
	private static final String CONSUMER_SECRET = "497310b6a0ec21f689cee0e62d2f7662";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";

	static ShareWeibo instance;
	static Activity mActivity;
	static String mContent;
	static String mPicPath;

	static public ShareWeibo getInstance(Activity activity, String content,
			String picPath) {
		mActivity = activity;
		mContent = content;
		mPicPath = picPath;
		if (instance == null) {
			instance = new ShareWeibo();
		}
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);

		// Oauth2.0
		// 隐式授权认证方式
		weibo.setRedirectUrl("http://www.3gqa.com");// 此处回调页内容应该替换为与appkey对应的应用回调页
		// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
		// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
		// 应用回调页不可为空
		weibo.authorize(activity, new WeiboDialogListener() {
			@Override
			public void onComplete(Bundle values) {
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				AccessToken accessToken = new AccessToken(token,
						CONSUMER_SECRET);
				accessToken.setExpiresIn(expires_in);
				Weibo.getInstance().setAccessToken(accessToken);
				//
				if (!TextUtils.isEmpty(mPicPath)&&!mPicPath.startsWith("http://")) {
					File picFile = new File(mPicPath);
					if (!picFile.exists()) {
						Toast.makeText(mActivity, "图片" + mPicPath + "不存在！",
								Toast.LENGTH_SHORT).show();
						mPicPath = null;
					}
				}
				try {
					Weibo weibo = Weibo.getInstance();
					weibo.share2weibo(mActivity, weibo.getAccessToken()
							.getToken(), weibo.getAccessToken().getSecret(),
							mContent, mPicPath);
					Intent i = new Intent(mActivity, ShareActivity.class);
					mActivity.startActivity(i);

				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

				}

			}

			@Override
			public void onError(DialogError e) {
				Toast.makeText(mActivity, "Auth error : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(mActivity, "Auth cancel", Toast.LENGTH_LONG)
						.show();
			}

			@Override
			public void onWeiboException(WeiboException e) {
				Toast.makeText(mActivity, "Auth exception : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		});

		return instance;
	}
}