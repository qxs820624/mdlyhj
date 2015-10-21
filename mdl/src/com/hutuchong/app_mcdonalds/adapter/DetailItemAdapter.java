package com.hutuchong.app_mcdonalds.adapter;

import java.util.ArrayList;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;

public class DetailItemAdapter extends BaseAdapter {
	int resId;
	Context mContext;
	public ArrayList<RSSItem> showItemList = new ArrayList<RSSItem>(0);

	// private Hashtable<Integer, View> vList = new Hashtable<Integer, View>();
	/**
	 * 
	 */
	public void clearListView() {
		showItemList.clear();
		this.notifyDataSetChanged();
	}

	//
	public DetailItemAdapter(Context context, int resId, RSSChannel channel) {
		mContext = context;
		this.resId = resId;
		if (channel == null)
			return;
		for (RSSItem item : channel.getItems()) {
			McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
			if (entity.isSelected()) {
				showItemList.add(item);
			}
			item.setReaded(entity.isSelected());
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
			holder.ivChk = (ImageView) itemView.findViewById(R.id.item_chk);
			holder.tvId = (TextView) itemView.findViewById(R.id.item_id);
			holder.tvText = (TextView) itemView.findViewById(R.id.item_text);
			holder.tvNum = (TextView) itemView.findViewById(R.id.item_num);
			holder.tvPrice = (TextView) itemView.findViewById(R.id.item_price);
		}
		//
		if (position % 2 == 0) {
			itemView.setBackgroundResource(R.drawable.list_item_background_0);
		} else {
			itemView.setBackgroundResource(R.drawable.list_item_background_1);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		/*
		 * View itemView = View.inflate((Context) m_activity,
		 * R.layout.channel_grid_item, null);
		 */
		//
		RSSItem item = showItemList.get(position);
		McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
		//
		if (entity.isSelected()) {
			holder.ivChk.setImageResource(R.drawable.btn_chk_ok);
		} else
			holder.ivChk.setImageResource(R.drawable.btn_chk_no);
		//
		holder.tvId.setText(entity.getId());
		//
		holder.tvPrice.setText("￥"+entity.getPrice());
		//
		holder.tvText.setText(entity.getName());
		//
		holder.tvNum.setText(entity.getNum() + "份");
		// vList.put(position, itemView);
		//
		return itemView;
	}

	private class ViewHolder {
		public ImageView ivChk;
		public TextView tvId;
		public TextView tvText;
		public TextView tvNum;
		public TextView tvPrice;
	}
}
