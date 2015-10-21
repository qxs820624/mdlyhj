package cn.coolworks.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

public class TextExtraView extends View {
	Context context;
	SurfaceHolder mHolder;
	int x = 0;
	int y = 0;
	int len = 0;
	Paint paint = new Paint();
	String autoScrollText;
	MyThread thread;
	int background;
	int textColor;

	public void setAutoScrollText(String text) {
		autoScrollText = text;
		initText();
	}

	public void setAutoScrollText(int resId) {
		autoScrollText = context.getText(resId).toString();
		initText();
	}

	private void initText() {
		x = 0;
		if (autoScrollText != null) {
			len = (int) paint.measureText(autoScrollText);
			if (thread != null)
				thread.setRunning(false);
			if (len > this.getWidth()) {
				thread = new MyThread();
				thread.setRunning(true);
				thread.start();
			}
		}
		invalidate();
	}

	public TextExtraView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public TextExtraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int count = attrs.getAttributeCount();
		for (int i = 0; i < count; i++) {
			if (attrs.getAttributeName(i).equals("background")) {
				background = Color.parseColor(attrs.getAttributeValue(i));
			} else if (attrs.getAttributeName(i).equals("textColor")) {
				textColor = Color.parseColor(attrs.getAttributeValue(i));
			} else if (attrs.getAttributeName(i).equals("paddingtop")) {
				String str = attrs.getAttributeValue(i).replace("px", "");
				str = attrs.getAttributeValue(i).replace(".0px", "");
				if (TextUtils.isDigitsOnly(str)) {
					y = Integer.parseInt(str);
				}
			}

		}
		this.context = context;
		init();
	}

	private void init() {
		//
		// this.setBackgroundColor(background);//如果设置后文字绘制不显示问题
		paint.setTextSize(16);
		paint.setColor(textColor);
		String familyName = "宋体";
		Typeface font = Typeface.create(familyName, Typeface.NORMAL);
		paint.setTypeface(font);
		// 如果没有调用这个方法，写上去的字不饱满，不美观。
		paint.setAntiAlias(true);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	public void onDraw(Canvas canvas) {
		onPaint(canvas);
	}

	public synchronized void onPaint(Canvas canvas) {
		if (canvas != null) {
			canvas.drawColor(background);
			if (autoScrollText != null) {
				canvas.drawText(autoScrollText, x, y, paint);
			}
		}
	}

	public class MyThread extends Thread {
		private boolean isRunning = true;

		public MyThread() {
		}

		public void setRunning(boolean run) {
			isRunning = run;
		}

		public void run() {
			// TODO Auto-generated method stub
			while (isRunning) {
				try {
					if (x == 0)
						Thread.sleep(2000);
					else
						Thread.sleep(50);
					x--;
					if (x + len < getWidth() - 20) {
						x = 0;
						Thread.sleep(2000);
					}
					postInvalidate();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
}