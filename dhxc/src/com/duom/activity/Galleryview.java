package com.duom.activity;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class Galleryview extends Activity {
	// 定义组件
	private TextView tv;
	private Gallery gallery;
	private PictureDB db;
	private ImageButton back, editsmall;
	private ImageView iv;
	private Button setmusic;
	private TextView wether;
	boolean select = false;
	// 创建类对象，用来调用该类的方�?
	Picture pt;
	// 存储从Scan传过来的主题
	String fromScanSubject = null;
	String fromScanDate = null;
	// 存储从add传过来的主题
	String fromAddSubject = null;
	// 存储从editview传过来的主题
	String fromEditview = null;
	String pathfromedit = null;
	// public static Cursor cursor;
	private LinearLayout layout;
	// 存储心情
	String texthumor;
	// /存储切换背景的数�?
	public int imageids[] = { R.drawable.muwenda, R.drawable.qiutianda,
			R.drawable.woniuda, R.drawable.katongda};
//	// 存储切换歌曲的数�?
//	public int musics[] = {R.raw.a , R.raw.b,R.raw.c ,R.raw.d};	
	// 初始化scan 类的对象
	Scan sc = new Scan();
	// /初始�?path数组，存储从数据库取出的信息
	public List<String> path = new ArrayList<String>();
	// 存储从Scan传过来的位置
	int forScan;
	int posi , fromedittogallery;
	// 存储当前相册主题和音乐的索引
	int music, bg;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.galleryview);
		// /初始化组�?
		layout = (LinearLayout) findViewById(R.id.layout);
		tv = (TextView) findViewById(R.id.xinqing);
		gallery = (Gallery) findViewById(R.id.gallery);
		back = (ImageButton) findViewById(R.id.backliulan);
		editsmall = (ImageButton) findViewById(R.id.editsmall);
		iv = (ImageView) findViewById(R.id.showimg);
		setmusic =(Button) findViewById(R.id.setmusic);
		wether = (TextView) findViewById(R.id.wether);
		
		setmusic.setBackgroundResource(R.drawable.play);

		
		// /点击gridview 传过position ,imageview 显示的图�?
		Intent in = getIntent();
		forScan = in.getIntExtra("position", 0);
		fromScanSubject = in.getStringExtra("zhuti1");
		fromScanDate = in.getStringExtra("fromScanDate");
		// fromAddSubject = in.getStringExtra("zhuti");
		fromEditview = in.getStringExtra("zhutifromedit");
		pathfromedit = in.getStringExtra("pathOfEdit");
		fromedittogallery = in.getIntExtra("fromEditTogallery", 112);
		// 根据 KEY_ZHUTI 获取查询游标
		if (db == null) {
			db = new PictureDB(this);
		}
		// 从Scan跳转过来 得到的主�?
		if (fromEditview == null) {
			// 根据主题查询数据库，得到�?��Picture 对象
			pt = db.selectOne(fromScanSubject);
			// 根据主题查询数据库，获取�?��该主题下的图片路�?
			path = db.querry1(fromScanSubject.toString());
			// 相册数组的大小大于零�?�?显示传过来位置的那张图片
			if (path != null && path.size() > 0) {
				Bitmap bit ;
				bit = sc.getBitmap(path.get(forScan), 220);
                if(bit.getWidth()>bit.getHeight()){
                	//bit = sc.getBitmap(path.get(forScan), 200);
                	bit =sc.getBigBitmapWithWidth(bit , 460);
                }else if (bit.getHeight()>bit.getWidth()) {
                	//bit = sc.getBitmapw(path.get(forScan), 220);
                	bit = sc.getBigBitmapWithHight(bit , 430);
                }
				iv.setImageBitmap(bit);						
									
				pt = db.getItem(fromScanSubject, path.get(forScan));
				texthumor = pt.getXinqing();
				if(texthumor == null){
				tv.setText("亲，您现在可以通过编辑，来添加你照相时的心情哦。。。");
				}else{
				tv.setText(texthumor);
				}				
				 int music = pt.getMusic()-1;					
				 if(music>=0){
				 Intent musicser = new Intent();
				 musicser.setAction("com.duom.daohangyinyue");
				 musicser.putExtra("puanduan", 101);
				 musicser.putExtra("index",music);
				 startService(musicser);
				 }
			}
		} else if (fromScanSubject == null) {
			pt = db.getItem(fromEditview, pathfromedit);
			path = db.querry1(fromEditview.toString());
			if (path != null && path.size() > 0) {
			//	Bitmap bit = sc.getBitmap(pathfromedit, 200);
				Bitmap bit ;
				bit = sc.getBitmap(pathfromedit, 220);
                if(bit.getWidth()>bit.getHeight()){
                	bit =sc.getBigBitmapWithWidth(bit ,460);
                }else if (bit.getHeight()>bit.getWidth()){
                	bit = sc.getBigBitmapWithHight(bit , 430);
                }
            	iv.setBackgroundResource(R.drawable.xiaokuang);
				iv.setPadding(10, 10, 10, 10);	
				iv.setImageBitmap(bit);	
				
				texthumor = pt.getXinqing();
				if(texthumor == null){
					tv.setText("亲，您现在可以通过编辑，来添加你照相时的心情哦。。。");
				}else{
				tv.setText(texthumor);
				}	
				
				 int music = pt.getMusic()-1;		
				 if(music >=0){
				 Intent musicser = new Intent();
				 musicser.setAction("com.duom.daohangyinyue");
				 musicser.putExtra("puanduan", 101);
				 musicser.putExtra("index",music);		
				 startService(musicser);
				 }
				Toast.makeText(this, ""+music, 2000).show();
			} else {
				Toast.makeText(this, "bianjidfefd", 2000).show();
			}
		}
		// 根据得到的对象，获取它的背景索引
		int bg = pt.getBg();
		// 将北京设置为该索引的那张图片	 
		layout.setBackgroundResource(imageids[bg]);		 				 
		// 添加适配�?
		gallery.setAdapter(new ImageAdapter(this, path));
		if(fromEditview == null){
		gallery.setSelection(forScan);
		}else{
			gallery.setSelection(fromedittogallery);
		}
	
		// // gallery 实现监听
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(Galleryview.this, position + "", 2000).show();
				posi = position;
				// Bitmap bit = BitmapFactory.decodeFile(path.get(position));
				//Bitmap bit = sc.getBitmap(path.get(position), 200);
				Bitmap bit ;
				bit = sc.getBitmap(path.get(position),220);				
                if(bit.getWidth()>bit.getHeight()){
                	bit =sc.getBigBitmapWithWidth(bit , 460);
                }else if (bit.getHeight()>bit.getWidth()){
                	bit = sc.getBigBitmapWithHight(bit , 430);
                }				
				iv.setPadding(5, 5, 5, 5);	
				iv.setImageBitmap(bit);	
				
				String p = path.get(position);
				Picture pi = db.getItem(p);
				String gettexthumor = pi.getXinqing();
				if (gettexthumor == null) {
					tv.setText("亲，您现在可以通过编辑，来添加你照相时的心情哦。。。");
				} else {
					tv.setText(gettexthumor);
				}
			}
		});
		// 音乐按钮监听
		setmusic.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(select == false ){
					setmusic.setBackgroundResource(R.drawable.display);
					select = true;
					 Intent musicser = new Intent();
					 musicser.setAction("com.duom.daohangyinyue");
					 musicser.putExtra("puanduan", 103);				
					 startService(musicser);
				}else if(select == true){
					setmusic.setBackgroundResource(R.drawable.play);
					select = false;
					 Intent musicser = new Intent();
					 musicser.setAction("com.duom.daohangyinyue");
					 musicser.putExtra("puanduan", 104);				
					 startService(musicser);
				}
			}
		});
		// back 监听
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
							 
				 Intent musicser = new Intent();
				 musicser.setAction("com.duom.daohangyinyue");
				 musicser.putExtra("puanduan", 102);				
				 startService(musicser);				 
				 finish();
			}
		});
		// 编辑 监听
		editsmall.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.putExtra("po", posi);

				if (fromEditview == null) {
					intent.putExtra("zhutitoedit", fromScanSubject);
					intent.putExtra("position", forScan);
					
				}else if(fromScanSubject == null){
					intent.putExtra("zhutitoedit", fromEditview);
					intent.putExtra("position", fromedittogallery);
				}
				intent.setClass(Galleryview.this, Editview.class);
				startActivity(intent);
			
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

	// /适配�?
	private final class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<String> mImagePath;
		public ImageAdapter(Context context, List<String> imagePath) {
			this.mContext = context;
			this.mImagePath = imagePath;
		}
		public int getCount() {
			return mImagePath.size();
		}
		public Object getItem(int position) {
			return position;
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setPadding(5, 5, 5, 5);
			Bitmap bit = sc.getBitmap(mImagePath.get(position), 100);			
			Bitmap getSmallbit = sc.getgallery(bit);
			imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
			//imageView.setLayoutParams( new Gallery.LayoutParams(154, 154));
			imageView.setImageBitmap(getSmallbit);
			return imageView;
		}
	}

}
