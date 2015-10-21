package cn.duome.fotoshare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.Ablum;
import cn.duome.fotoshare.entity.Item;

import com.hutuchong.util.BaseContent;

public class ItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	Ablum ablum;
	Context mContext;
	OnClickListener listener;

	public ItemAdapter(Context context, Ablum ablum, OnClickListener listener) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.ablum = ablum;
		this.mContext = context;
		this.listener = listener;
	}

	/**
	 * 
	 * @param ablum
	 */
	public void setAblum(Ablum ablum) {
		this.ablum = ablum;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ablum.getItems().size();
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
			itemView = mInflater.inflate(R.layout.item_item, null);
			//
			holder = new ViewHolder();
			//
			holder.ivThumb = (ImageButton) itemView.findViewById(R.id.iv_thumb);
			//
			itemView.setTag(holder);
		} else
			holder = (ViewHolder) itemView.getTag();
		Item item = ablum.getItems().get(position);
		String filename = item.getThumb();
		Bitmap bmp = BaseContent.getInstance(mContext).getCacheBitmap(filename,0,0);
		holder.ivThumb.setImageBitmap(bmp);
		//
		holder.ivThumb.setOnClickListener(this.listener);
		holder.ivThumb.setTag(position);
		//
		return itemView;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewHolder {
		public ImageButton ivThumb;
	}
}
