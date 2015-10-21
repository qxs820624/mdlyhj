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
	//��������ļ���
	private List<String> subjects;
	private LinearLayout layout , layimage;
	//���水ť�ļ���
	private List<Button> ib , ckbt , deletroute , tjbt;
	//������ͼ�ļ���
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
      //��ʼ��
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
		//��ʼ������
		init();
		//ѭ���԰�ť����
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
			//�鿴��ͼ  ������
			ckbt.get(i).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v.getTag().equals(k)){
						//��ȡ·�ߵ�����
						String route = ib.get(k).getText().toString();
						//���ַ������
						String[] rou = route.split("--");
						//�ֱ𱣴�
						String start1 = rou[0]; 
						String end1 = rou[1];	
						ShowRoute.whichOne = 0;
                    //��������ҳ�棬����ʼ���յ�λ�ô���ȥ����������
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
			//ɾ����· ����
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
						//����Ӧ�İ�ť����ͼ�ؼ��Ƴ�
						 layout.removeView(ib.get(k));
						 layout.removeView(vw.get(k));
					}
				}
			});
			//   ���/�鿴��ᰴť
			tjbt.get(i).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v.getTag().equals(k)){
						
					String route = ib.get(k).getText().toString();
                   //����ť�ϵ����ǡ������ᡱ��ʱ ����ת�����ҳ��
					 if(tjbt.get(k).getText().toString().equalsIgnoreCase("������")){
					 Intent it = new Intent();					 
					 it.putExtra("route", route);
					 it.setClass(Myroute.this, Add.class);
					 startActivity(it);
					  }
					   //����ť�ϵ����ǡ��鿴��ᡱ��ʱ  ��ת���鿴���ҳ��
					 else if(tjbt.get(k).getText().toString().equalsIgnoreCase("�鿴���")){
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
		// ɨ�����е�����
		subjects = db.qurryAllRoute();
		// ���û�д������� ����ʾ��û�д���////////////////////////////////////
		layimage.setVisibility(View.VISIBLE);
		if (subjects.size() == 0) {
		 //�Ƴ��ؼ�
		 layout.removeView(bnt);
		 layout.removeView(view);
			return;
		} else {
			layimage.setVisibility(View.GONE);
			// ��������⣬ѭ��ȡ������
			for (int i = 0; i < subjects.size(); i++) {
				//��̬��Ӱ�ť
				bnt = new Button(this);
				//��������
				bnt.setText(subjects.get(i));
				bnt.setHeight(80);
				bnt.setTextColor(Color.rgb(133, 74, 42));
				bnt.setBackgroundResource(R.drawable.myroutefengexian);
				//���ñ�ǣ�������ʱ���жϱ�ǣ���ȷ�����µ��ĸ���ť
				bnt.setTag(j);
				j++;
				//�Ѱ�ť�ӵ���������
				ib.add(bnt);
				layout.addView(bnt);
				//��̬���VIew
				 view = new View(getApplicationContext());
				 //��ȡview�Ĳ���
				view = getLayoutInflater().inflate(R.layout.yincang, null);
				//��ʼ���ؼ�
				 ck = (Button) view.findViewById(R.id.ckdt);
				 dh = (Button) view.findViewById(R.id.ksdh);
				 tj = (Button) view.findViewById(R.id.tjxc);
			    //���ñ�ǣ�������ʱ���жϱ�ǣ���ȷ�����µ��ĸ���ť
				 ck.setTag(x);
				 dh.setTag(x);
				 tj.setTag(x);
				//�ֱ�ӵ���Ӧ�ļ�����
				 ckbt.add(ck);
				 deletroute.add(dh);
				 tjbt.add(tj);	
				 //����View���ɼ�
				view.setVisibility(View.GONE);
				x++;
				if(db == null){
					db = new PictureDB(this);
				}
				//��ѯ ���⣬��������ⲻΪ�գ����Դ�����ᣩ����ť�ϵ�������Ϊ���鿴��ᡱ
				Picture pt =db.curryroute(subjects.get(i));
				if(pt.getZhuti() != null){
					tjbt.get(j-1).setText("�鿴���");
				}
				vw.add(view);
				layout.addView(view);
			}
		}
	}
}
