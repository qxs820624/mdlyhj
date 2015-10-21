package com.hutuchong.util;

import android.graphics.Canvas;
import android.view.View;

/**
 * 
 * @author Administrator
 * 
 */
public class MoveView implements ViewHandle {

	private View mView;

	public MoveView(View view) {
		mView = view;
	}

	@Override
	public void adjustOffset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawMain(Canvas c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveOffset(int offx, int offy) {
		// TODO Auto-generated method stub
		if (mView == null)
			return;
		int t = mView.getTop();
		int l = mView.getLeft();
		int b = mView.getBottom();
		int r = mView.getRight();
		mView.layout(l + offx, t + offy, r + offx, b + offy);
		mView.postInvalidate();
	}

	@Override
	public boolean scroll(float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void slide(int offx, int offy, boolean isNeedAdjust) {
		// TODO Auto-generated method stub

	}

}