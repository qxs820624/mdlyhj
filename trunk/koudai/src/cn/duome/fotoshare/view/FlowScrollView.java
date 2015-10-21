package cn.duome.fotoshare.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * 扩展瀑布展示方式的滚动视图
 * 
 * @author dky
 * 
 */
public class FlowScrollView extends ScrollView {
	/**
	 * 重载滚动条位置变换时处理函数
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		onScrollListener.onAutoScroll();
	}

	private Handler handler;
	private LinearLayout view;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public FlowScrollView(Context context) {
		super(context);

	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 */
	public FlowScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FlowScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	/**
	 * 这个获得总的高度
	 */
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	/**
	 * 计算按滚动位置
	 */
	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	/**
	 * 初始化
	 */
	private void init() {

		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// process incoming messages here
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					int height = 0;
					for (int i = 0; i < view.getChildCount(); i++) {
						if (height == 0
								|| view.getChildAt(i).getMeasuredHeight() < height)
							height = view.getChildAt(i).getMeasuredHeight();
					}
					if (height - 20 <= getScrollY() + getHeight()) {
						if (onScrollListener != null) {
							onScrollListener.onBottom();
						}

					} else if (getScrollY() == 0) {
						if (onScrollListener != null) {
							onScrollListener.onTop();
						}
					} else {
						if (onScrollListener != null) {
							onScrollListener.onScroll();
						}
					}
					break;
				default:
					break;
				}
			}
		};

	}

	/**
	 * 触摸侦听器
	 */
	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (view != null && onScrollListener != null) {
					handler.sendMessageDelayed(handler.obtainMessage(1), 200);
				}
				break;

			default:
				break;
			}
			return false;
		}

	};

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = (LinearLayout) getChildAt(0);
		if (view != null) {
			init();
		}
	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll();
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
