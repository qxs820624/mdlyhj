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
/*
 * �������û��ô֪ʶ�㣬���Ǽ򵥵��ж��Ƿ�Ϊ�գ��������һ��֪ʶ
 * 
 * */
public class Login extends HttpBaseActivity{
	String email;
	Context context ;
	 private SharedPreferences prefs;
	private EditText userName ,  userPassword ; 
	private Button loginbnt , register ;
	private ImageButton back ;
	static int uid , mark;
	private ProgressDialog pd ;
	static String nick , header1 , sex , humor;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		context = this;
		userName = (EditText)findViewById(R.id.username);
		userPassword = (EditText)findViewById(R.id.userpassword);
		loginbnt = (Button)findViewById(R.id.loginbnt);
		register = (Button)findViewById(R.id.registerbnt);
		back = (ImageButton)findViewById(R.id.backlogin);
		
		loginbnt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
//				pd = new ProgressDialog(Login.this).setMessage("���ڵ�¼����");				
				Login.header1 = null ;
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(userName.getText().toString())) {
					Toast.makeText(Login.this, "���䲻��Ϊ��!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(userPassword.getText().toString())) {
					Toast.makeText(Login.this, "���벻��Ϊ��!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				pd = new ProgressDialog(context);
				pd.setMessage("���ڵ�¼������");
				pd.show();
				String url = mHost + "/car_map/login.php?flg=json";
				StringBuffer data = new StringBuffer();
				//
				data.append("name=");
				email = userName.getText().toString(); 
				data.append(URLEncoder.encode(email));
				//
				data.append("&pwd=");
				data.append(URLEncoder.encode(userPassword.getText().toString()));
				// ����������֤��½
				HttpRequestTask request = new HttpRequestTask();
				request.execute(url, data.toString());
				Log.e("00000000000", data.toString());
			}
		});
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(Login.this, Register.class);
				startActivity(it);		
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
		try {
			pd.cancel();
			if(result == null){
				Toast.makeText(Login.this, "��ȡ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONObject json = new JSONObject(result);
			int ret = json.optInt("ret", -1);
			if (ret == 1) {				
				tip = URLDecoder.decode(json.optString("tip"));
				nick = URLDecoder.decode(json.optString("nick"));
				sex = URLDecoder.decode(json.optString("sex"));
				header1 = URLDecoder.decode(json.optString("header"));
				humor = URLDecoder.decode(json.optString("sign"));
				if(header1.equalsIgnoreCase("")){
					header1 = null;
				}
		        uid = json.optInt("uid");
		        Task1Activity.headerfresh = true ;
				// ���뵽��һ��ҳ��
				this.finish();
//				saveDate() ;
				Task1Activity.layuser.setVisibility(View.VISIBLE);
			} else {
				tip = "�ʺź����벻ƥ��,������!";
			}
		} catch (Exception ex) {
			tip = ex.getMessage();
			ex.printStackTrace();
		}
		if (!TextUtils.isEmpty(tip))
			Toast.makeText(Login.this, tip, Toast.LENGTH_SHORT)
					.show();
	}
}
