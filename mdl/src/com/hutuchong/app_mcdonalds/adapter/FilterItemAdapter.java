package com.hutuchong.app_mcdonalds.adapter;

import java.util.HashMap;

import mobi.domore.mcdonalds.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterItemAdapter extends BaseAdapter {
	int resId;
	Context mContext;
	HashMap<String, Boolean[]> mNames;

	//
	public FilterItemAdapter(Context context, int resId,
			HashMap<String, Boolean[]> names) {
		mContext = context;
		this.resId = resId;
		mNames = names;
	}

	public int getCount() {
		return mNames.size();
	}

	public Object getItem(int position) {
		return mNames.get(position);
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
			holder.tvText = (TextView) itemView.findViewById(R.id.item_text);
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
		String name = (String) mNames.keySet().toArray()[position];
		Boolean[] bb = mNames.get(name);
		if (bb[1]) {
			holder.ivChk.setImageResource(R.drawable.btn_chk_ok);
		} else
			holder.ivChk.setImageResource(R.drawable.btn_chk_no);
		//
		holder.tvText.setText(name);
		//
		// vList.put(position, itemView);
		//
		return itemView;
	}

	private class ViewHolder {
		public ImageView ivChk;
		public TextView tvText;
	}
}
