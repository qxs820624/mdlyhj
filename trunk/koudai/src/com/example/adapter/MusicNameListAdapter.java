package com.example.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.chuangyu.music.R;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.example.mymusic.MusicNameListActivity;

public class MusicNameListAdapter extends BaseAdapter {
	public List<MusicInfo> list = new ArrayList<MusicInfo>();
	LayoutInflater li = null;
	MusicNameListActivity context;

	public MusicNameListAdapter(MusicNameListActivity c, List<MusicInfo> ls) {
		// TODO Auto-generated constructor stub
		this.list = ls;
		this.context = c;
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
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		MusicInfo minfo = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = li.inflate(R.layout.music_list_items, null);
			viewHolder.musicNameTextView = (TextView) convertView
					.findViewById(R.id.music_list_items_name);
			viewHolder.musicSingerTextView = (TextView) convertView
					.findViewById(R.id.music_list_items_singer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.musicNameTextView.setText(minfo.getSongName());
		viewHolder.musicSingerTextView.setText(minfo.getSingerName());
		return convertView;
	}

	static class ViewHolder {
		public TextView musicSingerTextView;
		public TextView musicNameTextView;
	}
}
