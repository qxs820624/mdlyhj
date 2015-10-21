package com.duom.activity;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.jingxunnet.cmusic.R;

public class Register extends HttpBaseActivity{	
	private EditText reuserName ,  reuserPassword ,repeatPassword , mail; 
	//确定
	private Button resure;
	String email;
	String name;
	Context context ;
	private ProgressDialog pd ;
	private ImageButton back ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置 没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registerclass);		
		context = this;
        //初始化控件
		reuserName = (EditText)findViewById(R.id.reusername);
		reuserPassword = (EditText)findViewById(R.id.reuserpassword);
		repeatPassword = (EditText)findViewById(R.id.repeatuserpassword);
		mail = (EditText)findViewById(R.id.mail);
		resure = (Button)findViewById(R.id.resure);
		back = (ImageButton)findViewById(R.id.backregist);
		resure.setOnClickListener(new OnClickListener() {
			       
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pd = new ProgressDialog(context);
				pd.setMessage("正在注册。。。");
				// /判断是否为空,如果为空提示
				if (reuserName.getText().toString() == null
						|| reuserName.getText().toString().trim().length() == 0) {
					Toast.makeText(Register.this, "请填写用户名！", Toast.LENGTH_SHORT).show();
					//请求焦点
					reuserName.requestFocus();
					return;
				}
				// /判断是否为空  如果为空提示
				if (reuserPassword.getText().toString() == null
						|| reuserPassword.getText().toString().trim().length() == 0) {
					Toast.makeText(Register.this, "请填写密码！", Toast.LENGTH_SHORT).show();
					reuserPassword.requestFocus();
					return;
				}
				if (repeatPassword.getText().toString() == null
						|| repeatPassword.getText().toString().trim().length() == 0) {
					Toast.makeText(Register.this, "请再次输入密码！", Toast.LENGTH_SHORT).show();
					repeatPassword.requestFocus();
					return;
				}
				// /判断是否为空  如果为空提示
				if (mail.getText().toString() == null
						|| mail.getText().toString().trim().length() == 0) {
					Toast.makeText(Register.this, "请输入邮箱！", Toast.LENGTH_SHORT).show();
					mail.requestFocus();
					return;
				}
				//判断俩次输入的密码是否相同
				if (!reuserPassword.getText().toString()
						.equals(repeatPassword.getText().toString())) {
					Toast.makeText(Register.this, "两次输入密码不一致!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				pd = new ProgressDialog(context);
				pd.setMessage("正在注册。。。");
				pd.show();
                 Task1Activity.registinfo = true ;
				//构造请求url
				String url = mHost + "/car_map/reg.php?flg=json"; 
				//构造可追加的字符串
				StringBuffer data = new StringBuffer();
				//
				data.append("name=");
				name = reuserName.getText().toString();
				data.append(URLEncoder.encode(name));
				//
				data.append("&email=");
				email = mail.getText().toString();
				data.append(URLEncoder.encode(email));
				//
				data.append("&pwd=");
				data.append(URLEncoder.encode(reuserPassword.getText().toString()));
				// 注册请求
				HttpRequestTask request = new HttpRequestTask();
				request.execute(url, data.toString());
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	void resultData(String result) {
		// TODO Auto-generated method stub
		String tip = null;
		pd.cancel();
		try {
			if(result == null){
				Toast.makeText(Register.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONObject json = new JSONObject(result);
			int ret = json.optInt("ret", -1);
			if (ret == 1) {
				pd.cancel();						
				 Login.nick = "";			
                 Login.sex = "3";	
                 Login.header1 = null;
				 Login.uid = json.optInt("uid");
							    
//				// 进入到下一个页面
//				Intent it = new Intent();
//				it.setClass(RegisterClass.this, Info.class);
//				startActivity(it);
//				Info.bitinfo = null;
				 Toast.makeText(Register.this, "注册成功，请重新登录！", Toast.LENGTH_SHORT)
					.show();
				finish();
			} else {
				tip = "该帐号或邮箱已经注册，请登录!";
			}
		} catch (Exception ex) {
			tip = ex.getMessage();
			ex.printStackTrace();
		}
		if (!TextUtils.isEmpty(tip))
			Toast.makeText(Register.this, tip, Toast.LENGTH_SHORT)
					.show();
	}

}
