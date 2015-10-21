package com.hutuchong.app_user.adapter;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutuchong.util.Commond;
import com.hutuchong.util.ServiceHandle;

public class ItemAdapter extends BaseAdapter {
	Activity m_activity;
	int resId;
	OnClickListener mClickListener;
	OnClickListener mDownListener;
	OnClickListener mDelListener;
	OnClickListener mReplyListener;
	public ServiceHandle mService;
	public ArrayList<RSSItem> showItemList = new ArrayList<RSSItem>(0);

	/**
	 * 
	 */
	public void clearListView() {
		showItemList.clear();
		this.notifyDataSetChanged();
	}

	//
	public void setChannel(RSSChannel channel, Boolean isClear) {
		if (isClear) {
			// vList.clear();
			showItemList.clear();
		}
		ArrayList<RSSItem> items = channel.getItems();
		int count = items.size();
		for (int i = 0; i < count; i++) {
			RSSItem item = items.get(i);
			String pkg = item.getImageUrl();
			if (("apk_ad".equals(item.getCategory()))
					&& Commond.apkInstalled(m_activity, pkg, false)) {
				// 已经安装
			} else {
				showItemList.add(item);
			}
		}
	}

	//
	public ItemAdapter(Activity activity, ServiceHandle service, int resId,
			OnClickListener clickListener, OnClickListener downListener,
			OnClickListener delListener, OnClickListener replyListener) {
		this.resId = resId;
		mClickListener = clickListener;
		mDownListener = downListener;
		mDelListener = delListener;
		mReplyListener = replyListener;
		m_activity = activity;
		this.mService = service;
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
			LayoutInflater vi = (LayoutInflater) m_activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resId, null);
			ViewHolder holder = new ViewHolder();
			itemView.setTag(holder);
			holder.ivRead = (ImageView) itemView.findViewById(R.id.item_read);
			holder.ivReadText = (TextView) itemView
					.findViewById(R.id.item_read_text);
			holder.tvCate = (TextView) itemView.findViewById(R.id.item_cate);
			holder.tvDate = (TextView) itemView.findViewById(R.id.item_date);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.item_title);
			holder.tvDesc = (TextView) itemView.findViewById(R.id.item_desc);
			holder.ivImage1 = (ImageView) itemView
					.findViewById(R.id.item_image1);
			holder.ivImage2 = (ImageView) itemView
					.findViewById(R.id.item_image2);
			holder.btnDown = (Button) itemView.findViewById(R.id.item_down);
			holder.vItemPanel = itemView.findViewById(R.id.item_panel);
			holder.vMorePanel = itemView.findViewById(R.id.item_more);
			holder.vDelPanel = itemView.findViewById(R.id.del_panel);
			holder.vReplyPanel = itemView.findViewById(R.id.reply_panel);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		/*
		 * View itemView = View.inflate((Context) m_activity,
		 * R.layout.channel_grid_item, null);
		 */
		//
		RSSItem item = showItemList.get(position);
		if (item.isReaded()) {
			holder.ivRead.setBackgroundResource(R.drawable.msg_readed);
			holder.ivReadText.setText(R.string.readed_text);
		} else {
			holder.ivRead.setBackgroundResource(R.drawable.msg_unread);
			holder.ivReadText.setText(R.string.unread_text);
		}
		//
		holder.tvCate.setText(item.getCategory());
		holder.tvDate.setText(item.getPubDate());
		holder.tvTitle.setText(item.getTitle());
		// 详细
		if (TextUtils.isEmpty(item.getDescription()))
			holder.tvDesc.setVisibility(View.GONE);
		else {
			holder.tvDesc.setVisibility(View.VISIBLE);
			holder.tvDesc.setText(item.getDescription());
		}
		// 图片1
		if (TextUtils.isEmpty(item.getImageUrl())) {
			holder.ivImage1.setVisibility(View.GONE);
		} else {
			holder.ivImage1.setVisibility(View.VISIBLE);
			Bitmap b = mService.getCacheBitmap(item.getImageUrl());
			holder.ivImage1.setImageBitmap(b);
			holder.ivImage1.setTag(item.getImageUrl());
		}
		// 图片2
		if (TextUtils.isEmpty(item.getThumbailUrl())) {
			holder.ivImage2.setVisibility(View.GONE);
		} else {
			holder.ivImage2.setVisibility(View.VISIBLE);
			Bitmap b = mService.getCacheBitmap(item.getThumbailUrl());
			holder.ivImage2.setImageBitmap(b);
			holder.ivImage2.setTag(item.getThumbailUrl());

		}
		// 下载按钮
		if (TextUtils.isEmpty(item.getFavLink())) {
			holder.btnDown.setVisibility(View.GONE);
		} else {
			holder.btnDown.setVisibility(View.VISIBLE);
			//
			holder.btnDown.setTag(position);
			holder.btnDown.setOnClickListener(mDownListener);
		}
		//
		if (item.isVisible()) {
			holder.vMorePanel.setVisibility(View.VISIBLE);
		} else {
			holder.vMorePanel.setVisibility(View.GONE);
		}
		holder.vItemPanel.setTag(position);
		holder.vItemPanel.setOnClickListener(mClickListener);
		//
		holder.vDelPanel.setOnClickListener(mDelListener);
		holder.vDelPanel.setTag(position);
		holder.vReplyPanel.setOnClickListener(mReplyListener);
		holder.vReplyPanel.setTag(position);
		//

		return itemView;
	}

	private class ViewHolder {
		public ImageView ivRead;
		public TextView ivReadText;
		public TextView tvCate;
		public TextView tvDate;
		public TextView tvTitle;
		public TextView tvDesc;
		public ImageView ivImage1;
		public ImageView ivImage2;
		public Button btnDown;
		public View vItemPanel;
		public View vMorePanel;
		public View vDelPanel;
		public View vReplyPanel;
	}
}
