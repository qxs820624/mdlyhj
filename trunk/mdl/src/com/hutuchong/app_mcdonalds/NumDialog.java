package com.hutuchong.app_mcdonalds;

import mobi.domore.mcdonalds.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.hutuchong.app_mcdonalds.data.McdonaldsEntity;
import com.hutuchong.util.Commond;

/**
 * 
 * @author sunxml
 * 
 */
public class NumDialog {
	Context mContext;
	Dialog dialog;
	McdonaldsEntity mEntity;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public NumDialog(Context context, OnCancelListener cancelListener,
			McdonaldsEntity entity) {
		mContext = context;
		mEntity = entity;
		dialog = Commond.createDialog(mContext, R.layout.app_mcdonalds_num);
		dialog.setOnCancelListener(cancelListener);
		// 关闭按钮
		View btnClose = dialog.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		View num_1 = dialog.findViewById(R.id.num_1);
		num_1.setTag(1);
		num_1.setOnClickListener(listener);
		View num_2 = dialog.findViewById(R.id.num_2);
		num_2.setTag(2);
		num_2.setOnClickListener(listener);
		View num_3 = dialog.findViewById(R.id.num_3);
		num_3.setTag(3);
		num_3.setOnClickListener(listener);
		View num_4 = dialog.findViewById(R.id.num_4);
		num_4.setTag(4);
		num_4.setOnClickListener(listener);
		View num_5 = dialog.findViewById(R.id.num_5);
		num_5.setTag(5);
		num_5.setOnClickListener(listener);
		View num_6 = dialog.findViewById(R.id.num_6);
		num_6.setTag(6);
		num_6.setOnClickListener(listener);
		View num_7 = dialog.findViewById(R.id.num_7);
		num_7.setTag(7);
		num_7.setOnClickListener(listener);
		View num_8 = dialog.findViewById(R.id.num_8);
		num_8.setTag(8);
		num_8.setOnClickListener(listener);
		View num_9 = dialog.findViewById(R.id.num_9);
		num_9.setTag(9);
		num_9.setOnClickListener(listener);
		View num_10 = dialog.findViewById(R.id.num_10);
		num_10.setTag(10);
		num_10.setOnClickListener(listener);
		View num_11 = dialog.findViewById(R.id.num_11);
		num_11.setTag(11);
		num_11.setOnClickListener(listener);
		View num_12 = dialog.findViewById(R.id.num_12);
		num_12.setTag(12);
		num_12.setOnClickListener(listener);
	}

	/**
	 * 
	 */
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int value = (Integer) v.getTag();
			mEntity.setSelected(true);
			mEntity.setNum(value);
			dialog.cancel();
		}
	};

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}
}