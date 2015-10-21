package com.hutuchong.app_mcdonalds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.Debug;
import cn.coolworks.util.StringUtil;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.example.mymusic.MusicChartListActivity;
import com.example.uitl.GetPublicKey;
import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;
import com.hutuchong.app_mcdonalds.service.McdonaldsService;
import com.hutuchong.app_user.db.DBAdapter;
import com.hutuchong.app_user.service.UserService;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;

public class McdonaldsActivity extends BaseActivity implements ActivityHandle {
	int screenWidth;
	int screenHeight;
	boolean isMain;
	DBAdapter db = new DBAdapter(this);
	ImageButton mNavNewsItem;
	ImageButton mNavSoftItem;
	ImageButton mNavFotoItem;
	ImageButton mNavBookItem;
	String[] hosts = { "http://xlb.jianrencun.com/mcdonalds/",
			"http://xlb.jianrencun.com/mcdonalds/" };
	String homeUrl = "client_mcdonalds.php";
	String navUrl = "client_mcdonalds_navlist.php";
	final int PAGE_NAV_LIST = -1;
	int mTabIndex = 0;
	ImageDialog imageDialog;
	NavListDialog navDialog;
	int[] optionIconId;
	String[] optionTitleId;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;
	//
	TableLayout tableLayout;

	HashMap<String, Boolean[]> mAllNames = new HashMap<String, Boolean[]>();
	ArrayList<View> mItemViews = new ArrayList<View>();
	ArrayList<TableRow> mItemParents = new ArrayList<TableRow>();
	private UIHandler mUIHandler = new UIHandler();
	private ProgressDialog dialog;
	private long requestTime;
	private ProgressDialog mProgress = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		int hostindex = ((int) (Math.random() * 10)) % (hosts.length);

		String host = hosts[hostindex];
		Debug.d("host:" + host);
		homeUrl = host + homeUrl;
		navUrl = host + navUrl;

		// umeng的自动更新
		// MobclickAgent.update(this);
		// MobclickAgent.setUpdateOnlyWifi(false);
		//837229ad4c293e0e6359986480a040858779775bd049158b0a5f7724d20839df0ad3ef453c33e3d3cb90b46708e3b4574abe1710d966c65c8a4a6994adf38c66cef2da2784994379bfb27b66c6464ab5e6c208716a0f61f20c49a61e35734d387ffec4053b0a8218ee25f39187c2d3327faab7c8cb8a11f26cc424c45bd918ef
		String key = GetPublicKey.getSignInfo(getApplicationContext());
		Log.e("key", key);
		super.init(false);
		Intent uservice = new Intent(mContext, UserService.class);
		mContext.startService(uservice);
		//
		setContentView(R.layout.app_mcdonalds);
		//

		dialog = ProgressDialog.show(McdonaldsActivity.this, null, "请稍候……",
				true, false);
		requestTime = System.currentTimeMillis();
		new Thread(new LoadData()).start();

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
		loadBtnBack(null, R.id.btn_left, View.GONE);
		loadBtnMsg(R.id.btn_right, View.GONE);
		//
		// refleshMessageView(db, R.id.btn_left);
		//
		bindService = new BindService(this, this, McdonaldsService.class);
	}

	class LoadData extends Thread {
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			// if (!InitCmmInterface.initCheck(MainActivity.this)) {
			Hashtable<String, String> b = InitCmmInterface
					.initCmmEnv(McdonaldsActivity.this);
			Message m = new Message();
			m.what = 0;
			m.obj = b;
			mUIHandler.sendMessage(m);
			// } else {
			// if (null != dialog) {
			// dialog.dismiss();
			// }
			//
			// Toast.makeText(MainActivity.this, "已初始化过", Toast.LENGTH_LONG)
			// .show();
			// }
			Looper.loop();
		}
	}

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long responseTime = System.currentTimeMillis() - requestTime;

			switch (msg.what) {
			case 0:
				if (msg.obj == null) {
					hideProgressBar();
					Toast.makeText(McdonaldsActivity.this, "结果 = null",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// new AlertDialog.Builder(MainActivity.this).setTitle("结果")
				// .setMessage((msg.obj).toString())
				// .setPositiveButton("确认", null).show();

				break;
			}
			hideProgressBar();
			if (null != dialog) {
				dialog.dismiss();
			}
		}
	}

	void hideProgressBar() {

		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mProgress != null) {
					mProgress.dismiss();
					mProgress = null;
				}
			}
		});
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		// int pageid = Commond.PAGE_CATEGORY;
		int resTitleId = R.string.app_name;
		// 发送umeng错误消息
		// MobclickAgent.onError(this);
		Commond.loadScroller(this, screenWidth);
		// pageid = Commond.PAGE_CHANNEL;
		// service.categoryUrl = "http://3gqa.com/news_client.php";
		// service.categoryUrl =
		// "http://news.hutudan.com/3g/category.php?parent_id=123";
		// service.favUrl =
		// "http://news.hutudan.com/3g/favlist.php?parent_id=123";
		// service.searchUrl =
		// "http://news.hutudan.com/3g/search.php?parent_id=123";
		//
		if (isMain) {
			String loginUrl = "http://news.hutudan.com/3g/login.php?parent_id=123";
			requestItem(loginUrl, Commond.PAGE_LOGIN, true);
		}
		initIntent(homeUrl, Commond.PAGE_CHANNEL, resTitleId);
		//
		// ImageView ib = (ImageView)
		// this.findViewById(R.id.btn_title_icon);
		// ib.setImageResource(R.drawable.app_news_icon);
		View selectedBgView = findViewById(R.id.top_channel_bg_selected);
		setChannelList(R.id.channellist_grid_1, selectedBgView);
		//
		// gridView.setOnScrollListener(new OnScrollListenerEx());
		//
		View viewDetail = findViewById(R.id.btn_detail);
		viewDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mChannel == null)
					return;
				for (RSSItem item : mChannel.getItems()) {
					McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
					if (entity.isSelected()) {
						DetailDialog detailDialog = new DetailDialog(
								McdonaldsActivity.this, mChannel,
								detailCancelListener);
						detailDialog.show();
						return;
					}
				}
				Commond.showToast(mContext, "目前您还没选中任何套餐!");
			}

		});
		//
		loadNavBar();
	}

	private void setPriceText(String price, String save) {
		TextView tv_price = (TextView) findViewById(R.id.tv_price);
		tv_price.setText("￥" + price);
		TextView tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setText("省￥" + save);
	}

	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public void requestNavList(double lat, double lon) {
		String newurl = StringUtil.appendNameValue(navUrl, "lat",
				Double.toString(lat));
		newurl = StringUtil
				.appendNameValue(newurl, "lon", Double.toString(lon));
		requestItem(newurl, PAGE_NAV_LIST, false);
	}

	/**
	 * 
	 */
	OnClickListener iconListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (Commond.userInfo.isBlockImg()) {
				Commond.showToast(mContext, "当前为无图模式，请在菜单中选中有图模式！");
				return;
			}
			View itemView = (View) v.getTag();
			int index = (Integer) itemView.getTag();
			RSSItem item = mChannel.getItem(index);
			if (item == null || TextUtils.isEmpty(item.getImageUrl()))
				return;
			imageDialog = new ImageDialog(McdonaldsActivity.this);
			imageDialog.show();
			requestItem(item.getImageUrl(), Commond.PAGE_FULL_IMAGE, false);
		}
	};
	/**
	 * 
	 */
	OnClickListener selNumListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			View itemView = (View) v.getTag();
			int index = (Integer) itemView.getTag();
			RSSItem item = mChannel.getItem(index);
			McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
			// if (!entity.isSelected()) {
			// Commond.showToast(mContext, "请先选中该套餐，再选择份数！");
			// return;
			// }
			NumDialog detailDialog = new NumDialog(McdonaldsActivity.this,
					numCancelListener, entity);
			detailDialog.show();
		}
	};
	/**
	 * 
	 */
	OnClickListener selChkListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			View itemView = (View) arg0.getTag();
			int index = (Integer) itemView.getTag();
			RSSItem item = mChannel.getItem(index);

			if (item != null) {
				McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
				if (entity != null) {
					if (entity.getNum() == 0)
						entity.setNum(1);
					if (entity.isSelected())
						entity.setNum(0);
					entity.setSelected(!entity.isSelected());
					setTableItem(itemView);
					//
					String[] prices = McdonaldsEntity.totalPrice(mChannel);
					setPriceText(prices[0], prices[1]);
				}
			}
		}
	};
	OnCancelListener numCancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			for (View itemView : mItemViews) {
				setTableItem(itemView);
			}
			String[] prices = McdonaldsEntity.totalPrice(mChannel);
			setPriceText(prices[0], prices[1]);
		}
	};
	OnCancelListener detailCancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			for (View itemView : mItemViews)
				setTableItem(itemView);
			String[] prices = McdonaldsEntity.totalPrice(mChannel);
			setPriceText(prices[0], prices[1]);
		}
	};
	OnCancelListener filterCancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (mChannel == null)
				return;
			// 判断是否有变化
			boolean isupdate = false;
			for (String key : mAllNames.keySet()) {
				Boolean[] bb = mAllNames.get(key);
				if (bb[0] != bb[1]) {
					isupdate = true;
				}
				bb[0] = bb[1];
			}
			if (!isupdate)
				return;
			//
			for (RSSItem item : mChannel.getItems()) {
				boolean isvisible = false;
				McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
				for (String name : entity.names) {
					Boolean[] bb = mAllNames.get(name);
					if (bb[0]) {
						isvisible = true;
						break;
					}
				}
				item.setVisible(isvisible);
			}
			loadTableView();
			setTitleText(getString(R.string.nav_filter_text));
		}
	};

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
			ImageView iv = (ImageView) tableLayout.findViewWithTag(url);
			if (iv != null) {
				iv.setImageBitmap(service.getCacheBitmap(url));
			}
			// for (View itemView : mItemViews)
			// setTableItem(itemView);
			break;
		case Commond.PAGE_FULL_IMAGE:
			Bitmap bm = service.getCacheBitmap(url);
			imageDialog.showImage(bm);
			break;
		case PAGE_NAV_LIST:
			RSSChannel channel = service.getChannel(url);
			this.navDialog.loadData(channel);
			break;
		}
		super.loadUpdateThread(url, pageId, isClear);
	}

	/**
	 * 
	 */
	public void loadNavFav(final String url) {
		View fav = findViewById(R.id.fav_title_panel);
		if (fav == null)
			return;
		fav.setVisibility(View.VISIBLE);
		fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 删除本地保存的收藏内容，保证每次次进入的时候重新从服务器获取
				bindService.service.delFile(bindService.service.favUrl);
				//
				Intent i = new Intent(mContext, McdonaldsActivity.class);
				i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
				i.putExtra(ContantValue.EXTRA_TITLE,
						mContext.getText(R.string.fav_title));
				i.putExtra(ContantValue.EXTRA_URL, url);
				i.putExtra(ContantValue.EXTRA_FLG, ContantValue.EXTRA_FLG_FAV);
				startActivity(i);
				//
			}
		});
	}

	/**
	 * 
	 */
	private void loadChannel(String url, boolean isClear) {
		Debug.d("loadChannel url:" + url);
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
		if (isClear) {
			mAllNames.clear();
			mItemViews.clear();
			mItemParents.clear();
			String[] prices = McdonaldsEntity.totalPrice(mChannel);
			setPriceText(prices[0], prices[1]);
		}
		// 更多
		Commond.copyright = mChannel.getCopyright();
		loadNavMore(R.id.nav_item_more_panel);
		//
		for (RSSItem item : mChannel.getItems()) {
			McdonaldsEntity entity = new McdonaldsEntity();
			item.setTag(entity);
			//
			String title = item.getTitle();
			String[] tmps = title.split("!!!");
			if (tmps.length < 4)
				return;
			//
			entity.setId(tmps[0]);
			entity.setPrice(Float.valueOf(tmps[1]));
			//
			entity.setSave(Float.valueOf(tmps[2]));
			//
			entity.setName(tmps[3]);
			for (String name : entity.names) {
				Boolean[] bb = new Boolean[2];
				bb[0] = Boolean.FALSE;
				bb[1] = Boolean.FALSE;
				mAllNames.put(name, bb);
			}
		}
		Button btnDate = (Button) findViewById(R.id.btn_right);
		if (btnDate != null) {
			if (!TextUtils.isEmpty(mChannel.getPubDate())) {
				StringBuffer date = new StringBuffer("有效期至");
				date.append('\n');
				date.append(mChannel.getPubDate());
				btnDate.setText(date.toString());
				btnDate.setVisibility(View.VISIBLE);
			} else
				btnDate.setVisibility(View.INVISIBLE);
		}
		//
		// gridAdapter = (GridItemAdapter) gridView.getAdapter();
		// if (gridAdapter == null) {
		// gridAdapter = new GridItemAdapter(McdonaldsActivity.this, service,
		// R.layout.app_mcdonalds_grid_item, iconListener,
		// selNumListener, selChkListener);
		// gridAdapter.setRSSChannel(mChannel);
		// }
		// //
		// if (isClear || gridView.getAdapter() == null) {
		// gridView.setAdapter(gridAdapter);
		// } else {
		// gridAdapter.notifyDataSetChanged();
		// }
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
			View itemView = vi.inflate(R.layout.app_mcdonalds_grid_item, null);
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
		tableLayout = (TableLayout) findViewById(R.id.tableview);
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
		View ivIconFg = itemView.findViewById(R.id.item_icon_fg);
		TextView tvText1 = (TextView) itemView.findViewById(R.id.item_text_1);
		TextView tvText2 = (TextView) itemView.findViewById(R.id.item_text_2);
		TextView tvText3 = (TextView) itemView.findViewById(R.id.item_text_3);
		Button btnSelNum = (Button) itemView.findViewById(R.id.btn_sel_num);
		ImageButton ibSelChk = (ImageButton) itemView
				.findViewById(R.id.btn_sel_chk);
		RSSItem item = mChannel.getItem(index);
		McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
		//
		if (!Commond.userInfo.isBlockImg()) {
			if (!TextUtils.isEmpty(item.getThumbailUrl())) {
				Bitmap b = (Bitmap) entity.getTag();
				if (b == null) {
					b = service.getCacheBitmap(item.getThumbailUrl());
					entity.setTag(b);
				}
				if (b != null)
					ivIcon.setImageBitmap(b);
				else
					ivIcon.setImageResource(R.drawable.default_image);
			}
		} else {
			ivIcon.setImageResource(R.drawable.default_no_image);
		}
		ivIcon.setTag(item.getThumbailUrl());
		//
		tvText1.setText(entity.getId() + " ￥" + entity.getPrice());
		tvText2.setText("省￥" + entity.getSave() + "起");
		tvText3.setText(entity.getName());
		//
		btnSelNum.setText(entity.getNum() + "份");
		if (entity.isSelected()) {
			ibSelChk.setImageResource(R.drawable.sel_check_pressed);
		} else {
			ibSelChk.setImageResource(R.drawable.sel_check_normal);
		}
		//
		ivIconFg.setTag(itemView);
		ivIconFg.setOnClickListener(iconListener);
		btnSelNum.setTag(itemView);
		btnSelNum.setOnClickListener(selNumListener);
		ibSelChk.setTag(itemView);
		ibSelChk.setOnClickListener(selChkListener);
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		// refleshMessageView(db, R.id.btn_right);
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
	public void loadNavBar() {
		// 全部
		View vAll = findViewById(R.id.nav_item_all_panel);
		vAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mChannel == null)
					return;
				boolean isupdate = false;
				for (RSSItem item : mChannel.getItems()) {
					if (!item.isVisible()) {
						isupdate = true;
					}
					item.setVisible(true);
				}
				if (isupdate) {
					loadTableView();
				}
				setTitleText(getString(R.string.nav_all_text));
			}
		});
		// 筛选
		View vFilter = findViewById(R.id.nav_item_filter_panel);
		vFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mAllNames.size() == 0)
					return;
				FilterDialog filterDialog = new FilterDialog(
						McdonaldsActivity.this, mAllNames, filterCancelListener);
				filterDialog.show();
			}
		});
		// 周边
		View vMap = findViewById(R.id.nav_item_map_panel);
		vMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				navDialog = new NavListDialog(McdonaldsActivity.this);
				navDialog.show();
			}
		});
		// 菜单
		View vOption = findViewById(R.id.nav_item_menu_panel);
		vOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Commond.showOptionMenu(McdonaldsActivity.this, false);
			}
		});

		View musicly = findViewById(R.id.nav_item_music_panel);
		musicly.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MusicChartListActivity.class);
				startActivity(intent);
			}
		});

		// 更多
		// loadNavMore(R.id.nav_item_more_panel);
		//
		final int[] optionIconIds = { R.drawable.menu_help,
				R.drawable.menu_nopic, R.drawable.menu_refresh,
				R.drawable.menu_msg, R.drawable.menu_ver,
				R.drawable.menu_clear, R.drawable.menu_about,
				R.drawable.menu_exit };
		final int[] optionIconTitles = { R.string.menu_help,
				R.string.menu_nopic, R.string.menu_refresh, R.string.menu_msg,
				R.string.menu_ver, R.string.menu_clear, R.string.menu_about,
				R.string.menu_exit };
		//
		loadOptionMenu(null, optionIconIds, optionIconTitles);
	}

	/**
	 * 
	 */
	public void switchShowPic() {
		for (View itemView : mItemViews) {
			setTableItem(itemView);
		}
	}
}