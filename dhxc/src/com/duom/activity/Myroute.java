package com.duom.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.jingxunnet.cmusic.R;
import com.duom.tool.Picture;
import com.duom.tool.PictureDB;

public class Myroute extends Activity {
	private ImageButton back;
	private PictureDB db;
	//保存主题的集合
	private List<String> subjects;
	private LinearLayout layout , layimage;
	//保存按钮的集合
	private List<Button> ib , ckbt , deletroute , tjbt;
	//保存视图的集合
	private List<View> vw;
	private int j, x;
	int index ;
	View view;
	Button bnt;
	
	Button ck, dh , tj;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myroute);
      //初始化
		back = (ImageButton) findViewById(R.id.ibback);
		layout = (LinearLayout) findViewById(R.id.llroute);	
		layimage = (LinearLayout) findViewById(R.id.llimage);	
		
		ib = new ArrayList<Button>();
		ckbt = new ArrayList<Button>();
		deletroute = new ArrayList<Button>();
		tjbt = new ArrayList<Button>();
		vw = new ArrayList<View>();
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//初始化方法
		init();
		//循环对按钮监听
		for (int i = 0; i < j; i++) {
			final int k = i;
			ib.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (vw.get(k).getVisibility() == View.GONE) {
						vw.get(k).setVisibility(View.VISIBLE);
						ib.get(k).setBackgroundColor(Color.parseColor("#ffda8b"));
						for (int i = 0; i < k; i++) {
							vw.get(i).setVisibility(View.GONE);
							 ib.get(i).setBackgroundResource(R.drawable.myroutefengexian);
						}
						for (int i = vw.size() - 1; i > k; i--) {
							vw.get(i).setVisibility(View.GONE);
							 ib.get(i).setBackgroundResource(R.drawable.myroutefengexian);
						}
						
					} else {
						  vw.get(k).setVisibility(View.GONE);
						  ib.get(k).setBackgroundResource(R.drawable.myroutefengexian);
					}
					
				}
			});	
			//查看地图  监听。
			ckbt.get(i).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v.getTag().equals(k)){
						//获取路线的名称
						String route = ib.get(k).getText().toString();
						//将字符串拆分
						String[] rou = route.split("--");
						//分别保存
						String start1 = rou[0]; 
						String end1 = rou[1];	
						ShowRoute.whichOne = 0;
                    //跳到导航页面，将起始，终点位置传过去，进行搜索
	                 Navigation.is = true;
	                 Navigation.key = 1;
					  Intent it = new Intent();
					  it.putExtra("start11", start1);
					  it.putExtra("end11", end1);
					  it.setClass(Myroute.this, Navigation.class);
					  startActivity(it);
					}
				}
			});
			//删除线路 监听
			deletroute.get(i).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v.getTag().equals(k)){	
						String route = ib.get(k).getText().toString();
						//Toast.makeText(Myroute.this, "route"+route, Toast.LENGTH_LONG);
						if(db == null){						
							db = new PictureDB(Myroute.this);					
						}
						db.delete(route);
						Toast.makeText(Myroute.this, "route"+route, Toast.LENGTH_LONG)
						.show();
						//将对应的按钮和视图控件移除
						 layout.removeView(ib.get(k));
						 layout.removeView(vw.get(k));
					}
				}
			});
			//   添加/查看相册按钮
			tjbt.get(i).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v.getTag().equals(k)){
						
					String route = ib.get(k).getText().toString();
                   //当按钮上的字是“添加相册”，时 ，跳转到添加页面
					 if(tjbt.get(k).getText().toString().equalsIgnoreCase("添加相册")){
					 Intent it = new Intent();					 
					 it.putExtra("route", route);
					 it.setClass(Myroute.this, Add.class);
					 startActivity(it);
					  }
					   //当按钮上的字是“查看相册”，时  跳转到查看相册页面
					 else if(tjbt.get(k).getText().toString().equalsIgnoreCase("查看相册")){
//							 Intent it = new Intent();
//							 it.setClass(Myroute.this, Photo.class);
//							 startActivity(it);
							if(db == null){
								db = new PictureDB(Myroute.this);
							}
							Picture pt =db.curryroute(subjects.get(k));
							int music = pt.getMusic();
							int bg = pt.getBg();
							Intent it = new Intent();
							it.putExtra("zhutifromMyroute",pt.getZhuti());
							it.putExtra("musicFromMyroute", music);
							it.putExtra("bgFromMyroute", bg);
							it.putExtra("routeFromMyroute",route);
							it.setClass(Myroute.this, Scan.class);
							startActivity(it);
					  }
					}	
					finish();
				}
			});
		}
	}
	public void  init(){
		if (db == null) {
			db = new PictureDB(Myroute.this);
		}
		List<String> photos = new ArrayList<String>();
		// 扫描所有的主题
		subjects = db.qurryAllRoute();
		// 如果没有创建主题 ，提示还没有创建////////////////////////////////////
		layimage.setVisibility(View.VISIBLE);
		if (subjects.size() == 0) {
		 //移除控件
		 layout.removeView(bnt);
		 layout.removeView(view);
			return;
		} else {
			layimage.setVisibility(View.GONE);
			// 如果有主题，循环取出主题
			for (int i = 0; i < subjects.size(); i++) {
				//动态添加按钮
				bnt = new Button(this);
				//设置属性
				bnt.setText(subjects.get(i));
				bnt.setHeight(80);
				bnt.setTextColor(Color.rgb(133, 74, 42));
				bnt.setBackgroundResource(R.drawable.myroutefengexian);
				//设置标记，监听的时候判断标记，来确定按下的哪个按钮
				bnt.setTag(j);
				j++;
				//把按钮加到集合里面
				ib.add(bnt);
				layout.addView(bnt);
				//动态添加VIew
				 view = new View(getApplicationContext());
				 //获取view的布局
				view = getLayoutInflater().inflate(R.layout.yincang, null);
				//初始化控件
				 ck = (Button) view.findViewById(R.id.ckdt);
				 dh = (Button) view.findViewById(R.id.ksdh);
				 tj = (Button) view.findViewById(R.id.tjxc);
			    //设置标记，监听的时候判断标记，来确定按下的哪个按钮
				 ck.setTag(x);
				 dh.setTag(x);
				 tj.setTag(x);
				//分别加到对应的集合中
				 ckbt.add(ck);
				 deletroute.add(dh);
				 tjbt.add(tj);	
				 //设置View不可见
				view.setVisibility(View.GONE);
				x++;
				if(db == null){
					db = new PictureDB(this);
				}
				//查询 主题，如果该主题不为空，（以创建相册）将按钮上的字设置为“查看相册”
				Picture pt =db.curryroute(subjects.get(i));
				if(pt.getZhuti() != null){
					tjbt.get(j-1).setText("查看相册");
				}
				vw.add(view);
				layout.addView(view);
			}
		}
	}
}
