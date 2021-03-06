package cn.duome.fotoshare.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cn.duome.fotoshare.entity.FlowTag;

import com.hutuchong.util.BaseContent;

public class FlowView extends ImageView {

	private FlowTag flowTag;
	private Context context;
	public Bitmap bitmap;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private Handler viewHandler;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {
		setAdjustViewBounds(true);

	}

	/**
	 * 加载图片
	 */
	public void LoadImage() {
		if (getFlowTag() != null) {
			// task = new ImageLoaderTask(this);
			// task.execute(getFlowTag());
			new LoadImageThread().start();
		}
	}

	/**
	 * 重新加载图片
	 */
	public void Reload() {
		if (this.bitmap == null && getFlowTag() != null) {
			// task = new ImageLoaderTask(this);
			// task.execute(getFlowTag());
			new LoadImageThread().start();
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public FlowTag getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag) {
		this.flowTag = flowTag;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {

			if (flowTag != null) {
				bitmap = BaseContent.getInstance(context).getCacheBitmap(
						flowTag.getFileName(), 0,0);
				if (bitmap != null) {

					// 此处不能直接更新UI，否则会发生异常：
					// CalledFromWrongThreadException: Only the original thread
					// that
					// created a view hierarchy can touch its views.
					// 也可以使用Handler或者Looper发送Message解决这个问题

					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							if (bitmap != null) {// 此处在线程过多时可能为null
								int width = bitmap.getWidth();// 获取真实宽高
								int height = bitmap.getHeight();

								LayoutParams lp = getLayoutParams();
								lp.height = (height * flowTag.getItemWidth())
										/ width;// 调整高度
								// setLayoutParams(lp);
								setScaleType(ImageView.ScaleType.FIT_XY);
								setImageBitmap(bitmap);// 将引用指定到同一个对象，方便销毁
								Handler h = getViewHandler();
								Message m = h.obtainMessage(flowTag.what,
										width, height, FlowView.this);
								h.sendMessage(m);
							}
						}
					});
				}
			}

		}
	}
}
