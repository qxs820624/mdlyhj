package com.angeeks.market;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSImage;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.Debug;

import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.AppInfo;
import com.hutuchong.app_game.service.GameService;
import com.hutuchong.app_game.util.MarketCommond;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;

public class MarketDetailActivity extends BaseActivity implements
		ActivityHandle {
	Context mContext;
	RSSItem mItem = null;
	String initUrl = null;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;
	AppInfo appInfo;
	//
	LinearLayout llPanel1;
	LinearLayout llPanel2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// Commond.initAppList(mContext, mHandler);
		super.init(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_market_detail);
		//
		llPanel1 = (LinearLayout) findViewById(R.id.scroll_panel_1);
		llPanel2 = (LinearLayout) findViewById(R.id.scroll_panel_2);
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
		if (i != null) {
			int index = i.getIntExtra("index", -1);
			if (index >= 0) {
				appInfo = (AppInfo) Commond.showItemListRef.get(index).getTag();
				initUrl = appInfo.linkUrl;
			}
		}
		if (appInfo == null)
			appInfo = new AppInfo();

		initIntent(initUrl, Commond.PAGE_ITEM, R.string.app_name);
		//
		ImageButton ibBack = (ImageButton) findViewById(R.id.btn_home);
		ibBack.setImageBitmap(null);
		MarketCommond.loadBackBtn(this, R.id.btn_home,
				R.drawable.btn_back_background);
		MarketCommond.loadAdminTab(this, true);
		MarketCommond.loadSearchTab(this, true);
		loadNavBar();
		//
		loadOptionMenu();
		//
		initTopPanel();
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
		case Commond.PAGE_ITEM:
			loadItem(url);
			break;
		case Commond.PAGE_ICON:
			Bitmap bm = service.getCacheBitmap(url);
			if (bm != null) {
				ImageView iv1 = (ImageView) llPanel1.findViewWithTag(url);
				if (iv1 != null) {
					iv1.setImageBitmap(bm);
				}
				//
				ImageView iv2 = (ImageView) llPanel2.findViewWithTag(url);
				if (iv2 != null) {
					iv2.setImageBitmap(bm);
				}
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear);
	}

	/**
	 * 
	 */
	private void loadItem(String url) {
		RSSChannel channel = service.getChannel(url);
		//
		if (channel == null || channel.getItems().size() == 0) {
			bindService.service.delFile(url);
			showTipIcon(url, Commond.PAGE_ITEM);
			return;
		}
		this.mChannel = channel;
		hideTipIcon();
		//
		View vPanel = findViewById(R.id.sv_detail_panel);
		//
		TextView tvDesc = (TextView) vPanel.findViewById(R.id.tv_detail_desc);
		String desc = mChannel.getDescription();
		desc = brToN(desc);
		mChannel.setDescription(desc);
		//
		tvDesc.setText(desc);
		//
		TextView tvPermission = (TextView) vPanel
				.findViewById(R.id.tv_detail_permission);
		String copy = mChannel.getCopyright();
		copy = brToN(copy);
		mChannel.setCopyright(copy);
		tvPermission.setText(copy);
		//
		vPanel.setVisibility(View.VISIBLE);
		//
		llPanel1.removeAllViews();
		// 截图
		RSSImage rssImage = mChannel.getRSSImage();
		if (rssImage != null && !TextUtils.isEmpty(rssImage.getLink())) {
			Debug.d("rssImage.getLink():" + rssImage.getLink());
			String[] images = rssImage.getLink().split("\\|");
			for (String imageUrl : images) {
				ImageView iv = new ImageView(mContext);
				iv.setTag(imageUrl);
				LayoutParams lp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.FILL_PARENT);
				llPanel1.addView(iv, lp);
				//
				requestImage(imageUrl, Commond.PAGE_ICON);
			}
		}
		// 相关软件
		llPanel2.removeAllViews();
		for (RSSItem item : mChannel.getItems()) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = vi.inflate(R.layout.app_market_detail_item, null);
			//
			View ivIcon = itemView.findViewById(R.id.iv_icon);
			ivIcon.setTag(item.getThumbailUrl());
			//
			TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
			tvTitle.setText(item.getTitle());
			//
			llPanel2.addView(itemView);
			//
			requestImage(item.getThumbailUrl(), Commond.PAGE_ICON);
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String brToN(String str) {
		String result = str;
		String[] strs = str.split("<br />");
		if (strs.length > 1) {
			StringBuffer sb = new StringBuffer();
			int len = strs.length;
			for (int i = 0; i < len; i++) {
				sb = sb.append(strs[i]);
				if (i < len - 1)
					sb = sb.append('\n');
			}
			result = sb.toString();
		}
		return result;
	}

	/**
	 * 
	 */
	private void initTopPanel() {
		ImageView iv = (ImageView) findViewById(R.id.iv_detail_icon);
		if (appInfo.appBitmap == null) {
			appInfo.appBitmap = service.getCacheBitmap(appInfo.iconUrl);
		}
		if (appInfo.appBitmap != null) {
			iv.setImageBitmap(appInfo.appBitmap);
		} else {
			iv.setImageResource(R.drawable.icon_bg);
		}
		//
		TextView tv_title = (TextView) findViewById(R.id.tv_detail_title);
		tv_title.setText(appInfo.appName);
		//
		StringBuffer info = new StringBuffer();
		info = info.append("版本：");
		info = info.append(appInfo.versionName);
		info = info.append("  大小：");
		info = info.append(appInfo.sizeStr);
		info = info.append('\n');
		info = info.append("下载次数：");
		info = info.append(appInfo.downCount);
		TextView tv_info = (TextView) findViewById(R.id.tv_detail_info);
		tv_info.setText(info.toString());
		//
		final TextView tvDownText = (TextView) findViewById(R.id.tv_down_text);
		boolean isInstalled = Commond.appList.containsKey(appInfo.packageName);
		if (isInstalled) {
			tvDownText.setText("更新");
		} else {
			tvDownText.setText("下载");
		}
		View downPanel = findViewById(R.id.ll_down_panel);
		downPanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Commond.downloadApk(mContext, appInfo.appName,
						service.rootPath, appInfo.apkUrl)) {
					tvDownText.setText(R.string.downloading);
				}
			}
		});
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