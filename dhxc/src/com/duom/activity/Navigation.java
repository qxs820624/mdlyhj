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
 // ��ͼ����
	public BMapManager mapmanger;
	//��ȡ��λ��Ϣ
	private MKLocationManager locationManager = null;
	//��λ�ı�Ǹ���
	private MyLocationOverlay myLocationOverlay;
	//��ͼ������
	MapController mapController;
	//�����·�ļ���
	public static List<String> step;
	ListView suggestion, suggestionzd;
	//��������
	boolean sug = false, sugzd = false, isChange = true;
	//�ݳ������Ľ��
	static MKDrivingRouteResult mkResult;
	//��ʼ �ͽ���λ�� 
	private static EditText startEdit;
	private  EditText endEdit;
	public static MKPlanNode start;
	public static MKPlanNode end;
	
	private Button suresurch, change;
	public static boolean is = false;
	// ��ȡ�ص�
	private Geocoder geo;
	public ImageButton back ;
	List<Address> addresses = null;
	//������
	public static MKSearch mKSearch;
	//map��ͼ
	private MapView mapView;
	//���������ַ�������
	String[] suggestions;
	//������հ�ť
	public Button qingkong, clean;
    //�����ж��Ƿ���ת
	public static int key = 0 , num;

	String start11;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigation);

		num = 0;
		//��ʼ��
		startEdit = (EditText) findViewById(R.id.start);
		endEdit = (EditText) findViewById(R.id.end);
		suresurch = (Button) findViewById(R.id.suresearch);
		suggestion = (ListView) findViewById(R.id.suggestion);
		suggestionzd = (ListView) findViewById(R.id.suggestionzd);
		change = (Button) findViewById(R.id.change);
		qingkong = (Button) findViewById(R.id.qingkong);
		clean = (Button) findViewById(R.id.clean);
		back = (ImageButton) findViewById(R.id.backnavigation);
	  //��ʼ�� ��ͼ����
		mapmanger = new BMapManager(getApplication());
		//�ַ���Ϊ���������ͼapi֤��
		mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C", null);
		//���ص�ͼ
		super.initMapActivity(mapmanger);
     //��ȡ��ǰ�ĵ���λ��  ��λ
		locationManager = mapmanger.getLocationManager();
		locationManager.requestLocationUpdates(this);
		locationManager.enableProvider((int) MKLocationManager.MK_GPS_PROVIDER);
		//���õ�ͼ����֧������
		mapView = (MapView) findViewById(R.id.bmapsView);
		mapView.setBuiltInZoomControls(true);
        //��ͼ������
		mapController = mapView.getController();
		mapController.setZoom(12);
		//��Ӷ�λ�ı��
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		myLocationOverlay.enableMyLocation();
		//���ָ����
		myLocationOverlay.enableCompass();
		mapView.getOverlays().add(myLocationOverlay);
        //��ʼ��������
		mKSearch = new MKSearch();
		mKSearch.init(mapmanger, new MySearchListener());
       //��myroute��ת���� �� ������ʼ���յ�λ�ã���������
		Intent it = getIntent();
		start11 = it.getStringExtra("start11");
		String end11 = it.getStringExtra("end11");
		if(start11 != null && end11!= null){
			start = new MKPlanNode();
			start.name = start11.toString();
			end = new MKPlanNode();
			end.name = end11.toString();
			mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mKSearch.drivingSearch("�й�", start, "�й�", end);
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
					// Ϊtrue ʱ�� �ɻ�ý�������
					isChange = true;
				}
			}
		});
		// �Ա༭����� �������������ȡ����ʱ�򣬿��Ի����ʾ
		endEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isChange = true;
				}
			}
		});
       //�༭�����ݱ仯����
		startEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (isChange == true) {
					if (num > 0) {
						//ÿ�ζ�Ҫ���³�ʼ����ͼ���� ����������ÿ�ζ��������������ʾ
						suggestions = null;
						mapmanger = new BMapManager(getApplication());
						mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C",
								null);
						mKSearch = new MKSearch();
						mKSearch.init(mapmanger, new MySearchListener());
					}
					mKSearch.suggestionSearch(startEdit.getText().toString());
					suggestion.setVisibility(View.VISIBLE);
					//������������������Ƿ���ʾ
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
        // �յ�������� ������ʼ�������һ��
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
		
		//����ʾ��ѡ����м���
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
		//����ʾ��ѡ����м���
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
		//ȷ����ť����
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
				//�õ���ʼλ�� �� ��ʼ����
				start = new MKPlanNode();
				end = new MKPlanNode();
				start.name = startEdit.getText().toString();
				end.name = endEdit.getText().toString();
				mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				mKSearch.drivingSearch("�й�", start, "�й�", end);
				mapView.invalidate(); // ˢ�µ�ͼ
			}
		});
		// /�Խ�����ť����
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
		// ������հ�ť����
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
	//�ֻ����ذ�������
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
   //ʵ��MKsearchListener�ӿڡ�
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}
        //�ݳ�����·�ߵĽ��
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(Navigation.this, "������δ�ҵ��ɹ�", Toast.LENGTH_LONG)
						.show();
				return;
			}
			//��·�߽��в�֣��浽������
			mkStep(arg0, null, null);
			mkResult = arg0;
			//��key == 1����·��ͼ����������ʾ�ڵ�ͼ��
			if(key == 1){
			mapView.getOverlays().clear() ;
			RouteOverlay routeOverlay = new RouteOverlay(Navigation.this,
					mapView);
			routeOverlay.setData(mkResult.getPlan(0).getRoute(0));
			mapView.getOverlays().add(routeOverlay);
			}
			//��KYE == 0����ת��showroute����
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
     //���ؽ���
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(Navigation.this, "������δ�ҵ��ɹ�", Toast.LENGTH_LONG)
						.show();
				return;
			}
			//��ʼ�����������������ʾ
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
			//�յ����������������ʾ
			else if (sugzd == true) {
				suggestions = null;
				//���ٶȵ�ͼ�Դ�����ʾ���浽�����С�
				int size = arg0.getSuggestionNum();
				suggestions = new String[size];
				//����ʾ�����ݼӵ��������� �� 
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
  //��activity�����������ȵ����������
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		if (mapmanger != null) {
			mapmanger.start();
		}
	 //ˢ�µ�ͼ
		mapView.invalidate();
		//�����ͼ�ϵı��
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
   //�ֻ���λ��ʱʱ����
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
   //�Զ���Ĳ��·�ߵķ���
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
		//�����ѡ�����������ʱ�����˷�����������Ŀû�õ���
		else if (res2 != null) {
			numSteps = res2.getPlan(0).getNumLines();
			int i;
			int j1 = res2.getPlan(0).getNumRoute();
			if (numSteps > 0) {
				for (i = 0; i < numSteps; i++) {
					MKLine line = res2.getPlan(0).getLine(i);
					if (line.getType() == MKLine.LINE_TYPE_BUS) {
						step.add("��������" + line.getTitle() + "·");
						step.add("��" + line.getGetOnStop().name + "����"
								+ line.getNumViaStops() + "վ��"
								+ line.getGetOffStop().name + "---"
								+ line.getDistance() + "m");

					} else if (line.getType() == MKLine.LINE_TYPE_SUBWAY) {
						step.add("����" + line.getTitle());
						step.add("��" + line.getGetOnStop().name + "����"
								+ line.getNumViaStops() + "վ��"
								+ line.getGetOffStop().name + "---"
								+ line.getDistance() + "m");
					}
					if (i <= j1)
						step.add("��" + line.getGetOffStop().name + "�³�������"
								+ res2.getPlan(0).getRoute(i + 1).getDistance()
								+ "m");
				}
			}
			step.add("�����յ�");
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
