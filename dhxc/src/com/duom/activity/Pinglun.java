package com.duom.activity;
import java.net.URLEncoder;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import com.jingxunnet.cmusic.R;

public class Pinglun extends HttpBaseActivity{
	
	private Button sure , cancle ; 
	private RatingBar rb ;
	private EditText et ;
	private ImageButton back ;
	int position;
	private ProgressDialog pd ;
	private ProgressBar  pb ;
	
	 String  pinglun ; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pinglun);
	
		Intent it = getIntent();
		position = it.getIntExtra("position" , 9);
		
		sure = (Button)findViewById(R.id.surepinglun);
		cancle = (Button)findViewById(R.id.quxiaopinglun);
		back = (ImageButton)findViewById(R.id.backpinglun);
		rb = (RatingBar)findViewById(R.id.barpinglun);
		et = (EditText)findViewById(R.id.etpinglun);
		pb = (ProgressBar)findViewById(R.id.progressall);
				
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
			    it.setClass(Pinglun.this, Comment.class);
			    Task1Activity.fromshouye = false ;
			    startActivity(it);
			    finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		   if(Task1Activity.layuser.getVisibility() == View.GONE){
			   Toast.makeText(Pinglun.this, "亲，登录后，才可以评论哦！", 2000).show();
			   Intent it = new Intent();
			   it.setClass(Pinglun.this, Login.class);
			   startActivity(it);
			   return;
		   }
           v.setClickable(false);
		   pb.setVisibility(View.VISIBLE);
		    String url = "http://user.hutudan.com/car_map/comment.php?flg=json";
			StringBuffer data = new StringBuffer();
			//
			if(Login.uid != 0){
				data.append("uid=");
				data.append(Login.uid);	
			}else {
				data.append("uid=");
				data.append(Task1Activity.uid);	
			}
			data.append("&id=");
			data.append(position);
			
			data.append("&content=");
			pinglun = et.getText().toString(); 
			data.append(URLEncoder.encode(pinglun));
			//
			data.append("&score=");
			float num = rb.getRating();
			data.append(num);
			// 请求网络验证登陆
			HttpRequestTask request = new HttpRequestTask();
			request.execute(url, data.toString());
			Comment.isRefresh = true;
			}
		});
	}
	@Override
	void resultData(String result) {
		// TODO Auto-generated method stub
		String tip = null;
		try {
			 pb.setVisibility(View.GONE);
			if(result == null){
				Toast.makeText(Pinglun.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONObject json = new JSONObject(result);
			int ret = json.optInt("ret", -1);
			if (ret == 1) {
				//tip = URLDecoder.decode(json.optString("tip"));
			    tip = "评论成功";		
			    finish();
			    Intent it = new Intent();
			    it.setClass(Pinglun.this, Comment.class);
			    Task1Activity.fromshouye = false ;
			    startActivity(it);
				}
		} catch (Exception ex) {
			tip = ex.getMessage();
			ex.printStackTrace();
		}
		if (!TextUtils.isEmpty(tip))
			Toast.makeText(Pinglun.this, tip, Toast.LENGTH_SHORT)
					.show();
	}
	
	
}
