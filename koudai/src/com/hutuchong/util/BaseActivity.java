package com.hutuchong.util;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.RequestDataEntity;
import cn.duome.fotoshare.utils.UpdateThread;

import com.hutuchong.inter_face.ActivityHandle;
import com.hutuchong.inter_face.OnRequestImageListener;
import com.jw.http.ProgressListener;

/**
 * Activity
 * 
 * @author 3gqa.com
 * 
 */
public class BaseActivity extends Activity implements ActivityHandle,
		ProgressListener {
	//
	public Context mContext;
	//
	public UpdateThread updateThread;
	//
	public int progressTextId = 0;// R.id.progressText;
	public int progressImageId = 0;// R.id.progressImage
	public int btn_refreshId = 0;// R.id.btn_refresh

	//
	int requesttingCount = 0;

	public String mTitle;
	public String extra_flg;

	/**
	 * 
	 * @param isFullScreen
	 * @param btn_refreshId
	 * @param progressImageId
	 * @param progressTextId
	 */
	public void init(boolean isFullScreen, int btn_refreshId,
			int progressImageId, int progressTextId) {
		mContext = this;
		//
		this.btn_refreshId = btn_refreshId;
		this.progressImageId = progressImageId;
		this.progressTextId = progressTextId;
		// android.os.Debug.startMethodTracing("ImageActivity_tracker");
		BaseCommond.getDeviceInfo(this);
		// 加载用户信息
		BaseCommond.getUserInfo(this);
		BaseCommond.userInfo.setFullscreen(isFullScreen);
		//
		showStatusBar(BaseCommond.userInfo.isFullscreen());
	}

	/**
	 * 
	 * @param defaultUrl
	 * @param defaultPageId
	 * @param titleResId
	 */
	public void initIntent(String defaultUrl, int defaultPageId,
			int titleResId, boolean isClear, boolean isRefresh,
			String lasttime, boolean isShowProgressImage) {
		//
		String subtitle = null;
		String url = null;
		int pid = defaultPageId;
		//
		Intent i = this.getIntent();
		if (i != null) {
			//
			extra_flg = i.getStringExtra(BaseContant.EXTRA_FLG);
			//
			String title = i.getStringExtra(BaseContant.EXTRA_TITLE);
			if (TextUtils.isEmpty(title)) {
				mTitle = getString(titleResId);
			} else
				mTitle = title;
			subtitle = i.getStringExtra(BaseContant.EXTRA_SUB_TITLE);
			if (!TextUtils.isEmpty(subtitle)) {
				mTitle += " >> " + subtitle;
			}
			//
			url = i.getStringExtra(BaseContant.EXTRA_URL);
			pid = i.getIntExtra(BaseContant.EXTRA_PAGEID, defaultPageId);
		}
		//
		if (TextUtils.isEmpty(url))
			url = defaultUrl;
		//
		if (!TextUtils.isEmpty(url)) {
			requestItem(url, pid, isClear, isRefresh, lasttime,
					isShowProgressImage);
		}
		//
	}

	// ///////////////////////////////////////////////////
	// 清理缓存开始
	// ///////////////////////////////////////////////////
	/**
	 * 
	 * @author sunxml
	 * 
	 */
	public class ClearCacheRunnable implements Runnable {
		@Override
		public void run() {
			BaseCommond.clearSavedFile(
					BaseContent.getInstance(mContext).rootPath, 0);
			BaseContent.getInstance(mContext).clearCacheFile();
			Message msg = new Message();
			clearCacheHandler.sendMessage(msg);
		}
	}

	Handler clearCacheHandler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			showToast(R.string.menu_clear_finished);
		}
	};

	// ///////////////////////////////////////////////////
	// 清理缓存结束
	// ///////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	public void onShowPress() {

	}

	/**
	 * 
	 */
	public void hideTipIcon() {
		final View vRefresh = findViewById(btn_refreshId);
		if (vRefresh == null)
			return;
		vRefresh.setVisibility(View.GONE);
	}

	/**
	 * 
	 */
	public OnRequestImageListener imagelistener = new OnRequestImageListener() {
		@Override
		public void requestImage(String url, int pageId) {
			// TODO Auto-generated method stub
			// System.out.println("requestImage url:" + url);
			BaseActivity.this.requestImage(url, pageId);
		}
	};

	/**
	 * 
	 * @param channel
	 */
	public void requestImages(RSSChannel channel, boolean isImage, int pageId) {
		if (channel == null)
			return;
		if (updateThread != null) {
			updateThread.stopRequest();
			updateThread = null;
		}
		int count = channel.getItems().size();
		for (int i = 0; i < count; i++) {
			RSSItem item = channel.getItem(i);
			//
			if (isImage)
				requestImage(item.getImageUrl(), pageId);
			requestImage(item.getThumbailUrl(), BaseContant.PAGE_ICON);
		}
	}

	/**
	 * 
	 * @param index
	 */
	public void requestImage(String url, int pageid) {
		// Debug.d("requestImage url:" + url);
		if (TextUtils.isEmpty(url))
			return;
		if (!url.startsWith("http://") && !url.startsWith("file://"))
			return;
		if (updateThread == null) {
			updateThread = new UpdateThread(this, true);
			updateThread.startRequest();
		}
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(pageid);
		entity.getItems().put(BaseContant.EXTRA_URL, url);
		updateThread.addRequest(entity);
	}

	/**
	 * 
	 * @param url
	 * @param pageId
	 * @param isClear
	 */
	public void requestItem(String url, int pageId, boolean isClear,
			boolean isRefresh, String lasttime, boolean isShowProgressImage) {
		// Debug.d("requestItem url:" + url);
		if (TextUtils.isEmpty(url))
			return;
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(pageId);
		entity.getItems().put(BaseContant.EXTRA_URL, url);
		//
		entity.getItems().put(BaseContant.MESSAGE_ISCLEAR_RESULT, isClear);
		entity.getItems().put(BaseContant.MESSAGE_ISREFRESH_RESULT, isRefresh);
		if (!TextUtils.isEmpty(lasttime))
			entity.getItems()
					.put(BaseContant.MESSAGE_LASTTIME_RESULT, lasttime);
		entity.getItems().put(BaseContant.MESSAGE_ISSHOWPROGRESSIMAGE_RESULT,
				isShowProgressImage);
		//
		hideTipIcon();
		//
		UpdateThread thread = new UpdateThread(this, entity);
		thread.startRequest();
	}

	/**
	 * 
	 * @param url
	 * @param pageId
	 * @param isClear
	 */
	public void requestItem(String url, int pageId, boolean isClear,
			boolean isRefresh, String data, String filename) {
		// Debug.d("requestItem url:" + url);
		if (TextUtils.isEmpty(url))
			return;
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(pageId);
		entity.getItems().put(BaseContant.EXTRA_URL, url);
		entity.getItems().put(BaseContant.MESSAGE_ISCLEAR_RESULT, isClear);
		entity.getItems().put(BaseContant.MESSAGE_ISREFRESH_RESULT, isRefresh);
		if (!TextUtils.isEmpty(data))
			entity.getItems().put(BaseContant.EXTRA_DATA, data);
		if (!TextUtils.isEmpty(filename))
			entity.getItems().put(BaseContant.EXTRA_FILENAME, filename);
		//
		hideTipIcon();
		//
		UpdateThread thread = new UpdateThread(this, entity);
		thread.startRequest();
	}

	/**
	 * 
	 */
	public void onUpdateRequest(RequestDataEntity entity) {
		// TODO Auto-generated method stub
		int pageid = entity.getPageID();
		boolean isClear = false;
		boolean isRefresh = false;
		requesttingCount++;
		// System.out.println("requesttingCount:" + requesttingCount);
		Object obj = entity.getItems().get(BaseContant.MESSAGE_ISCLEAR_RESULT);
		if (obj != null) {
			isClear = ((Boolean) obj).booleanValue();
		}
		obj = entity.getItems().get(BaseContant.MESSAGE_ISREFRESH_RESULT);
		if (obj != null) {
			isRefresh = ((Boolean) obj).booleanValue();
		}
		String lasttime = (String) entity.getItems().get(
				BaseContant.MESSAGE_LASTTIME_RESULT);
		//
		boolean isShowProgressImage = true;
		obj = entity.getItems().get(
				BaseContant.MESSAGE_ISSHOWPROGRESSIMAGE_RESULT);
		if (obj != null) {
			isShowProgressImage = ((Boolean) obj).booleanValue();
		}
		//
		String url = (String) entity.getItems().get(BaseContant.EXTRA_URL);
		boolean isRemote = false;
		// Debug.d("onUpdateRequest url:" + url);
		// Debug.d("onUpdateRequest pageid:" + pageid);
		switch (pageid) {
		case BaseContant.PAGE_LOGIN:
			loginRequest(url);
			isClear = true;
			break;
		case BaseContant.PAGE_CATEGORY:
		case BaseContant.PAGE_CHANNEL:
		case BaseContant.PAGE_FEED:
		case BaseContant.PAGE_ITEM:
			// 表示网络请求开始,显示等待状态
			if (isClear && isShowProgressImage)
				BaseCommond.sendProcessingMessage(mHandler);
			isRemote = BaseContent.getInstance(mContext).requestChannel(url,
					isRefresh, lasttime);
			break;
		case BaseContant.PAGE_IMAGE:
			// 表示网络请求开始,显示等待状态
			BaseCommond.sendProcessingMessage(mHandler);
		case BaseContant.PAGE_ICON:
			// 这里的isClear：代表获取图片过程中是否成功
			isClear = BaseContent.getInstance(mContext).requestFile(mContext,
					null, url);
			break;
		case BaseContant.PAGE_FILE:
		case BaseContant.PAGE_FULL_IMAGE:
			// 这里的isClear：代表获取图片过程中是否成功
			isClear = BaseContent.getInstance(mContext).requestFile(mContext,
					mProgressListener, url);
			break;
		case BaseContant.PAGE_POST:
			if (isClear)
				BaseCommond.sendProcessingMessage(mHandler);
			String data = (String) entity.getItems()
					.get(BaseContant.EXTRA_DATA);
			BaseContent.getInstance(mContext).requestChannel(url, null, null,
					null, data, isRefresh);
			break;
		case BaseContant.PAGE_UPLOAD_FOTO:
			if (isClear)
				BaseCommond.sendProcessingMessage(mHandler);
			String filename = (String) entity.getItems().get(
					BaseContant.EXTRA_FILENAME);
			String data1 = (String) entity.getItems().get(
					BaseContant.EXTRA_DATA);
			BaseContent.getInstance(this).uploadFile(null, url, null, data1,
					filename);
			// 发送网络请求处理完毕的消息
			BaseCommond.sendProcessedMessage(mHandler, pageid, url, isClear,
					isRefresh, isRemote);
			break;
		default:
			// 表示网络请求开始,显示等待状态
			if (isClear)
				BaseCommond.sendProcessingMessage(mHandler);
			isRemote = BaseContent.getInstance(mContext).requestChannel(url,
					isRefresh, lasttime);
			break;
		}
		// 发送网络请求处理完毕的消息
		BaseCommond.sendProcessedMessage(mHandler, pageid, url, isClear,
				isRefresh, isRemote);
	}

	private void loginRequest(String url) {

	}

	/**
	 * 
	 * @param url
	 * @param pageid
	 * @param resId
	 * @param isRefresh
	 * @param adapter
	 * @param channel
	 * @return
	 */
	public boolean showTipIcon(final String url, final int pageid,
			Adapter adapter, RSSChannel channel) {
		if (channel == null) {
			if (adapter == null || (adapter != null && adapter.getCount() == 0)) {
				BaseContent.getInstance(mContext).delFile(url);
				showTipIcon(url, pageid, R.drawable.nothing, true);
			} else {
				hideTipIcon();
			}
			return false;
		}
		if (adapter == null) {
			return true;
		}
		if (channel.getItems().size() == 0 && adapter.getCount() == 0) {
			BaseContent.getInstance(mContext).delFile(url);
			showTipIcon(url, BaseContant.PAGE_CHANNEL, R.drawable.nothing,
					false);
			return false;
		}
		hideTipIcon();
		return true;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public boolean showTipIcon(final String url, final int pageid, int resId,
			boolean isRefresh) {
		final View vRefresh = findViewById(R.id.btn_refresh);
		if (vRefresh == null)
			return false;
		//
		vRefresh.setVisibility(View.VISIBLE);
		if (isRefresh && !TextUtils.isEmpty(url)) {
			vRefresh.setBackgroundResource(R.drawable.btn_reflesh_background);
			vRefresh.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					vRefresh.setVisibility(View.GONE);
					requestItem(url, pageid, true, true, null, true);
				}
			});
		} else {
			vRefresh.setBackgroundResource(resId);
		}
		return true;
	}

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	public View progressView;
	/**
	 * 
	 */
	public Handler mProgressHandler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			// mListAdapter.notifyDataSetChanged();
			Bundle b = m.getData();
			if (b == null)
				return;
			String tag = b.getString("tag");
			String text = b.getString("text");
			if (progressView != null && !TextUtils.isEmpty(text)) {
				TextView tvProgress = (TextView) progressView
						.findViewWithTag(tag);
				if (tvProgress != null)
					tvProgress.setText(text);
			}
			b = null;
			m = null;
		}
	};

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	/**
	 * 
	 */
	protected ProgressListener mProgressListener = new ProgressListener() {

		@Override
		public void onProgress(Object tag, int progress) {
			// TODO Auto-generated method stub
			// Debug.d("progress:" + progress);
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString(BaseContant.MESSAGE_PROGRESS_RESULT, progress + "%");
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

		@Override
		public void onToastError(String msg) {
			// TODO Auto-generated method stub
			Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onDialogError(String msg) {
			// TODO Auto-generated method stub
			Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onBaseUrl(String url) {
			// TODO Auto-generated method stub

		}
	};

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	/**
	 * 
	 * @param m
	 */
	public boolean handleMessage(Message m) {
		return false;
	}

	//
	protected HandlerEx mHandler = new HandlerEx(this);

	/**
	 * 
	 */
	public class HandlerEx extends Handler {
		BaseActivity mActivity;

		public HandlerEx(BaseActivity baseActivity) {
			// TODO Auto-generated constructor stub
			this.mActivity = baseActivity;
		}

		@Override
		public void handleMessage(Message m) {
			if (m.getData() != null) {
				// 自处理
				if (mActivity.handleMessage(m))
					return;
				// 显示下载进度信息
				String progress = m.getData().getString(
						BaseContant.MESSAGE_PROGRESS_RESULT);
				if (!TextUtils.isEmpty(progress)) {
					TextView tv = (TextView) mActivity
							.findViewById(progressTextId);
					if (tv != null) {
						tv.setText(progress);
						tv.setVisibility(View.VISIBLE);
					}
					m = null;
					return;
				}
				// 显示提示消息
				String message = m.getData().getString(
						BaseContant.MESSAGE_MESSAGE_RESULT);
				if (!TextUtils.isEmpty(message)) {
					if (m.what == 1 && !mActivity.isFinishing())
						Toast.makeText(BaseActivity.this, message,
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(BaseActivity.this, message,
								Toast.LENGTH_SHORT).show();
				}
				//
				int result = m.getData()
						.getInt(BaseContant.MESSAGE_DATA_RESULT);
				// System.out.println("result:" + result);
				TextView tv = (TextView) mActivity.findViewById(progressTextId);
				View pview = mActivity.findViewById(progressImageId);
				if (result == BaseContant.MESSAGE_PROCESSINGFLAG) {
					if (pview != null) {
						// System.out.println("pview VISIBLE ");
						pview.setVisibility(View.VISIBLE);
					}
					if (tv != null) {
						// tv.setText("0%");
						// tv.setVisibility(View.VISIBLE);
					}
				} else {
					boolean isClear = m.getData().getBoolean(
							BaseContant.MESSAGE_ISCLEAR_RESULT);
					boolean isRefresh = m.getData().getBoolean(
							BaseContant.MESSAGE_ISREFRESH_RESULT);
					boolean isRemote = m.getData().getBoolean(
							BaseContant.MESSAGE_ISREMOTE_RESULT);
					String url = m.getData().getString(
							BaseContant.MESSAGE_URL_RESULT);
					requesttingCount--;
					if (pview != null && requesttingCount == 0) {
						pview.setVisibility(View.GONE);
					}
					if (tv != null) {
						tv.setVisibility(View.GONE);
					}
					//
					int pageid = m.getData().getInt(
							BaseContant.MESSAGE_PAGEID_RESULT);

					try {
						loadUpdateThread(url, pageid, isClear, isRefresh,
								isRemote);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	};

	/**
	 * 
	 * @param url
	 * @param pageId
	 */
	public void loadUpdateThread(String url, int pageId, boolean isClear,
			boolean isRefresh, boolean isRemote) throws Exception {
		switch (pageId) {
		}
	}

	/**
	 * 
	 */
	private void showStatusBar(boolean isFullScreen) {
		if (isFullScreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}


	/**
	 * 
	 */
	public void clearCache() {
		BaseCommond.clearDownedTempFile(mContext);
		BaseCommond.clearSavedFile(BaseContent.getInstance(mContext).rootPath,
				BaseCommond.userInfo.getSaveDays());
	}

	@Override
	public void onProgress(Object tag, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onToastError(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogError(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBaseUrl(String url) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onDestroy() {
		//
		if (updateThread != null)
			updateThread.stopRequest();
		//
		super.onDestroy();
	}

	/**
	 * 
	 * @param resId
	 */
	public void showToast(int resId) {
	}
}