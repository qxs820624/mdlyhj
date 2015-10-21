package com.hutuchong.util;

import mobi.domore.mcdonalds.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class DotsView extends View {

	private Bitmap mDotBmp;
	private int mCount = 0;
	private boolean layout_alignParentRight = false;
	private boolean layout_alignParentBottom = false;
	private int mRowMaxCount = 7;
	private int mRowCount = 0;

	public void setCount(int mCount) {
		this.mCount = mCount;
		this.mRowCount = (mCount / mRowMaxCount)
				+ ((mRowCount % mRowMaxCount) > 0 ? 1 : 0);
		//
		int w = this.mDotBmp.getWidth() * mCount;
		if (this.mRowCount > 0)
			w = this.mDotBmp.getWidth() * mRowMaxCount;
		int h = this.mRowCount * mDotBmp.getHeight();
		this.measure(w, h);
	}

	//
	public DotsView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mDotBmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.indicator_normal);
	}

	public DotsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mDotBmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.indicator_normal);
		int[] atts = { android.R.attr.layout_alignParentRight,
				android.R.attr.layout_alignParentBottom,
				android.R.attr.layout_marginBottom };
		TypedArray params = context.obtainStyledAttributes(attrs, atts);
		layout_alignParentRight = params.getBoolean(0, false);
		layout_alignParentBottom = params.getBoolean(1, false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = 0;
		int y = 0;
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00);

		if (layout_alignParentBottom) {
			y = this.getHeight() - mDotBmp.getHeight();
		}
		//
		if (layout_alignParentRight) {
			x = this.getWidth() - mDotBmp.getWidth();
		}
		for (int i = 0; i < mCount; i++) {
			canvas.drawBitmap(mDotBmp, x, y, null);
			if (layout_alignParentRight) {
				x -= mDotBmp.getWidth();
			} else
				x += mDotBmp.getWidth();
			//
			if (i == mRowMaxCount - 1) {
				y -= mDotBmp.getHeight();
				if (layout_alignParentRight) {
					x = this.getWidth() - mDotBmp.getWidth();
				} else
					x = 0;
			}
		}
	}
}