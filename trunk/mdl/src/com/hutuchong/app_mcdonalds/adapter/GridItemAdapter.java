package com.hutuchong.app_mcdonalds.adapter;

import java.util.ArrayList;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;
import com.hutuchong.util.ServiceHandle;

public class GridItemAdapter extends BaseAdapter {
	Activity m_activity;
	int resId;
	OnClickListener mIconListener;
	OnClickListener mSelNumListener;
	OnClickListener mSelChkListener;
	public ServiceHandle mService;
	ArrayList<RSSItem> showList = new ArrayList<RSSItem>();

	//
	public GridItemAdapter(Activity activity, ServiceHandle service, int resId,
			OnClickListener iconListener, OnClickListener selNumListener,
			OnClickListener selChkListener) {
		mIconListener = iconListener;
		mSelNumListener = selNumListener;
		mSelChkListener = selChkListener;
		this.resId = resId;
		m_activity = activity;
		this.mService = service;
	}

	public void setRSSChannel(RSSChannel channel) {
		showList.clear();
		for (RSSItem item : channel.getItems()) {
			if (item.isVisible()) {
				showList.add(item);
			}
		}
	}

	public int getCount() {
		return showList.size();
	}

	public Object getItem(int position) {
		return showList.get(position);
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
			LayoutInflater vi = (LayoutInflater) m_activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resId, null);
			ViewHolder holder = new ViewHolder();
			itemView.setTag(holder);
			holder.ivIcon = (ImageView) itemView.findViewById(R.id.item_icon);
			holder.ivIconFg = itemView.findViewById(R.id.item_icon_fg);
			holder.tvText1 = (TextView) itemView.findViewById(R.id.item_text_1);
			holder.tvText2 = (TextView) itemView.findViewById(R.id.item_text_2);
			holder.tvText3 = (TextView) itemView.findViewById(R.id.item_text_3);
			holder.btnSelNum = (Button) itemView.findViewById(R.id.btn_sel_num);
			holder.ibSelChk = (ImageButton) itemView
					.findViewById(R.id.btn_sel_chk);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		/*
		 * View itemView = View.inflate((Context) m_activity,
		 * R.layout.channel_grid_item, null);
		 */

		//
		RSSItem item = showList.get(position);
		McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
		//
		if (!TextUtils.isEmpty(item.getThumbailUrl())) {
			Bitmap b = (Bitmap) entity.getTag();
			if (b == null) {
				b = mService.getCacheBitmap(item.getThumbailUrl());
				entity.setTag(b);
			}
			holder.ivIcon.setImageBitmap(b);
		}
		holder.tvText1.setText(entity.getId() + " ￥" + entity.getPrice());
		holder.tvText2.setText("省￥" + entity.getSave() + "起");
		holder.tvText3.setText(entity.getName());
		//
		holder.btnSelNum.setText(entity.getNum() + "份");
		if (entity.isSelected()) {
			holder.ibSelChk.setImageResource(R.drawable.sel_check_pressed);
		} else {
			holder.ibSelChk.setImageResource(R.drawable.sel_check_normal);
		}
		//
		holder.ivIconFg.setTag(item);
		holder.ivIconFg.setOnClickListener(mIconListener);
		holder.btnSelNum.setTag(item);
		holder.btnSelNum.setOnClickListener(mSelNumListener);
		holder.ibSelChk.setTag(item);
		holder.ibSelChk.setOnClickListener(mSelChkListener);
		//
		// vList.put(position, itemView);
		//
		return itemView;
	}

	private class ViewHolder {
		public ImageView ivIcon;
		public View ivIconFg;
		public TextView tvText1;
		public TextView tvText2;
		public TextView tvText3;
		public Button btnSelNum;
		public ImageButton ibSelChk;
	}
}
