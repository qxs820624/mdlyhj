package cn.duome.fotoshare.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.Ablum;

import com.hutuchong.util.BaseContent;

public class AlbumItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	ArrayList<Ablum> items;
	Context mContext;

	public AlbumItemAdapter(Context context, ArrayList<Ablum> items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return items.size();
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
			itemView = mInflater.inflate(R.layout.ablum_item, null);
			//
			holder = new ViewHolder();
			//
			holder.ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
			holder.tvCount = (TextView) itemView.findViewById(R.id.tv_count);
			//
			itemView.setTag(holder);
		} else
			holder = (ViewHolder) itemView.getTag();
		Ablum ablum = items.get(position);
		String url = ablum.getItem().getThumb();
		Bitmap bmp = BaseContent.getInstance(mContext).getCacheBitmap(url,179,194);
		holder.ivThumb.setImageBitmap(bmp);
		holder.tvTitle.setText(ablum.getItem().getName());
		holder.tvCount.setText(Integer.toString(ablum.getItems().size()));
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
		public TextView tvCount;
	}
}
