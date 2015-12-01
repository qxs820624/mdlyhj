package com.example.mymusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.chuangyu.music.R;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.example.adapter.MusicNameListAdapter;
import com.example.constants.ConstantsMusic;

public class MusicNameListActivity extends Activity {
	TextView titleTv;
	ImageButton backBtn;
	ListView musicListView;
	private Dialog dialog = null;
	List<MusicInfo> list = new ArrayList<MusicInfo>();
	List<MusicInfo> morelist = new ArrayList<MusicInfo>();
	MusicListTread mThread;
	String chartCode = "";
	String chartName = "";
	String chartLocal = "";
	MusicNameListAdapter mAdapter;
	int page = 1;

	String[] localsongname = { "想你", "同住屋檐下", "你是好人", "来跳跳舞吧", "分不开",
			"爱河深,恨河深", "最美的伤口", "美丽的天使", "烦恼惹的祸", "彻底", "心中的堂皇绣", "姻缘",
			"我的芳名叫马丽", "同一个太阳同一个月亮", "昙花一笑", "旗袍", "其实你不懂我的爱", "梦飞黄鹤",
			"感恩你爱我, 记得我爱你", "风姿花传", "端午节", "海风", "心在等待", "海风在吹", "一首老情歌",
			"没有你没有爱", "你是我的未来", "爱不会是现在", "穿越了大海", "当真", "谁说的忘记是什么", "幸福的是每一天",
			"我还是可以", "天一直在笑", "每一次和你分开", "这样的爱你", "心中的梦里", "回忆不是你", "爱的空气",
			"一个人的世界里", "相信还有你", "怎么能这样忘记", "我想忘记你", "为你守候一生", "风雨中", "就算你走了",
			"忍不住流泪", "彼此的温度", "思念的人", "幸福是因为有你", "我的眼睛里只有你", "心底的悲伤", "伤心泪" };
	String[] loclasingername = { "马丽", "马丽", "马丽", "马丽", "马丽", "马丽", "张宇轩",
			"张宇轩", "张宇轩", "张宇轩", "马丽", "马丽", "马丽", "马丽", "马丽", "马丽", "马丽",
			"马丽", "马丽", "马丽", "马丽", "张宇轩", "储维", "储维", "储维", "储维", "储维", "储维",
			"储维", "储维", "储维", "储维", "储维", "储维", "储维", "储维", "储维", "储维", "储维",
			"储维", "储维", "储维", "储维", "李正宇", "李正宇", "李正宇", "李正宇", "李正宇", "李正宇",
			"李正宇", "李正宇", "李正宇", "李正宇", };
	String[] localsongid = { "600902000009532120", "600902000009532124",
			"600902000009532128", "600902000009532132", "600902000009532136",
			"600902000009532140", "600902000009532144", "600902000009532148",
			"600902000009532152", "600902000009532156", "600902000009532516",
			"600902000009532520", "600902000009532524", "600902000009532528",
			"600902000009532532", "600902000009532536", "600902000009532540",
			"600902000009532544", "600902000009532548", "600902000009532552",
			"600902000009532556", "600902000009532560", "600902000009534428",
			"600902000009534432", "600902000009534436", "600902000009534440",
			"600902000009534444", "600902000009534448", "600902000009534452",
			"600902000009534456", "600902000009534460", "600902000009534464",
			"600902000009534468", "600902000009534472", "600902000009534476",
			"600902000009534480", "600902000009534484", "600902000009534488",
			"600902000009534492", "600902000009534496", "600902000009534500",
			"600902000009534504", "600902000009535360", "600902000009535548",
			"600902000009535552", "600902000009535556", "600902000009535560",
			"600902000009535564", "600902000009535568", "600902000009535572",
			"600902000009535576", "600902000009535580", "600902000009535584" };
	String[] localsongdownid = { "600902000009532122", "600902000009532126",
			"600902000009532130", "600902000009532134", "600902000009532138",
			"600902000009532142", "600902000009532146", "600902000009532150",
			"600902000009532154", "600902000009532158", "600902000009532518",
			"600902000009532522", "600902000009532526", "600902000009532530",
			"600902000009532534", "600902000009532538", "600902000009532542",
			"600902000009532546", "600902000009532550", "600902000009532554",
			"600902000009532558", "600902000009532562", "600902000009534430",
			"600902000009534434", "600902000009534438", "600902000009534442",
			"600902000009534446", "600902000009534450", "600902000009534454",
			"600902000009534458", "600902000009534462", "600902000009534466",
			"600902000009534470", "600902000009534474", "600902000009534478",
			"600902000009534482", "600902000009534486", "600902000009534490",
			"600902000009534494", "600902000009534498", "600902000009534502",
			"600902000009534506", "600902000009535362", "600902000009535550",
			"600902000009535554", "600902000009535558", "600902000009535562",
			"600902000009535566", "600902000009535570", "600902000009535574",
			"600902000009535578", "600902000009535582", "600902000009535586" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		Intent intent = getIntent();
		chartCode = intent.getStringExtra("chartcode");
		chartName = intent.getStringExtra("chartname");
		chartLocal = intent.getStringExtra("chartlocal");
		titleTv = (TextView) findViewById(R.id.music_list_title);
		backBtn = (ImageButton) findViewById(R.id.music_list_back_imagebutton);
		musicListView = (ListView) findViewById(R.id.music_listview);
		dialog = new Dialog(this, R.style.theme_dialog_alert);
		dialog.setContentView(R.layout.dialog_layout);
		titleTv.setText(chartName);
		if ("1".equals(chartLocal)) {
			// 本地加载歌曲
			System.out.println("加载本地音乐");
			MusicInfo minfo;
			list.clear();
			for (int i = 0; i < localsongname.length; i++) {
				minfo = new MusicInfo();
				minfo.setMusicId(localsongid[i]);
				minfo.setSingerName(loclasingername[i]);
				minfo.setSongName(localsongname[i]);
				minfo.setRingListenDir(localsongdownid[i]);// 下载ID
				list.add(minfo);
			}
			mAdapter = new MusicNameListAdapter(MusicNameListActivity.this,
					list);
			musicListView.setAdapter(mAdapter);
		} else {
			mThread = new MusicListTread();
			mThread.start();
		}
		musicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mAdapter.list.size() > 0) {
					Intent intent = new Intent(getApplicationContext(),
							PlayMusicActivity.class);
					intent.putExtra("musicId", mAdapter.list.get(position)
							.getMusicId());
					intent.putExtra("songName", mAdapter.list.get(position)
							.getSongName());
					intent.putExtra("singerName", mAdapter.list.get(position)
							.getSingerName());
					intent.putExtra("songListenDir", mAdapter.list
							.get(position).getSongListenDir());
					intent.putExtra("crbtListenDir", mAdapter.list
							.get(position).getCrbtListenDir());
					intent.putExtra("downid", mAdapter.list.get(position)
							.getRingListenDir());
					intent.putExtra("price", mAdapter.list.get(position)
							.getPrice());
					intent.putExtra("local", chartLocal);
					startActivity(intent);
				}
			}
		});
		musicListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						page++;
						System.out.println(page + "///");
						mThread = new MusicListTread();
						mThread.start();
						// musicListView.setSelection(musicListView.indexOfChild(view));
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

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

		MusicListRsp m = MusicQueryInterface.getMusicsByChartId(
				MusicNameListActivity.this, chartCode, page, 30);
		morelist = m.getMusics();
		if (morelist == null) {
			cpinfoHandler
					.sendEmptyMessage(ConstantsMusic.HANDLER_CANCEL_PROGRESS);
			Toast.makeText(getApplicationContext(), "无更多歌曲", 0);
			return;
		}
		if (morelist.size() > 0) {
			for (MusicInfo minfo : morelist) {
				list.add(minfo);
			}
			morelist.clear();
			cpinfoHandler
					.sendEmptyMessage(ConstantsMusic.HANDLER_CANCEL_PROGRESS);
			cpinfoHandler.post(new Runnable() {

				@Override
				public void run() {
					try {
						mAdapter = new MusicNameListAdapter(
								MusicNameListActivity.this, list);
						musicListView.setAdapter(mAdapter);
						mThread.stopThread(false);
					} catch (Exception e) {

					}
				}
			});
		} else {
			cpinfoHandler
					.sendEmptyMessage(ConstantsMusic.HANDLER_CANCEL_PROGRESS);
		}
	}
}
