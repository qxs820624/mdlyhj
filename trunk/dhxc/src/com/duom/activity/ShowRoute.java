package com.duom.activity;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class ShowRoute extends com.baidu.mapapi.MapActivity {
	//显示路线
	private ListView showRoute;
	//显示地图控件
	public BMapManager mapmanger;
	//按钮
	private Button fastest, shortest, cheapest, check, creatRoute, save;
	//百度地图的搜索类
	public MKSearch mKSearch;
	//返回
	private ImageButton back;
	//搜索的起点和终点
	public MKPlanNode start;
	public MKPlanNode end;
	//存储整条路线的分段信息
	public List<String> stepShow;
	//按条件搜索
	public static int whichOne = 0;
	//数据库
	private PictureDB db;

	
	String ss;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showroute);
		//初始化控件
		fastest = (Button) findViewById(R.id.fastest);
		shortest = (Button) findViewById(R.id.shortest);
		cheapest = (Button) findViewById(R.id.cheapest);
		check = (Button) findViewById(R.id.check);
		creatRoute = (Button) findViewById(R.id.navigation);
		save = (Button) findViewById(R.id.save);
		showRoute = (ListView) findViewById(R.id.route);
		back = (ImageButton) findViewById(R.id.ibback);

		fastest.setBackgroundResource(R.drawable.jiachexianlupress);
		shortest.setBackgroundResource(R.drawable.jiachexianlu);
		cheapest.setBackgroundResource(R.drawable.jiachexianlu);

		MostNew.saveRoute = new ArrayList<String>();		
		mapmanger = new BMapManager(getApplication());
		//百度地图开发者的证书号
		mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C", null);
		mKSearch = new MKSearch();
		//百度地图搜索的监听
		mKSearch.init(mapmanger, new SearchListener());
		//起始位置
		start = new MKPlanNode();
		end = new MKPlanNode();
		start.name = Navigation.start.name;
		end.name = Navigation.end.name;
         // 最快速度
		fastest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
               //三个按钮设置背景
				fastest.setBackgroundResource(R.drawable.jiachexianlupress);
				shortest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlu);
               //设置搜索的条件，时间优先 （条件分为：时间优先 ， 路程最短优先 ， 省钱优先）
				mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				//开始 从 起始位置到终点位置搜索
				mKSearch.drivingSearch("北京", start, "北京", end);
				//判断是按哪种条件搜索
				whichOne = 0;
			}
		});
		//路程最短 按钮的监听
		shortest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//把另外俩个按钮设置为 未选中的图片
				fastest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlu);
				//把最短设置为选中图片
				shortest.setBackgroundResource(R.drawable.jiachexianlupress);
				//按路程最短搜索
				mKSearch.setDrivingPolicy(MKSearch.ECAR_FEE_FIRST);
				mKSearch.drivingSearch("北京", start, "北京", end);
				whichOne = 1;

			}
		});
		//最省钱搜索 监听
		cheapest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fastest.setBackgroundResource(R.drawable.jiachexianlu);
				shortest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlupress);
				mKSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
				mKSearch.drivingSearch("北京", start, "北京", end);
				whichOne = 2;

			}
		});
		//从Navigation中拆分路线的集合，赋值给stepshow
		stepShow = Navigation.step;
		//设置适配器
		showRoute.setAdapter(new adapter(this));
		showRoute.setCacheColorHint(0);

		// 查看线路监听
		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				//true为开始搜索
				Navigation.is = true;
			}
		});
		//创建线路监听
		creatRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//定义保存线路的格式
			 ss = start.name + "--" + end.name ; 
			 //打开数据库
				if (db == null) {
					db = new PictureDB(ShowRoute.this);
				}
			// 插入一条只有路线的数据
				db.insert(new Picture(null, null , 0 , 0 , null , ss , null));				
						// TODO Auto-generated method stub
						Intent it = new Intent();
						//将路线传过去
						it.putExtra("routefromshowroute", ss);
						//跳转到Prompt页面
						it.setClass(ShowRoute.this, Prompt.class);
						startActivity(it);
			}
		});
		//收藏路线监听
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//定义保存线路的格式
				 ss = start.name + "------------" + end.name ; 
//				 //集合的去重方法，判断集合是否包含ss， 不包含加入，包含则不加入
//				 if(!MostNew.saveRoute.contains(ss)){
//					 MostNew.saveRoute.add(ss);
//			        Toast.makeText(ShowRoute.this, "收藏成功！", 2000).show();
//				 }	else{
//					Toast.makeText(ShowRoute.this, "已收藏，请勿重复操作！", 2000).show(); 
//				 }
				// 集合的去重方法，判断集合是否包含ss， 不包含加入，包含则不加入
					if (!MostNew.saveRoute.contains(ss)) {
						MostNew.saveRoute.add(ss);
						Toast.makeText(ShowRoute.this, "收藏成功！", 2000).show();
						v.setBackgroundResource(R.drawable.yishoucang);
						Log.e("dfs1111", MostNew.saveRoute + "");
					} else {
						MostNew.saveRoute.remove(ss);
						Log.e("dfs1111", MostNew.saveRoute + "");
						v.setBackgroundResource(R.drawable.save11);
						Toast.makeText(ShowRoute.this, "已取消收藏!", 2000).show();
					}
			}     
		});
		//返回监听
		back.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
   //mksearch进行搜索，必须要实现的监听接口 ， （内部类）
	public class SearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}
      //得到驾车路线的搜索结果
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
     //判断搜索的结果是否为空
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(ShowRoute.this, "抱愧，未找到成果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			Navigation.mkResult = arg0;
			//调用mkstep()方法，将路线拆分，并存入集合中
			mkStep(arg0, null, null);
			//为listview加载适配器
			showRoute.setAdapter(new adapter(ShowRoute.this));
			showRoute.setCacheColorHint(0);
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (mapmanger != null) {
			mapmanger.start();
		}
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub

		return false;
	}
	@Override
	protected void onPause() {
		if (mapmanger != null) {
			mapmanger.stop();
		}
		// TODO Auto-generated method stub
		super.onPause();
	}
	//拆分路线的方法
	public void mkStep(MKDrivingRouteResult res1, MKTransitRouteResult res2,
			MKWalkingRouteResult res3) {
		stepShow = new ArrayList<String>();
		int numSteps;
		if (res1 != null) {
			//得到第一条路线
			numSteps = res1.getPlan(0).getRoute(0).getNumSteps() - 1;
			if (numSteps > 0) {
				//将路线拆分，加入集合
				for (int i = 0; i < numSteps; i++) {
					stepShow.add(res1.getPlan(0).getRoute(0).getStep(i)
							.getContent());
				}
			}
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
			return stepShow.size();// 项目总数
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
			View view = LayoutInflater.from(context).inflate(R.layout.routeitem, null);
			TextView route = (TextView)view.findViewById(R.id.rt);	
			route.setText(stepShow.get(position));
			return view;
			
		}
	}

}
