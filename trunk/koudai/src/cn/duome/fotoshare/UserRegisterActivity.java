package cn.duome.fotoshare;

import java.net.URLEncoder;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.duome.fotoshare.utils.Constant;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseCommond;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;
/**
 * 用户登陸页面
 * @author dky
 *
 */
public class UserRegisterActivity extends FotoBaseActivity {
	//用户Emial
	String mEmail;
	String mNick;
	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化
		init(getText(R.string.register).toString(), R.layout.userregister, true);
		// 点击侦听器
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.btn_register:
					// 用户名
					EditText etNick = (EditText) findViewById(R.id.et_nick);
					mNick = etNick.getText().toString();
					if (TextUtils.isEmpty(mNick)) {
						Toast.makeText(mContext, "请正确输入用户名!",
								Toast.LENGTH_SHORT).show();
						return;
					}
					// 邮箱
					EditText etEmail = (EditText) findViewById(R.id.et_email);
					mEmail = etEmail.getText().toString();
					if (TextUtils.isEmpty(mEmail)) {
						Toast.makeText(mContext, "请正确输入登陆邮箱!",
								Toast.LENGTH_SHORT).show();
						return;
					}
					// 密码
					EditText etPwd = (EditText) findViewById(R.id.et_pwd);
					String pwd = etPwd.getText().toString();
					if (TextUtils.isEmpty(pwd)) {
						Toast.makeText(mContext, "密码不能为空!", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					//
					String url = "http://xlb.jianrencun.com/fotoshare/user_reg.php";
					String data = "email=" + URLEncoder.encode(mEmail)
							+ "&nick=" + URLEncoder.encode(mNick) + "&pwd="
							+ URLEncoder.encode(pwd);
					requestItem(url, BaseContant.PAGE_POST, true, true, data,
							null);
					break;
				}
			}
		};
		// 注册按钮
		View reg = findViewById(R.id.btn_register);
		reg.setOnClickListener(clickListener);
	}

	/**
	 * 服务器返回数据处理
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear,
			boolean isRefresh, boolean isRemote) throws Exception {
		//
		switch (pageId) {
		case BaseContant.PAGE_POST:
			RSSChannel channel = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel != null && !TextUtils.isEmpty(channel.getTitle())) {
				String[] tmps = channel.getTitle().split("\\|");
				if ("0".equals(tmps[0])) {
					this.setResult(Constant.RESPONSE_CODE_REG);
					BaseCommond.userInfo.setEmail(mEmail);
					BaseCommond.userInfo.setUserName(mNick);
					BaseCommond.userInfo.saveData();
					finish();
				}
				Toast.makeText(mContext, tmps[1], Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mContext, "注册失败", Toast.LENGTH_LONG).show();
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear, isRefresh, isRemote);
	}
}