package com.duom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.jingxunnet.cmusic.R;
/*
 * 
 * 提示页面，创建完成线路，是否创建相册。
 * */
public class Prompt extends Activity{
	
	private Button sure , cancle ; 
	private String ss;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.prompt);
		//初始化按钮
		sure = (Button)findViewById(R.id.surebutton);
		cancle = (Button)findViewById(R.id.canclebutton);
		
		Intent it = getIntent();
		//从showroute跳转过来， 获取搜索的路线
		ss = it.getStringExtra("routefromshowroute");		
		sure.setOnClickListener(new OnClickListener() {
			
			@Override
			//点击确定按钮，跳转到Add页面，并把路线传过去
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("routefromprompt", ss);
				it.setClass(Prompt.this, Add.class);
				startActivity(it);
				finish();
			}
		});
		//点击取消按钮，跳转到Myroute页面，并把路线传过去
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(Prompt.this, Myroute.class);
				startActivity(it);
				finish();
			}
		});
	}
}
