package com.hutuchong.app_mcdonalds;

import java.util.HashMap;

import mobi.domore.mcdonalds.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hutuchong.app_mcdonalds.adapter.FilterItemAdapter;
import com.hutuchong.util.Commond;

/**
 * 
 * @author sunxml
 * 
 */
public class FilterDialog {
	Context mContext;
	Dialog dialog;
	ListView listView;
	FilterItemAdapter filterAdapter;
	HashMap<String, Boolean[]> mNames;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public FilterDialog(Context context, HashMap<String, Boolean[]> names,
			OnCancelListener cancelListener) {
		mContext = context;
		mNames = names;
		dialog = Commond.createDialog(mContext, R.layout.app_mcdonalds_filter);
		dialog.setOnCancelListener(cancelListener);
		// 关闭按钮
		View btnCancel = dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

		// 确定按钮
		View btnOk = dialog.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean ishas = false;
				for (String key : mNames.keySet()) {
					Boolean values[] = mNames.get(key);
					if (values[1]) {
						ishas = true;
						break;
					}
				}
				if (!ishas) {
					Commond.showToast(mContext, "请至少选择一项单品!");
					return;
				}
				dialog.cancel();
			}

		});
		//
		listView = (ListView) dialog.findViewById(R.id.list_view);
		filterAdapter = new FilterItemAdapter(mContext,
				R.layout.app_mcdonalds_filter_item, mNames);
		listView.setAdapter(filterAdapter);
		//
		listView.setOnItemClickListener(listener);
	}

	/**
	 * 
	 */
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String key = (String) mNames.keySet().toArray()[arg2];
			Boolean[] bb = mNames.get(key);
			bb[1] = !bb[1];
			filterAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}
}