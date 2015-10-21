package com.duom.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.jingxunnet.cmusic.R;
/*
 *  显示分享列表，用到listview， 和它的适配器。
 * */
public class Listview extends Activity {
	private int[] images;
	private String[] titles ={"开心网","人人网","新浪微博"};
	private List<Map<String , Object>> dataes;
	private final String[] KEYS = {"img" , "title"};
	
	private ListView list;	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview);
		
	    list = (ListView) findViewById(R.id.lv);
			dataes = getData();
			SimpleAdapter adapter = new SimpleAdapter(
					this, 
					dataes, 
					R.layout.sim, 
					KEYS, 
					new int[]{R.id.listitem_img , R.id.listitem_title}
					);
			list.setAdapter(adapter);
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
//                     Intent in = getIntent();
//                     in.putExtra("positionClick", position);
//         			setResult(200, in);
					Toast.makeText(Listview.this, "敬请期待！", 2000).show();
         			finish();
				}
			}) ;
   }
	private List<Map<String , Object>> getData() {
		List<Map<String , Object>> dataes = new ArrayList<Map<String , Object>>();
		images = new int[]{R.drawable.wb_kaixin001 , R.drawable.wb_renren, R.drawable.wb_sina};
		//titles = getResources().getStringArray(R.array.name);
		
		for(int i = 0 ; i < images.length;i++) {
			Map<String , Object> map = new HashMap<String, Object>();
			map.put(KEYS[0], images[i]);
			map.put(KEYS[1], titles[i]);	
			dataes.add(map);
		}return dataes;
             
    }
}
