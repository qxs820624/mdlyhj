package com.hutuchong.app_mcdonalds.adapter;

import java.util.ArrayList;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NavListItemAdapter extends BaseAdapter {
	int resId;
	Context mContext;
	public ArrayList<RSSItem> showItemList = new ArrayList<RSSItem>(0);

	/**
	 * 
	 */
	public void clearListView() {
		showItemList.clear();
		this.notifyDataSetChanged();
	}

	//
	public NavListItemAdapter(Context context, int resId, RSSChannel channel) {
		mContext = context;
		this.resId = resId;
		if (channel == null)
			return;
		for (RSSItem item : channel.getItems()) {
			showItemList.add(item);
		}
	}

	public int getCount() {
		return showItemList.size();
	}

	public Object getItem(int position) {
		return showItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (position < vList.size()) {
		// return vList.get(position);
		// }
		View itemView = convertView;
		if (itemView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resId, null);
			ViewHolder holder = new ViewHolder();
			itemView.setTag(holder);
			holder.tvId = (TextView) itemView.findViewById(R.id.item_id);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.item_title);
			holder.tvAdd = (TextView) itemView.findViewById(R.id.item_add);
			holder.tvPhone = (TextView) itemView.findViewById(R.id.item_phone);
			holder.tvAddTip = (TextView) itemView
					.findViewById(R.id.item_add_tip);
			holder.tvPhoneTip = (TextView) itemView
					.findViewById(R.id.item_phone_tip);
		}
		//
		if (position % 2 == 0) {
			itemView.setBackgroundResource(R.drawable.list_item_background_0);
		} else {
			itemView.setBackgroundResource(R.drawable.list_item_background_1);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		//
		RSSItem item = showItemList.get(position);
		//
		StringBuffer ids = new StringBuffer();
		ids = ids.append((char) ('A' + position));
		holder.tvId.setText(ids);
		//
		holder.tvTitle.setText(item.getTitle());
		//
		if (TextUtils.isEmpty(item.getDescription())) {
			holder.tvAdd.setVisibility(View.GONE);
			holder.tvAddTip.setVisibility(View.GONE);
		} else {
			holder.tvAdd.setVisibility(View.VISIBLE);
			holder.tvAddTip.setVisibility(View.VISIBLE);
			holder.tvAdd.setText(item.getDescription());
		}
		//
		if (TextUtils.isEmpty(item.getAuthor())) {
			holder.tvPhone.setVisibility(View.GONE);
			holder.tvPhoneTip.setVisibility(View.GONE);
		} else {
			holder.tvPhone.setVisibility(View.VISIBLE);
			holder.tvPhoneTip.setVisibility(View.VISIBLE);
			holder.tvPhone.setText(item.getAuthor());
		}
		// vList.put(position, itemView);
		//
		return itemView;
	}

	private class ViewHolder {
		public TextView tvId;
		public TextView tvTitle;
		public TextView tvAdd;
		public TextView tvAddTip;
		public TextView tvPhone;
		public TextView tvPhoneTip;
	}
}
