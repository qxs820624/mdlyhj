package com.hutuchong.app_game;

import java.lang.reflect.Method;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.RequestDataEntity;
import cn.coolworks.util.StringUtil;
import cn.coolworks.util.Tools;
import cn.coolworks.util.UpdateThread;
import cn.coolworks.view.HorizontalScrollViewEx;

import mobi.domore.mcdonalds.R;
import com.hutuchong.adapter.ChannelAdapter;
import com.hutuchong.app_game.adapter.MarketItemAdapter;
import com.hutuchong.app_game.assist.ElasticInterpolator;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.data.Data;
import com.hutuchong.util.AdUtils;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BaseService;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;
import com.hutuchong.util.GotoActivity;
import com.hutuchong.util.ItemData;

public class MarketListActivity_1 extends BaseActivity implements
		ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	ListView listView;
	MarketItemAdapter mMarketAdapter;
	String categoryUrl;
	String loginUrl;
	boolean isMain;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_marketlist_1);
		//
		Intent i = this.getIntent();
		String action = i.getAction();
		isMain = "android.intent.action.MAIN".equals(action);
		appFlash(isMain);
		if (!isMain) {
			View v = findViewById(R.id.channellist_scroll_panel);
			v.setVisibility(View.GONE);
		} else {
			// View v = findViewById(R.id.gradient_up);
			// v.setVisibility(View.GONE);
		}
		//
		listView = (ListView) findViewById(R.id.listview);
		// loadNavBack(null);
		//
		bindService = new BindService(this, this, GameService.class);
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		Intent i = this.getIntent();
		String defaultUrl = service.categoryUrl;
		if (i != null) {
			String url = i.getStringExtra(ContantValue.EXTRA_URL);
			if (!TextUtils.isEmpty(url)) {
				defaultUrl = url;
			}
		}
		service.categoryUrl = defaultUrl;
		//
		if (isMain && !TextUtils.isEmpty(loginUrl)) {
			requestItem(loginUrl, Commond.PAGE_LOGIN, true);
		}
		//
		initIntent(service.categoryUrl, Commond.PAGE_CATEGORY,
				R.string.app_name);
		//
		View selectedBgView = findViewById(R.id.top_channel_bg_selected);
		setChannelList(R.id.channellist_grid_1, selectedBgView);
		//
		progressView = this.listView;
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final View v = arg1.findViewById(R.id.item_op);
				// Debug.d("v.getWidth():" + v.getWidth());
				int w = Tools.dip2px(mContext, 64);// v.getWidth();
				// Debug.d("w:" + w);
				if (v.getVisibility() == View.GONE) {
					v.setVisibility(View.VISIBLE);
				} else {
					w = -w;// -v.getWidth();
					v.setVisibility(View.GONE);
				}
				//
				TranslateAnimation anim = new TranslateAnimation(w, 0.0F, 0.0F,
						0.0F);
				ElasticInterpolator in = new ElasticInterpolator(1.0F, 0.3F);
				anim.setInterpolator(in);
				anim.setDuration(1000L);
				arg1.startAnimation(anim);

				return;
			}

		});
		//
		listView.setOnScrollListener(new OnScrollListenerEx(listView));
	}

	/**
	 * 
	 * @param channel
	 */
	private void requestGame(RSSItem item) {
		if (updateThread == null) {
			updateThread = new UpdateThread(this, service, true);
			updateThread.startRequest();
		}
		// 下载
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(Commond.PAGE_FILE);

		entity.getItems().put("url", item.getImageUrl());
		if (!TextUtils.isEmpty(item.getFeedback()))
			entity.getItems().put("fb", item.getFeedback());
		entity.getItems().put("obj", item);
		ItemData itemData = (ItemData) item.getTag();
		itemData.progress = (String) this.getText(R.string.wait);
		sendChangeMsg(itemData.tag, itemData.progress);
		updateThread.addRequest(entity);
	}

	/**
	 * 
	 */
	@Override
	public void onUpdateRequest(BaseService service, RequestDataEntity entity) {
		// TODO Auto-generated method stub
		int pageid = entity.getPageID();
		switch (pageid) {
		case Commond.PAGE_FILE:
			// 表示网络请求开始,显示等待状态
			// Commond.sendProcessingMessage(mHandler);
			//
			String url = (String) entity.getItems().get("url");
			Object obj = entity.getItems().get("obj");

			service.requestFile(obj, mDownloadListener, url);
			// 发送网络请求处理完毕的消息
			// Commond.sendProcessedMessage(mHandler, null, pageid, url);
			//
			String filename = service.getExitFileName(url);
			if (installApk(filename)) {
				String fb = (String) entity.getItems().get("fb");
				//
				if (!TextUtils.isEmpty(fb))
					requestItem(fb, Commond.PAGE_DOWN, false);
			}
			return;
		}
		super.onUpdateRequest(service, entity);
	}

	/**
	 * 
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear)
			throws Exception {
		//
		switch (pageId) {
		case Commond.PAGE_CATEGORY:
			loadCategory(url);
			break;
		case Commond.PAGE_CHANNEL:
			loadChannel(url, isClear);
			break;
		case Commond.PAGE_IMAGE:
			ImageView iv = (ImageView) listView.findViewWithTag(url);
			if (iv != null) {
				iv.setImageBitmap(service.getCacheBitmap(url));
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear);
	}

	/**
	 * 
	 */
	private void loadCategory(String url) {
		//
		RSSChannel channel = service.getChannel(url);
		if (channel == null || channel.getItems().size() == 0) {
			bindService.service.delFile(url);
			showTipIcon(url, Commond.PAGE_CATEGORY);
			return;
		}
		hideTipIcon();
		// Commond.copyright = channel.getCopyright();
		//
		int size = channel.getItems().size();
		if (size > 1) {
			this.loadNavSearch(R.string.search_title, R.string.search_hint);
			this.loadNavBar();
		}
		//
		this.service.adCategory = channel.getCategory();
		AdUtils.setShowAd(this, channel.getCategory(), AdUtils.PAGE_LIST, 0);
		//
		//
		View v = findViewById(R.id.channellist_scroll_panel);
		if (size > 1) {
			v.setVisibility(View.VISIBLE);
			//
			View view = this.findViewById(R.id.channellist_panel_);
			ViewGroup.LayoutParams params = view.getLayoutParams();
			int width = size * params.width;
			//
			View vLeft = this.findViewById(R.id.channel_left_panel);
			View vRight = this.findViewById(R.id.channel_right_panel);
			int colw = 0;
			if (width > Commond.sW) {
				HorizontalScrollViewEx scrollpanel = (HorizontalScrollViewEx) this
						.findViewById(R.id.channellist_scroll_view);
				scrollpanel.initLeftRightView(vLeft, vRight);
				vRight.setVisibility(View.VISIBLE);
				params.width = width;
				colw = width;
			} else {
				vLeft.setVisibility(View.GONE);
				vRight.setVisibility(View.GONE);
				View selectedBgView = findViewById(R.id.top_channel_bg_selected);
				colw = Commond.sW / size;
				selectedBgView.getLayoutParams().width = colw;
				params.width = Commond.sW;
			}
			GridView gv = (GridView) findViewById(R.id.channellist_grid_1);
			if (gv != null) {
				gv.setColumnWidth(colw);
			}
		} else {
			v.setVisibility(View.GONE);
		}
		v.requestLayout();
		//
		GridView gv = (GridView) findViewById(R.id.channellist_grid_1);
		gv.setNumColumns(channel.getItems().size());
		ChannelAdapter adapter = new ChannelAdapter(MarketListActivity_1.this,
				service, channel, R.layout.channel_grid_item_1);
		gv.setAdapter(adapter);
		// Commond.showChannelList(this, false);
		//
		if (size > 0) {
			RSSItem item = channel.getItems().get(0);
			requestItem(item.getLink(), Commond.PAGE_CHANNEL, true);
		}
	}

	/**
	 * 
	 */
	private void loadChannel(String url, boolean isClear) {
		if (mMarketAdapter == null)
			mMarketAdapter = new MarketItemAdapter(this, service,
					R.layout.app_market_item, downListener, favListener,
					voteListener);
		//
		RSSChannel channel = service.getChannel(url);
		if (channel == null || channel.getItems().size() == 0) {
			if (mMarketAdapter.getCount() == 0) {
				bindService.service.delFile(url);
				showTipIcon(url, Commond.PAGE_CHANNEL);
			}
			return;
		}
		hideTipIcon();
		this.mChannel = channel;
		//
		// this.setNavText(mChannel.getTitle());
		//
		mMarketAdapter.setChannel(mChannel, showItemList, isClear);
		if (isClear || listView.getAdapter() == null) {
			listView.setAdapter(mMarketAdapter);
		} else {
			mMarketAdapter.notifyDataSetChanged();
		}
		//
		requestImages(mChannel, false);
	}

	/**
	 * 
	 */
	public void clearListView() {
		if (mMarketAdapter != null) {
			mMarketAdapter.clearListView();
		}
	}

	/**
	 * 
	 */
	public OnClickListener downListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RSSItem item = (RSSItem) v.getTag();
			// TODO Auto-generated method stub
			// 判断应用程序是否被安装了
			String pkg = item.getCategory();
			boolean isstalled = Commond.apkInstalled(mContext, pkg, true);
			if (isstalled)
				return;
			service.delFile(item.getImageUrl());
			// 请求下载
			String[] title = item.getTitle().split("_");
			if (Commond.downloadApk(mContext, title[0], service.rootPath,
					item.getImageUrl())) {
				TextView tvProgress = (TextView) v
						.findViewById(R.id.item_progress);
				if (tvProgress != null)
					tvProgress.setText(R.string.downloading);
				// 发送请求反馈
				if (!TextUtils.isEmpty(item.getFeedback()))
					requestItem(item.getFeedback(), Commond.PAGE_DOWN, false);
			}
		}
	};
	/**
	 * 
	 */
	public OnClickListener favListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RSSItem item = (RSSItem) v.getTag();
			boolean isFav = Data.getFavList(mContext).contains(
					item.getCategory());
			ImageView iv = (ImageView) v.findViewById(R.id.item_fav);
			TextView tv = (TextView) v.findViewById(R.id.tv_fav);
			String fav = "0";
			if (isFav) {
				Data.getFavList(mContext).remove(item.getCategory());
				iv.setBackgroundResource(R.drawable.fav_background);
				tv.setText(R.string.fav_text);
				fav = "0";
			} else {
				Data.getFavList(mContext).add(item.getCategory());
				iv.setBackgroundResource(R.drawable.favdel_background);
				tv.setText(R.string.faved_text);
				fav = "1";
			}
			Data.saveFavList(mContext);
			// 向服务器发送请求
			String tmp = item.getComments();
			if (!TextUtils.isEmpty(tmp)) {
				String[] tmps = tmp.split("@@");
				if (tmps.length > 0) {
					String url = tmps[0];
					url = StringUtil.appendNameValue(url, "fav", fav);
					requestItem(url, Commond.PAGE_FAV, false);
				}
			}
		}
	};
	/**
	 * 
	 */
	public OnClickListener voteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RSSItem item = (RSSItem) v.getTag();
			boolean isVote = Data.getVoteList(mContext).contains(
					item.getCategory());
			ImageView iv = (ImageView) v.findViewById(R.id.item_vote);
			TextView tv = (TextView) v.findViewById(R.id.tv_vote);
			String vote = "0";
			if (isVote) {
				Data.getVoteList(mContext).remove(item.getCategory());
				iv.setBackgroundResource(R.drawable.vote_background);
				tv.setText(R.string.vote_text);
				vote = "0";
			} else {
				Data.getVoteList(mContext).add(item.getCategory());
				iv.setBackgroundResource(R.drawable.votedel_background);
				tv.setText(R.string.voted_text);
				vote = "1";
			}
			Data.saveVoteList(mContext);
			// 向服务器发送请求
			String tmp = item.getComments();
			if (!TextUtils.isEmpty(tmp)) {
				String[] tmps = tmp.split("@@");
				if (tmps.length > 1) {
					String url = tmps[1];
					url = StringUtil.appendNameValue(url, "vote", vote);
					requestItem(url, Commond.PAGE_VOTE, false);
				}
			}
		}
	};

	/**
	 * 
	 */
	public void outAndIn(String url, int resid, boolean isRightOutLeftIn) {
		Intent i = new Intent(this, MarketListActivity_1.class);
		i.putExtra("initUrl", url);
		i.putExtra("initTabResId", resid);
		startActivity(i);

		/* Finish splash activity so user cant go back to it. */
		this.needStopService = false;
		this.finish();

		/*
		 * Apply our splash exit (fade out) and main entry (fade in) animation
		 * transitions.
		 */
		// @@1.5
		// overridePendingTransition(R.anim.gotoprevin, R.anim.gotoprevout);
		try {
			Method method = Activity.class.getMethod(
					"overridePendingTransition", new Class[] { int.class,
							int.class });
			if (isRightOutLeftIn) {
				method.invoke(this, R.anim.gotoprevin, R.anim.gotoprevout);
			} else {
				method.invoke(this, R.anim.gotonextin, R.anim.gotonextout);
			}
		} catch (Exception e) {
			// Can't change animation, so do nothing
		}
	}

	/**
	 * 
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		if (Commond.hideChannelSearchPanel(e, this)) {
			return true;
		}
		return super.dispatchTouchEvent(e);
	}

	/**
	 * 
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		GotoActivity.gotoAbout(mContext);
		return true;
	}

	@Override
	public void onDestroy() {
		//
		// android.os.Debug.stopMethodTracing();
		super.onDestroy();
	}
}