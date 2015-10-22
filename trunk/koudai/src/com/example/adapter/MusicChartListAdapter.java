package com.example.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuangyu.music.R;

import com.cmsc.cmmusic.common.data.ChartInfo;
import com.example.mymusic.MusicChartListActivity;

public class MusicChartListAdapter extends BaseAdapter {
	public List<ChartInfo> list = new ArrayList<ChartInfo>();

	LayoutInflater li = null;
	MusicChartListActivity context;
	int[] icon;

	public MusicChartListAdapter(MusicChartListActivity c, List<ChartInfo> ls,
			int[] iconay) {
		// TODO Auto-generated constructor stub
		this.list = ls;
		this.context = c;
		this.icon = iconay;
		li = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ChartInfo cinfo = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = li.inflate(R.layout.chart_list_items, null);
			viewHolder.musicNameTextView = (TextView) convertView
					.findViewById(R.id.chart_list_items_name);
			viewHolder.musicIconImageView = (ImageView) convertView
					.findViewById(R.id.chart_list_items_icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.musicNameTextView.setText(cinfo.getChartName());
		//viewHolder.musicIconImageView.setImageResource(icon[position]);
		return convertView;
	}

	static class ViewHolder {
		public ImageView musicIconImageView;
		public TextView musicNameTextView;
	}

}
