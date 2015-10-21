package com.hutuchong.util;

import android.graphics.Canvas;

/**
 * 
 * @author 3gqa.com
 * 
 */
public interface ViewHandle {
	public void drawMain(Canvas c);
	//
	public boolean scroll(float distanceX, float distanceY);

	//
	public void slide(int offx, int offy, boolean isNeedAdjust);

	public void moveOffset(int offx, int offy);

	public void adjustOffset();
}