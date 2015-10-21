package com.angeeks.market;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.GridView;
import cn.coolworks.handle.ActivityHandle;

import mobi.domore.mcdonalds.R;
import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.app_game.util.MarketCommond;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;

public class MarketSearchActivity extends BaseActivity implements
		ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	String initUrl = "";
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_market_search);
		//
		bindService = new BindService(this, this, GameService.class);
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		initIntent(initUrl, Commond.PAGE_CHANNEL, R.string.app_name);
		//
		MarketCommond.loadHomeTab(this);
		MarketCommond.loadAdminTab(this,true);
		MarketCommond.loadSearchTab(this,false);
		loadNavBar();
		Intent i = this.getIntent();
		int resId = -1;
		if (i != null) {
			resId = i.getIntExtra("resid", resId);
		}
		MarketCommond.switchNavBar(this, resId);
		loadOptionMenu();
		loadNavMenu();
		//
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