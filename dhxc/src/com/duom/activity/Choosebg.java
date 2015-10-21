package com.duom.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.jingxunnet.cmusic.R;

public class Choosebg extends Activity{
	private GridView gvchoose;
	int i;
	boolean select = false;
	int image[] = {  R.drawable.muwen,R.drawable.qiutian, R.drawable.woniu, R.drawable.katong};
	 public void onCreate(Bundle savedInstanceState) {
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.choosebg);  
	     
	     gvchoose = (GridView)findViewById(R.id.gvchoose);
	     gvchoose.setAdapter(new Imageadapter(this));
	     
	    
	   
	        gvchoose.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
					 i = position;
					
					 Intent data=new Intent();  
					 data.putExtra("index", i);  

					setResult(20, data);  
	
					   finish();   
				}
			});
	        	  
	 }
	 class Imageadapter extends BaseAdapter{
	    	private   Activity activity;  ;
	    	LayoutInflater inflater;
	    	
	    	public Imageadapter(Activity a){
	    		  activity = a;  
	    		  inflater = LayoutInflater.from(activity); 
	    	}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return image.length;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView iv =new ImageView(activity);
				iv.setImageResource(image[position%image.length]);				
				iv.setLayoutParams( new GridView.LayoutParams(100, 150));
				iv.setScaleType(ScaleType.FIT_CENTER);
				return iv;
				//自定义边框�?、点击后换边�?
//				 ImageView im = new ImageView(activity);  
//				          if (convertView == null) {  
//				              convertView = inflater.inflate(R.layout.item_grid, null);  
//				              im = (ImageView) convertView.findViewById(R.id.iv_image);  		         	
//				        	
//				               convertView.setTag(im);  		                
//				            }else{  
//				                im = (ImageView) convertView.getTag();  
//				            }  
//				           im.setImageResource(image[position]); 
//				           im.setScaleType(ScaleType.FIT_CENTER);
//				            return convertView;  

			}
	    	
	    }
}