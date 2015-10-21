package com.hutuchong.app_game.adapter;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import mobi.domore.mcdonalds.R;

import com.hutuchong.app_game.AppInfo;

public class ManageItemAdapter extends BaseAdapter {
	Context mContext;
	int resId;
	OnClickListener mDelListener;
	HashMap<String, AppInfo> mAppInfoList = new HashMap<String, AppInfo>();

	//
	public ManageItemAdapter(Context context, int resId,
			HashMap<String, AppInfo> appInfoList, OnClickListener delListener) {
		this.resId = resId;
		mAppInfoList = appInfoList;
		mDelListener = delListener;
		mContext = context;
	}

	public int getCount() {
		return mAppInfoList.size();
	}

	public Object getItem(int position) {
		return mAppInfoList.get(position);
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
			holder.ivIcon = (ImageView) itemView.findViewById(R.id.item_icon);
			holder.tvName = (TextView) itemView.findViewById(R.id.item_name);
			holder.tvPkg = (TextView) itemView.findViewById(R.id.item_pkg);
			holder.tvVer = (TextView) itemView.findViewById(R.id.item_ver);
			holder.tvSize = (TextView) itemView.findViewById(R.id.item_size);
			holder.ibDel = (ImageButton) itemView.findViewById(R.id.item_del);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		/*
		 * View itemView = View.inflate((Context) m_activity,
		 * R.layout.channel_grid_item, null);
		 */
		//
		if (position % 2 == 0) {
			itemView.setBackgroundResource(R.drawable.list_item_background_0);
		} else {
			itemView.setBackgroundResource(R.drawable.list_item_background_1);
		}
		Object key = mAppInfoList.keySet().toArray()[position];
		AppInfo info = mAppInfoList.get(key);
		//
		holder.ivIcon.setImageDrawable(info.appIcon);
		holder.tvName.setText(info.appName);
		holder.tvPkg.setText("包名：" + info.packageName);
		holder.tvVer.setText("版本：" + info.versionName);
		holder.tvSize.setText("大小：" + Float.toString(info.size));
		holder.ibDel.setOnClickListener(mDelListener);
		holder.ibDel.setTag(info.packageName);

		return itemView;
	}

	private class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvPkg;
		public TextView tvVer;
		public TextView tvSize;
		public ImageButton ibDel;
	}
}
