package com.duom.activity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class Scan extends Activity{
	private ImageButton  back;
	private GridView gv;
	private TextView tv ;
	private PictureDB db;
	private Photo po ;
	Button takePhoto , album ;
	int longposi;
	private Bitmap bmp;
	
	String route;
	int music; int background;
	public List<String> paths = new ArrayList<String>();
	private String path;
	private String zhuti;
	// 自定义pop视图 ，用view显示
	View view;
	PopupWindow pop;
	// 定义按钮
	Button editbnt, sharebnt, deletebnt;
	String[] projection = { MediaStore.Images.Thumbnails._ID };
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTORESOULT = 3;// 结果
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		
        takePhoto =(Button)findViewById(R.id.photo);
        album =(Button)findViewById(R.id.add);
        back =(ImageButton)findViewById(R.id.back);
        tv = (TextView)findViewById(R.id.tv);
        gv = (GridView)findViewById(R.id.gv);
        
        po = new Photo();
        Intent it = getIntent();
        String subjectFromAdd = it.getStringExtra("zhutiFormAdd");
    	Intent intent = getIntent();
		// 从photo传过来的主题
		String subject = intent.getStringExtra("zhutifromPhoto");
		//从Myroute传过来主题
		String subjectfromMyoute = intent.getStringExtra("zhutifromMyroute");

		if (subject == null && subjectfromMyoute == null) {
			  route = intent.getStringExtra("routefromadd");
			  music = intent.getIntExtra("musicFromAdd", 7);
			  Log.e("00000musicFromAdd", ""+music);
			  background = intent.getIntExtra("fromAddbg", 5);
			  zhuti = subjectFromAdd;

		} else if(subjectFromAdd == null && subjectfromMyoute == null){
			 route = intent.getStringExtra("routeFromPhote");
			 music = intent.getIntExtra("musicFromPhoto", 8);
			 Log.e("00000musicFromAdd", ""+music);
			 background = intent.getIntExtra("bgFromPhoto", 2);
			 zhuti = subject;

		}else if(subjectFromAdd == null && subject == null ){
			 route = intent.getStringExtra("routeFromMyroute");
			 music = intent.getIntExtra("musicFromMyroute", 8);
			 background = intent.getIntExtra("bgFromMyroute", 2);
			zhuti = subjectfromMyoute;
		}
		tv.setText(zhuti.toString());
		if (db == null) {
			db = new PictureDB(this);
		}
		
		paths = db.querry1(zhuti);
		Log.e("000000000000", paths + "");
		// 为gv添加适配器
		gv.setAdapter(new ImageAdapter(this, paths));
		// 加载自定义的menu界面
		initPopupWindow();
		setupViews();
		// ///长按gridview 监听，显示一个自定的pop视图
		gv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!pop.isShowing()) {
					pop.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM,
							0, 0);
					// 存储选中的位置
					longposi = position;
				} else {
					pop.dismiss();
				}
				return true;
			}
		});
		// ///单击gridview 监听，跳到Galleryview。
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// pop 自定义view关闭
				pop.dismiss();
				// 单击gv 跳转到galleryview ，将主题 和点击的位置传过去
				Intent in = new Intent();
				in.putExtra("zhuti1", zhuti.toString());
				in.setClass(Scan.this, Galleryview.class);
				in.putExtra("position", position);
				startActivity(in);
			}
		});
        takePhoto.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				// haomiao用来存储照相时的时间 ，并用它作为相片的名字
				String haomiao = String.valueOf(System.currentTimeMillis());
				// 调用照相头的功能
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(
						MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory().getPath()
								.toString(), haomiao + ".jpg")));
				// 启动activity并等待返回结果
				startActivityForResult(intent, PHOTOHRAPH);
				if (db == null) {
					db = new PictureDB(Scan.this);
				}
				// 拍完照，将相片的路径存到数据库。
				db.insert(new Picture(zhuti.toString(), null, music,
						background, Environment.getExternalStorageDirectory()
								.getPath().toString()
								+ "/" + haomiao + ".jpg", route, null));
			}
		});
        album.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						IMAGE_UNSPECIFIED);
				// 启动activity并等待返回结果
				startActivityForResult(intent, 2);
			}
		});
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(Scan.this, Photo.class);
				startActivity(it);
				finish();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
			Intent it = new Intent();
			it.setClass(Scan.this, Photo.class);
			startActivity(it);
			finish();
		}
		return true;
	}
	// /返回的结果处理
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			//拍完后，重新查询数据库，加载适配器。将照的相片显示出来
			paths = db.querry1(zhuti);
			gv.setAdapter(new ImageAdapter(this, paths));
		}
		if (data == null)
			return;

		if (requestCode == 2) {
			Bitmap bm = null;
			// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
			ContentResolver resolver = getContentResolver();
			try {
				Uri originalUri = data.getData(); // 获得图片的uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// 这个是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				path = cursor.getString(column_index);
				// /把选择的相册相片的路径保存到 数据库
				Toast.makeText(this, ""+music +" df"+ background+ path, 2000).show();
				db.insert(new Picture(zhuti.toString(), null, music, background, path
						.toString(), route,null));
				paths = db.querry1(zhuti);
				gv.setAdapter(new ImageAdapter(this, paths));
			} catch (Exception e) {
				Toast.makeText(this, "添加失败，请重新添加！", 2000).show();
			}
			super.onActivityResult(requestCode, resultCode, data);
		}   
		if(requestCode == 200){
			if(db == null){
				db = new PictureDB(Scan.this);					
			}
		    Picture  sharept = db.getItem(paths.get(longposi));
			String xinqing = sharept.getXinqing();
			String picUrl = sharept.getPath();
		
			String title = sharept.getZhuti();
			String downUrl = "";
      	int positon = data.getIntExtra("positionClick", 200);
//      	switch (positon) {
//			case 0:
//				ShareKaixin001.getInstance(Scan.this).share(
//				title, xinqing ,  null, getString(R.string.app_name), picUrl);
//				break;
//			case 1:
//				ShareRenren.getInstance(Scan.this).shareFeed(
//				title,  xinqing ,  picUrl,
//				getString(R.string.app_name), downUrl);
//				break;
//			case 2:
//				ShareWeibo.getInstance(Scan.this, xinqing  ,
//				picUrl);
//				break;
//			}
        }
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
			final ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// ////////// 将图片缩小
			Bitmap bit = po.getBitmap(mImagePath.get(position), 100);
			Bitmap getSmallbit = po.getSmallBitmap(bit);
			imageView.setImageBitmap(getSmallbit);
			imageView.setLayoutParams(new GridView.LayoutParams(180, 181));
			imageView.setBackgroundResource(R.drawable.biankuangbg);
			return imageView;
		}
	}
	private void initPopupWindow() {
		// 加载自定义的view
		view = this.getLayoutInflater().inflate(R.layout.menu, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(false);
	}

	private void setupViews() {
		// 初始化 view里面的组件 ，并设置监听
		editbnt = (Button) view.findViewById(R.id.bianji);
		sharebnt = (Button) view.findViewById(R.id.fenxiang);
		deletebnt = (Button) view.findViewById(R.id.shanchu);
		 //监听
		editbnt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 从scan页面跳转到edit 页面
				Intent edit = new Intent();
				// 将点击图片的位置 和 所属的主题 传过去
				edit.putExtra("pos", longposi);
				edit.putExtra("zhutitoedit1", zhuti);
//				edit.putExtra("dateToEdit", sstime);
				edit.setClass(Scan.this, Editview.class);
				startActivity(edit);
				pop.dismiss();  
			}
		});
		deletebnt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = paths.get(longposi);
				db.delete(path, zhuti);
				paths = db.querry1(zhuti);
				// 为gv添加适配器
				gv.setAdapter(new ImageAdapter(Scan.this, paths));
				pop.dismiss();
			}
		});
		sharebnt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent it = new Intent();
//				it.setClass(Scan.this, Listview.class);
//				startActivityForResult(it, 200);
				pop.dismiss();
				Toast.makeText(Scan.this, "敬请期待！", 2000).show();		
			}
		});

	}

	// ////////处理图片的方法，，按照图片的一定比例来缩小，解决内存溢出的办法。
	public Bitmap getBitmap(String path, float height) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
			options.inJustDecodeBounds = false;
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = (int) (options.outHeight / (float) height);
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

	// ////////处理图片的方法，，按照图片的一定比例来缩小，解决内存溢出的办法。
	public Bitmap getBitmapw(String path, float weight) {
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
	public Bitmap getgallery(Bitmap bitmap) {
		int nHeight = 120;
		int nWidth = 120;
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
	public Bitmap getlistitem(Bitmap bitmap) {
		int nHeight = 69;
		int nWidth = 69;
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
	public Bitmap getBigBitmapWithWidth(Bitmap bitmap , int nWidth) {
		// int nHeight = 120;
		int width = bitmap.getWidth();
		int heightofpicture = bitmap.getHeight();
		float scaleWidth = ((float) nWidth) / width;
		// float scaleHeight = ((float) nHeight)/heightofpicture;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, width, heightofpicture,
				matrix, true);
		return temp;
	}

	public Bitmap getBigBitmapWithHight(Bitmap bitmap , int nHeight) {
		int width = bitmap.getWidth();
		int heightofpicture = bitmap.getHeight();
		float scaleHeight = ((float) nHeight) / heightofpicture;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleHeight, scaleHeight);
		Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, width, heightofpicture,
				matrix, true);
		return temp;
	}
}
