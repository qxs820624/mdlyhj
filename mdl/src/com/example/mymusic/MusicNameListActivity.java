package com.example.mymusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.domore.mcdonalds.R;

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
	MusicNameListAdapter mAdapter;
	int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		Intent intent = getIntent();
		chartCode = intent.getStringExtra("chartcode");
		chartName = intent.getStringExtra("chartname");
		titleTv = (TextView) findViewById(R.id.music_list_title);
		backBtn = (ImageButton) findViewById(R.id.music_list_back_imagebutton);
		musicListView = (ListView) findViewById(R.id.music_listview);
		dialog = new Dialog(this, R.style.theme_dialog_alert);
		dialog.setContentView(R.layout.dialog_layout);
		titleTv.setText(chartName);
		mThread = new MusicListTread();
		mThread.start();

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
					intent.putExtra("price", mAdapter.list.get(position)
							.getPrice());
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
					//	musicListView.setSelection(musicListView.indexOfChild(view));
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
