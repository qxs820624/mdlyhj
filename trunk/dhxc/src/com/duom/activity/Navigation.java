package com.duom.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLine;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.RouteOverlay;
import com.jingxunnet.cmusic.R;

public class Navigation extends com.baidu.mapapi.MapActivity implements
		LocationListener {
 // 地图管理
	public BMapManager mapmanger;
	//获取定位信息
	private MKLocationManager locationManager = null;
	//定位的标记覆盖
	private MyLocationOverlay myLocationOverlay;
	//地图控制器
	MapController mapController;
	//拆分线路的集合
	public static List<String> step;
	ListView suggestion, suggestionzd;
	//搜索建议
	boolean sug = false, sugzd = false, isChange = true;
	//驾车搜索的结果
	static MKDrivingRouteResult mkResult;
	//起始 和结束位置 
	private static EditText startEdit;
	private  EditText endEdit;
	public static MKPlanNode start;
	public static MKPlanNode end;
	
	private Button suresurch, change;
	public static boolean is = false;
	// 获取地点
	private Geocoder geo;
	public ImageButton back ;
	List<Address> addresses = null;
	//搜索类
	public static MKSearch mKSearch;
	//map视图
	private MapView mapView;
	//搜索建议字符串数组
	String[] suggestions;
	//俩个清空按钮
	public Button qingkong, clean;
    //用来判断是否跳转
	public static int key = 0 , num;

	String start11;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigation);

		num = 0;
		//初始化
		startEdit = (EditText) findViewById(R.id.start);
		endEdit = (EditText) findViewById(R.id.end);
		suresurch = (Button) findViewById(R.id.suresearch);
		suggestion = (ListView) findViewById(R.id.suggestion);
		suggestionzd = (ListView) findViewById(R.id.suggestionzd);
		change = (Button) findViewById(R.id.change);
		qingkong = (Button) findViewById(R.id.qingkong);
		clean = (Button) findViewById(R.id.clean);
		back = (ImageButton) findViewById(R.id.backnavigation);
	  //初始化 地图管理，
		mapmanger = new BMapManager(getApplication());
		//字符串为个人申请地图api证书
		mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C", null);
		//加载地图
		super.initMapActivity(mapmanger);
     //获取当前的地理位置  定位
		locationManager = mapmanger.getLocationManager();
		locationManager.requestLocationUpdates(this);
		locationManager.enableProvider((int) MKLocationManager.MK_GPS_PROVIDER);
		//设置地图可以支持缩放
		mapView = (MapView) findViewById(R.id.bmapsView);
		mapView.setBuiltInZoomControls(true);
        //地图控制器
		mapController = mapView.getController();
		mapController.setZoom(12);
		//添加定位的标记
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		myLocationOverlay.enableMyLocation();
		//添加指南针
		myLocationOverlay.enableCompass();
		mapView.getOverlays().add(myLocationOverlay);
        //初始化搜索类
		mKSearch = new MKSearch();
		mKSearch.init(mapmanger, new MySearchListener());
       //从myroute跳转过来 ， 传过起始，终点位置，进行搜索
		Intent it = getIntent();
		start11 = it.getStringExtra("start11");
		String end11 = it.getStringExtra("end11");
		if(start11 != null && end11!= null){
			start = new MKPlanNode();
			start.name = start11.toString();
			end = new MKPlanNode();
			end.name = end11.toString();
			mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mKSearch.drivingSearch("中国", start, "中国", end);
		}
		//
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		startEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// 为true 时， 可获得建议搜索
					isChange = true;
				}
			}
		});
		// 对编辑框进行 焦点监听，当获取焦点时候，可以获得提示
		endEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isChange = true;
				}
			}
		});
       //编辑框内容变化监听
		startEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (isChange == true) {
					if (num > 0) {
						//每次都要重新初始化地图管理 ，这样才能每次都根据你的输入提示
						suggestions = null;
						mapmanger = new BMapManager(getApplication());
						mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C",
								null);
						mKSearch = new MKSearch();
						mKSearch.init(mapmanger, new MySearchListener());
					}
					mKSearch.suggestionSearch(startEdit.getText().toString());
					suggestion.setVisibility(View.VISIBLE);
					//用来设置俩个建议框是否显示
					sug = true;
					sugzd = false;
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
        // 终点输入监听 ，和起始输入监听一样
		endEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (isChange == true) {
					if (num > 0) {
						suggestions = null;
						mapmanger = new BMapManager(getApplication());
						mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C",
								null);
						mKSearch = new MKSearch();
						mKSearch.init(mapmanger, new MySearchListener());
					}
					mKSearch.suggestionSearch(endEdit.getText().toString());
					suggestionzd.setVisibility(View.VISIBLE);
					sug = false;
					sugzd = true;
				}
				if(endEdit.getText().equals("")){
					suggestionzd.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		//对提示框选择进行监听
		suggestion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String ss = suggestions[position];
				startEdit.setText(ss);
				suggestion.setVisibility(8);
				suggestions = null;
				Toast.makeText(Navigation.this, ss, 2000).show();

			}

		});
		//对提示框选择进行监听
		suggestionzd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String ss = suggestions[position];
				endEdit.setText(ss);
				suggestionzd.setVisibility(8);
				suggestions = null;
				Toast.makeText(Navigation.this, ss, 2000).show();
			}
		});
		//确定按钮监听
		suresurch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (num > 0) {
					suggestions = null;
					mapmanger = new BMapManager(getApplication());
					mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C",
							null);
					mKSearch = new MKSearch();
					mKSearch.init(mapmanger, new MySearchListener());
				}
				//得到起始位置 ， 开始搜索
				start = new MKPlanNode();
				end = new MKPlanNode();
				start.name = startEdit.getText().toString();
				end.name = endEdit.getText().toString();
				mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				mKSearch.drivingSearch("中国", start, "中国", end);
				mapView.invalidate(); // 刷新地图
			}
		});
		// /对交换按钮监听
		change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (startEdit != null && endEdit != null) {
					isChange = false;
					String se = startEdit.getText().toString();
					String ee = endEdit.getText().toString();
					endEdit.setText(se);
					startEdit.setText(ee);
					endEdit.clearFocus();
				}
			}
		});
		// 俩个清空按钮监听
		qingkong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				endEdit.setText("");
				suggestionzd.setVisibility(8);
			}
		});
		clean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startEdit.setText("");
				suggestion.setVisibility(8);
			}
		});
	}
	//手机返回按键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
           if(suggestion.getVisibility()!=View.GONE || suggestionzd.getVisibility()!=View.GONE){
        	   suggestion.setVisibility(View.GONE);
        	   suggestionzd.setVisibility(View.GONE);
           }
           else{
        	   finish();
           }
		}
		return true;
	}
   //实现MKsearchListener接口。
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}
        //驾车搜索路线的结果
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(Navigation.this, "抱愧，未找到成果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			//将路线进行拆分，存到集合中
			mkStep(arg0, null, null);
			mkResult = arg0;
			//当key == 1，将路线图画出来，显示在地图上
			if(key == 1){
			mapView.getOverlays().clear() ;
			RouteOverlay routeOverlay = new RouteOverlay(Navigation.this,
					mapView);
			routeOverlay.setData(mkResult.getPlan(0).getRoute(0));
			mapView.getOverlays().add(routeOverlay);
			}
			//当KYE == 0，跳转到showroute界面
			if( key == 0){
			Intent search = new Intent();
			search.setClass(Navigation.this, ShowRoute.class);
			startActivity(search);
			num++;
			}
			key = 0;
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
     //搜素建议
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(Navigation.this, "抱愧，未找到成果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			//起始点输入的搜索建议提示
			if (sug == true) {
				suggestions = null;
				int size = arg0.getSuggestionNum();
				suggestions = new String[size];
				for (int i = 0; i < size; i++) {
					suggestions[i] = arg0.getSuggestion(i).key;
					ArrayAdapter<String> suggestionString = new ArrayAdapter<String>(
							Navigation.this,
							android.R.layout.simple_list_item_1, suggestions);
					suggestion.setAdapter(suggestionString);
				}
			} 
			//终点输入的搜索建议提示
			else if (sugzd == true) {
				suggestions = null;
				//将百度地图自带的提示，存到集合中。
				int size = arg0.getSuggestionNum();
				suggestions = new String[size];
				//将提示的内容加到适配器中 。 
				for (int i = 0; i < size; i++) {
					suggestions[i] = arg0.getSuggestion(i).key;
					ArrayAdapter<String> suggestionString = new ArrayAdapter<String>(
							Navigation.this,
							android.R.layout.simple_list_item_1, suggestions);
					suggestionzd.setAdapter(suggestionString);
				}
			}
		}
	}
  //当activity重新启动，先调用这个方法
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		if (mapmanger != null) {
			mapmanger.start();
		}
	 //刷新地图
		mapView.invalidate();
		//清楚地图上的标记
		mapView.getOverlays().clear() ;
		if (is == true) {
			mapView.getOverlays().clear() ;
			RouteOverlay routeOverlay = new RouteOverlay(Navigation.this,
					mapView);
			routeOverlay.setData(mkResult.getPlan(0).getRoute(0));
			mapView.getOverlays().add(routeOverlay);
		}
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub

		return false;
	}

//	@Override
//	protected void onDestroy() {
//		mapmanger.destroy();
//		mapmanger = null;
//		is = false;
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}

	@Override
	protected void onPause() {
		if (mapmanger != null) {
			mapmanger.stop();
		}
		// TODO Auto-generated method stub
		super.onPause();
	}
   //手机定位的时时监听
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			final GeoPoint pt = new GeoPoint(
					(int) (location.getLatitude() * 1000000),
					(int) (location.getLongitude() * 1000000));
			mapController.setCenter(pt);
		}
	}
   //自定义的拆分路线的方法
	public void mkStep(MKDrivingRouteResult res1, MKTransitRouteResult res2,
			MKWalkingRouteResult res3) {
		step = new ArrayList<String>();
		int numSteps;
		if (res1 != null) {
			numSteps = res1.getPlan(0).getRoute(0).getNumSteps() - 1;
			if (numSteps > 0) {
				for (int i = 0; i < numSteps; i++) {
					step.add(res1.getPlan(0).getRoute(0).getStep(i)
							.getContent());
				}
			}
		}
		//这个是选择乘坐公交车时，座乘方案、。。项目没用到。
		else if (res2 != null) {
			numSteps = res2.getPlan(0).getNumLines();
			int i;
			int j1 = res2.getPlan(0).getNumRoute();
			if (numSteps > 0) {
				for (i = 0; i < numSteps; i++) {
					MKLine line = res2.getPlan(0).getLine(i);
					if (line.getType() == MKLine.LINE_TYPE_BUS) {
						step.add("乘坐公交" + line.getTitle() + "路");
						step.add("自" + line.getGetOnStop().name + "经过"
								+ line.getNumViaStops() + "站到"
								+ line.getGetOffStop().name + "---"
								+ line.getDistance() + "m");

					} else if (line.getType() == MKLine.LINE_TYPE_SUBWAY) {
						step.add("乘坐" + line.getTitle());
						step.add("自" + line.getGetOnStop().name + "经过"
								+ line.getNumViaStops() + "站到"
								+ line.getGetOffStop().name + "---"
								+ line.getDistance() + "m");
					}
					if (i <= j1)
						step.add("在" + line.getGetOffStop().name + "下车，步行"
								+ res2.getPlan(0).getRoute(i + 1).getDistance()
								+ "m");
				}
			}
			step.add("到达终点");
		} else {
			numSteps = res3.getPlan(0).getRoute(0).getNumSteps() - 1;
			if (numSteps > 0) {
				for (int i = 0; i < numSteps; i++) {
					step.add(res3.getPlan(0).getRoute(0).getStep(i)
							.getContent());
				}
			}
		}
	}
}
