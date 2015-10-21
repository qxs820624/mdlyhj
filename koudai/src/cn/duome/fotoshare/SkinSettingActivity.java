package cn.duome.fotoshare;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseCommond;
/**
 * 皮肤设置页面
 * @author dky
 *
 */
public class SkinSettingActivity extends FotoBaseActivity {
	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化
		init(getText(R.string.skin_setting_title).toString(),R.layout.skinsetting,true);
		// 皮肤切换
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.skin_btn_1:
					BaseCommond.userInfo.setSkin(0);
					break;
				case R.id.skin_btn_2:
					BaseCommond.userInfo.setSkin(1);
					break;
				case R.id.skin_btn_3:
					BaseCommond.userInfo.setSkin(2);
					break;
				case R.id.skin_btn_4:
					BaseCommond.userInfo.setSkin(3);
					break;
				}
				BaseCommond.userInfo.saveData();
				switchSkin();
			}
		};
		View v1 = this.findViewById(R.id.skin_btn_1);
		v1.setOnClickListener(listener);
		View v2 = this.findViewById(R.id.skin_btn_2);
		v2.setOnClickListener(listener);
		View v3 = this.findViewById(R.id.skin_btn_3);
		v3.setOnClickListener(listener);
		View v4 = this.findViewById(R.id.skin_btn_4);
		v4.setOnClickListener(listener);
	}
}