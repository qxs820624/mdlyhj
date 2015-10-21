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
	//��ʾ·��
	private ListView showRoute;
	//��ʾ��ͼ�ؼ�
	public BMapManager mapmanger;
	//��ť
	private Button fastest, shortest, cheapest, check, creatRoute, save;
	//�ٶȵ�ͼ��������
	public MKSearch mKSearch;
	//����
	private ImageButton back;
	//�����������յ�
	public MKPlanNode start;
	public MKPlanNode end;
	//�洢����·�ߵķֶ���Ϣ
	public List<String> stepShow;
	//����������
	public static int whichOne = 0;
	//���ݿ�
	private PictureDB db;

	
	String ss;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showroute);
		//��ʼ���ؼ�
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
		//�ٶȵ�ͼ�����ߵ�֤���
		mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C", null);
		mKSearch = new MKSearch();
		//�ٶȵ�ͼ�����ļ���
		mKSearch.init(mapmanger, new SearchListener());
		//��ʼλ��
		start = new MKPlanNode();
		end = new MKPlanNode();
		start.name = Navigation.start.name;
		end.name = Navigation.end.name;
         // ����ٶ�
		fastest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
               //������ť���ñ���
				fastest.setBackgroundResource(R.drawable.jiachexianlupress);
				shortest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlu);
               //����������������ʱ������ ��������Ϊ��ʱ������ �� ·��������� �� ʡǮ���ȣ�
				mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				//��ʼ �� ��ʼλ�õ��յ�λ������
				mKSearch.drivingSearch("����", start, "����", end);
				//�ж��ǰ�������������
				whichOne = 0;
			}
		});
		//·����� ��ť�ļ���
		shortest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//������������ť����Ϊ δѡ�е�ͼƬ
				fastest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlu);
				//���������Ϊѡ��ͼƬ
				shortest.setBackgroundResource(R.drawable.jiachexianlupress);
				//��·���������
				mKSearch.setDrivingPolicy(MKSearch.ECAR_FEE_FIRST);
				mKSearch.drivingSearch("����", start, "����", end);
				whichOne = 1;

			}
		});
		//��ʡǮ���� ����
		cheapest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fastest.setBackgroundResource(R.drawable.jiachexianlu);
				shortest.setBackgroundResource(R.drawable.jiachexianlu);
				cheapest.setBackgroundResource(R.drawable.jiachexianlupress);
				mKSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
				mKSearch.drivingSearch("����", start, "����", end);
				whichOne = 2;

			}
		});
		//��Navigation�в��·�ߵļ��ϣ���ֵ��stepshow
		stepShow = Navigation.step;
		//����������
		showRoute.setAdapter(new adapter(this));
		showRoute.setCacheColorHint(0);

		// �鿴��·����
		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				//trueΪ��ʼ����
				Navigation.is = true;
			}
		});
		//������·����
		creatRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���屣����·�ĸ�ʽ
			 ss = start.name + "--" + end.name ; 
			 //�����ݿ�
				if (db == null) {
					db = new PictureDB(ShowRoute.this);
				}
			// ����һ��ֻ��·�ߵ�����
				db.insert(new Picture(null, null , 0 , 0 , null , ss , null));				
						// TODO Auto-generated method stub
						Intent it = new Intent();
						//��·�ߴ���ȥ
						it.putExtra("routefromshowroute", ss);
						//��ת��Promptҳ��
						it.setClass(ShowRoute.this, Prompt.class);
						startActivity(it);
			}
		});
		//�ղ�·�߼���
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���屣����·�ĸ�ʽ
				 ss = start.name + "------------" + end.name ; 
//				 //���ϵ�ȥ�ط������жϼ����Ƿ����ss�� ���������룬�����򲻼���
//				 if(!MostNew.saveRoute.contains(ss)){
//					 MostNew.saveRoute.add(ss);
//			        Toast.makeText(ShowRoute.this, "�ղسɹ���", 2000).show();
//				 }	else{
//					Toast.makeText(ShowRoute.this, "���ղأ������ظ�������", 2000).show(); 
//				 }
				// ���ϵ�ȥ�ط������жϼ����Ƿ����ss�� ���������룬�����򲻼���
					if (!MostNew.saveRoute.contains(ss)) {
						MostNew.saveRoute.add(ss);
						Toast.makeText(ShowRoute.this, "�ղسɹ���", 2000).show();
						v.setBackgroundResource(R.drawable.yishoucang);
						Log.e("dfs1111", MostNew.saveRoute + "");
					} else {
						MostNew.saveRoute.remove(ss);
						Log.e("dfs1111", MostNew.saveRoute + "");
						v.setBackgroundResource(R.drawable.save11);
						Toast.makeText(ShowRoute.this, "��ȡ���ղ�!", 2000).show();
					}
			}     
		});
		//���ؼ���
		back.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
   //mksearch��������������Ҫʵ�ֵļ����ӿ� �� ���ڲ��ࣩ
	public class SearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}
      //�õ��ݳ�·�ߵ��������
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
     //�ж������Ľ���Ƿ�Ϊ��
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(ShowRoute.this, "������δ�ҵ��ɹ�", Toast.LENGTH_LONG)
						.show();
				return;
			}
			Navigation.mkResult = arg0;
			//����mkstep()��������·�߲�֣������뼯����
			mkStep(arg0, null, null);
			//Ϊlistview����������
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
	//���·�ߵķ���
	public void mkStep(MKDrivingRouteResult res1, MKTransitRouteResult res2,
			MKWalkingRouteResult res3) {
		stepShow = new ArrayList<String>();
		int numSteps;
		if (res1 != null) {
			//�õ���һ��·��
			numSteps = res1.getPlan(0).getRoute(0).getNumSteps() - 1;
			if (numSteps > 0) {
				//��·�߲�֣����뼯��
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
			return stepShow.size();// ��Ŀ����
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
			//�Զ����view ��ͼ����������Դ
			View view = LayoutInflater.from(context).inflate(R.layout.routeitem, null);
			TextView route = (TextView)view.findViewById(R.id.rt);	
			route.setText(stepShow.get(position));
			return view;
			
		}
	}

}
