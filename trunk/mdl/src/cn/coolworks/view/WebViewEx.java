package cn.coolworks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import cn.coolworks.util.Debug;

import mobi.domore.mcdonalds.R;

public class WebViewEx extends WebView {
	private Context mContext;
	private View mView;
	Animation tovisibleAnim;
	AnimationListener tovisibleListener;
	Animation togoneAnim;
	AnimationListener togoneListener;

	public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public WebViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public WebViewEx(Context context) {
		super(context);
		mContext = context;
	}

	public void initView(View view) {
		this.mView = view;
		//
		tovisibleAnim = AnimationUtils.loadAnimation(mContext,
				R.anim.tovisible_1s);
		tovisibleListener = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// mView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		};
		togoneAnim = AnimationUtils.loadAnimation(mContext, R.anim.togone_1s);
		togoneListener = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// mView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		};
		tovisibleAnim.setAnimationListener(tovisibleListener);
		togoneAnim.setAnimationListener(togoneListener);
		tovisibleAnim.setFillEnabled(true);
		tovisibleAnim.setFillAfter(true);
		togoneAnim.setFillEnabled(true);
		togoneAnim.setFillAfter(true);
	}

	private void setView() {
		Debug.d("computeVerticalScrollOffset:" + computeVerticalScrollOffset());
		Debug.d("computeVerticalScrollExtent:" + computeVerticalScrollExtent());
		Debug.d("computeVerticalScrollRange:" + computeVerticalScrollRange());
		if (mView == null)
			return;

		if (computeVerticalScrollOffset() + computeVerticalScrollExtent() == computeVerticalScrollRange()) {
			mView.setVisibility(View.VISIBLE);
			mView.startAnimation(tovisibleAnim);
		} else {
			if (mView.getVisibility() == View.VISIBLE)
				mView.startAnimation(togoneAnim);
			mView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//setView();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		setView();
	}
}