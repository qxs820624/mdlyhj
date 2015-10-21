package com.hutuchong.adapter;

import mobi.domore.mcdonalds.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutuchong.util.BaseService;

public class MenuAdapter extends BaseAdapter {
	Context c;
	int layoutId;
	int[] resIds = null;
	Drawable[] ress = null;
	Object[] mMenuTitle;
	Object[] mIconUrls = null;
	Object[] mLinkUrls = null;
	BaseService mService;

	/**
	 * 
	 * @param iconUrls
	 * @param linkUrls
	 * @param titles
	 */
	public void updateData(Object[] iconUrls, Object[] linkUrls, Object[] titles) {
		this.mIconUrls = iconUrls;
		this.mLinkUrls = linkUrls;
		this.mMenuTitle = titles;
	}

	//
	public MenuAdapter(Context c, int layoutId, int[] resIds, String[] titles) {
		this.c = c;
		this.layoutId = layoutId;
		this.resIds = resIds;
		this.mMenuTitle = titles;
	}

	//
	public MenuAdapter(Context c, int layoutId, Drawable[] resIds,
			String[] titles) {
		this.c = c;
		this.layoutId = layoutId;
		this.ress = resIds;
		this.mMenuTitle = titles;
	}

	//
	public MenuAdapter(Context c, int layoutId, Object[] iconUrls,
			Object[] linkUrls, Object[] titles, BaseService service) {
		this.c = c;
		this.layoutId = layoutId;
		this.mService = service;
		//
		updateData(iconUrls, linkUrls, titles);
	}

	public int getCount() {
		return mMenuTitle.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		if (itemView == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(layoutId, null);
			ViewHolder holder = new ViewHolder();
			itemView.setTag(holder);
			holder.ivThumb = (ImageView) itemView
					.findViewById(R.id.item_menu_icon);
			holder.tvTitle = (TextView) itemView
					.findViewById(R.id.item_menu_text);
			holder.ivDivider = (ImageView) itemView
					.findViewById(R.id.item_menu_divider);
		}
		ViewHolder holder = (ViewHolder) itemView.getTag();
		//
		if (position == 0 && holder.ivDivider != null) {
			holder.ivDivider.setVisibility(View.GONE);
		}
		//
		if (resIds != null)
			holder.ivThumb.setImageResource(resIds[position]);
		else if (ress != null)
			holder.ivThumb.setImageDrawable(ress[position]);
		else if (mIconUrls != null) {
			Bitmap bmp = mService
					.getCacheBitmap((String) this.mIconUrls[position]);
			holder.ivThumb.setImageBitmap(bmp);
			holder.ivThumb.setTag(mIconUrls[position]);
		}
		//
		holder.tvTitle.setText((String) mMenuTitle[position]);
		if (mLinkUrls != null)
			holder.tvTitle.setTag(mLinkUrls[position]);
		//
		return itemView;
	}

	private class ViewHolder {
		public ImageView ivThumb;
		public TextView tvTitle;
		public ImageView ivDivider;
	}
}
