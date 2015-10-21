package com.duom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.jingxunnet.cmusic.R;

/*
 * �˴�ѡ������ �� ѡ��ʱ�䣬 ѡ�������� ������һ����ģʽ�͸�ʽ����ɵ� ���ڴ� ֻע�� ѡ��ʱ�� choosedate �� ���� ��
 *    ����ҳ�涼��butoon �����������ͼƬ��ʵ��
 * */
public class Choosemusic extends Activity implements OnClickListener {
	private Button wu, bnt1, bnt2, bnt3, bnt4, sure, cancle;
	boolean select1 = false;
	boolean select2 = false;
	boolean select3 = false;
	boolean select4 = false;
	boolean select = false;
	int musics[] = {};
	int musicindex;
	private SharedPreferences prefs;
	String musicname[] = { "��","�����λ�", "��������", "���Ĺ���", "�������" };

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosemusic);
		wu = (Button) findViewById(R.id.wu);
		bnt1 = (Button) findViewById(R.id.bnt1);
		bnt2 = (Button) findViewById(R.id.bnt2);
		bnt3 = (Button) findViewById(R.id.bnt3);
		bnt4 = (Button) findViewById(R.id.bnt4);
		sure = (Button) findViewById(R.id.bntsure);
		cancle = (Button) findViewById(R.id.bntcancle);

		wu.setOnClickListener(this);
		bnt1.setOnClickListener(this);
		bnt2.setOnClickListener(this);
		bnt3.setOnClickListener(this);
		bnt4.setOnClickListener(this);
		sure.setOnClickListener(this);
		cancle.setOnClickListener(this);

		wu.setBackgroundResource(R.drawable.wu);
		bnt1.setBackgroundResource(R.drawable.langman);
		bnt2.setBackgroundResource(R.drawable.huainian);
		bnt3.setBackgroundResource(R.drawable.huanxin);
		bnt4.setBackgroundResource(R.drawable.jieri);
		
		int index = readDate();
		Log.e("music", index+"");
		if(index>=0 && index <= 4){
		if(Add.tvmusic.getText().equals(musicname[index])){
		switch (index) {
		case 0:
			wu.setBackgroundResource(R.drawable.wudianji);
			break;
		case 1:
			bnt1.setBackgroundResource(R.drawable.langmandianji);
			break;
		case 2:
			bnt2.setBackgroundResource(R.drawable.huainiandianji);
			break;
		case 3:
			bnt3.setBackgroundResource(R.drawable.huanxindianji);
			break;
		case 4:
			bnt4.setBackgroundResource(R.drawable.jieridianji);
			break;
		default:
			break;
		 }
		}
		else{
			wu.setBackgroundResource(R.drawable.wudianji);
		  }
     	}
		else{
			wu.setBackgroundResource(R.drawable.wudianji);
		  }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == wu) {
			if (select == true) {
				wu.setBackgroundResource(R.drawable.wu);
				select = false;
				if (select1 == false && select2 == false && select3 == false
						&& select4 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}
			} else {
				wu.setBackgroundResource(R.drawable.wudianji);
				bnt1.setBackgroundResource(R.drawable.langman);
				bnt2.setBackgroundResource(R.drawable.huainian);
				bnt3.setBackgroundResource(R.drawable.huanxin);
				bnt4.setBackgroundResource(R.drawable.jieri);
				select = true;
				select1 = false;
				select2 = false;
				select3 = false;
				select4 = false;

				if (select1 == false && select2 == false && select3 == false
						&& select4 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}
			}
		}
		if (v == bnt1) {
			if (select1 == true) {
				bnt1.setBackgroundResource(R.drawable.langman);
				select1 = false;
				// �������� ���رո���
				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 102);
				startService(musicser);
			} else {
				bnt1.setBackgroundResource(R.drawable.langmandianji);
				bnt2.setBackgroundResource(R.drawable.huainian);
				bnt3.setBackgroundResource(R.drawable.huanxin);
				bnt4.setBackgroundResource(R.drawable.jieri);
				wu.setBackgroundResource(R.drawable.wu);
				select1 = true;
				select2 = false;
				select3 = false;
				select4 = false;
				select = false;
				// �������� ����ʼ����ѡ�и���
				if (select2 == false && select3 == false && select4 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}

				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 101);
				musicser.putExtra("index", 0);
				startService(musicser);
			}
		}
		if (v == bnt2) {
			if (select2 == true) {
				bnt2.setBackgroundResource(R.drawable.huainian);
				select2 = false;
				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 102);
				startService(musicser);
			} else {
				bnt2.setBackgroundResource(R.drawable.huainiandianji);
				bnt1.setBackgroundResource(R.drawable.langman);
				bnt3.setBackgroundResource(R.drawable.huanxin);
				bnt4.setBackgroundResource(R.drawable.jieri);
				wu.setBackgroundResource(R.drawable.wu);
				select2 = true;
				select1 = false;
				select3 = false;
				select4 = false;
				select = false;

				if (select1 == false && select3 == false && select4 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}

				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 101);
				musicser.putExtra("index", 1);
				startService(musicser);
			}
		}
		if (v == bnt3) {
			if (select3 == true) {
				bnt3.setBackgroundResource(R.drawable.huanxin);
				select3 = false;
				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 102);
				startService(musicser);
			} else {
				bnt3.setBackgroundResource(R.drawable.huanxindianji);
				bnt2.setBackgroundResource(R.drawable.huainian);
				bnt1.setBackgroundResource(R.drawable.langman);
				bnt4.setBackgroundResource(R.drawable.jieri);
				wu.setBackgroundResource(R.drawable.wu);
				select3 = true;
				select1 = false;
				select2 = false;
				select4 = false;
				select = false;

				if (select1 == false && select2 == false && select4 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}

				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 101);
				musicser.putExtra("index", 2);
				startService(musicser);
			}
		}
		if (v == bnt4) {
			if (select4 == true) {
				bnt4.setBackgroundResource(R.drawable.jieri);
				select4 = false;
				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 102);
				startService(musicser);
			} else {
				bnt4.setBackgroundResource(R.drawable.jieridianji);
				bnt2.setBackgroundResource(R.drawable.huainian);
				bnt1.setBackgroundResource(R.drawable.langman);
				bnt3.setBackgroundResource(R.drawable.huanxin);
				wu.setBackgroundResource(R.drawable.wu);
				select4 = true;
				select1 = false;
				select3 = false;
				select2 = false;
				select = false;

				if (select2 == false && select3 == false && select1 == false) {
					Intent musicser = new Intent();
					musicser.setAction("com.duom.daohangyinyue");
					musicser.putExtra("puanduan", 102);
					startService(musicser);
				}

				Intent musicser = new Intent();
				musicser.setAction("com.duom.daohangyinyue");
				musicser.putExtra("puanduan", 101);
				musicser.putExtra("index", 3);
				startService(musicser);
			}
		}
		if (v == sure) {
			// /���ȷ����ť���ж��ĸ���ť�Ǳ�ѡ�е�״̬�����ش�ֵ��
			if (select == true) {
				musicindex = 0;
			}
			if (select1 == true) {
				musicindex = 1;
			}
			if (select2 == true) {
				musicindex = 2;
			}
			if (select3 == true) {
				musicindex = 3;
			}
			if (select4 == true) {
				musicindex = 4;
			}
			Intent data = new Intent();
			data.putExtra("musicindex", musicindex);
			// �����������Լ����ã��������ó�30
			setResult(30, data);
			// �رյ����Activity
			finish();
			Intent musicser = new Intent();
			musicser.setAction("com.duom.daohangyinyue");
			musicser.putExtra("puanduan", 102);
			startService(musicser);
			saveDate();
		}
		if (v == cancle) {
			finish();

			Intent musicser = new Intent();
			musicser.setAction("com.duom.daohangyinyue");
			musicser.putExtra("puanduan", 102);
			startService(musicser);
		}
	}
	private void saveDate() {
		// String ss= paCalendar.getInstance();
		prefs = getSharedPreferences("BirthDate", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("lastmusic", musicindex);
		editor.commit(); // ע��һ��Ҫд�˺�������䣩
	}

	private int readDate() {
		SharedPreferences prefs = getSharedPreferences("BirthDate",
				Context.MODE_PRIVATE);
		return prefs.getInt("lastmusic", 0x100);
	}
}
