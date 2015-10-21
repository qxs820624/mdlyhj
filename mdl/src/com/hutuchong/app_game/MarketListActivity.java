package com.hutuchong.app_game;

import java.lang.reflect.Method;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.RequestDataEntity;
import cn.coolworks.util.StringUtil;
import cn.coolworks.util.Tools;
import cn.coolworks.util.UpdateThread;

import com.angeeks.market.ManageActivity;
import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.adapter.MarketItemAdapter;
import com.hutuchong.app_game.assist.ElasticInterpolator;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.data.Data;
import com.hutuchong.util.AdUtils;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BaseService;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.GotoActivity;
import com.hutuchong.util.ItemData;
import com.jw.http.Connect;

public class MarketListActivity extends BaseActivity implements ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	ListView listView;
	MarketItemAdapter mMarketAdapter;
	String categoryUrl;
	String favUrl;
	String newestUrl;
	String searchUrl;
	String topUrl;
	String hotUrl;
	String loginUrl;
	int selectedTabId;
	String initUrl;
	int initTabResId;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_marketlist);
		//
		initTab();
		//
		Intent i = this.getIntent();
		initUrl = i.getStringExtra("initUrl");
		if (TextUtils.isEmpty(initUrl)) {
			initUrl = newestUrl;
			loginUrl = Data.getTabList(mContext).get("login")[1];
			appFlash(true);
		} else {
			appFlash(false);
		}
		initTabResId = i.getIntExtra("initTabResId", R.id.btn_tab_1);
		//
		listView = (ListView) findViewById(R.id.listview);
		// loadNavBack(null);
		//
		bindService = new BindService(this, this, GameService.class);
	}

	/**
	 * 
	 */
	private void initTab() {
		//
		String[] values = Data.getTabList(mContext).get("category");
		// String categoryTitle = values[0];
		categoryUrl = values[1];
		//
		values = Data.getTabList(mContext).get("favlist");
		// String favTitle = values[0];
		favUrl = values[1];
		//
		values = Data.getTabList(mContext).get("search");
		// String searchTitle = values[0];
		searchUrl = values[1];
		//
		values = Data.getTabList(mContext).get("newest");
		String newestTitle = values[0];
		newestUrl = values[1];
		//
		values = Data.getTabList(mContext).get("top");
		String topTitle = values[0];
		topUrl = values[1];
		//
		values = Data.getTabList(mContext).get("hot");
		String hotTitle = values[0];
		hotUrl = values[1];
		//
		Button btn1 = (Button) this.findViewById(R.id.btn_tab_1);
		btn1.setText(newestTitle);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// clearListView();
				// requestItem(newestUrl, Commond.PAGE_CHANNEL, true);
				// switchTabBg(R.id.btn_tab_1);
				if (initTabResId == R.id.btn_tab_1)
					return;
				switchTabBg(R.id.btn_tab_1);
				outAndIn(newestUrl, R.id.btn_tab_1, true);

			}
		});
		//
		Button btn2 = (Button) this.findViewById(R.id.btn_tab_2);
		btn2.setText(topTitle);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// clearListView();
				// requestItem(topUrl, Commond.PAGE_CHANNEL, true);
				// switchTabBg(R.id.btn_tab_2);
				if (initTabResId == R.id.btn_tab_2)
					return;
				switchTabBg(R.id.btn_tab_2);
				if (initTabResId == R.id.btn_tab_1)
					outAndIn(topUrl, R.id.btn_tab_2, true);
				else
					outAndIn(topUrl, R.id.btn_tab_2, false);
			}

		});
		//
		Button btn3 = (Button) this.findViewById(R.id.btn_tab_3);
		btn3.setText(hotTitle);
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// clearListView();
				// requestItem(hotUrl, Commond.PAGE_CHANNEL, true);
				// switchTabBg(R.id.btn_tab_3);
				if (initTabResId == R.id.btn_tab_3)
					return;
				switchTabBg(R.id.btn_tab_3);
				outAndIn(hotUrl, R.id.btn_tab_3, false);
			}

		});
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		service.categoryUrl = categoryUrl;
		service.favUrl = favUrl;
		service.searchUrl = searchUrl;
		//
		if (!TextUtils.isEmpty(loginUrl)) {
			requestItem(loginUrl, Commond.PAGE_LOGIN, true);
		}
		//
		switchTabBg(initTabResId);
		requestItem(initUrl, Commond.PAGE_CHANNEL, true);
		//
		initIntent(service.categoryUrl, Commond.PAGE_CATEGORY,
				R.string.app_name);
		//
		this.needStopService = true;
		this.needBackConfirm = true;
		//
		View selectedBgView = findViewById(R.id.top_channel_bg_selected);
		setChannelList(R.id.optionmenu_popup_grid, selectedBgView);
		//
		loadNavFav();
		loadNavBar();
		loadOptionMenu();
		loadNavMenu();
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
		this.loadListFooterView(listView);

	}

	/**
	 * 
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (mMarketAdapter != null)
					mMarketAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 
	 */
	public void setFavText() {
		clearListView();
		switchTabBg(R.id.fav_title_panel);
	}

	/**
	 * 
	 * @param input
	 */
	public void onNavSearch(String input) {
		clearListView();
		switchTabBg(R.id.search_title_panel);
		String url = StringUtil.appendNameValue(service.searchUrl, "input",
				Connect.urlEncode(input));
		requestItem(url, Commond.PAGE_CHANNEL, true);
	}

	/**
	 * 
	 * @param channel
	 */
	public void requestGame(RSSItem item) {
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
			this.showListFooterView(listView, false);
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
		if (channel == null || channel.getItems() == null
				|| channel.getItems().size() == 0) {
			bindService.service.delFile(url);
			showTipIcon(url, Commond.PAGE_CATEGORY);
			return;
		}
		//
		// Commond.copyright = channel.getCopyright();
		if (channel.getItems().size() > 1) {

		}
		//
		this.service.adCategory = channel.getCategory();
		AdUtils.setShowAd(this, channel.getCategory(), AdUtils.PAGE_LIST, 0);
		//
		// GridView gv = (GridView) findViewById(R.id.optionmenu_popup_grid);
		// ChannelAdapter adapter = new ChannelAdapter(MarketListActivity.this,
		// service, channel, R.layout.optionmenu_popup);
		// gv.setAdapter(adapter);
		//
		// Commond.showChannelList(this, false);
		//
		// if (channel.getItems() != null && channel.getItems().size() > 0) {
		// RSSItem item = channel.getItems().get(0);
		// requestItem(item.getLink(), Commond.PAGE_CHANNEL, true);
		// //
		// setCategoryText(item.getTitle());
		// }
	}

	/**
	 * 
	 * @param tabId
	 */
	public void switchTabBg(int tabId) {
		if (selectedTabId > 0)
			setTabBg(selectedTabId, false);
		setTabBg(tabId, true);
		//
		selectedTabId = tabId;
	}

	/**
	 * 
	 * @param tabId
	 * @param isSelected
	 */
	private void setTabBg(int tabId, boolean isSelected) {
		View v = this.findViewById(tabId);
		if (isSelected)
			v.setBackgroundResource(R.drawable.tab_selected_bg);
		else
			v.setBackgroundResource(R.drawable.tab_background);
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
		if (channel == null) {
			if (mMarketAdapter.getCount() == 0) {
				bindService.service.delFile(url);
				showTipIcon(url, Commond.PAGE_CHANNEL);
			}
			return;
		}
		this.mChannel = channel;
		//
		this.setNavText(mChannel.getTitle());
		//
		mMarketAdapter.setChannel(mChannel, showItemList, isClear);
		if (isClear) {
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
		Intent i = new Intent(this, MarketListActivity.class);
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
	public void loadOptionMenu() {
		//
		int[] optionIconIds = { R.drawable.menu_help, R.drawable.menu_nopic,
				R.drawable.menu_refresh, R.drawable.menu_msg,
				R.drawable.menu_ver, R.drawable.menu_more,
				R.drawable.menu_about, R.drawable.menu_exit };
		int[] optionIconTitles = { R.string.menu_help, R.string.menu_nopic,
				R.string.menu_refresh, R.string.menu_msg, R.string.menu_ver,
				R.string.menu_moreapp, R.string.menu_about, R.string.menu_exit };

		optionGridView = (GridView) findViewById(R.id.optionmenu_popup_grid);
		optionGridView.setVisibility(View.INVISIBLE);
		optionsAdapter = new OptionMenuAdapter(mContext,
				R.layout.optionmenu_item, optionIconIds, optionIconTitles);
		optionGridView.setNumColumns(4);
		optionGridView.setAdapter(optionsAdapter);
		optionGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					Intent i = new Intent(mContext, ManageActivity.class);
					mContext.startActivity(i);
					break;
				case 6:
					GotoActivity.gotoAbout(mContext);
					break;
				case 7:
					finish();
					break;
				}
				Commond.showOptionMenu(MarketListActivity.this, false);
			}
		});
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

	@Override
	public void onDestroy() {
		//
		// android.os.Debug.stopMethodTracing();
		super.onDestroy();
	}
}