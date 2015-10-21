package com.hutuchong.app_mcdonalds;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hutuchong.app_mcdonalds.adapter.DetailItemAdapter;
import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;
import com.hutuchong.util.Commond;

/**
 * 
 * @author sunxml
 * 
 */
public class DetailDialog {
	Context mContext;
	RSSChannel mChannel;
	Dialog dialog;
	ListView listView;
	DetailItemAdapter detailAdapter;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public DetailDialog(Context context, RSSChannel channel,
			OnCancelListener cancelListener) {
		mContext = context;
		mChannel = channel;
		dialog = Commond.createDialog(mContext, R.layout.app_mcdonalds_detail);
		dialog.setOnCancelListener(cancelListener);
		// 关闭按钮
		View btnCancel = dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (RSSItem item : mChannel.getItems()) {
					McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
					entity.setSelected(item.isReaded());
				}
				dialog.dismiss();
			}
		});

		// 确定按钮
		View btnOk = dialog.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mChannel == null)
					return;
				for (RSSItem item : mChannel.getItems()) {
					McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
					if (!entity.isSelected())
						entity.setNum(0);
				}
				dialog.cancel();
			}
		});
		//
		listView = (ListView) dialog.findViewById(R.id.list_view);
		detailAdapter = new DetailItemAdapter(mContext,
				R.layout.app_mcdonalds_detail_item, channel);
		listView.setAdapter(detailAdapter);
		//
		listView.setOnItemClickListener(listener);
		//
		setPriceText();
	}

	/**
	 * 
	 */
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			RSSItem item = detailAdapter.showItemList.get(arg2);
			McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
			entity.setSelected(!entity.isSelected());
			detailAdapter.notifyDataSetChanged();
			setPriceText();
		}
	};

	private void setPriceText() {
		String[] prices = McdonaldsEntity.totalPrice(mChannel);
		//
		TextView tv_title = (TextView) dialog.findViewById(R.id.detail_title);
		tv_title.setText("总计：￥" + prices[0] + "  省：￥" + prices[1]);
	}

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}
}