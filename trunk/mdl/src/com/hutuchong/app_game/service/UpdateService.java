package com.hutuchong.app_game.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import com.hutuchong.util.Commond;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import cn.coolworks.util.Debug;
import mobi.domore.mcdonalds.R;

public class UpdateService extends Service {
	// 退出服务需要使用
	Intent mIntent;
	// 下载状态
	private int count = 0;
	private final static int REQUEST_CODE_DOWNNING = 0;
	private final static int REQUEST_CODE_INSTALL = 1;
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private Hashtable<String, UpdateEntity> downList = new Hashtable<String, UpdateEntity>();

	// 通知栏
	private NotificationManager updateNotificationManager = null;

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		// Debug.d("UpdateService onStart...");
		mIntent = intent;
		handleCommand(intent);
	}

	// @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Debug.d("UpdateService onStartCommand...");
		mIntent = intent;
		handleCommand(intent);
		return 1;// 1:START_STICKY;
	}

	private void handleCommand(Intent intent) {
		// 获取传值
		String title = intent.getStringExtra("title");
		String key = intent.getStringExtra("url");
		if (downList.containsKey(key)) {
			Commond.showToast(this, title + ":已在下载中！");
			return;
		}
		Commond.showToast(this, title + "：开始下载！");
		UpdateEntity entity = new UpdateEntity();
		count++;
		entity.url = key;
		entity.id = count;
		entity.title = title;

		entity.saveFilePath = intent.getStringExtra("filepath");

		entity.updateNotification = new Notification();
		// 设置下载过程中，点击通知栏，回到主界面
		// 通知栏跳转Intent
		entity.updateIntent = new Intent();
		entity.updateIntent.setClassName(getPackageName(),
				"com.hutuchong.app_game.MarketListActivity");
		entity.updatePendingIntent = PendingIntent.getActivity(
				UpdateService.this, REQUEST_CODE_DOWNNING, entity.updateIntent,
				0);
		// 设置通知栏显示内容
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.app_market_notify);
		contentView.setTextViewText(R.id.notificationTitle, entity.title
				+ ":开始下载");
		contentView.setTextViewText(R.id.notificationPercent, "1%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 1, false);
		entity.updateNotification.contentView = contentView;
		entity.updateNotification.contentIntent = entity.updatePendingIntent;

		entity.updateNotification.icon = android.R.drawable.stat_sys_download;
		entity.updateNotification.tickerText = entity.title + "：开始下载";
		// entity.updateNotification.setLatestEventInfo(UpdateService.this,
		// entity.title, "0%", entity.updatePendingIntent);
		//
		downList.put(entity.url, entity);
		if (updateNotificationManager == null)
			this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 发出通知
		updateNotificationManager.notify(entity.id, entity.updateNotification);
		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable(entity)).start();
		return;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	class updateRunnable implements Runnable {
		UpdateEntity mEntity = null;

		public updateRunnable(UpdateEntity entity) {
			mEntity = entity;
		}

		Message message = updateHandler.obtainMessage();

		public void run() {

			//
			Bundle data = new Bundle();
			data.putString("key", mEntity.url);
			message.setData(data);
			message.what = DOWNLOAD_COMPLETE;
			try {
				long downloadSize = downloadUpdateFile();
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}

		public long downloadUpdateFile() throws Exception {
			//
			int downloadCount = 0;
			int currentSize = 0;
			long totalSize = 0;
			int updateTotalSize = 0;

			HttpURLConnection httpConnection = null;
			InputStream is = null;
			FileOutputStream fos = null;

			try {
				URL url = new URL(mEntity.url);
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("User-Agent",
						"PacificHttpClient");
				if (currentSize > 0) {
					httpConnection.setRequestProperty("RANGE", "bytes="
							+ currentSize + "-");
				}
				httpConnection.setConnectTimeout(10000);
				httpConnection.setReadTimeout(20000);
				updateTotalSize = httpConnection.getContentLength();
				if (httpConnection.getResponseCode() == 404) {
					throw new Exception("fail!");
				}
				is = httpConnection.getInputStream();
				File file = new File(mEntity.saveFilePath);

				if (!file.exists())
					file.createNewFile();
				fos = new FileOutputStream(file, false);
				byte buffer[] = new byte[4096];
				int readsize = 0;
				while ((readsize = is.read(buffer)) > 0) {
					fos.write(buffer, 0, readsize);
					totalSize += readsize;
					// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
					int progress = (int) totalSize * 100 / updateTotalSize;
					if ((downloadCount == 0) || progress > downloadCount) {
						downloadCount += 10;

						// mEntity.updateNotification.setLatestEventInfo(
						// UpdateService.this, mEntity.title + ":正在下载",
						// (int) totalSize * 100 / updateTotalSize + "%",
						// mEntity.updatePendingIntent);

						mEntity.updateNotification.contentView.setTextViewText(
								R.id.notificationPercent, progress + "%");
						mEntity.updateNotification.contentView
								.setProgressBar(R.id.notificationProgress, 100,
										progress, false);
						updateNotificationManager.notify(mEntity.id,
								mEntity.updateNotification);
					}
				}
				buffer = null;
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
				}
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
			return totalSize;
		}
	}

	/**
	 * 
	 */
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			UpdateEntity entity = null;
			String key = null;
			Bundle bundle = msg.getData();
			if (bundle != null) {
				key = bundle.getString("key");
				entity = downList.get(key);
			}
			if (entity == null)
				return;
			// 指定Flag，Notification.FLAG_AUTO_CANCEL意指点击这个Notification后，立刻取消自身
			// 这符合一般的Notification的运作规范
			entity.updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			entity.updateNotification.icon = android.R.drawable.stat_sys_download_done;
			entity.updateNotification.contentView.setImageViewResource(
					R.id.notificationImage,
					android.R.drawable.stat_sys_download_done);
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
				File file = new File(entity.saveFilePath);
				if (file.exists()) {
					Uri uri = Uri.fromFile(file);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					entity.updatePendingIntent = PendingIntent.getActivity(
							UpdateService.this, REQUEST_CODE_INSTALL,
							installIntent, 0);

					// entity.updateNotification.defaults =
					// Notification.DEFAULT_SOUND;// 铃声提醒
					entity.updateNotification.setLatestEventInfo(
							UpdateService.this, entity.title, "下载完成,点击安装。",
							entity.updatePendingIntent);
				}
				file = null;
				// 停止服务
				// stopService(entity.updateIntent);
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				entity.updateNotification.setLatestEventInfo(
						UpdateService.this, entity.title, "下载失败,请重试。",
						entity.updatePendingIntent);

			default:
				// stopService(entity.updateIntent);
			}
			updateNotificationManager.notify(entity.id,
					entity.updateNotification);
			// 在这里删除可能会出现安装解析包的错误（最好在其它地方进行删除）
			// File updateFile = new File(entity.saveFilePath);
			// if (updateFile.exists()) {
			// // 当不需要的时候，清除之前的下载文件，避免浪费用户空间
			// updateFile.delete();
			// }
			// updateNotificationManager.cancel(entity.id);
			downList.remove(key);
			if (downList.size() == 0)
				stopService(mIntent);
		}
	};
}