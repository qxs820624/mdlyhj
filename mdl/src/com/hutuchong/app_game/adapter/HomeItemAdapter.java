package com.hutuchong.app_game.adapter;

import java.util.ArrayList;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mobi.domore.mcdonalds.R;
import com.hutuchong.app_game.AppInfo;
import com.hutuchong.util.ServiceHandle;

public class HomeItemAdapter extends BaseAdapter {
	Context mContext;
	ServiceHandle mService;
	int resId;
	private ArrayList<RSSItem> mShowItemList = new ArrayList<RSSItem>();

	public void clearListView() {
		mShowItemList.clear();
		this.notifyDataSetChanged();
	}

	//
	public void setChannel(RSSChannel channel, Boolean isClear) {
		if (isClear) {
			mShowItemList.clear();
		}
		ArrayList<RSSItem> items = channel.getItems();
		int count = items.size();
		// 注意：下面的代码会导致java.util.ConcurrentModificationException
		// for (RSSItem item : items) {
		// itemList.add(item);
		// }
		for (int i = 0; i < count; i++) {
			RSSItem item = items.get(i);
			mShowItemList.add(item);
		}
	}

	//
	public HomeItemAdapter(Context context, ServiceHandle service, int resId) {
		this.resId = resId;
		mContext = context;
		mService = service;
	}

	public int getCount() {
		if (mShowItemList == null)
			return 0;
		return mShowItemList.size();
	}

	public Object getItem(int position) {
		if (mShowItemList == null)
			return null;
		return mShowItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		// Debug.d("getView position:" + position);
		if (mShowItemList == null)
			return convertView;

		ViewHolder holder = null;
		View itemView = convertView;
		if (itemView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resId, null);
			//
			holder = new ViewHolder();
			//
			holder.ivIcon = (ImageView) itemView.findViewById(R.id.item_icon);
			holder.tvText1 = (TextView) itemView.findViewById(R.id.item_text_1);
			holder.tvText2 = (TextView) itemView.findViewById(R.id.item_text_2);
			//
			itemView.setTag(holder);
		} else
			holder = (ViewHolder) itemView.getTag();
		//
		if (position % 4 == 0 || position % 4 == 1) {
			itemView.setBackgroundResource(R.drawable.list_item_background_0);
		} else {
			itemView.setBackgroundResource(R.drawable.list_item_background_1);
		}
		RSSItem item = mShowItemList.get(position);
		AppInfo info = (AppInfo) item.getTag();
		if (info.appBitmap == null && !TextUtils.isEmpty(info.iconUrl)) {
			info.appBitmap = mService.getCacheBitmap(item.getThumbailUrl());
		}
		if (info.appBitmap != null)
			holder.ivIcon.setImageBitmap(info.appBitmap);
		else
			holder.ivIcon.setImageResource(R.drawable.icon_bg);
		holder.ivIcon.setTag(info.iconUrl);
		//
		holder.tvText1.setText("版本：" + info.versionName);
		holder.tvText2.setText(info.appName);

		return itemView;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewHolder {
		public ImageView ivIcon;
		public TextView tvText1;
		public TextView tvText2;
	}
}
