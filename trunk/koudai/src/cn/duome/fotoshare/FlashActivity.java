package cn.duome.fotoshare;

import com.chuangyu.music.R;
import com.example.uitl.GetPublicKey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.duome.fotoshare.utils.FotoCommond;

/**
 * 软件启动时处理页面
 * 
 * @author dky
 * 
 */
public class FlashActivity extends Activity {
	// 当前时间
	long mCurrentuTime = 0;
	boolean isLoading = true;

	/**
	 * Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置
		FotoCommond.setFullScreen(this, true);

		Log.e("key", GetPublicKey.getSignInfo(getApplicationContext()));
		//
		setContentView(R.layout.flash);
		String flashName = FotoCommond.getAdapterName(this, "flash");
		FotoCommond.setImgeViewSrc(this, R.id.iv_bg, flashName);
		//
		gotoMain();
		// 加载图片列表
		new Thread() {
			@Override
			public void run() {
				FotoCommond.loadAblum();
				isLoading = false;
			}
		}.start();
	}

	/**
	 * 转到程序主界面
	 */
	private void gotoMain() {
		new Thread() {
			@Override
			public void run() {
				mCurrentuTime = System.currentTimeMillis();
				while (isLoading
						|| (System.currentTimeMillis() - mCurrentuTime) < 3 * 1000) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mFilishHandler.sendEmptyMessage(2);
			}
		}.start();
	}

	/**
	 * 超时处理器
	 */
	Handler mFilishHandler = new Handler() {
		public void handleMessage(Message msg) {
			//
			Intent intent = new Intent(FlashActivity.this,
					BaseActivityGroup.class);
			overridePendingTransition(R.anim.gotonextin, R.anim.gotonextout);
			startActivity(intent);
			finish();
		}
	};
}