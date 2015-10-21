package com.duom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.jingxunnet.cmusic.R;

public class Showall extends Activity{
	private TextView et ;
	private Button sureall;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showall);
		
		et = (TextView)findViewById(R.id.alledit);
		sureall = (Button) findViewById(R.id.sureall);
		Intent it = getIntent();
		String pinglun = it.getStringExtra("pinglun");
		
		et.setText(pinglun);
		
		sureall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				finish();
			}
		});
	}
}
