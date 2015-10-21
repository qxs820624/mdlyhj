package com.hutuchong.app_game.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import mobi.domore.mcdonalds.R;
import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.coolworks.util.StringUtil;

import com.hutuchong.data.Data;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ItemData;
import com.hutuchong.util.ServiceHandle;

public class MarketItemAdapter extends BaseAdapter {
	Context mContext;
	int resId;
	public ServiceHandle mService;
	OnClickListener mDownListener;
	OnClickListener mFavListener;
	OnClickListener mVoteListener;
	private HashMap<Integer, View> vList = new HashMap<Integer, View>();

	private HashMap<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
	private ArrayList<RSSItem> mShowItemList;

	public void clearListView() {
		mShowItemList.clear();
		this.notifyDataSetChanged();
	}

	//
	public void setChannel(RSSChannel channel, ArrayList<RSSItem> showItemList,
			Boolean isClear) {
		if (showItemList == null)
			return;
		mShowItemList = showItemList;
		if (isClear) {
			vList.clear();
			mShowItemList.clear();
			int c = icons.size() - 1;
			for (int del = c; del >= 0; del--) {
				Bitmap delBitmap = icons.get(del);
				if (delBitmap != null && !delBitmap.isRecycled()) {
					icons.remove(del);
					delBitmap.recycle();
				}
			}
		}
		ArrayList<RSSItem> items = channel.getItems();
		int count = items.size();
		// 注意：下面的代码会导致java.util.ConcurrentModificationException
		// for (RSSItem item : items) {
		// itemList.add(item);
		// }
		for (int i = 0; i < count; i++) {
			RSSItem item = items.get(i);
			String pkg = item.getImageUrl();
			if (("apk_ad".equals(item.getCategory()))
					&& Commond.apkInstalled(mContext, pkg)) {
				// 已经安装
			} else {
				mShowItemList.add(item);
			}
		}
		//
		new LoadTask().execute();
	}

	//
	public MarketItemAdapter(Context context, ServiceHandle service, int resId,
			OnClickListener downListener, OnClickListener favListener,
			OnClickListener voteListener) {
		this.resId = resId;
		mContext = context;
		this.mService = service;
		this.mDownListener = downListener;
		this.mFavListener = favListener;
		this.mVoteListener = voteListener;
	}

	public int getCount() {
		if (mShowItemList == null)
			return 0;
		return mShowItemList.size();
	}

	public Object getItem(int position) {
		if (mShowItemList == null)
			return null;
		return mShowItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		// Debug.d("getView position:" + position);
		if (mShowItemList == null)
			return convertView;
		RSSItem item = mShowItemList.get(position);
		View itemView = null;
		ViewHolder holder = null;
		if (position < vList.size()) {
			itemView = vList.get(position);
			holder = (ViewHolder) itemView.getTag();
		} else {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = vi.inflate(resId, null);
			//
			holder = new ViewHolder();
			//
			holder.tvIcon = (ImageView) itemView.findViewById(R.id.item_icon);
			holder.tvProgress = (TextView) itemView
					.findViewById(R.id.item_progress);
			holder.tvTitle = (TextView) itemView.findViewById(R.id.item_title);
			holder.tvSize = (TextView) itemView.findViewById(R.id.item_size);
			holder.tvAuthor = (TextView) itemView
					.findViewById(R.id.item_author);
			holder.ivDown = (ImageView) itemView.findViewById(R.id.item_down);
			holder.ivFav = (ImageView) itemView.findViewById(R.id.item_fav);
			holder.tvFav = (TextView) itemView.findViewById(R.id.tv_fav);
			holder.ivVote = (ImageView) itemView.findViewById(R.id.item_vote);
			holder.tvVote = (TextView) itemView.findViewById(R.id.tv_vote);
			//
			//
			View down = itemView.findViewById(R.id.item_down_panel);
			down.setOnClickListener(mDownListener);
			down.setTag(item);
			View fav = itemView.findViewById(R.id.item_fav_panel);
			fav.setOnClickListener(mFavListener);
			fav.setTag(item);
			View vote = itemView.findViewById(R.id.item_vote_panel);
			vote.setOnClickListener(mVoteListener);
			vote.setTag(item);
		}
		//
		if (position % 2 == 0) {
			itemView.setBackgroundResource(R.drawable.list_item_background_0);
		} else {
			itemView.setBackgroundResource(R.drawable.list_item_background_1);
		}
		//
		itemView.setTag(holder);
		//
		Bitmap b = icons.get(position);
		if (b == null) {
			b = mService.getCacheBitmap(item.getThumbailUrl());
			icons.put(position, b);
		}
		holder.tvIcon.setImageBitmap(b);
		holder.tvIcon.setTag(item.getThumbailUrl());
		//
		ItemData itemData = (ItemData) item.getTag();
		if (itemData == null) {
			itemData = new ItemData();
			item.setTag(itemData);
		}
		//
		String tmp[] = item.getTitle().split("_");
		//
		if (tmp.length > 0) {
			holder.tvTitle.setText(tmp[0]);
		}
		//
		if (tmp.length > 1) {
			holder.tvAuthor.setText(tmp[1]);
		}
		//
		if (tmp.length > 2) {
			String size = StringUtil.readableFileSize(tmp[2]);
			holder.tvSize.setText(size);
		}
		itemData.tag = Integer.toString(position);
		// itemData.tag = item.getClass().getName();
		holder.tvProgress.setTag(itemData.tag);
		//
		if (itemData.downloaded) {
			holder.ivDown.setBackgroundResource(R.drawable.read_background);
			holder.tvProgress.setText(R.string.read_text);
		} else {
			holder.ivDown.setBackgroundResource(R.drawable.down_background);
			holder.tvProgress.setText(R.string.download_text);
		}
		//
		if (Data.getFavList(mContext).contains(item.getCategory())) {
			holder.ivFav.setBackgroundResource(R.drawable.favdel_background);
			holder.tvFav.setText(R.string.faved_text);
		} else {
			holder.ivFav.setBackgroundResource(R.drawable.fav_background);
			holder.tvFav.setText(R.string.fav_text);
		}
		//
		if (Data.getVoteList(mContext).contains(item.getCategory())) {
			holder.ivVote.setBackgroundResource(R.drawable.votedel_background);
			holder.tvVote.setText(R.string.voted_text);
		} else {
			holder.ivVote.setBackgroundResource(R.drawable.vote_background);
			holder.tvVote.setText(R.string.vote_text);
		}
		//
		vList.put(position, itemView);
		return itemView;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewHolder {
		public ImageView tvIcon;
		public TextView tvProgress;
		public TextView tvTitle;
		public TextView tvAuthor;
		public TextView tvSize;
		public ImageView ivDown;
		public ImageView ivFav;
		public TextView tvFav;
		public ImageView ivVote;
		public TextView tvVote;
	}

	/**
	 * 
	 * @author sunxml
	 * 
	 */
	class LoadTask extends AsyncTask<String, Integer, Integer> {
		/**
		 * 
		 */
		protected Integer doInBackground(String... pkgs) {
			try {
				int len = mShowItemList.size();
				for (int index = 0; index < len; index++) {
					RSSItem item = mShowItemList.get(index);
					ItemData itemData = (ItemData) item.getTag();
					if (itemData == null) {
						itemData = new ItemData();
						item.setTag(itemData);
					} else if (!TextUtils.isEmpty(itemData.progress))
						continue;
					//
					String tmp[] = item.getTitle().split("_");
					//
					long size = 0;
					if (tmp.length > 2 && TextUtils.isDigitsOnly(tmp[2])) {
						size = Long.parseLong(tmp[2]);
					}
					//
					String pkg = item.getCategory();

					if (Commond.apkInstalled(mContext, pkg)) {
						itemData.downloaded = true;
					} else {
						long filesize = mService
								.getFileSize(item.getImageUrl());
						if (filesize > 0 && size == filesize) {
							itemData.downloaded = true;
						}
					}
					//
				}
			} catch (Exception ex) {

			}
			return 0;
		}

		/**
		 * 
		 */
		protected void onProgressUpdate(Integer... values) {
		}

		protected void onPostExecute(Integer result) {
			MarketItemAdapter.this.notifyDataSetChanged();
		}
	};
}
