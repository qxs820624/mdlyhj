package com.duom.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jingxunnet.cmusic.R;
/*
 * 这个类，显示收藏路线。
 * 就用到 一个适配器，baseadapter,为listview加载数据
 * */
public class Mysave extends Activity{
	private ListView lv ;
	private ImageButton ib ;
	List<String> ss , mysave;
	LinearLayout llsave ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mysave);
		//初始化
		lv = (ListView)findViewById(R.id.savelv);
		ib = (ImageButton)findViewById(R.id.backmysave);
		llsave = (LinearLayout)findViewById(R.id.llsave);
		
		ib.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mysave = new ArrayList<String>();    
		 ss =new ArrayList<String>();     
		 if(MostNew.saveRoute != null ){		
		  ss.addAll(MostNew.saveRoute);
		 }
		 if( MostNew.routes!= null){
		 ss.addAll(MostNew.routes);	
		 }
		 for(int i = 0 ; i<ss.size() ; i++){
			 if(!mysave.contains(ss.get(i))){
				  mysave.add(ss.get(i));
			 }
		 }
		 if(mysave.size() == 0){
	      Toast.makeText(Mysave.this, "没有收藏线路.", 2000).show();
	      llsave.setVisibility(View.VISIBLE);
			 return;			
		 }else {
         lv.setAdapter(new adapter(this));      
		}	
	}
	private class adapter extends BaseAdapter
	{
		private Context context;
		public adapter(Context context) {
			this.context = context;		
		}
		@Override
		public int getCount()

		{
			return mysave.size();// 项目总数
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//自定义的view 视图，适配器资源
		   TextView tx = new TextView(getApplicationContext());
		   tx.setTextColor(Color.parseColor("#854a2a"));
		   tx.setHeight(100);
		   tx.setTextSize(22);
		   tx.setGravity(Gravity.CENTER);
		   tx.setText(mysave.get(position));
			return tx;
			
		}
	}
}
