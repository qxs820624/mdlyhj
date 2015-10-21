package com.hutuchong.adapter;

import mobi.domore.mcdonalds.R;
import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutuchong.util.ServiceHandle;

public class ChannelAdapter extends BaseAdapter {
	Activity m_activity;
	private RSSChannel mChannel;
	public ServiceHandle mService;
	private int resid;

	//
	public void setChannel(RSSChannel channel) {
		mChannel = channel;
	}

	//
	public ChannelAdapter(Activity activity, ServiceHandle service,
			RSSChannel channel,int resid) {
		m_activity = activity;
		this.mChannel = channel;
		this.mService = service;
		this.resid = resid;
	}

	public int getCount() {
		return mChannel.getItems().size();
	}

	public Object getItem(int position) {
		return mChannel.getItem(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		if (itemView == null) {
			LayoutInflater vi = (LayoutInflater) m_activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resid, null);
			ViewHolder holder = new ViewHolder();
			itemView.setTag(holder);
			holder.ivThumb = (ImageView) itemView.findViewById(R.id.item_icon);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.item_text);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		//
		RSSItem item = mChannel.getItem(position);
		//
		Bitmap b = mService.getCacheBitmap(item.getThumbailUrl());
		if (b == null) {
			holder.ivThumb.setTag(item.getThumbailUrl());
		} else {
			holder.ivThumb.setImageBitmap(b);
			holder.ivThumb.setVisibility(View.VISIBLE);
		}
		//
		holder.tvTitle.setText(item.getTitle());
		//
		return itemView;
	}

	private class ViewHolder {
		public ImageView ivThumb;
		public TextView tvTitle;
	}
}
