package com.angeeks.market;

import java.util.ArrayList;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.StringUtil;

import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.AppInfo;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;

public class MarketHomeActivity_1 extends BaseActivity implements ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	String initUrl = "http://market.moreapp.cn/3g/items.php?pid=7";
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;
	//
	ArrayList<View> mItemViews = new ArrayList<View>();
	ArrayList<TableRow> mItemParents = new ArrayList<TableRow>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		//Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_markethome);
		appFlash(true);
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
		this.needStopService = true;
		this.needBackConfirm = true;
		//
		loadNavBar();
		loadOptionMenu();
		loadNavMenu();
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
			AppInfo entity = new AppInfo();
			item.setTag(entity);
			//
			String title = item.getTitle();
			String[] tmps = title.split("_");
			if (tmps.length < 3)
				return;
			//
			entity.appName = tmps[0];
			entity.desc = tmps[1];
			entity.sizeStr = "版本：" + StringUtil.readableFileSize(tmps[2]);
			entity.versionName = item.getFavLink();
		}
		initTableView();
		loadTableView();
		//
		requestImages(mChannel, false);
	}

	private final int WC = ViewGroup.LayoutParams.FILL_PARENT;
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	private void initTableView() {
		if (mChannel == null)
			return;
		for (int cell = 0; cell < mChannel.getItems().size(); cell++) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = vi.inflate(R.layout.app_markethome_grid_item, null);
			if (cell % 4 == 2 || cell % 4 == 3)
				itemView.setBackgroundResource(R.drawable.list_item_background_0);
			else
				itemView.setBackgroundResource(R.drawable.list_item_background_1);
			itemView.setClickable(true);
			// //////////////////////////////////////////
			mItemViews.add(itemView);
			itemView.setTag(cell);
			setTableItem(itemView);
		}
	}

	/**
	 * 
	 */
	private void loadTableView() {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableview);
		for (TableRow rowView : mItemParents)
			rowView.removeAllViews();
		tableLayout.removeAllViews();
		//
		TableRow tableRow = null;
		int cell = 0;
		for (View itemView : mItemViews) {
			int index = (Integer) itemView.getTag();
			RSSItem item = mChannel.getItem(index);
			if (item.isVisible()) {
				// //////////////////////////////////////////
				if (cell % 2 == 0) {
					tableRow = new TableRow(this);
					// 新建的TableRow添加到TableLayout
					LayoutParams lp = new TableLayout.LayoutParams(FP, WC);
					// lp.topMargin = 10;
					// lp.leftMargin = 5;
					tableLayout.addView(tableRow, lp);
				}
				tableRow.addView(itemView);
				mItemParents.add(tableRow);
				cell++;
			}
		}
	}

	private void setTableItem(View itemView) {
		int index = (Integer) itemView.getTag();
		ImageView ivIcon = (ImageView) itemView.findViewById(R.id.item_icon);
		TextView tvText1 = (TextView) itemView.findViewById(R.id.item_text_1);
		TextView tvText2 = (TextView) itemView.findViewById(R.id.item_text_2);
		RSSItem item = mChannel.getItem(index);
		AppInfo entity = (AppInfo) item.getTag();
		//
		if (!TextUtils.isEmpty(item.getThumbailUrl())) {
			if (entity.appBitmap == null) {
				entity.appBitmap = service
						.getCacheBitmap(item.getThumbailUrl());
			}
			ivIcon.setImageBitmap(entity.appBitmap);
		}
		tvText1.setText(entity.sizeStr);
		tvText2.setText(entity.appName);
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