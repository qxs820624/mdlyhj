package com.duom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.jingxunnet.cmusic.R;

public class Choosewether extends Activity implements OnClickListener{
	  private Button cloudy ,sun ,snow ,mist , rain ,sure ,cancle;
	  boolean select1 = false;
	  boolean select2 = false;
	  boolean select3 = false;
	  boolean select4 = false;
	  boolean select5 = false;
	  int wetherindex;
	  private SharedPreferences prefs;
	  String wethername[] = { "ÇçÌì", "ÒõÌì", "À×Óê", "Îíö°", "Ñ©Ìì" };
	  
	  public void onCreate(Bundle savedInstanceState) {
			 requestWindowFeature(Window.FEATURE_NO_TITLE);
			 super.onCreate(savedInstanceState);
		     setContentView(R.layout.choosewether);
		     cloudy = (Button)findViewById(R.id.yintian);
		     sun = (Button)findViewById(R.id.qingtian);
		     snow = (Button)findViewById(R.id.xuetian);
		     mist = (Button)findViewById(R.id.wuai);
		     rain = (Button)findViewById(R.id.leiyu);
		     sure = (Button)findViewById(R.id.wethersure);
		     cancle = (Button)findViewById(R.id.wethercancle);
		     
	         cloudy.setOnClickListener(this);
	         sun.setOnClickListener(this);
	         snow.setOnClickListener(this);
	         mist.setOnClickListener(this);
	         rain.setOnClickListener(this);
	         sure.setOnClickListener(this);
	         cancle.setOnClickListener(this);
	         
	         cloudy.setBackgroundResource(R.drawable.yintian);
	         sun.setBackgroundResource(R.drawable.qingtian);
	         snow.setBackgroundResource(R.drawable.xuetian);
	         mist.setBackgroundResource(R.drawable.wuai);
	         rain.setBackgroundResource(R.drawable.leiyu);
	         
	         int index = readDate();
	         if(index>=0 && index <= 4){
	         if(Add.tvwether.getText().equals(wethername[index])){
	 		switch (index) {
	 		case 0:
	 			 sun.setBackgroundResource(R.drawable.qingtiandianji);
	 			break;
	 		case 1:
	 			 cloudy.setBackgroundResource(R.drawable.yintiandianji);
	 			break;
	 		case 2:
	 			snow.setBackgroundResource(R.drawable.xuetiandianji);
	 			break;
	 		case 3:
	 			mist.setBackgroundResource(R.drawable.wuaidianji);
	 			break;
	 		case 4:
	 			rain.setBackgroundResource(R.drawable.leiyudianji);
	 			break;
	 		default:
	 			break;
	 		}
	         }else{
	        	 sun.setBackgroundResource(R.drawable.qingtiandianji);
	           }
	         }else{
	        	 sun.setBackgroundResource(R.drawable.qingtiandianji);
	           }
		 }
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 if(v == sun){
				 if(select1 == true){
					 sun.setBackgroundResource(R.drawable.qingtian);
					 select1 = false;
				 }else {
					 sun.setBackgroundResource(R.drawable.qingtiandianji);
					 cloudy.setBackgroundResource(R.drawable.yintian);
					 snow.setBackgroundResource(R.drawable.xuetian);
					 mist.setBackgroundResource(R.drawable.wuai);
					 rain.setBackgroundResource(R.drawable.leiyu);
					 select1 = true; 
					 select2 = false;
					 select3= false;
					 select4= false;
					 select5= false;
				 }
			 }
			if(v == cloudy){
				 if(select2 == true){
					 cloudy.setBackgroundResource(R.drawable.yintian);
					 select2 = false;
				 }else {
					 cloudy.setBackgroundResource(R.drawable.yintiandianji);				
					 sun.setBackgroundResource(R.drawable.qingtian);
					 snow.setBackgroundResource(R.drawable.xuetian);
					 mist.setBackgroundResource(R.drawable.wuai);
					 rain.setBackgroundResource(R.drawable.leiyu);
					 select2 = true; 
					 select1 = false;
					 select3= false;
					 select4= false;
					 select5= false;
				 }						 
			}

			 if(v == snow){
				 if(select3 == true){
					 snow.setBackgroundResource(R.drawable.xuetian);				
					 select3 = false;
				 }else {
					 snow.setBackgroundResource(R.drawable.xuetiandianji);
					 sun.setBackgroundResource(R.drawable.qingtian);
					 cloudy.setBackgroundResource(R.drawable.yintian);
					 mist.setBackgroundResource(R.drawable.wuai);
					 rain.setBackgroundResource(R.drawable.leiyu);
					 select3 = true; 
					 select1 = false;
					 select2= false;
					 select4= false;
					 select5= false;
				 }
			 }
			 if(v == mist){
				 if(select4 == true){
					 mist.setBackgroundResource(R.drawable.wuai);
					 select4 = false;
				 }else {
					 mist.setBackgroundResource(R.drawable.wuaidianji);
					 sun.setBackgroundResource(R.drawable.qingtian);
					 cloudy.setBackgroundResource(R.drawable.yintian);
					 snow.setBackgroundResource(R.drawable.xuetian);
					 rain.setBackgroundResource(R.drawable.leiyu);
					 select4 = true; 
					 select1 = false;
					 select3 = false;
					 select2 = false;
					 select5 = false;
				 }
			 }
				if(v == rain){
					 if(select5 == true){
						 rain.setBackgroundResource(R.drawable.leiyu);
						 select5 = false;
					 }else {
						 rain.setBackgroundResource(R.drawable.leiyudianji);				
						 sun.setBackgroundResource(R.drawable.qingtian);
						 snow.setBackgroundResource(R.drawable.xuetian);
						 mist.setBackgroundResource(R.drawable.wuai);
						 cloudy.setBackgroundResource(R.drawable.yintian);
						 select5 = true; 
						 select2 = false;
						 select3= false;
						 select4= false;
						 select1 = false;
					 }						 
				}
			 if(v == sure){

				 if(select1 == true){
					 wetherindex =0;
				 }
				 if(select2 == true){
					 wetherindex = 1;
				 }
				 if(select3 == true){
					 wetherindex = 2;
				 }
				 if(select4 == true){
					 wetherindex = 3;
				 }
				 if(select5 == true){
					 wetherindex = 4;
				 }
				 Intent data=new Intent();  
				 data.putExtra("wetherindex", wetherindex);  
				 setResult(40, data);  
				   finish(); 
				   saveDate();
			 }
			 if(v == cancle){
				 finish();
			 }
		     
	  }
		private void saveDate() {
			// String ss= paCalendar.getInstance();
			prefs = getSharedPreferences("BirthDate", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("lastwether", wetherindex);
			editor.commit(); 
		}

		private int readDate() {
			SharedPreferences prefs = getSharedPreferences("BirthDate",
					Context.MODE_PRIVATE);
			return prefs.getInt("lastwether", 0x103);
		}
}
