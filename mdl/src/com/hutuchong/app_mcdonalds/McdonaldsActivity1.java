package com.hutuchong.app_mcdonalds;

import java.net.URLEncoder;
import java.util.HashMap;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.Debug;
import cn.coolworks.util.StringUtil;

import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_mcdonalds.adapter.GridItemAdapter;
import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;
import com.hutuchong.app_mcdonalds.service.McdonaldsService;
import com.hutuchong.app_user.db.DBAdapter;
import com.hutuchong.app_user.service.UserService;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;
import com.hutuchong.util.GotoActivity;

public class McdonaldsActivity1 extends BaseActivity implements ActivityHandle {
	int screenWidth;
	int screenHeight;
	GridView gridView;
	GridItemAdapter gridAdapter;
	boolean isMain;
	DBAdapter db = new DBAdapter(this);
	ImageButton mNavNewsItem;
	ImageButton mNavSoftItem;
	ImageButton mNavFotoItem;
	ImageButton mNavBookItem;
	String homeUrl = "http://xlb.jianrencun.com/mcdonalds/client_mcdonalds.php";
	int mTabIndex = 0;
	ImageDialog imageDialog;
	int[] optionIconId;
	String[] optionTitleId;
	GridView optionGridView;
	OptionMenuAdapter optionsAdapter;

	HashMap<String, Boolean[]> mAllNames = new HashMap<String, Boolean[]>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// umeng的自动更新
		// MobclickAgent.update(this);
		// MobclickAgent.setUpdateOnlyWifi(false);
		//
		super.init(false);
		Intent uservice = new Intent(mContext, UserService.class);
		mContext.startService(uservice);
		//
		setContentView(R.layout.app_mcdonalds);
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
		gridView = (GridView) findViewById(R.id.gridview);
		loadBtnBack(null, R.id.btn_left, !isMain ? View.VISIBLE
				: View.INVISIBLE);
		loadBtnMsg(R.id.btn_right, View.VISIBLE);
		//
		refleshMessageView(db, R.id.btn_right);
		//
		bindService = new BindService(this, this, McdonaldsService.class);
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		//
		int pageid = Commond.PAGE_CATEGORY;
		int resTitleId = R.string.app_name;
		// 发送umeng错误消息
	//	MobclickAgent.onError(this);
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
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mChannel == null
						|| gridAdapter == null
						|| TextUtils.isEmpty(mChannel.getItem(arg2)
								.getImageUrl()))
					return;
				imageDialog = new ImageDialog(McdonaldsActivity1.this);
				imageDialog.show();
				requestItem(mChannel.getItem(arg2).getImageUrl(),
						Commond.PAGE_FULL_IMAGE, false);

			}
		});
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
								McdonaldsActivity1.this, mChannel,
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
	 */
	OnClickListener iconListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RSSItem item = (RSSItem) v.getTag();
			if (item == null || TextUtils.isEmpty(item.getImageUrl()))
				return;
			imageDialog = new ImageDialog(McdonaldsActivity1.this);
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
			RSSItem item = (RSSItem) v.getTag();
			McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
			if (!entity.isSelected()) {
				Commond.showToast(mContext, "请先选中该套餐，再选择份数！");
				return;
			}
			NumDialog detailDialog = new NumDialog(McdonaldsActivity1.this,
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
			RSSItem item = (RSSItem) arg0.getTag();

			if (item != null) {
				McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
				if (entity != null) {
					if (entity.getNum() == 0)
						entity.setNum(1);
					entity.setSelected(!entity.isSelected());
					gridAdapter.notifyDataSetChanged();
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
			gridAdapter.notifyDataSetChanged();
			String[] prices = McdonaldsEntity.totalPrice(mChannel);
			setPriceText(prices[0], prices[1]);
		}
	};
	OnCancelListener detailCancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			if (gridAdapter == null)
				return;
			gridAdapter.notifyDataSetChanged();
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
			boolean isupdate = false;
			for (RSSItem item : mChannel.getItems()) {
				boolean isvisible = false;
				McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
				for (String name : entity.names) {
					Boolean[] bb = mAllNames.get(name);
					if (bb[0]) {
						isvisible = true;
					}
				}
				if (item.isVisible() != isvisible) {
					isupdate = true;
					item.setVisible(isvisible);
				}
			}
			if (isupdate) {
				gridAdapter.setRSSChannel(mChannel);
				gridView.setAdapter(gridAdapter);
			}
		}
	};

	/**
	 * 
	 * @param input
	 */
	public void onNavSearch(String input) {
		service.delFile(service.searchUrl);
		String url = StringUtil.appendNameValue(service.searchUrl, "input",
				URLEncoder.encode(input));
		service.delFile(url);
		//
		Intent i = new Intent(mContext, McdonaldsActivity1.class);
		i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
		i.putExtra(ContantValue.EXTRA_TITLE,
				mContext.getText(R.string.search_title));
		i.putExtra(ContantValue.EXTRA_URL, url);

		startActivity(i);
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
		case Commond.PAGE_IMAGE:
			// ImageView iv = (ImageView) gridView.findViewWithTag(url);
			// if (iv != null) {
			// iv.setImageBitmap(service.getCacheBitmap(url));
			// }
			gridAdapter.notifyDataSetChanged();
			break;
		case Commond.PAGE_FULL_IMAGE:
			Bitmap bm = service.getCacheBitmap(url);
			imageDialog.showImage(bm);
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
				Intent i = new Intent(mContext, McdonaldsActivity1.class);
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
			if (gridAdapter.getCount() == 0) {
				bindService.service.delFile(url);
				showTipIcon(url, Commond.PAGE_CHANNEL);
			}
			return;
		}
		this.mChannel = channel;
		hideTipIcon();
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
		//
		gridAdapter = (GridItemAdapter) gridView.getAdapter();
		if (gridAdapter == null) {
			gridAdapter = new GridItemAdapter(McdonaldsActivity1.this, service,
					R.layout.app_mcdonalds_grid_item, iconListener,
					selNumListener, selChkListener);
			gridAdapter.setRSSChannel(mChannel);
		}
		//
		if (isClear || gridView.getAdapter() == null) {
			gridView.setAdapter(gridAdapter);
		} else {
			gridAdapter.notifyDataSetChanged();
		}
		//
		requestImages(mChannel, false);
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		refleshMessageView(db, R.id.btn_right);
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
					gridAdapter.setRSSChannel(mChannel);
					gridView.setAdapter(gridAdapter);
				}
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
						McdonaldsActivity1.this, mAllNames,
						filterCancelListener);
				filterDialog.show();
			}
		});
		// 周边
		View vMap = findViewById(R.id.nav_item_map_panel);
		vMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		// 菜单
		View vOption = findViewById(R.id.nav_item_menu_panel);
		vOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Commond.showOptionMenu(McdonaldsActivity1.this, false);
			}
		});
		// 更多
		loadNavMore(R.id.nav_item_more_panel);
		//
		final int[] optionIconIds = { R.drawable.menu_help,
				R.drawable.menu_nopic, R.drawable.menu_refresh,
				R.drawable.menu_msg, R.drawable.menu_ver, R.drawable.menu_more,
				R.drawable.menu_about, R.drawable.menu_exit };
		final int[] optionIconTitles = { R.string.menu_help,
				R.string.menu_nopic, R.string.menu_refresh, R.string.menu_msg,
				R.string.menu_ver, R.string.menu_moreapp, R.string.menu_about,
				R.string.menu_exit };
		//
		loadOptionMenu(null, optionIconIds, optionIconTitles);
	}
}