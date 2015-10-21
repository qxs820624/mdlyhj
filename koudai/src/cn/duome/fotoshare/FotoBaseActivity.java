package cn.duome.fotoshare;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import cn.duome.fotoshare.utils.FotoCommond;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseActivity;
import com.hutuchong.util.BaseCommond;
/**
 * 图片处理Activity基类
 * @author dky
 *
 */
public class FotoBaseActivity extends BaseActivity implements
		SensorEventListener {
	private boolean bSensor = true;
	private SensorManager sensorMgr;
	private Sensor sensor;
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;

	/**
	 * Activity启动时候初始化
	 */
	public void init(String title, int layoutId, boolean isShowBack) {
		// 初始化
		super.init(true, R.id.btn_refresh, R.id.progressImage,
				R.id.progressText);
		// 设置页面布局
		setContentView(layoutId);
		// 摇动感应初始化
		if (bSensor) {
			sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorMgr.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
		// 返回按钮处理

		View vBack = this.findViewById(R.id.btn_back);
		if (vBack != null) {
			if (isShowBack) {
				vBack.setVisibility(View.VISIBLE);
				vBack.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else
				vBack.setVisibility(View.GONE);
		}

		// 设置标题
		TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
		if (tvTitle != null) {
			tvTitle.setText(title);
		}
		// 设置背景
		FotoCommond.setImgeViewSrc(this, R.id.iv_bg, FotoCommond.mBg);
		if (FotoCommond.mBg == null)
			FotoCommond.setBgBitmap(this, BaseCommond.userInfo.getSkin());
	}
	/**
	 * 重载Activity暂停时调用函数
	 */
	protected void onPause() {
		if (sensorMgr != null) {
			sensorMgr.unregisterListener(this, this.sensor);
			sensorMgr = null;
		}
		super.onPause();
	}
	/**
	 * 重载Activity恢复时调用函数
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FotoCommond.setImgeViewSrc(this, R.id.iv_bg, FotoCommond.mBg);
	}
	/**
	 * 重载Activity配置变化时调用函数
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// land
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// port
		}
	}

	/**
	 * 
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 重载Activity感应变化时调用函数
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// System.out.println("onSensorChanged begin...");
		int sensor = event.sensor.getType();
		if (sensor == Sensor.TYPE_ACCELEROMETER) {
			// System.out.println("onSensorChanged 1...");
			long curTime = System.currentTimeMillis();
			// 每100毫秒检测一次
			if ((curTime - lastUpdate) > 3000) {
				System.out.println("onSensorChanged 2...");
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
				float[] values = event.values;
				x = values[SensorManager.DATA_X];
				y = values[SensorManager.DATA_Y];
				z = values[SensorManager.DATA_Z];

				float speed = Math.abs(x + y + z - last_x - last_y - last_z)
						/ diffTime * 10000;
				System.out.println("speed:" + speed);
				if (speed >= 3) {
					//
					if (BaseCommond.userInfo.getSkin() >= 3)
						BaseCommond.userInfo.setSkin(0);
					else
						BaseCommond.userInfo.setSkin(BaseCommond.userInfo
								.getSkin() + 1);
					switchSkin();
					BaseCommond.userInfo.saveData();
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
		// System.out.println("onSensorChanged end...");
	}

	/**
	 * 皮肤切换函数
	 */
	void switchSkin() {
		View v = FotoBaseActivity.this.findViewById(R.id.iv_bg);
		if (v == null)
			return;
		FotoCommond.setBgBitmap(this, BaseCommond.userInfo.getSkin());
		FotoCommond.setImgeViewSrc(this, R.id.iv_bg_0, FotoCommond.mBg);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.togone_1s);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				FotoCommond.setImgeViewSrc(FotoBaseActivity.this, R.id.iv_bg,
						FotoCommond.mBg);
				View v = FotoBaseActivity.this.findViewById(R.id.iv_bg);
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

		});

		v.startAnimation(anim);
	}

	/**
	 * 显示提示信息
	 * @param resId 提示信息字符串资源ID
	 */
	@Override
	public void showToast(int resId) {
		Toast.makeText(mContext, resId, Toast.LENGTH_LONG);
	}
}