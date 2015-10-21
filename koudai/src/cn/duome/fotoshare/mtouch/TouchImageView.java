package cn.duome.fotoshare.mtouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	private GestureDetector gestureScanner;
	private static final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	boolean isZoomIn = true;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	Context context;

	float initScale;
	float redundantYSpace;
	float redundantXSpace;

	/**
	 * Constructor
	 */
	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TouchImageView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 
	 * @param gestureScanner
	 */
	public void setGestureScanner(GestureDetector gestureScanner) {
		this.gestureScanner = gestureScanner;
	}

	/**
	 * 
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		if (gestureScanner != null && isZoomIn) {
			gestureScanner.onTouchEvent(e);
		}
		return super.dispatchTouchEvent(e);
	}

	private void init(Context context) {
		super.setClickable(true);
		this.context = context;

		matrix.setTranslate(1f, 1f);
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent rawEvent) {
				WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

				// Dump touch event to log
				/*
				 * if (Viewer.isDebug == true) { dumpEvent(event); }
				 */
				// Handle touch events here...
				float[] values = new float[9];
				matrix.getValues(values);
				float scaleX = values[Matrix.MSCALE_X];
				float scaleY = values[Matrix.MSCALE_Y];
				isZoomIn = scaleX <= initScale ? true : false;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					Log.d(TAG, "mode=DRAG");
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					Log.d(TAG, "oldDist=" + oldDist);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						// midPoint(mid, event);
						mode = ZOOM;
						// Log.d(TAG, "mode=ZOOM");
					}
					break;
				case MotionEvent.ACTION_UP:
					int xDiff = (int) Math.abs(event.getX() - start.x);
					int yDiff = (int) Math.abs(event.getY() - start.y);
					if (xDiff < 8 && yDiff < 8) {
						performClick();
					}
					// break;
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					// Log.d(TAG, "mode=NONE");

					if (scaleX < initScale) {
						// 自动恢复到初始大小（若图片初始高或宽大于视图的高和宽，则恢复到视图大小）
						float toScaleX = initScale / scaleX;
						float toScaleY = initScale / scaleY;
						matrix.postScale(toScaleX, toScaleY, mid.x, mid.y);
						setImageMatrix(matrix);
						// Center the image
						matrix.getValues(values);
						matrix.postTranslate(redundantXSpace
								- values[Matrix.MTRANS_X], redundantYSpace
								- values[Matrix.MTRANS_Y]);
					} else if (scaleX > initScale) {
						// 移动的距离
						float mx = event.getX() - start.x;
						float my = event.getY() - start.y;
						//
						float transX = values[Matrix.MTRANS_X];
						float transY = values[Matrix.MTRANS_Y];
						Drawable drawable = getDrawable();
						if (drawable != null) {
							Rect imageBounds = drawable.getBounds();
							float scaledHeight = imageBounds.height() * scaleY;
							float scaledWidth = imageBounds.width() * scaleX;
							//
							float mmx = 0;
							float mmy = 0;
							if (mx > 0) {// 向右移动
								if (transX > 0
										&& transX + scaledWidth > getWidth()) {
									if (scaledWidth < getWidth())
										mmx = -(transX + scaledWidth - getWidth());
									else
										mmx = -transX;
								}
							} else if (mx < 0) {// 向左移动
								if (transX < 0
										&& transX + scaledWidth < getWidth()) {
									if (scaledWidth < getWidth())
										mmx = -transX;
									else
										mmx = -(transX + scaledWidth - getWidth());
								}
							}
							if (my > 0) {// 向下移动
								if (transY > 0
										&& transY + scaledHeight > getHeight()) {
									if (scaledHeight < getHeight())
										mmy = -(transY + scaledHeight - getHeight());
									else
										mmy = -transY;
								}
							} else if (my < 0) {// 向上移动
								if (transY < 0
										&& transY + scaledHeight < getHeight()) {
									if (scaledHeight < getHeight())
										mmy = -transY;
									else
										mmy = -(transY + scaledHeight - getHeight());
								}
							}
							matrix.postTranslate(mmx, mmy);
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						// 在缩小模式，不响应移动
						if (scaleX <= initScale) {
							break;
						}
						// 移动的距离
						float mx = event.getX() - start.x;
						float my = event.getY() - start.y;
						matrix.set(savedMatrix);
						matrix.postTranslate(mx, my);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						Log.d(TAG, "newDist=" + newDist);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}
				setImageMatrix(matrix);
				return true; // indicate event was handled
			}

		});
	}

	/**
	 * 
	 * @param bm
	 */
	public void setImage(Bitmap bm) {
		if (bm == null)
			return;
		// mBitmap.recycle();
		// mBitmap = null;
		super.setImageBitmap(bm);
		System.out.println("width:" + bm.getWidth() + " height:"
				+ bm.getHeight());
		System.out.println("width:" + this.getWidth() + " height:"
				+ this.getHeight());
		// Fit to screen.
		float scale;
		if ((this.getHeight() / bm.getHeight()) >= (this.getWidth() / bm
				.getWidth())) {
			scale = (float) this.getWidth() / (float) bm.getWidth();
		} else {
			scale = (float) this.getHeight() / (float) bm.getHeight();
		}
		if (scale > 1)
			scale = 1;
		initScale = scale;

		matrix.reset();
		savedMatrix.reset();
		mid.x = 0;
		mid.y = 0;

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postScale(scale, scale, mid.x, mid.y);
		setImageMatrix(matrix);

		// Center the image
		redundantYSpace = (float) this.getHeight()
				- (scale * (float) bm.getHeight());
		redundantXSpace = (float) this.getWidth()
				- (scale * (float) bm.getWidth());

		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		mid.set(this.getWidth() / 2, this.getHeight() / 2);

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postTranslate(redundantXSpace, redundantYSpace);
		setImageMatrix(matrix);
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(WrapMotionEvent event) {
		// ...
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(WrapMotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, WrapMotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}