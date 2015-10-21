package cn.coolworks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewEx extends HorizontalScrollView {
	public View mLeftView;
	public View mRightView;

	public HorizontalScrollViewEx(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalScrollViewEx(Context context) {
		super(context);
	}

	public void initLeftRightView(View leftView, View rightView) {
		this.mLeftView = leftView;
		this.mRightView = rightView;
	}

	private void setLeftRightView() {
		if (mLeftView == null || mRightView == null)
			return;
		if (computeHorizontalScrollOffset() == 0)
			mLeftView.setVisibility(View.INVISIBLE);
		else
			mLeftView.setVisibility(View.VISIBLE);
		//
		// System.out.println("computeHorizontalScrollOffset:"
		// + computeHorizontalScrollOffset());
		// System.out.println("computeHorizontalScrollExtent:"
		// + computeHorizontalScrollExtent());
		// System.out.println("computeHorizontalScrollRange:"
		// + computeHorizontalScrollRange());
		// System.out
		// .println("==:"
		// + (computeHorizontalScrollOffset()
		// + computeHorizontalScrollExtent() - computeHorizontalScrollRange()));
		if (computeHorizontalScrollOffset() + computeHorizontalScrollExtent() >= computeHorizontalScrollRange()) {
			mRightView.setVisibility(View.INVISIBLE);
		} else {
			mRightView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setLeftRightView();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		setLeftRightView();
	}
}