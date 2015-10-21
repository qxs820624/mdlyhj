package com.hutuchong.app_user;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.coolworks.handle.ActivityHandle;

import com.hutuchong.app_game.service.UpdateService;
import com.hutuchong.app_user.adapter.ItemAdapter;
import com.hutuchong.app_user.db.DBAdapter;
import com.hutuchong.app_user.service.UserService;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BindService;
import com.hutuchong.util.Commond;

public class MessageListActivity extends BaseActivity implements ActivityHandle {
	int screenWidth;
	int screenHeight;
	ListView listView;
	ItemAdapter listAdapter;
	boolean isMain;
	DBAdapter db = new DBAdapter(this);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		super.init(false);
		//
		setContentView(R.layout.app_messagelist);
		//
		listView = (ListView) findViewById(R.id.listview);
		loadBtnBack(null, R.id.btn_left, View.VISIBLE);
		loadBtnSend(R.id.btn_right);
		TextView tvTitle = (TextView) this.findViewById(R.id.title_text);
		tvTitle.setText(R.string.message_title);
		//
		bindService = new BindService(this, this, UserService.class);
	}

	/**
	 * 
	 */
	public void onBinddedService() {
		super.onBinddedService();
		Commond.loadScroller(this, screenWidth);
		//
		listView.setOnScrollListener(new OnScrollListenerEx(listView));
		loadChannel(0, 30);
	}

	/**
	 * 
	 */
	private void loadBtnSend(int resId) {
		Button vSend = (Button) findViewById(resId);
		vSend.setBackgroundResource(R.drawable.btn_share_background);
		vSend.setText(R.string.send_message);
		vSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 
	 */
	OnClickListener mItemMoreListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mChannel == null || listAdapter == null)
				return;
			//
			int index = (Integer) v.getTag();
			RSSItem item = listAdapter.showItemList.get(index);
			View vMore = v.findViewById(R.id.item_more);
			if (item.isVisible()) {
				item.setVisible(false);
				vMore.setVisibility(View.GONE);
			} else {
				item.setVisible(true);

				vMore.setVisibility(View.VISIBLE);
				if (!item.isReaded()) {
					db.setReaded(item.getId(), true);
					item.setReaded(true);
					listAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	/**
	 * 
	 */
	OnClickListener mDownListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mChannel == null || listAdapter == null)
				return;
			//
			Integer index = (Integer) v.getTag();
			RSSItem item = listAdapter.showItemList.get(index);
			String favlink = item.getFavLink();
			if (TextUtils.isEmpty(favlink))
				return;
			String[] tmps = favlink.split("\\|");
			if (tmps.length > 1) {
				String apkinfo = tmps[0];
				String apkurl = tmps[1];
				String[] infos = apkinfo.split(":");
				String flg = infos[0];
				String pkg = infos[1];
				String title = infos[2];
				String size = infos[3];
				String ver = infos[4];
				// 请求下载
				if (!Commond.apkInstalled(mContext, pkg, true)) {
					return;
				} else {
					Intent i = new Intent(mContext, UpdateService.class);
					i.putExtra("url", apkurl);
					i.putExtra("title", title);
					i.putExtra("filepath", service.getTempFileName(apkurl));
					startService(i);
				}
			}
		}
	};
	/**
	 * 
	 */
	OnClickListener mDelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mChannel == null || listAdapter == null)
				return;
			Integer index = (Integer) v.getTag();
			RSSItem item = listAdapter.showItemList.get(index);
			if (db.deleteItem(Long.parseLong(item.getId()))) {
				listAdapter.showItemList.remove(index.intValue());
				listAdapter.notifyDataSetChanged();
			}
		}
	};
	/**
	 * 
	 */
	OnClickListener mReplyListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mChannel == null || listAdapter == null)
				return;
		}
	};

	/**
	 * 
	 */
	private void loadChannel(int begin, int count) {
		//
		listAdapter = (ItemAdapter) listView.getAdapter();
		if (listAdapter == null) {
			listAdapter = new ItemAdapter(MessageListActivity.this, service,
					R.layout.app_message_item, mItemMoreListener,
					mDownListener, mDelListener, mReplyListener);
		}
		//
		RSSChannel channel = db.getItems(begin, count);
		if (channel == null || channel.getItems().size() == 0) {
			if (listAdapter.getCount() == 0) {
				showTipIcon(null, Commond.PAGE_CHANNEL);
			}
			return;
		}
		this.mChannel = channel;
		hideTipIcon();
		listAdapter.setChannel(mChannel, false);
		//
		if (listView.getAdapter() == null) {
			listView.setAdapter(listAdapter);
		} else {
			listAdapter.notifyDataSetChanged();
		}
		requestImages(mChannel, true);
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
	public void clearListView() {
		listAdapter = (ItemAdapter) listView.getAdapter();
		if (listAdapter != null) {
			listAdapter.clearListView();
		}
	}
}