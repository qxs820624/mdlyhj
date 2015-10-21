package com.angeeks.market;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.coolworks.handle.ActivityHandle;

import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.AppInfo;
import com.hutuchong.app_game.adapter.ListItemAdapter;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.app_game.util.MarketCommond;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;

public class MarketListActivity extends BaseActivity implements ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;
	//
	int resId = -1;
	//
	ListView listView;
	ListItemAdapter itemAdapter;
	//
	int currentTabId = 0;
	int tabIds[] = { R.id.btn_tab_1, R.id.btn_tab_2, R.id.btn_tab_3 };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_market_list);
		Intent i = this.getIntent();
		String action = i.getAction();
		boolean isMain = "android.intent.action.MAIN".equals(action);
		appFlash(isMain);
		listView = (ListView) findViewById(R.id.listview);
		//
		bindService = new BindService(this, this, GameService.class);
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		initIntent(null, Commond.PAGE_CHANNEL, R.string.app_name);
		//
		MarketCommond.loadAdminTab(this, true);
		MarketCommond.loadSearchTab(this, true);
		loadNavBar();
		Intent i = this.getIntent();
		if (i != null) {
			resId = i.getIntExtra("resid", resId);
		}
		MarketCommond.switchNavBar(this, resId);
		//
		if (resId == MarketCommond.resTabIds[0]
				|| resId == MarketCommond.resTabIds[1]) {
			ImageButton ibBack = (ImageButton) findViewById(R.id.btn_home);
			ibBack.setImageBitmap(null);
			MarketCommond.loadBackBtn(this, R.id.btn_home,
					R.drawable.btn_back_background);
			//
			this.needStopService = false;
			this.needBackConfirm = false;
		} else {
			MarketCommond.loadHomeTab(this);
			//
			this.needStopService = true;
			this.needBackConfirm = true;
		}
		//
		loadOptionMenu();
		loadNavMenu();
		//
		if (itemAdapter == null) {
			itemAdapter = new ListItemAdapter(mContext, bindService.service,
					R.layout.app_market_list_item);
		}
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Commond.showItemListRef = mChannel.getItems();
				Intent i = new Intent(mContext, MarketDetailActivity.class);
				i.putExtra("index", arg2);
				mContext.startActivity(i);
			}
		});
		//

		//
		initTabs();
		switchTab(R.id.btn_tab_1);
	}

	/**
	 * 
	 */
	private void initTabs() {
		for (int i = 0; i < tabIds.length; i++) {
			View vTab = findViewById(tabIds[i]);
			vTab.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (currentTabId != arg0.getId()) {
						currentTabId = arg0.getId();
						switchTab(arg0.getId());
					}
				}

			});
		}
	}

	/**
	 * 
	 * @param id
	 */
	private void switchTab(int id) {
		for (int i = 0; i < tabIds.length; i++) {

			View vTab = findViewById(tabIds[i]);
			if (id == tabIds[i]) {
				requestItem(MarketCommond.listTabUrls[i], Commond.PAGE_CHANNEL,
						true);
				vTab.setBackgroundResource(R.drawable.tab_selected_bg);
				itemAdapter.clearListView();
			} else {
				vTab.setBackgroundResource(R.drawable.tab_background);
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear)
			throws Exception {
		//
		switch (pageId) {
		case Commond.PAGE_CHANNEL:
			loadChannel(url, isClear);
			break;
		case Commond.PAGE_ICON:

			break;
		}
		super.loadUpdateThread(url, pageId, isClear);
	}

	/**
	 * 
	 */
	private void loadChannel(String url, boolean isClear) {
		RSSChannel channel = service.getChannel(url);
		//
		if (channel == null || channel.getItems().size() == 0) {
			bindService.service.delFile(url);
			showTipIcon(url, Commond.PAGE_CHANNEL);
			return;
		}
		this.mChannel = channel;
		hideTipIcon();
		//
		for (RSSItem item : mChannel.getItems()) {
			if (item.getTag() == null)
				item.setTag(new AppInfo(item));
		}
		itemAdapter.setChannel(mChannel, isClear);
		itemAdapter.notifyDataSetChanged();
		//
		requestImages(mChannel, false);
	}

	/**
	 * 
	 */
	public void loadOptionMenu() {
		//
		final int[] optionIconIds = { /* R.drawable.menu_help, */
		R.drawable.menu_nopic, R.drawable.menu_refresh,
		/* R.drawable.menu_msg, */R.drawable.menu_ver, /* R.drawable.menu_more, */
		/* R.drawable.menu_about, */R.drawable.menu_exit };
		final int[] optionIconTitles = { /* R.string.menu_help, */
		R.string.menu_nopic, R.string.menu_refresh,
		/* R.string.menu_msg, */R.string.menu_ver, /* R.string.menu_moreapp, */
		/* R.string.menu_about, */R.string.menu_exit };
		//
		loadOptionMenu(null, optionIconIds, optionIconTitles);
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