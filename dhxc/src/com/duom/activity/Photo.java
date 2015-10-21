package com.duom.activity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.R.array;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class Photo extends Activity {
	private PictureDB db;
	//显示相册
	private GridView gd;
	// 定义数组paths 来存储从数据库取出来的信息
	private List<String> zhutis = new ArrayList<String>();
	private List<String> paths = new ArrayList<String>();
	List<String> photos ; 
	//picture 对象
	Picture pict;
	//
	LinearLayout llphoto ;
	private ProgressDialog pd ;
	//上传数据
	Picture pt;
	String route;
	TestUpload tul;
	View view;
	PopupWindow pop;
	Button edit , delete , cancle ,shangchuan;
	ImageView iv;
	int bg  , music; 
	String subject;
	ImageButton back ;
	Bitmap getSmallbit;
	private Bitmap bmp;
	List<Button> vb = new ArrayList<Button>();
	int i = 0 , posi;
	
	static ArrayList<Integer> tags = new ArrayList<Integer>();
	int tag;
	 int p = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo);
       //初始化
		i = 0;
		tul = new TestUpload(false);
		gd = (GridView) findViewById(R.id.gd);
		back =(ImageButton)findViewById(R.id.xiangceback);
		llphoto = (LinearLayout)findViewById(R.id.llphoto);
		
		//对gridview 进行点击监听
		gd.setOnItemClickListener(new OnItemClickListener() {   
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(db == null){
					db = new PictureDB(Photo.this);
				}
				Picture pt =db.selectOne(zhutis.get(position));
				int music = pt.getMusic();
				int bg = pt.getBg();
				String route = pt.getRoute();
				Intent it = new Intent();
				it.putExtra("zhutifromPhoto", zhutis.get(position));
				it.putExtra("musicFromPhoto", music);
				it.putExtra("bgFromPhoto", bg);
				it.putExtra("routeFromPhote",route);
				it.setClass(Photo.this, Scan.class);
				startActivity(it);
				finish();
			}						
		});
		//对gridview 进行长按监听
		gd.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//
				pict =db.selectOne(zhutis.get(position));
				if (!pop.isShowing()) {
					pop.showAtLocation(findViewById(R.id.mainlayout), Gravity.BOTTOM,
							0, 0);
				} else {
					pop.dismiss();
				}
				return true;
			}
		});	
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// 加载自定义的menu界面
		initPopupWindow();
		setupViews();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (db == null) {
			db = new PictureDB(this);
		}
		photos = new ArrayList<String>();
		//扫描所有的主题
		zhutis = db.qurryAllSubject();
		//如果没有创建主题 ，提示还没有创建
		if (zhutis.size() == 0) {
			llphoto.setVisibility(View.VISIBLE);
		} else {
			//如果有主题，循环取出主题
			for (int i = 0; i < zhutis.size(); i++) {
				paths = db.querry1(zhutis.get(i));
				//将每个主题的相册的第一张相片 路径存到集合photos中，
				if(paths.size() != 0){
                photos.add(paths.get(0));
				}
				//如果该相册没有相片，就添加一个 “nohave”,来标记，此相册没有图片
				else {
					  photos.add("nothave");
				}
			}
		}
		//加载适配器
		gd.setAdapter(new ImageAdapter(this, photos));
		super.onResume();
	}
	// ////////处理图片的方法，，按照图片的一定比例来缩小，解决内存溢出的办法。
	public Bitmap getBitmap(String path, float weight) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
			options.inJustDecodeBounds = false;
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = (int) (options.outWidth / (float) weight);
			if (be <= 0)
				be = 1;
			options.inSampleSize = be;
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bmp = BitmapFactory.decodeFile(path, options);
			return bmp;
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}
		return null;
	}
   //缩放图片
	public Bitmap getSmallBitmap(Bitmap bitmap) {
		int nHeight = 154;
		int nWidth = 154;
		int width = bitmap.getWidth();
		int heightofpicture = bitmap.getHeight();
		float scaleWidth = ((float) nWidth) / width;
		float scaleHeight = ((float) nHeight) / heightofpicture;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, width, heightofpicture,
				matrix, true);
		return temp;
	}

	// ///////// 为gv 自定义的适配器，来加载图片，显示在gv上
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
			// ////////// 将图片缩小
			Bitmap bit = null;
			//如果取到的标记是“nohave” ， 设置默认图片
			 if(mImagePath.get(position).equals("nothave")){
				 Resources res=getResources(); 
				 bit=BitmapFactory.decodeResource(res, R.drawable.xiangcetu); 
			 }else{
				 //其它的都是有图片的相册，得到图片
			     bit = getBitmap(mImagePath.get(position), 100);
			     posi = position;
			 }
			 //缩放
			 getSmallbit = getSmallBitmap(bit);
			//自定义的View ， 来实现特殊的相册显示效果
			View view = new View(getApplicationContext());
			view = getLayoutInflater().inflate(R.layout.xiangceitem, null);
			TextView tv =(TextView)view.findViewById(R.id.xiangcetv);
			//初始化控件
			
			shangchuan = (Button)view.findViewById(R.id.shangchuanxiangce);
			shangchuan.setBackgroundResource(R.drawable.shangchuan1);
			shangchuan.setTag(i);
			i++;
//			vb.add(shangchuan);
			shangchuan.setText("上传相册");
			iv = (ImageView)view.findViewById(R.id.xiangceiv);
		   //监听上传按钮
			shangchuan.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					if(Task1Activity.layuser.getVisibility() == View.GONE){
						Toast.makeText(Photo.this, "登录后才可以上传哦！", 2000).show();
						return;
					}
					pd = new ProgressDialog(Photo.this);
					pd.setMessage("正在上传。。。");
					pd.show();
					int  tag = (Integer)v.getTag()-1;
					tags.add(tag);
					pt = db.selectOne(zhutis.get(tag));
					Bitmap bitmap1 = getBitmap(mImagePath.get(tag), 100);

						String route1 = pt.getRoute();
						
					String url = "http://user.hutudan.com/car_map/uploadroute.php?flg=json";
					StringBuffer data = new StringBuffer();
					//得到图片资源，保存为文件
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					bitmap1.compress(CompressFormat.PNG, 100, os);
					byte[] bytes = os.toByteArray();
					//信息资源
					String rt[] = route1.split("--");
					String start = rt[0];
					String end = rt[1];
					
					data.append("uid=");
					data.append(Login.uid);
					
					data.append("&baddress=");
					data.append(URLEncoder.encode(start.toString()));
					
					data.append("&eaddress=");
					data.append(URLEncoder.encode(end.toString()));
					
					data.append("&bgpic=");
					data.append(pt.getBg());
					
					data.append("&music=");
					data.append(pt.getMusic());
					
					data.append("&text=");
					data.append(URLEncoder.encode(route.toString()));
					
					
					data.append("&address=");
					data.append(URLEncoder.encode(pt.getDizhi()));
					
					data.append("&title=");
					data.append(URLEncoder.encode(pt.getZhuti()));
									
				    Info in = new Info();
					File file = in.byteToFile(bytes);	
					byte [] bt =tul.upload(url,data.toString(), file.getAbsolutePath().toString());
					String ss = new String(bt);
		
					String tip = null;
					try {					
						JSONObject json = new JSONObject(ss);
						pd.cancel();
						int ret = json.optInt("ret", -1);
						if (ret == 1) {								
							tip = "上传成功!";
							p = 1;	
							MostNew.isfresh = true ;
							v.setBackgroundResource(R.drawable.buttonpress);
							((Button) v).setText("已上传");
							v.setClickable(false);							
						} else {
							tip = "上传请求失败,请重试!";
						}
					} catch (Exception ex) {
						tip = ex.getMessage();
						ex.printStackTrace();
					}
					if (!TextUtils.isEmpty(tip))
						Toast.makeText(Photo.this, tip, Toast.LENGTH_SHORT)
								.show();
				}    
			});
				for(int i = 0 ; i<tags.size() ; i++){
					 if((Integer)shangchuan.getTag()== tags.get(i)+1){
							shangchuan.setBackgroundResource(R.drawable.buttonpress);
							shangchuan.setText("已上传");
							shangchuan.setClickable(false);
					 }
				}
			iv.setImageBitmap(getSmallbit);
			if(db == null){
				db = new PictureDB(Photo.this);
			}
		 route = db.getRoute(mImagePath.get(position));
			tv.setText(route);
			return view;
		}
	}
	private void initPopupWindow() {
		// 加载自定义的view
		view = this.getLayoutInflater().inflate(R.layout.xiangmumenu, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
	}
	private void setupViews() {
	    // 初始化 view里面的组件 ，并设置监听
		edit = (Button) view.findViewById(R.id.editxiangce);
		delete = (Button) view.findViewById(R.id.deletexiangce);
		cancle =(Button)view.findViewById(R.id.canclexiangce);
		// //监听
		edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
									
				bg = pict.getBg();
				music = pict.getMusic();
				subject = pict.getZhuti();
				
				Intent it = new Intent();
				it.putExtra("background", bg);
				it.putExtra("musicFromXiangmu", music);
				it.putExtra("zhuti", subject.toString());
				it.setClass(Photo.this, Add.class);
				startActivity(it);
				finish();

			   pop.dismiss();	
			}
		});
		//////////删除相册。。。
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if (db == null) {
					db = new PictureDB(Photo.this);
				}
					String zt = pict.getZhuti();
					String route = pict.getRoute();
					db.deleteAll(zt.toString() , route );	
					
					photos = new ArrayList<String>();
					//扫描所有的主题
					zhutis = db.qurryAllSubject();
					if (zhutis.size() == 0) {
						Toast.makeText(Photo.this, "亲，还没有创建相册哦！", 2000).show();
					} else {
						//如果有主题，循环取出主题
						for (int i = 0; i < zhutis.size(); i++) {
							paths = db.querry1(zhutis.get(i));
							if(paths.size() != 0){
			                photos.add(paths.get(0));
							}else {
								  photos.add("nothave");
							}
						}
					}
					gd.setAdapter(new ImageAdapter(Photo.this, photos));	
				    pop.dismiss();	
			}  				
		});
		cancle.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
			}
		});
	}
}
