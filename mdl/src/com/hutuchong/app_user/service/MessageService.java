package com.hutuchong.app_user.service;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSException;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSItem;
import org.gnu.stealthp.rsslib.RSSParser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import mobi.domore.mcdonalds.R;

import com.hutuchong.app_user.db.DBAdapter;
import com.hutuchong.http.HotInterface;
import com.hutuchong.util.ContantValue;

public class MessageService extends Service {
	// 退出服务需要使用
	Intent mIntent;
	boolean isRunning = false;
	DBAdapter db = new DBAdapter(this);
	HotInterface inter;
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
		if (updateNotificationManager == null)
			this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 获取传值
		if (!isRunning) {
			isRunning = true;
			if (inter == null)
				inter = new HotInterface(false);
			new Thread(new UpdateRunnable()).start();
		}
		return;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @author sunxml
	 * 
	 */
	class UpdateRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRunning) {
				try {
					db.open();
					String lasttime = "";
					Cursor c = db.getLastTime();
					if (c != null) {
						if (c.moveToFirst())
							lasttime = c.getString(0);
					}
					db.close();
					String url = ContantValue.selfmsgUrl + "?lasttime="
							+ lasttime;
					System.out.println("selfmsgUrl:" + url);
					byte[] data = inter.read(null, url);
					if (data != null) {
						RSSHandler hand = new RSSHandler();
						try {
							RSSParser.parseXml(data, hand, false);
							RSSChannel channel = hand.getRSSChannel();
							if (channel != null) {
								db.open();

								String pubdate = channel.getPubDate();
								String title = "";
								String cat = "";
								for (RSSItem item : channel.getItems()) {
									if (TextUtils.isEmpty(title)) {
										title = item.getTitle();
										cat = TextUtils.isEmpty(item
												.getCategory()) ? "最新消息" : item
												.getCategory() + ":";
									}
									if (TextUtils.isEmpty(pubdate)) {
										pubdate = item.getPubDate();
									}
									if (!TextUtils.isEmpty(pubdate)) {
										db.insertItem(
												TextUtils.isEmpty(item
														.getCategory()) ? "最新消息"
														: item.getCategory(),
												item.getTitle(),
												item.getLink(), item
														.getDescription(), item
														.getImageUrl(), item
														.getThumbailUrl(), item
														.getFavLink(), item
														.getPubDate());
									}
								}
								if (!TextUtils.isEmpty(pubdate)) {
									db.updateLastDate(pubdate);
								}
								db.close();
								// 发出通知
								Notification msgNotify = new Notification();
								Intent msgIntent = new Intent();
								msgIntent
										.setClassName(getPackageName(),
												"com.hutuchong.app_user.MessageListActivity");
								PendingIntent msgPendingIntent = PendingIntent
										.getActivity(MessageService.this, 0,
												msgIntent, 0);
								msgNotify.contentView = new RemoteViews(
										getPackageName(),
										R.layout.app_message_notify);
								msgNotify.contentView.setTextViewText(
										R.id.cate, cat);
								msgNotify.contentView.setTextViewText(
										R.id.title, title);
								msgNotify.contentIntent = msgPendingIntent;
								msgNotify.icon = android.R.drawable.stat_notify_voicemail;
								msgNotify.tickerText = title;
								msgNotify.flags |= Notification.FLAG_AUTO_CANCEL;

								updateNotificationManager.notify(0, msgNotify);
							}
						} catch (RSSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Thread.sleep(60 * 60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					isRunning = false;
					e.printStackTrace();
				}
			}
		}
	}
}