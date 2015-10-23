package com.example.mymusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.init.InitCmmInterface;
import com.duom.activity.Task1Activity;
import com.example.adapter.MusicChartListAdapter;
import com.example.constants.ConstantsMusic;
import com.jingxunnet.cmusic.R;

public class MusicChartListActivity extends Activity {
	private Dialog dialog = null;
	List<ChartInfo> list = new ArrayList<ChartInfo>();
	MusicListTread mThread;
	MusicChartListAdapter mAdapter;
	ListView chartListView;
	ImageButton backBtn;
	public int[] iconarray = { R.drawable.icon_jinqu, R.drawable.icon_xinge,
			R.drawable.icon_cailing, R.drawable.icon_yinyue };
	private UIHandler mUIHandler = new UIHandler();
	Hashtable<String, String> initResult = new Hashtable<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart_list);
		dialog = new Dialog(this, R.style.theme_dialog_alert);
		dialog.setContentView(R.layout.dialog_layout);
		chartListView = (ListView) findViewById(R.id.chart_listview);
		backBtn = (ImageButton) findViewById(R.id.chart_list_back_imagebutton);
		InitCmmInterface.initSDK(MusicChartListActivity.this);
		if (!InitCmmInterface.initCheck(getApplicationContext())) {
			new Thread(new T1()).start();
		} else {
			Toast.makeText(getApplicationContext(), "已经成功初始化数据", 0).show();
		}
		mThread = new MusicListTread();
		mThread.start();

		chartListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// final int i = position;
				// new Thread() {
				// @Override
				// public void run() {
				// MusicListRsp m = MusicQueryInterface
				// .getMusicsByChartId(MusicListActivity.this,
				// mAdapter.list.get(i).getChartCode(), 1,
				// 30);
				// System.out.println(m.toString());
				// }
				// }.start();
				if (mAdapter.list.size() > 0) {
					Intent intent = new Intent(getApplicationContext(),
							MusicNameListActivity.class);
					intent.putExtra("chartcode", mAdapter.list.get(position)
							.getChartCode());
					intent.putExtra("chartname", mAdapter.list.get(position)
							.getChartName());
					startActivity(intent);
				}
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Task1Activity.tv0
				.setBackgroundResource(R.drawable.music_migu_btn_pressed);
		Task1Activity.tv2.setBackgroundResource(R.drawable.daohang);
		Task1Activity.tv.setBackgroundResource(R.drawable.zhuye);
		Task1Activity.tv3.setBackgroundResource(R.drawable.xiangce);
		super.onResume();
	}

	class T1 extends Thread {
		@Override
		public void run() {
			super.run();
			Looper.prepare();

			initResult = InitCmmInterface
					.initCmmEnv(MusicChartListActivity.this);
			Message m = new Message();
			m.what = 0;
			m.obj = initResult;
			mUIHandler.sendMessage(m);

			Looper.loop();
		}
	}

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				if (msg.obj == null) {
					Toast.makeText(getApplicationContext(), "初始化失败", 0).show();
					return;
				}
				System.out.println(initResult);
				if (null != initResult) {
					Toast.makeText(getApplicationContext(),
							initResult.get("desc").toString(), 0).show();
				}
				break;
			}
		}
	}

	class MusicListTread extends Thread {
		private boolean _run = true;

		public void stopThread(boolean run) {
			this._run = !run;
		}

		@Override
		public void run() {
			if (_run) {
				try {
					musiclist();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Handler cpinfoHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case ConstantsMusic.HANDLER_SHOW_PROGRESS:
				dialog.setCancelable(true);
				dialog.show();
				break;
			case ConstantsMusic.HANDLER_CANCEL_PROGRESS:
				dialog.cancel();
				mThread.stopThread(false);
				break;
			}
		};
	};

	private void musiclist() throws ClientProtocolException, IOException {
		cpinfoHandler.sendEmptyMessage(ConstantsMusic.HANDLER_SHOW_PROGRESS);

		ChartListRsp c = MusicQueryInterface.getChartInfo(
				getApplicationContext(), 1, 30);
		list = c.getChartInfos();
		cpinfoHandler.sendEmptyMessage(ConstantsMusic.HANDLER_CANCEL_PROGRESS);
		cpinfoHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					mAdapter = new MusicChartListAdapter(
							MusicChartListActivity.this, list, iconarray);
					chartListView.setAdapter(mAdapter);
					mThread.stopThread(false);
				} catch (Exception e) {

				}
			}
		});
	}
}
