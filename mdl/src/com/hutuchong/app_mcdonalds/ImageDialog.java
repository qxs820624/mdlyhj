package com.hutuchong.app_mcdonalds;

import mobi.domore.mcdonalds.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hutuchong.util.Commond;

/**
 * 
 * @author sunxml
 * 
 */
public class ImageDialog {
	Context mContext;
	Dialog dialog;
	ProgressBar progressBar;
	ImageView imageView;

	/**
	 * 构造类
	 * 
	 * @param context
	 */
	public ImageDialog(Context context) {
		mContext = context;
		dialog = Commond.createDialog(mContext, R.layout.app_mcdonalds_image);
		progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		imageView = (ImageView) dialog.findViewById(R.id.item_image);
		//
		View v = dialog.findViewById(R.id.btn_close);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}

	public void showImage(Bitmap bm) {
		progressBar.setVisibility(View.GONE);
		imageView.setImageBitmap(bm);
	}
}