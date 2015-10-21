package cn.duome.fotoshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuangyu.music.R;

public class ShareItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	Context mContext;
	OnClickListener listener;
	int[] iconIds;
	int[] titleIds;

	public ShareItemAdapter(Context context, int[] iconIds, int[] titleIds) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.iconIds = iconIds;
		this.mContext = context;
		this.titleIds = titleIds;
	}

	@Override
	public int getCount() {
		return iconIds.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View itemView = convertView;
		if (itemView == null) {
			itemView = mInflater.inflate(R.layout.item_share, null);
			//
			holder = new ViewHolder();
			//
			holder.ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
			//
			itemView.setTag(holder);
		} else
			holder = (ViewHolder) itemView.getTag();
		holder.ivThumb.setImageResource(iconIds[position]);
		holder.tvTitle.setText(titleIds[position]);
		//
		return itemView;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewHolder {
		public ImageView ivThumb;
		public TextView tvTitle;
	}
}
