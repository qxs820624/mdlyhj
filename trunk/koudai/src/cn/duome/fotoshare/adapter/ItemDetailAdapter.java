package cn.duome.fotoshare.adapter;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chuangyu.music.R;

import com.hutuchong.inter_face.OnRequestImageListener;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;

public class ItemDetailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	RSSChannel mChannel;
	Context mContext;
	OnRequestImageListener mImagelistener;
	OnClickListener mClickListener;

	public ItemDetailAdapter(Context context, RSSChannel channel,
			OnRequestImageListener imagelistener, OnClickListener clickListener) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mChannel = channel;
		this.mContext = context;
		this.mImagelistener = imagelistener;
		mClickListener = clickListener;
	}

	@Override
	public int getCount() {
		return mChannel.getItems().size();
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
			itemView = mInflater.inflate(R.layout.itemdetail_item, null);
			//
			holder = new ViewHolder();
			//
			holder.ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
			//
			holder.llList = (LinearLayout) itemView
					.findViewById(R.id.comments_panel);
			holder.llLove = (LinearLayout) itemView
					.findViewById(R.id.love_panel);
			holder.llWb = (LinearLayout) itemView.findViewById(R.id.wb_panel);
			holder.llFav = (LinearLayout) itemView.findViewById(R.id.fav_panel);
			//
			holder.tvLove = (TextView) itemView.findViewById(R.id.tv_love);
			holder.tvWb = (TextView) itemView.findViewById(R.id.tv_wb);
			holder.tvFav = (TextView) itemView.findViewById(R.id.tv_fav);
			//
			itemView.setTag(holder);
		} else {
			holder = (ViewHolder) itemView.getTag();
		}
		RSSItem item = mChannel.getItem(position);
		String url = item.getImageUrl();
		if (!TextUtils.isEmpty(url)) {
			Bitmap bitmap = BaseContent.getInstance(mContext).getCacheBitmap(
					url,0,0);
			if (bitmap == null) {
				holder.ivThumb
						.setImageResource(R.drawable.default_image_loading);
				mImagelistener.requestImage(url, BaseContant.PAGE_IMAGE);
			} else
				holder.ivThumb.setImageBitmap(bitmap);
			holder.ivThumb.setTag(url);
		}
		else
		{
			holder.ivThumb
			.setImageResource(R.drawable.default_image_loading);
		}
		//
		holder.llList.removeAllViews();
		String commentListUrl = "http://xlb.jianrencun.com/fotoshare/foto_comments.php";
		commentListUrl += "?id=" + item.getId();
		holder.llList.setTag(commentListUrl);
		mImagelistener.requestImage(commentListUrl, BaseContant.PAGE_CHANNEL);
		//
		holder.llLove.setOnClickListener(mClickListener);
		holder.llLove.setTag(item.getId() + "_love");
		holder.llWb.setOnClickListener(mClickListener);
		holder.llWb.setTag(item.getId() + "_wb");
		holder.llFav.setOnClickListener(mClickListener);
		holder.llFav.setTag(item.getId() + "_fav");
		//
		String love = "0";
		String fav = "0";
		if (!TextUtils.isEmpty(item.getComments())) {
			String[] tmps = item.getComments().split("\\|");
			if (tmps.length == 3) {
				love = tmps[0];
				fav = tmps[1];
			}
		}
		holder.tvLove.setText(love);
		holder.tvFav.setText(fav);
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
		public LinearLayout llList;
		public LinearLayout llLove;
		public TextView tvLove;
		public LinearLayout llWb;
		public TextView tvWb;
		public LinearLayout llFav;
		public TextView tvFav;
	}
}
