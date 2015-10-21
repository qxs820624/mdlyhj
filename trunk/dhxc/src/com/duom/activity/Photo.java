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
	//��ʾ���
	private GridView gd;
	// ��������paths ���洢�����ݿ�ȡ��������Ϣ
	private List<String> zhutis = new ArrayList<String>();
	private List<String> paths = new ArrayList<String>();
	List<String> photos ; 
	//picture ����
	Picture pict;
	//
	LinearLayout llphoto ;
	private ProgressDialog pd ;
	//�ϴ�����
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
       //��ʼ��
		i = 0;
		tul = new TestUpload(false);
		gd = (GridView) findViewById(R.id.gd);
		back =(ImageButton)findViewById(R.id.xiangceback);
		llphoto = (LinearLayout)findViewById(R.id.llphoto);
		
		//��gridview ���е������
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
		//��gridview ���г�������
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
		// �����Զ����menu����
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
		//ɨ�����е�����
		zhutis = db.qurryAllSubject();
		//���û�д������� ����ʾ��û�д���
		if (zhutis.size() == 0) {
			llphoto.setVisibility(View.VISIBLE);
		} else {
			//��������⣬ѭ��ȡ������
			for (int i = 0; i < zhutis.size(); i++) {
				paths = db.querry1(zhutis.get(i));
				//��ÿ����������ĵ�һ����Ƭ ·���浽����photos�У�
				if(paths.size() != 0){
                photos.add(paths.get(0));
				}
				//��������û����Ƭ�������һ�� ��nohave��,����ǣ������û��ͼƬ
				else {
					  photos.add("nothave");
				}
			}
		}
		//����������
		gd.setAdapter(new ImageAdapter(this, photos));
		super.onResume();
	}
	// ////////����ͼƬ�ķ�����������ͼƬ��һ����������С������ڴ�����İ취��
	public Bitmap getBitmap(String path, float weight) {
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
   //����ͼƬ
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
			// ////////// ��ͼƬ��С
			Bitmap bit = null;
			//���ȡ���ı���ǡ�nohave�� �� ����Ĭ��ͼƬ
			 if(mImagePath.get(position).equals("nothave")){
				 Resources res=getResources(); 
				 bit=BitmapFactory.decodeResource(res, R.drawable.xiangcetu); 
			 }else{
				 //�����Ķ�����ͼƬ����ᣬ�õ�ͼƬ
			     bit = getBitmap(mImagePath.get(position), 100);
			     posi = position;
			 }
			 //����
			 getSmallbit = getSmallBitmap(bit);
			//�Զ����View �� ��ʵ������������ʾЧ��
			View view = new View(getApplicationContext());
			view = getLayoutInflater().inflate(R.layout.xiangceitem, null);
			TextView tv =(TextView)view.findViewById(R.id.xiangcetv);
			//��ʼ���ؼ�
			
			shangchuan = (Button)view.findViewById(R.id.shangchuanxiangce);
			shangchuan.setBackgroundResource(R.drawable.shangchuan1);
			shangchuan.setTag(i);
			i++;
//			vb.add(shangchuan);
			shangchuan.setText("�ϴ����");
			iv = (ImageView)view.findViewById(R.id.xiangceiv);
		   //�����ϴ���ť
			shangchuan.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					if(Task1Activity.layuser.getVisibility() == View.GONE){
						Toast.makeText(Photo.this, "��¼��ſ����ϴ�Ŷ��", 2000).show();
						return;
					}
					pd = new ProgressDialog(Photo.this);
					pd.setMessage("�����ϴ�������");
					pd.show();
					int  tag = (Integer)v.getTag()-1;
					tags.add(tag);
					pt = db.selectOne(zhutis.get(tag));
					Bitmap bitmap1 = getBitmap(mImagePath.get(tag), 100);

						String route1 = pt.getRoute();
						
					String url = "http://user.hutudan.com/car_map/uploadroute.php?flg=json";
					StringBuffer data = new StringBuffer();
					//�õ�ͼƬ��Դ������Ϊ�ļ�
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					bitmap1.compress(CompressFormat.PNG, 100, os);
					byte[] bytes = os.toByteArray();
					//��Ϣ��Դ
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
							tip = "�ϴ��ɹ�!";
							p = 1;	
							MostNew.isfresh = true ;
							v.setBackgroundResource(R.drawable.buttonpress);
							((Button) v).setText("���ϴ�");
							v.setClickable(false);							
						} else {
							tip = "�ϴ�����ʧ��,������!";
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
							shangchuan.setText("���ϴ�");
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
		// �����Զ����view
		view = this.getLayoutInflater().inflate(R.layout.xiangmumenu, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
	}
	private void setupViews() {
	    // ��ʼ�� view�������� �������ü���
		edit = (Button) view.findViewById(R.id.editxiangce);
		delete = (Button) view.findViewById(R.id.deletexiangce);
		cancle =(Button)view.findViewById(R.id.canclexiangce);
		// //����
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
		//////////ɾ����ᡣ����
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
					//ɨ�����е�����
					zhutis = db.qurryAllSubject();
					if (zhutis.size() == 0) {
						Toast.makeText(Photo.this, "�ף���û�д������Ŷ��", 2000).show();
					} else {
						//��������⣬ѭ��ȡ������
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
