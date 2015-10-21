package cn.duome.fotoshare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.Ablum;
import cn.duome.fotoshare.entity.Item;

import com.hutuchong.util.BaseContent;

public class FotoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	Ablum ablum;
	Context mContext;

	public FotoAdapter(Context context, Ablum ablum) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.ablum = ablum;
		this.mContext = context;
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
			itemView = mInflater.inflate(R.layout.item_foto, null);
			//
			holder = new ViewHolder();
			//
			holder.ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
			//
			itemView.setTag(holder);
		} else
			holder = (ViewHolder) itemView.getTag();
		Item item = ablum.getItems().get(position);
		Bitmap bmp = BaseContent.getInstance(mContext).getCacheBitmap(
				item.getPath(),0,0);
		holder.ivThumb.setImageBitmap(bmp);
		holder.ivThumb.setTag(bmp);
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
	}
}
