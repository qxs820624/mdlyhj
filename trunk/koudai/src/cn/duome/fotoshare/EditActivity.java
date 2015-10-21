package cn.duome.fotoshare;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import cn.duome.fotoshare.mtouch.TouchImageView;
import cn.duome.fotoshare.utils.Constant;
import cn.duome.fotoshare.utils.FotoCommond;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;

/**
 * 图片编辑处理页面
 * 
 * @author dky
 * 
 */
public class EditActivity extends FotoBaseActivity {
	private TouchImageView zoomView;
	public GestureDetector gestureScanner;
	int[] resids = { R.id.header_panel, R.id.edit_menu_panel,
			R.id.edge_menu_panel, R.id.nav_panel };
	Bitmap mOldBitmap;
	Bitmap mNewBitmap;
	String mFileName;
	String mThumbName;
	String mTitle;
	View vEdit;
	View vEdge;
	View vCombine;
	View vEditPanel;
	View vEdgePanel;
	View vCombinePanel;

	/**
	 * Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化
		init(getText(R.string.edit_title).toString(), R.layout.edit, true);
		// 保存按钮处理
		View v = findViewById(R.id.btn_share);
		v.setBackgroundResource(R.drawable.nav_btn_save_background);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(listener);

		setGestureDetector(resids);
		zoomView = (TouchImageView) this.findViewById(R.id.zoom_view);
		zoomView.setGestureScanner(gestureScanner);
		// Bitmap bitmap = ablum.getItems().get(subIndex).getBmpImage();
		reLoad();
		//

		View vLeft = findViewById(R.id.btn_left);
		vLeft.setOnClickListener(listener);
		View vRight = findViewById(R.id.btn_right);
		vRight.setOnClickListener(listener);
		View vCut = findViewById(R.id.btn_cut);
		vCut.setOnClickListener(listener);
		View vDel = findViewById(R.id.btn_del);
		vDel.setOnClickListener(listener);
		//
		vEdit = findViewById(R.id.btn_edit);
		vEdit.setOnClickListener(listener);
		vEdge = findViewById(R.id.btn_edge);
		vEdge.setOnClickListener(listener);
		vCombine = findViewById(R.id.btn_combine);
		vCombine.setOnClickListener(listener);
		vEditPanel = findViewById(R.id.edit_menu_panel);
		vEdgePanel = findViewById(R.id.edge_menu_panel);
		vCombinePanel = findViewById(R.id.combine_menu_panel);
		//
		View edge1 = findViewById(R.id.btn_edge_1);
		edge1.setOnClickListener(listener);
		View edge2 = findViewById(R.id.btn_edge_2);
		edge2.setOnClickListener(listener);
		View edge3 = findViewById(R.id.btn_edge_3);
		edge3.setOnClickListener(listener);
		View edge4 = findViewById(R.id.btn_edge_4);
		edge4.setOnClickListener(listener);
		View edge5 = findViewById(R.id.btn_edge_5);
		edge5.setOnClickListener(listener);
		// View edge6 = findViewById(R.id.btn_edge_6);
		// edge6.setOnClickListener(listener);
		//
		switchNavPanel(0);
	}

	/**
	 * 重新加载函数
	 */
	private void reLoad() {
		// 获取传递参数
		Intent i = this.getIntent();
		mFileName = i.getStringExtra(BaseContant.EXTRA_FILENAME);
		if (i.hasExtra(BaseContant.EXTRA_URL))
			mThumbName = i.getStringExtra(BaseContant.EXTRA_URL);
		if (i.hasExtra(BaseContant.EXTRA_TITLE))
			mTitle = i.getStringExtra(BaseContant.EXTRA_TITLE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 注意：没有这句话就出现zoomView的宽度和高度始终为0的情况
		int w = dm.widthPixels;
		int h = dm.heightPixels - 200;
		mOldBitmap = BaseContent.getInstance(mContext).getCacheBitmap(
				mFileName, w, h);
		zoomView.layout(0, 0, w, h);
		// zoomView.setImageBitmap(bitmap);
		zoomView.setImage(mOldBitmap);
		if (mNewBitmap != null) {
			if (!mNewBitmap.isRecycled())
				mNewBitmap.recycle();
			mNewBitmap = null;
		}
	}

	/**
	 * 切换底部操作按钮导航栏
	 * 
	 * @param index
	 */
	private void switchNavPanel(int index) {
		switch (index) {
		case 0:
			vEdit.setBackgroundResource(R.drawable.edit_nav_selected);
			vEdge.setBackgroundResource(R.drawable.edit_nav_background);
			vCombine.setBackgroundResource(R.drawable.edit_nav_background);
			vEditPanel.setVisibility(View.VISIBLE);
			vEdgePanel.setVisibility(View.GONE);
			vCombinePanel.setVisibility(View.GONE);
			break;
		case 1:
			vEdit.setBackgroundResource(R.drawable.edit_nav_background);
			vEdge.setBackgroundResource(R.drawable.edit_nav_selected);
			vCombine.setBackgroundResource(R.drawable.edit_nav_background);
			vEditPanel.setVisibility(View.GONE);
			vEdgePanel.setVisibility(View.VISIBLE);
			vCombinePanel.setVisibility(View.GONE);
			break;
		case 2:
			vEdit.setBackgroundResource(R.drawable.edit_nav_background);
			vEdge.setBackgroundResource(R.drawable.edit_nav_background);
			vCombine.setBackgroundResource(R.drawable.edit_nav_selected);
			vEditPanel.setVisibility(View.GONE);
			vEdgePanel.setVisibility(View.GONE);
			vCombinePanel.setVisibility(View.VISIBLE);
			break;
		}

	}

	/**
	 * 按钮点击侦听器
	 */
	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btn_share:
				// 保存
				if (mNewBitmap == null)
					mNewBitmap = mOldBitmap;
				if (FotoCommond.saveBitmapToFile(mNewBitmap, mFileName)) {
					FotoCommond.createThumb(mFileName, mThumbName);
					setResult(RESULT_OK, getIntent());
					finish();
				}
				break;
			case R.id.btn_edge_1:
				switchEdge(0);
				break;
			case R.id.btn_edge_2:
				switchEdge(1);
				break;
			case R.id.btn_edge_3:
				switchEdge(2);
				break;
			case R.id.btn_edge_4:
				switchEdge(3);
				break;
			case R.id.btn_edge_5:
				switchEdge(4);
				break;
			case R.id.btn_edge_6:
				switchEdge(5);
				break;
			case R.id.btn_edit:
				switchNavPanel(0);
				break;
			case R.id.btn_edge:
				switchNavPanel(1);
				break;
			case R.id.btn_combine:
				switchNavPanel(2);
				break;
			case R.id.btn_left:
				// animRotateFoto(path, thumb, -90);
				rotateBitmap(mFileName, mThumbName, -90);
				break;
			case R.id.btn_right:
				// animRotateFoto(path, thumb, 90);
				rotateBitmap(mFileName, mThumbName, 90);
				break;
			case R.id.btn_cut:
				// 首先保存
				if (mNewBitmap == null)
					mNewBitmap = mOldBitmap;
				if (FotoCommond.saveBitmapToFile(mNewBitmap, mFileName)) {
					FotoCommond.createThumb(mFileName, mThumbName);
				}
				Intent intent = new Intent("com.android.camera.action.CROP");
				File tempFile = new File(mFileName);
				intent.setDataAndType(Uri.fromFile(tempFile), "image/*");// 设置要裁剪的图片
				intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
				intent.putExtra("output", Uri.fromFile(tempFile));// 保存到原文件
				intent.putExtra("outputFormat", "JPEG");// 返回格式
				startActivityForResult(Intent.createChooser(intent, "裁剪图片"),
						Constant.REQUEST_CODE_CROP);
				overridePendingTransition(R.anim.gotonextin, R.anim.gotonextout);
				break;
			case R.id.btn_del:
				//
				AlertDialog.Builder builder = new Builder(EditActivity.this);
				builder.setMessage("确认删除吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								File f1 = new File(mFileName);
								f1.delete();
								File f2 = new File(mThumbName);
								f2.delete();
								//
								dialog.dismiss();
								//
								setResult(RESULT_OK, getIntent());
								finish();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
				break;
			}
		}
	};
	/**
	 * 边框用的资源图片ID
	 */
	int[][] edges = {
			{ R.drawable.edge_1_leftup, R.drawable.edge_1_rightup,
					R.drawable.edge_1_leftdown, R.drawable.edge_1_rightdown,
					R.drawable.edge_1_left, R.drawable.edge_1_right,
					R.drawable.edge_1_up, R.drawable.edge_1_down },
			{ R.drawable.edge_2_leftup, R.drawable.edge_2_rightup,
					R.drawable.edge_2_leftdown, R.drawable.edge_2_rightdown,
					R.drawable.edge_2_left, R.drawable.edge_2_right,
					R.drawable.edge_2_up, R.drawable.edge_2_down },
			{ R.drawable.edge_3_leftup, R.drawable.edge_3_rightup,
					R.drawable.edge_3_leftdown, R.drawable.edge_3_rightdown,
					R.drawable.edge_3_left, R.drawable.edge_3_right,
					R.drawable.edge_3_up, R.drawable.edge_3_down },
			{ R.drawable.edge_4_leftup, R.drawable.edge_4_rightup,
					R.drawable.edge_4_leftdown, R.drawable.edge_4_rightdown,
					R.drawable.edge_4_left, R.drawable.edge_4_right,
					R.drawable.edge_4_up, R.drawable.edge_4_down },
			{ R.drawable.edge_5_leftup, R.drawable.edge_5_rightup,
					R.drawable.edge_5_leftdown, R.drawable.edge_5_rightdown,
					R.drawable.edge_5_left, R.drawable.edge_5_right,
					R.drawable.edge_5_up, R.drawable.edge_5_down } };

	/**
	 * 切换边框处理函数
	 * 
	 * @param index
	 */
	private void switchEdge(int index) {
		// 左上角
		Bitmap leftup = BitmapFactory.decodeResource(getResources(),
				edges[index][0]);
		Bitmap rightup = BitmapFactory.decodeResource(getResources(),
				edges[index][1]);
		Bitmap leftdown = BitmapFactory.decodeResource(getResources(),
				edges[index][2]);
		Bitmap rightdown = BitmapFactory.decodeResource(getResources(),
				edges[index][3]);
		Bitmap left = BitmapFactory.decodeResource(getResources(),
				edges[index][4]);
		Bitmap right = BitmapFactory.decodeResource(getResources(),
				edges[index][5]);
		Bitmap up = BitmapFactory.decodeResource(getResources(),
				edges[index][6]);
		Bitmap down = BitmapFactory.decodeResource(getResources(),
				edges[index][7]);
		int hc = mOldBitmap.getHeight() / left.getHeight();
		int wc = mOldBitmap.getWidth() / up.getWidth();
		int hh = hc * left.getHeight();
		int ww = wc * up.getWidth();
		// 创建新图片

		Bitmap bitmap = Bitmap.createBitmap(
				ww + left.getWidth() + right.getWidth(), hh + up.getHeight()
						+ down.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas cv = new Canvas(bitmap);
		// 目标
		Rect dst = new Rect(left.getWidth(), up.getHeight(), left.getWidth()
				+ ww, up.getHeight() + hh);
		cv.drawBitmap(mOldBitmap, null, dst, null);
		// 左边
		for (int i = 0; i < hc; i++)
			cv.drawBitmap(left, 0, up.getHeight() + i * left.getHeight(), null);
		// 右边
		for (int i = 0; i < hc; i++) {
			// -2左右有两个像素的重叠
			cv.drawBitmap(right, left.getWidth() + ww, up.getHeight() + i
					* right.getHeight(), null);
		}
		// 上边

		for (int i = 0; i < wc; i++) {
			// -2左右有两个像素的重叠
			cv.drawBitmap(up, left.getWidth() + i * up.getWidth(), 0, null);
		}
		// 下边
		for (int i = 0; i < wc; i++) {
			// -2左右有两个像素的重叠
			cv.drawBitmap(down, left.getWidth() + i * down.getWidth(),
					up.getHeight() + hh, null);
		}
		// 左上角
		cv.drawBitmap(leftup, 0, 0, null);
		// 右上角
		cv.drawBitmap(rightup, bitmap.getWidth() - rightup.getWidth(), 0, null);
		// 左下角
		cv.drawBitmap(leftdown, 0, bitmap.getHeight() - leftdown.getHeight(),
				null);
		// 右下角
		cv.drawBitmap(rightdown, bitmap.getWidth() - rightdown.getWidth(),
				bitmap.getHeight() - rightdown.getHeight(), null);
		//
		if (mNewBitmap != null && !mNewBitmap.isRecycled()) {
			mNewBitmap.recycle();
			mNewBitmap = null;
		}
		mNewBitmap = bitmap;
		zoomView.setImage(mNewBitmap);
	}

	/**
	 * 旋转图片
	 * 
	 * @param path
	 * @param thumb
	 * @param direction
	 */
	private void animRotateFoto(final String path, final String thumb,
			final int direction) {
		// 动画设定(指定旋转动画) (startAngle, endAngle, rotateX, rotateY)
		RotateAnimation am = new RotateAnimation(0, direction,
				Animation.RELATIVE_TO_PARENT, 0.5f,
				Animation.RELATIVE_TO_PARENT, 0.5f);
		// 动画开始到结束的执行时间(1000 = 1 秒)
		am.setDuration(500);
		// 动画重复次数(-1 表示一直重复)
		// am. setRepeatCount ( 1 );
		// 图片配置动画
		zoomView.setAnimation(am);
		am.setFillAfter(true);
		am.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				rotateBitmap(path, thumb, direction);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 动画开始
		am.startNow();
	}

	/**
	 * 图片旋转
	 * 
	 * @param path
	 * @param thumb
	 * @param direction
	 */
	private void rotateBitmap(String path, String thumb, int direction) {
		Bitmap bmp1 = FotoCommond.gerZoomRotateBitmap(mOldBitmap, direction);
		if (mOldBitmap != null && !mOldBitmap.isRecycled()) {
			mOldBitmap.recycle();
			mOldBitmap = null;
		}
		mOldBitmap = bmp1;
		zoomView.setImage(mOldBitmap);
	}

	/**
	 * 设置手势处理
	 * 
	 * @param resids
	 */
	public void setGestureDetector(final int[] resids) {
		//
		gestureScanner = new GestureDetector(new OnGestureListener() {
			@Override
			public boolean onDown(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float arg3) {
				if (e1 == null || e2 == null)
					return false;
				// TODO Auto-generated method stub
				int SWIPE_X_MIN_DISTANCE = 200;
				int SWIPE_MAX_OFF_PATH = 250;
				int SWIPE_THRESHOLD_VELOCITY = 200;
				int swipeXDistance = (int) (e1.getX() - e2.getX());
				int swipeYDistance = (int) (e1.getY() - e2.getY());
				if (swipeXDistance > SWIPE_X_MIN_DISTANCE
				/* && Math.abs(swipeXDistance) > Math.abs(swipeYDistance) */
				&& Math.abs(swipeYDistance) < 100
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// viewFlipper.setInAnimation(slideLeftIn);
					// viewFlipper.setOutAnimation(slideLeftOut);
					// viewFlipper.showNext();
					// rightInLeftOut();
					return true;
				} else if (-swipeXDistance > SWIPE_X_MIN_DISTANCE
				/* && Math.abs(swipeXDistance) > Math.abs(swipeYDistance) */
				&& Math.abs(swipeYDistance) < 100
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// viewFlipper.setInAnimation(slideRightIn);
					// viewFlipper.setOutAnimation(slideRightOut);
					// viewFlipper.showPrevious();
					// rightOutLeftIn();
					return true;
				}
				if (swipeXDistance > SWIPE_X_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					// viewFlipper.setInAnimation(slideLeftIn);
					// viewFlipper.setOutAnimation(slideLeftOut);
					// viewFlipper.showNext();
				} else if (-swipeXDistance > SWIPE_X_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					// viewFlipper.setInAnimation(slideRightIn);
					// viewFlipper.setOutAnimation(slideRightOut);
					// viewFlipper.showPrevious();
				}
				return false;
			}

			@Override
			public void onLongPress(MotionEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
					float arg2, float arg3) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onSingleTapUp(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		gestureScanner
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						//
						for (int i = 0; i < resids.length; i++) {
							View v = findViewById(resids[i]);
							FotoCommond.hideViewAnim(EditActivity.this, v);
						}
						return true;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						return true;
					}
				});
	}

	/**
	 * Activity销毁处理是调用函数
	 */
	@Override
	public void onDestroy() {
//		if (mOldBitmap != null && !mOldBitmap.isRecycled()) {
//			mOldBitmap.recycle();
//			mOldBitmap = null;
//		}
		if (mNewBitmap != null && !mNewBitmap.isRecycled()) {
			mNewBitmap.recycle();
			mNewBitmap = null;
		}
		super.onDestroy();
	}

	/**
	 * 拍照后返回处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == Constant.REQUEST_CODE_CROP) {
				reLoad();
			}
		}
	}
}