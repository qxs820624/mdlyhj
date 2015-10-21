/**
 * $id$
 */
package com.hutuchong.share;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import cn.duome.fotoshare.utils.StringUtil;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.exception.RenrenException;
import com.renren.api.connect.android.feed.FeedPublishRequestParam;
import com.renren.api.connect.android.feed.FeedPublishResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;

/**
 * 演示如何使用系统提供的widget
 * 
 * @author Shaofeng Wang (shaofeng.wang@renren-inc.com)
 */
public class ShareRenren {
	static Renren renren;
	static String API_KEY = "f2bf97d7fb3e447f982cb2371ef667c1";
	static String SECRET_KEY = "1bfde30f02f2417ebc32420d635e4b03";
	static String APP_ID = "188194";// 口袋图片分享
	static Activity mActivity;
	//
	String mTitle;
	String mContent;
	String mLink;
	String mPicUrl;
	String mActionName;
	String mActionLink;

	static ShareRenren instance;

	static public ShareRenren getInstance(Activity activity) {
		if (instance == null) {
			instance = new ShareRenren();
			if (renren == null) {
				renren = new Renren(API_KEY, SECRET_KEY, APP_ID, activity);
			}
		}
		mActivity = activity;

		return instance;
	}

	/**
	 * 
	 * @param activity
	 * @param title
	 * @param content
	 * @param link
	 * @param picUrl
	 */
	public void shareFeed(String title, String content, String link,
			String picUrl, String actionName, String actionLink) {
		mTitle = title;
		if (mTitle != null && mTitle.length() > 15) {
			mTitle = mTitle.substring(0, 15);
		}
		mContent = content;
		if (TextUtils.isEmpty(mContent)) {
			mContent = mTitle;
		}

		int pos = mContent.indexOf("<p>");
		if (pos >= 0) {
			mContent = mContent.substring(pos);
		}
		mContent = StringUtil.html2Text(mContent);
		if (mContent != null && mContent.length() > 100) {
			mContent = mContent.substring(0, 100);
			mContent += "...";
		}
		mActionName = actionName;
		if (mActionName != null && mActionName.length() > 10) {
			mActionName = mActionName.substring(0, 10);
		}

		mLink = link;
		mPicUrl = picUrl;

		mActionLink = actionLink;
		renren.authorize(mActivity, null, renrenListener, 1);
	}

	/**
	 * 使用平台提供的widget dialog发布新鲜事
	 * 
	 * @param activity
	 * @param renren
	 */
	private void showFeedDialog() {
		FeedPublishRequestParam feed = new FeedPublishRequestParam(mTitle,
				mContent, mLink, // 新鲜事链接
				mPicUrl, // 新鲜事图片url
				"", mActionName, mActionLink, // 动作链接,
				"");
		final Handler handler = new Handler(mActivity.getMainLooper());
		AbstractRequestListener<FeedPublishResponseBean> listener = new AbstractRequestListener<FeedPublishResponseBean>() {
			@Override
			public void onRenrenError(RenrenError renrenError) {
				final int errorCode = renrenError.getErrorCode();
				// System.out.println("errorCode:" + errorCode);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
							Toast.makeText(mActivity, "发布新鲜事取消",
							Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mActivity, "发布新鲜事失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onFault(Throwable fault) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity, "发布新鲜事失败", Toast.LENGTH_SHORT)
								.show();
					}
				});
			}

			@Override
			public void onComplete(FeedPublishResponseBean bean) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity, "发布新鲜事成功", Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		};

		try {
			renren.publishFeed(mActivity, feed, listener);
		} catch (RenrenException e) {
			//Util.logger(e.getMessage());
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mActivity, "发布新鲜事失败", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
	}

	RenrenAuthListener renrenListener = new RenrenAuthListener() {

		@Override
		public void onComplete(Bundle values) {
			// startApiList();
			// System.out.println("onComplete");
			// WidgetDialogDemo.showFeedDialog(activity, renren);
			showFeedDialog();
		}

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			// System.out.println("onRenrenAuthError");
		}

		@Override
		public void onCancelLogin() {
			// System.out.println("onCancelLogin");
		}

		@Override
		public void onCancelAuth(Bundle values) {
			// System.out.println("onCancelAuth");
		}
	};
}
