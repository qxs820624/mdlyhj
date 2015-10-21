package com.hutuchong.util;

import java.util.Observable;
import java.util.Observer;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BaseView extends View implements ViewHandle, Observer {
	public Paint paint = new Paint();
	public Context mContext;
	private BaseService service;

	public BaseService getService() {
		return service;
	}

	public void setService(BaseService service) {
		this.service = service;
	}

	private GestureDetector mGestureDetector;

	public int offsetx = 0;
	public int offsety = 0;
	public int selOffY = 0;
	public int menuH = 30;

	//
	public RSSChannel mChannel = new RSSChannel();
	public Bitmap mIndicatorBmp;
	public Bitmap mCacheBmp;

	public String[] mMenuTitle = null;
	public Rect[] mMenuRects = null;
	public int mSelectedMenuIndex = -1;

	//

	//
	public BaseView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
		init(context);
	}

	public void init(Context context) {
		mContext = context;
		mGestureDetector = new GestureDetector(mContext,
				new HotGestureListener());

		//
		this.setDrawingCacheEnabled(false);

		paint.setAntiAlias(true);
		paint.setTextSize(18);
		paint.setTypeface(Typeface.SERIF);
		paint.setStyle(Paint.Style.STROKE);
		//
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setLongClickable(true);
		this.setClickable(true);
		this.setOnKeyListener(mKeyListener);
		this.setOnTouchListener(mTouchListener);
		//
	}

	/**
	 * 
	 */
	private OnKeyListener mKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_UP)
				return false;
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			} else {
				return false;
			}
			return true;
		}
	};

	/**
	 * 
	 */
	public OnTouchListener mTouchListener = new OnTouchListener() {
		private float mX;
		private float mY;

		public boolean onTouch(View v, MotionEvent event) {
			// /////////////////////////////////////////
			// //////////////////////////////////////////
			/*
			 * final int action = event.getAction(); int pointCount =
			 * event.getPointerCount(); if (pointCount == 1 &&
			 * mZoomState.getZoom() != 1f) { final float x = event.getX(); final
			 * float y = event.getY(); switch (action) { case
			 * MotionEvent.ACTION_DOWN: mX = x; mY = y; break; case
			 * MotionEvent.ACTION_MOVE: { final float dx = (x - mX) /
			 * v.getWidth(); final float dy = (y - mY) / v.getHeight();
			 * mZoomState.setPanX(mZoomState.getPanX() - dx);
			 * mZoomState.setPanY(mZoomState.getPanY() - dy);
			 * mZoomState.notifyObservers(); mX = x; mY = y; break; } } } if
			 * (pointCount == 2) { final float x0 =
			 * event.getX(event.getPointerId(0)); final float y0 =
			 * event.getY(event.getPointerId(0));
			 * 
			 * final float x1 = event.getX(event.getPointerId(1)); final float
			 * y1 = event.getY(event.getPointerId(1));
			 * 
			 * final float gap = getGap(x0, x1, y0, y1); switch (action) { case
			 * MotionEvent.ACTION_POINTER_2_DOWN: case
			 * MotionEvent.ACTION_POINTER_1_DOWN: mGap = gap; break; case
			 * MotionEvent.ACTION_MOVE: { final float dgap = (gap - mGap) /
			 * mGap; // Log.d("Gap", String.valueOf(dgap)); Log.d("Gap",
			 * String.valueOf((float) Math.pow(20, dgap)));
			 * mZoomState.setZoom(mZoomState.getZoom() (float) Math.pow(5,
			 * dgap)); mZoomState.notifyObservers(); mGap = gap; break; } }
			 * return true; }
			 */
			// /////////////////////////////////////////
			// ///////////////////////////////////////////
			if (!((BaseActivity) mContext).isTouchEnable())
				return true;
			boolean istouch = mGestureDetector.onTouchEvent(event);
			// Debug.d("istouch:" + istouch);
			// Debug.d(" onTouch MotionEvent.ACTION_UP:" + istouch);
			// Debug.d(" onTouch MotionEvent.ACTION_UP:" + MotionEvent.ACTION_UP
			// + " " + event.getAction());
			if (!istouch && event.getAction() == MotionEvent.ACTION_UP) {
				// Debug.d(" onTouch MotionEvent.ACTION_UP");
				adjustOffset();
			}
			return istouch;
		}
	};

	private float getGap(float x0, float x1, float y0, float y1) {
		return (float) Math.pow(
				Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2), 0.5);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//

		drawMain(canvas);
		//
		// drawIndicator(canvas);
		//
		// drawBottomMenu(canvas);
	}

	/**
	 * 
	 */
	@Override
	public void moveOffset(int offx, int offy) {
		offsetx += offx;
		offsety += offy;
		this.postInvalidate();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean clickUpItem(int x, int y) {
		return false;
	}

	/**
	 * 
	 */
	public boolean clickDownItem(int x, int y) {
		return false;
	}

	/**
	 * 
	 * @author 3gqa.com
	 * 
	 */
	class HotGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			// Debug.d("onSingleTapUp x:" + x + " y:" + y);
			// mActivity.onClickUp(x, y);
			clickUpItem(x, y);
			return false;
		}

		@Override
		public void onShowPress(MotionEvent ev) {
			// Debug.d("DEBUG", "onShowPress");
			((BaseActivity) mContext).onShowPress();
		}

		@Override
		public void onLongPress(MotionEvent ev) {
			// Debug.d("DEBUG", "onLongPress");
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// Debug.d("DEBUG", "onScroll e1:" + e1.getAction() + " e2:"
			// + e2.getAction());
			// 横向移动处理
			scroll(distanceX, distanceY);
			return false;
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			// Debug.d("onDownd x:" + x + " y:" + y);
			return clickDownItem(x, y);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// Debug.d("DEBUG", "onFling e1:" + e1.getAction() + " e2:"
			// + e2.getAction());
			// Debug.d("DEBUG", "onFling velocityX:" + velocityX + " velocityY:"
			// + velocityY);
			if (e1.getX() - e2.getX() > getWidth() / 2) {
				// Fling left

			} else if (e2.getX() - e1.getX() > getWidth() / 2) {
				// Fling right

			}
			int offy = (int) e2.getY() - (int) e1.getY();
			int offx = (int) e2.getX() - (int) e1.getX();
			slide(offx, offy, true);
			return true;
		}

		public boolean onDoubleTap(MotionEvent event) {
			// Debug.d("DEBUG", "onDoubleTap");
			return false;
		}
	}

	/**
	 * 
	 * @param offset
	 * @param isx
	 * @param isneedadjust
	 */
	public void startThreadSlide(int offx, int offy, boolean isneedadjust) {
		MoveThread moveThread = new MoveThread(this);
		moveThread.startMove(offx, offy, isneedadjust);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		invalidate();
	}

	@Override
	public void drawMain(Canvas c) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	@Override
	public void slide(int offx, int offy, boolean isNeedAdjust) {
		//
		//
		startThreadSlide(offx, offy, isNeedAdjust);
	}

	@Override
	public boolean scroll(float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void adjustOffset() {
	}
}