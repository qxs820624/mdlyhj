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
	// �Զ���pop��ͼ ����view��ʾ
	View view;
	PopupWindow pop;
	// ���尴ť
	Button editbnt, sharebnt, deletebnt;
	String[] projection = { MediaStore.Images.Thumbnails._ID };
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// ����
	public static final int PHOTORESOULT = 3;// ���
	
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
		// ��photo������������
		String subject = intent.getStringExtra("zhutifromPhoto");
		//��Myroute����������
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
		// Ϊgv���������
		gv.setAdapter(new ImageAdapter(this, paths));
		// �����Զ����menu����
		initPopupWindow();
		setupViews();
		// ///����gridview ��������ʾһ���Զ���pop��ͼ
		gv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!pop.isShowing()) {
					pop.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM,
							0, 0);
					// �洢ѡ�е�λ��
					longposi = position;
				} else {
					pop.dismiss();
				}
				return true;
			}
		});
		// ///����gridview ����������Galleryview��
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// pop �Զ���view�ر�
				pop.dismiss();
				// ����gv ��ת��galleryview �������� �͵����λ�ô���ȥ
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
				// haomiao�����洢����ʱ��ʱ�� ����������Ϊ��Ƭ������
				String haomiao = String.valueOf(System.currentTimeMillis());
				// ��������ͷ�Ĺ���
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(
						MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory().getPath()
								.toString(), haomiao + ".jpg")));
				// ����activity���ȴ����ؽ��
				startActivityForResult(intent, PHOTOHRAPH);
				if (db == null) {
					db = new PictureDB(Scan.this);
				}
				// �����գ�����Ƭ��·���浽���ݿ⡣
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
				// ����activity���ȴ����ؽ��
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
	// /���صĽ������
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// ����
		if (requestCode == PHOTOHRAPH) {
			//��������²�ѯ���ݿ⣬���������������յ���Ƭ��ʾ����
			paths = db.querry1(zhuti);
			gv.setAdapter(new ImageAdapter(this, paths));
		}
		if (data == null)
			return;

		if (requestCode == 2) {
			Bitmap bm = null;
			// ���ĳ������ContentProvider���ṩ���� ����ͨ��ContentResolver�ӿ�
			ContentResolver resolver = getContentResolver();
			try {
				Uri originalUri = data.getData(); // ���ͼƬ��uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // �Եõ�bitmapͼƬ
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// ����ǻ���û�ѡ���ͼƬ������ֵ
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// �����������ͷ 
				cursor.moveToFirst();
				// ����������ֵ��ȡͼƬ·��
				path = cursor.getString(column_index);
				// /��ѡ��������Ƭ��·�����浽 ���ݿ�
				Toast.makeText(this, ""+music +" df"+ background+ path, 2000).show();
				db.insert(new Picture(zhuti.toString(), null, music, background, path
						.toString(), route,null));
				paths = db.querry1(zhuti);
				gv.setAdapter(new ImageAdapter(this, paths));
			} catch (Exception e) {
				Toast.makeText(this, "���ʧ�ܣ���������ӣ�", 2000).show();
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
	// ///////// Ϊgv �Զ������������������ͼƬ����ʾ��gv��
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
			// ////////// ��ͼƬ��С
			Bitmap bit = po.getBitmap(mImagePath.get(position), 100);
			Bitmap getSmallbit = po.getSmallBitmap(bit);
			imageView.setImageBitmap(getSmallbit);
			imageView.setLayoutParams(new GridView.LayoutParams(180, 181));
			imageView.setBackgroundResource(R.drawable.biankuangbg);
			return imageView;
		}
	}
	private void initPopupWindow() {
		// �����Զ����view
		view = this.getLayoutInflater().inflate(R.layout.menu, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(false);
	}

	private void setupViews() {
		// ��ʼ�� view�������� �������ü���
		editbnt = (Button) view.findViewById(R.id.bianji);
		sharebnt = (Button) view.findViewById(R.id.fenxiang);
		deletebnt = (Button) view.findViewById(R.id.shanchu);
		 //����
		editbnt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ��scanҳ����ת��edit ҳ��
				Intent edit = new Intent();
				// �����ͼƬ��λ�� �� ���������� ����ȥ
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
				// Ϊgv���������
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
				Toast.makeText(Scan.this, "�����ڴ���", 2000).show();		
			}
		});

	}

	// ////////����ͼƬ�ķ�����������ͼƬ��һ����������С������ڴ�����İ취��
	public Bitmap getBitmap(String path, float height) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(path, options); // ��ʱ����bmΪ��
			options.inJustDecodeBounds = false;
			// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
			int be = (int) (options.outHeight / (float) height);
			if (be <= 0)
				be = 1;
			options.inSampleSize = be;
			// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
			bmp = BitmapFactory.decodeFile(path, options);

			return bmp;

		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}
		return null;
	}

	// ////////����ͼƬ�ķ�����������ͼƬ��һ����������С������ڴ�����İ취��
	public Bitmap getBitmapw(String path, float weight) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(path, options); // ��ʱ����bmΪ��
			options.inJustDecodeBounds = false;
			// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
			int be = (int) (options.outWidth / (float) weight);
			if (be <= 0)
				be = 1;
			options.inSampleSize = be;
			// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
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
