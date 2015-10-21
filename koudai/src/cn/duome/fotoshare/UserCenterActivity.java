package cn.duome.fotoshare;

import java.net.URLEncoder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.duome.fotoshare.utils.Constant;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseCommond;
import com.hutuchong.util.BaseContant;

/**
 * 用户中心页面
 * 
 * @author dky
 * 
 */
public class UserCenterActivity extends WaterFallBaseActivity {
	// 当前选中标签索引
	int tabIndex = -1;
	// 用户图片列表请求url
	String listUrl = "http://xlb.jianrencun.com/fotoshare/user_fotolist.php";
	Button mBtnMyFoto;
	Button mBtnMyFav;

	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化
		init(getText(R.string.user_center).toString(), R.layout.usercenter,
				true);
		// 设置注销按钮
		if (!TextUtils.isEmpty(BaseCommond.userInfo.getEmail())) {
			ImageButton ib = (ImageButton) findViewById(R.id.btn_share);
			ib.setVisibility(View.VISIBLE);
			ib.setBackgroundResource(R.drawable.btn_logout_background);
			ib.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					BaseCommond.userInfo.setUserName("");
					BaseCommond.userInfo.setEmail("");
					BaseCommond.userInfo.setUserHead("");
					BaseCommond.userInfo.saveData();
					finish();
				}
			});
		}
		// 图片夹列表
		//
		mBtnMyFoto = (Button) this.findViewById(R.id.btn_myfoto);
		mBtnMyFoto.setOnClickListener(listener);
		mBtnMyFav = (Button) this.findViewById(R.id.btn_myfav);
		mBtnMyFav.setOnClickListener(listener);
		//
		item_width = 130;
		page_count = 30;
		Display display = this.getWindowManager().getDefaultDisplay();
		int width = display.getWidth() - 120;
		column_count = width / item_width;// 根据屏幕大小计算列数
		column_margin = (width - (column_count * item_width))
				/ (column_count + 1);
		column_height = new int[column_count];
		//
		InitLayout();
		//
		switchTab(0);
		//
		setUserInfo();
	}

	/**
	 * 点击侦听器
	 */
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btn_myfoto:
				if (tabIndex == 0)
					return;
				reset();
				switchTab(0);
				break;
			case R.id.btn_myfav:
				if (tabIndex == 1)
					return;
				reset();
				switchTab(1);
				break;
			}
		}
	};

	/**
	 * tab切换处理函数
	 * 
	 * @param index
	 */
	public void switchTab(int index) {
		tabIndex = index;
		int flg = 0;
		switch (index) {
		case 0:
			flg = 0;
			mBtnMyFoto.setBackgroundResource(R.drawable.tab_left_pressed);
			mBtnMyFav.setBackgroundResource(R.drawable.tab_right_background);
			break;
		case 1:
			flg = 2;
			mBtnMyFoto.setBackgroundResource(R.drawable.tab_left_background);
			mBtnMyFav.setBackgroundResource(R.drawable.tab_right_pressed);
			break;
		}
		// 获取email判断是否登录
		String email = BaseCommond.userInfo.getEmail();
		if (TextUtils.isEmpty(email)) {
			Intent i = new Intent(mContext, UserLoginActivity.class);
			startActivityForResult(i, Constant.REQUEST_CODE_LOGIN);
		} else {
			String url = listUrl + "?flg=" + flg + "&email="
					+ URLEncoder.encode(email);
			this.requestItem(url, BaseContant.PAGE_CHANNEL, true, false, null,
					true);
		}
	}

	/**
	 * 设置用户信息
	 */
	private void setUserInfo() {
		// 昵称
		TextView tvNick = (TextView) this.findViewById(R.id.tv_nick);
		tvNick.setText(BaseCommond.userInfo.getUserName());
		// email
		TextView tvEmail = (TextView) this.findViewById(R.id.tv_email);
		tvEmail.setText(BaseCommond.userInfo.getEmail());
	}

	/**
	 * 登陆后返回处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.REQUEST_CODE_LOGIN) {
			// 登陆成功
			if (resultCode == Constant.RESPONSE_CODE_LOGIN) {
				//
				String email = BaseCommond.userInfo.getEmail();
				if (!TextUtils.isEmpty(email)) {
					String url = listUrl + "?flg=" + tabIndex + "&email="
							+ URLEncoder.encode(email);
					this.requestItem(url, BaseContant.PAGE_CHANNEL, true,
							false, null, true);
					setUserInfo();
				}
				return;
			} else
				finish();// 取消登陆或按返回
		}
	}
}