package com.angeeks.market;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import cn.coolworks.handle.ActivityHandle;

import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.adapter.CategoryItemAdapter;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.app_game.util.MarketCommond;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;

public class MarketCategoryActivity extends BaseActivity implements
		ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;
	//
	int resId = -1;
	//
	ListView listView;
	CategoryItemAdapter itemAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_market_category_list);
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
		this.needStopService = true;
		this.needBackConfirm = true;
		//
		MarketCommond.loadHomeTab(this);
		MarketCommond.loadAdminTab(this, true);
		MarketCommond.loadSearchTab(this, true);
		loadNavBar();
		Intent i = this.getIntent();
		if (i != null) {
			resId = i.getIntExtra("resid", resId);
		}
		MarketCommond.switchNavBar(this, resId);
		loadOptionMenu();
		loadNavMenu();
		//
		if (itemAdapter == null) {
			itemAdapter = new CategoryItemAdapter(mContext,
					bindService.service, R.layout.app_market_category_item);
		}
		listView.setAdapter(itemAdapter);
		//
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (mChannel == null)
					return;
				String url = mChannel.getItem(arg2).getLink();
				Intent i = new Intent(mContext, MarketListActivity.class);
				i.putExtra(ContantValue.EXTRA_URL, url);
				i.putExtra("resid", resId);
				mContext.startActivity(i);
			}
		});
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