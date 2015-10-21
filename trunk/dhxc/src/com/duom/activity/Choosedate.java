package com.duom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.jingxunnet.cmusic.R;

/*
 * 此处选择音乐 �?选择时间�?选择天气�?都是用一个的模式和格式来完成�?，在�?只注�?选择时间 choosedate �?界面 �?
 *    几个页面都用butoon 点击更换背景图片来实�?
 * */
public class Choosedate extends Activity implements OnClickListener {
	// 定义按钮
	private Button am, noon, pm, night, sure, cancle;
	private SharedPreferences prefs;
	// 用来判断当前哪个按钮被�?�?
	boolean select1 = false;
	boolean select2 = false;
	boolean select3 = false;
	boolean select4 = false;
	// 判断哪个按钮被�?中，将对应的索引�?存入
	int dateindex;

	String datename[] = { "����", "����", "����", "����" };
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosedate);
		// 初始�?组件
		am = (Button) findViewById(R.id.amb);
		noon = (Button) findViewById(R.id.noonb);
		pm = (Button) findViewById(R.id.pmb);
		night = (Button) findViewById(R.id.nightb);
		sure = (Button) findViewById(R.id.datesure);
		cancle = (Button) findViewById(R.id.datecancle);
		// 设置监听
		am.setOnClickListener(this);
		noon.setOnClickListener(this);
		pm.setOnClickListener(this);
		night.setOnClickListener(this);
		sure.setOnClickListener(this);
		cancle.setOnClickListener(this);
		// 设置每个按钮的初始化背景 �?
		am.setBackgroundResource(R.drawable.am);
		noon.setBackgroundResource(R.drawable.noon);
		pm.setBackgroundResource(R.drawable.pm);
		night.setBackgroundResource(R.drawable.night);

		int index = readDate();
		if(index>=0 && index <= 3){
		if(Add.tvdate.getText().equals(datename[index])){
		switch (index) {
		case 0:
			am.setBackgroundResource(R.drawable.amdianji);
			break;
		case 1:
			noon.setBackgroundResource(R.drawable.noondianji);
			break;
		case 2:
			pm.setBackgroundResource(R.drawable.pmdianji);
			break;
		case 3:
			night.setBackgroundResource(R.drawable.nightdianji);
			break;
		default:
			break;
		}
		  }else{
				am.setBackgroundResource(R.drawable.amdianji);
			}
		}else{
			am.setBackgroundResource(R.drawable.amdianji);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 上午被�?中的时�?，将别的按钮的�?中状态都设置为false
		if (v == am) {
			// 上午被�?中的时�?，单机按钮， select1 变为没被选中，并将button背景换为没被选中状�?
			if (select1 == true) {
				am.setBackgroundResource(R.drawable.am);
				select1 = false;
			} else {
				// 上午没�?中的时�?，单机按钮， select1
				// 变为被�?中，并将button背景换为被�?中状态，此时还是设置其他按钮都为没�?中的状�?
				// 下面几个同理
				am.setBackgroundResource(R.drawable.amdianji);
				noon.setBackgroundResource(R.drawable.noon);
				pm.setBackgroundResource(R.drawable.pm);
				night.setBackgroundResource(R.drawable.night);
				select1 = true;
				select2 = false;
				select3 = false;
				select4 = false;
			}
		}
		// 中午被�?中的时�?，将别的按钮的�?中状态都设置为false
		if (v == noon) {
			if (select2 == true) {
				noon.setBackgroundResource(R.drawable.noon);
				select2 = false;
			} else {
				noon.setBackgroundResource(R.drawable.noondianji);
				am.setBackgroundResource(R.drawable.am);
				pm.setBackgroundResource(R.drawable.pm);
				night.setBackgroundResource(R.drawable.night);
				select2 = true;
				select1 = false;
				select3 = false;
				select4 = false;
			}
		}
		// 下午被�?中的时�?，将别的按钮的�?中状态都设置为false
		if (v == pm) {
			if (select3 == true) {
				pm.setBackgroundResource(R.drawable.pm);
				select3 = false;
			} else {
				pm.setBackgroundResource(R.drawable.pmdianji);
				noon.setBackgroundResource(R.drawable.noon);
				am.setBackgroundResource(R.drawable.am);
				night.setBackgroundResource(R.drawable.night);
				select3 = true;
				select1 = false;
				select2 = false;
				select4 = false;
			}
		}
		if (v == night) {
			if (select4 == true) {
				night.setBackgroundResource(R.drawable.night);
				select4 = false;
			} else {
				night.setBackgroundResource(R.drawable.nightdianji);
				noon.setBackgroundResource(R.drawable.noon);
				am.setBackgroundResource(R.drawable.am);
				pm.setBackgroundResource(R.drawable.pm);
				select4 = true;
				select1 = false;
				select3 = false;
				select2 = false;
			}
		}
		if (v == sure) {
			// /点击确定按钮后，判断哪个按钮是被选中的状态，�?��传�?�?
			if (select1 == true) {
				dateindex = 0;
			}
			if (select2 == true) {
				dateindex = 1;
			}
			if (select3 == true) {
				dateindex = 2;
			}
			if (select4 == true) {
				dateindex = 3;
			}
			// 将最后的选择结果 ，�?过设置setresult 向回传�?结果
			Intent data = new Intent();
			data.putExtra("dateindex", dateindex);
			setResult(50, data);
			// 关闭掉这个Activity
			finish();
			saveDate();
		}
		if (v == cancle) {
			finish();
		}
	}

	private void saveDate() {
		// String ss= paCalendar.getInstance();
		prefs = getSharedPreferences("BirthDate", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("lastdate", dateindex);
		editor.commit(); // 注意�?��要写此函�?
	}

	private int readDate() {
		SharedPreferences prefs = getSharedPreferences("BirthDate",
				Context.MODE_PRIVATE);
		return prefs.getInt("lastdate", 0x100);
	}

}
