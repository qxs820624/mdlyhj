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
 * ��ʾҳ�棬���������·���Ƿ񴴽���ᡣ
 * */
public class Prompt extends Activity{
	
	private Button sure , cancle ; 
	private String ss;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.prompt);
		//��ʼ����ť
		sure = (Button)findViewById(R.id.surebutton);
		cancle = (Button)findViewById(R.id.canclebutton);
		
		Intent it = getIntent();
		//��showroute��ת������ ��ȡ������·��
		ss = it.getStringExtra("routefromshowroute");		
		sure.setOnClickListener(new OnClickListener() {
			
			@Override
			//���ȷ����ť����ת��Addҳ�棬����·�ߴ���ȥ
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("routefromprompt", ss);
				it.setClass(Prompt.this, Add.class);
				startActivity(it);
				finish();
			}
		});
		//���ȡ����ť����ת��Myrouteҳ�棬����·�ߴ���ȥ
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
