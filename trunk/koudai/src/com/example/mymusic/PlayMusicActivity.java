package com.example.mymusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.duome.http.DownLoad;

import com.chuangyu.music.R;
import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.data.DownloadResult;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.Result;
import com.example.musicutil.Player;

public class PlayMusicActivity extends Activity {
	private TextView tvMusicTitle;
	private TextView tvArtist;
	private SeekBar sbProgress;
	private TextView tvCurrentTime;
	private TextView tvDuration;
	private Button btnLast;
	private Button btnPlayOrPasue;
	private Button btnNext;
	ImageButton backBtn;
	Button buyBtn;
	Button downBtn;
	String musicId = "";
	String songName = "";
	String songListenDir = "";
	String crbtListenDir = "";
	String singerName = "";
	String price = "";
	String local = "";
	String downid = "";
	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);
		Intent intent = getIntent();
		musicId = intent.getStringExtra("musicId");
		songName = intent.getStringExtra("songName");
		songListenDir = intent.getStringExtra("songListenDir");
		crbtListenDir = intent.getStringExtra("crbtListenDir");
		singerName = intent.getStringExtra("singerName");
		price = intent.getStringExtra("price");
		local = intent.getStringExtra("local");
		downid = intent.getStringExtra("downid");

		tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
		tvArtist = (TextView) findViewById(R.id.tv_artist);
		sbProgress = (SeekBar) findViewById(R.id.sb_progress);
		tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		btnLast = (Button) findViewById(R.id.btn_last);
		btnPlayOrPasue = (Button) findViewById(R.id.btn_play_or_pasue);
		btnNext = (Button) findViewById(R.id.btn_next);
		backBtn = (ImageButton) findViewById(R.id.play_back_imagebutton);
		buyBtn = (Button) findViewById(R.id.btn_buy);
		downBtn = (Button) findViewById(R.id.btn_down);

		if ("1".equals(local)) {
			new Thread() {
				@Override
				public void run() {
					super.run();
					MusicInfoResult result = MusicQueryInterface
							.getMusicInfoByMusicId(PlayMusicActivity.this,
									musicId);
					songName = result.getMusicInfo().getSongName();
					singerName = result.getMusicInfo().getSingerName();
					price = result.getMusicInfo().getPrice();
					crbtListenDir = result.getMusicInfo().getCrbtListenDir();

					mUIHandler.obtainMessage(0, result).sendToTarget();
				}
			}.start();
		} else {
			player = new Player(crbtListenDir, sbProgress, tvCurrentTime,
					tvDuration);
			player.play();
		}

		tvMusicTitle.setText(songName);
		tvArtist.setText(singerName);

		btnPlayOrPasue.setBackgroundResource(R.drawable.player_btn_pause_style);

		btnPlayOrPasue.setOnClickListener(new ClickEvent());
		sbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

		downBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("1".equals(local)) {
					System.out.println("下载歌曲downid：" + downid);
					FullSongManagerInterface.getFullSongDownloadUrl(
							PlayMusicActivity.this, downid,
							new CMMusicCallback<DownloadResult>() {
								@Override
								public void operationResult(
										final DownloadResult downloadResult) {
									if (null != downloadResult) {
										DownLoad.downLoad(PlayMusicActivity.this,
												downloadResult.getDownUrl(),
												songName + ".mp3");
										// new AlertDialog.Builder(
										// PlayMusicActivity.this)
										// .setTitle("下载：" + songName)
										// .setMessage(
										// downloadResult.toString())
										// .setPositiveButton("确认", null)
										// .show();
									}

								}
							});
				} else {
					System.out.println("下载歌曲musicId：" + musicId);
					FullSongManagerInterface.getFullSongDownloadUrl(
							PlayMusicActivity.this, musicId,
							new CMMusicCallback<DownloadResult>() {
								@Override
								public void operationResult(
										final DownloadResult downloadResult) {
									if (null != downloadResult) {
										DownLoad.downLoad(
												PlayMusicActivity.this,
												downloadResult.getDownUrl(),
												songName + ".mp3");
										// new AlertDialog.Builder(
										// PlayMusicActivity.this)
										// .setTitle("下载：" + songName)
										// .setMessage(
										// downloadResult.toString())
										// .setPositiveButton("确认", null)
										// .show();
									}

								}
							});
				}

			}
		});

		buyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RingbackManagerInterface.buyRingBack(PlayMusicActivity.this,
						musicId, new CMMusicCallback<Result>() {
							@Override
							public void operationResult(Result ret) {
								if (null != ret) {
									new AlertDialog.Builder(
											PlayMusicActivity.this)
											.setTitle("订制：" + songName)
											.setMessage(ret.toString())
											.setPositiveButton("确认", null)
											.show();
								}
							}
						});
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

	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				if (msg.obj == null) {

					return;
				}
				player = new Player(crbtListenDir, sbProgress, tvCurrentTime,
						tvDuration);
				player.play();
				break;
			}

		}
	}

	class ClickEvent implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			if (arg0 == btnPlayOrPasue) {
				boolean pause = player.pause();
				if (pause) {
					// btnPause.setText("继续");
					btnPlayOrPasue
							.setBackgroundResource(R.drawable.player_btn_play_style);
				} else {
					btnPlayOrPasue
							.setBackgroundResource(R.drawable.player_btn_pause_style);

				}
			}
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
			// tv1.setText(MusicUtil.formatTime(player.mediaPlayer.getCurrentPosition()));
			// tv2.setText(MusicUtil.formatTime(player.mediaPlayer.getDuration()));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		player.stop();
		super.onDestroy();
	}
}
