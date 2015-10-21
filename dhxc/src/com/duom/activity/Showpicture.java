package com.duom.activity;

import java.util.ArrayList;
import java.util.List;

import cn.duome.dkymarket.entity.AsyncImageLoader;
import cn.duome.dkymarket.entity.Commentitem;
import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class Showpicture extends Activity {
	// 显示图片
	ImageView iv;
	Gallery gv;
	LinearLayout ll;
	ImageButton back;
	Scan sc;
	Task1Activity tt ;
	// 存放图片的路径
	public List<String> paths = new ArrayList<String>();
	// 背景图片资源
	public int imageids[] = { R.drawable.muwenda, R.drawable.qiutianda,
			R.drawable.woniuda, R.drawable.katongda };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showpicture);
		
		sc = new Scan();
        tt = new Task1Activity() ;
		paths = Comment.ls;
		//接收 跳转过来时，传的值	
		Intent it = getIntent();
		String title = it.getStringExtra("title");
		String route = it.getStringExtra("routetoshowpic");
		int music = it.getIntExtra("music", 12)-1;
		int bg = it.getIntExtra("bg", 13);
		int positionfromcomment = it.getIntExtra("position", 14);
		//初始化控件
		iv = (ImageView)findViewById(R.id.showpic);
		gv =(Gallery)findViewById(R.id.galleryshowpic);
		ll = (LinearLayout)findViewById(R.id.splayout);
		back = (ImageButton)findViewById(R.id.backshowpic);
		//设置背景
		ll.setBackgroundResource(imageids[bg]);

		
		// 添加适配�?
		gv.setAdapter(new ImageAdapter(this));
		// // gallery 实现监听
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				posi = position;
				// Bitmap bit = BitmapFactory.decodeFile(path.get(position));
				//Bitmap bit = sc.getBitmap(path.get(position), 200);
				Bitmap bit ;
				String url =paths.get(position);
				bit = tt.makeBitmap(url);	
                if(bit.getWidth()>bit.getHeight()){
                	bit =sc.getBigBitmapWithWidth(bit , 460);
                }else if (bit.getHeight()>bit.getWidth()){
                	bit = sc.getBigBitmapWithHight(bit , 430);
                }
				
				iv.setPadding(5, 5, 5, 5);	
				iv.setImageBitmap(bit);	
			}
		});
	// 相册数组的大小大于零�?�?显示传过来位置的那张图片
				if (paths != null && paths.size() > 0) {
					Bitmap bit ;
					
					bit = tt.makeBitmap(paths.get(positionfromcomment));
	                if(bit.getWidth()>bit.getHeight()){
	                	//bit = sc.getBitmap(path.get(forScan), 200);
	                	bit =sc.getBigBitmapWithWidth(bit , 460);
	                }else if (bit.getHeight()>bit.getWidth()) {
	                	//bit = sc.getBitmapw(path.get(forScan), 220);
	                	bit = sc.getBigBitmapWithHight(bit , 430);
	                }
					iv.setImageBitmap(bit);		
				}
				
		 if(music>=0){
			 Intent musicser = new Intent();
			 musicser.setAction("com.duom.daohangyinyue");
			 musicser.putExtra("puanduan", 101);
			 musicser.putExtra("index",music);
			 startService(musicser);
			 }
		 back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent musicser = new Intent();
				 musicser.setAction("com.duom.daohangyinyue");
				 musicser.putExtra("puanduan", 102);				
				 startService(musicser);				 
				 finish();
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent musicser = new Intent();
			musicser.setAction("com.duom.daohangyinyue");
			musicser.putExtra("puanduan", 102);
			startService(musicser);
			finish();
		}
		return true;
	}
	
	// 为gallery 创建适配器
	class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		// 返回items的个数，数据源中数据的个数
		public int getCount() {
			// TODO Auto-generated method stub
			 return Comment.ls.size();
//			return Integer.MAX_VALUE;
		}

		// 得到一个item
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// 得到item对应控件的id
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// 得到一个Item对应的控件 : ImageView
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView view = new ImageView(context);
//			 view.setImageBitmap(ls.get(position));
			AsyncImageLoader imageLoader = new AsyncImageLoader();
			Drawable cachedDrawable = imageLoader.loadDrawable(Comment.ls.get(position), 
					view, new AsyncImageLoader.ImageDownloadCallBack() {
						@Override
						public void imageLoaded(String imgURL,
								Drawable downloadedDrawable, ImageView imageView) {
							// TODO Auto-generated method stub
							if (downloadedDrawable != null) {
								BitmapDrawable bd = (BitmapDrawable)downloadedDrawable;
								Bitmap bm = bd.getBitmap();
								Bitmap bit = sc.getgallery(bm);
								imageView.setImageBitmap(bit);
//								imageView.setImageDrawable(downloadedDrawable);
							}
						}
					});
	      //说明此图片已被下载并缓存   
          if(cachedDrawable != null){ 
      		 BitmapDrawable bd = (BitmapDrawable)cachedDrawable;
				Bitmap bm = bd.getBitmap();
				Bitmap bit = sc.getgallery(bm);
				view.setImageBitmap(bit);
//        	 view.setImageDrawable(cachedDrawable); 
    	   }
			// 设置图片的缩放或者拉伸模式
			view.setScaleType(ScaleType.FIT_CENTER);
			view.setPadding(5, 5, 5, 5);
			view.setBackgroundColor(Color.parseColor("#FFFFFF"));
			return view;
		}
	}		

}
