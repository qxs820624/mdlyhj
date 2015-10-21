package com.duom.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class Editview extends Activity {
	// /定义组件
	private ImageView iv;
	private EditText et;
	private ImageButton back, save;
	private Button turnleft, turnright, cut, share;
	// /存储跳转过来的主�?
	String subjectFromGallery, subjectFromScan;
	Bitmap bit;
	// 存储跳转过来，带的位�?
	int posit, i;
	public PictureDB db;
	public String picturePath;
	Picture pt;
	File file;
	Picture sharept;
	boolean isturn = false;
	// 扫描数据库，得到该主题下的图片路径的集合
	public List<String> path = new ArrayList<String>();
	
	Bitmap resizedBitmap;
	
	public FileOutputStream  m_fileOutPutStream ; 
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editview);
		// 初始化控�?
		iv = (ImageView) findViewById(R.id.editshow);
		back = (ImageButton) findViewById(R.id.backbianji);
		save = (ImageButton) findViewById(R.id.savesmall);
		turnleft = (Button) findViewById(R.id.zuo);
		turnright = (Button) findViewById(R.id.you);
		cut = (Button) findViewById(R.id.jianqie);
		share = (Button) findViewById(R.id.fenxiang);
		et = (EditText) findViewById(R.id.etxinqing);
		try { 
		    m_fileOutPutStream = new FileOutputStream(path.get(posit));
		} catch (Exception e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 

		// 初始化Matrix类对象，用来处理图片的旋转问�?
		final Matrix matrix = new Matrix();
		Intent in = getIntent();
		// 从gallery 跳转过来，带的位�?
		    i = in.getIntExtra("po", 4);
		if(i == 0){
			i = in.getIntExtra("position", 111);
		}
		subjectFromGallery = in.getStringExtra("zhutitoedit");
		// 从gridview 跳转过来，带的位�?
		posit = in.getIntExtra("pos", 1);
		subjectFromScan = in.getStringExtra("zhutitoedit1");
		if (db == null) {
			db = new PictureDB(this);
		}
		// 查询数据库，获取主题下的�?��图片路径
		Scan sc = new Scan();
		if (subjectFromGallery == null) {
			path = db.querry1(subjectFromScan.toString());
			bit = sc.getBitmap(path.get(posit), 220);
			if(bit.getWidth()>bit.getHeight()){
            	bit =sc.getBigBitmapWithWidth(bit , 460);
            }else {
            	bit = sc.getBigBitmapWithHight(bit , 430);
			}
		} else {
			path = db.querry1(subjectFromGallery.toString());
			//bit = sc.getBitmap(path.get(i), 200);
			bit = sc.getBitmap(path.get(i), 220);
			if(bit.getWidth()>bit.getHeight()){
            	bit =sc.getBigBitmapWithWidth(bit , 460);
            }else if(bit.getWidth()<bit.getHeight()){
            	bit = sc.getBigBitmapWithHight(bit , 430);
			}
		}	
		iv.setImageBitmap(bit);	
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (db == null) {
					db = new PictureDB(Editview.this);
				}
				if (subjectFromGallery == null) {
					picturePath = path.get(posit);
					 pt = db.getItem(subjectFromScan , picturePath);
				} else {
					picturePath = path.get(i);
					 pt = db.getItem(subjectFromGallery ,picturePath);
				}
				if(isturn == true){
				try {
					saveBitmap(resizedBitmap);
				} catch (Exception e) {				
					e.printStackTrace();
				}
				   }				
				String subjectOfEdit = pt.getZhuti();
				if (et.getText().toString() == null
						|| et.getText().toString().trim().length() <= 0) {
					pt.setXinqing("��ܰ��ʾ���ף�����û�б༭Ŷ����");
				} else {
					pt.setXinqing(et.getText().toString());
				}
				db.update(pt , picturePath);
                String pathfromedit = pt.getPath();
				Intent it = new Intent();
				it.putExtra("zhutifromedit", subjectOfEdit);
				it.putExtra("pathOfEdit", pathfromedit);
				if (subjectFromGallery == null) {
					it.putExtra("fromEditTogallery", posit);
				} else {
					it.putExtra("fromEditTogallery", i);
				}				
				it.setClass(Editview.this, Galleryview.class);
				startActivity(it);
				finish();
			}
		});
		turnright.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// /图片向右旋转90
				matrix.postRotate(90);
				 resizedBitmap = Bitmap.createBitmap(bit, 0, 0,
						bit.getWidth(), bit.getHeight(), matrix, true);
				iv.setImageBitmap(resizedBitmap);
				isturn = true;			
			}
		});
		turnleft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// /向左旋转90
				matrix.postRotate(-90);
				 resizedBitmap = Bitmap.createBitmap(bit, 0, 0,
				bit.getWidth(), bit.getHeight(), matrix, true);
				iv.setImageBitmap(resizedBitmap);				
				isturn = true;	
			}
		});
		cut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.android.camera.action.CROP");
				//intent.setDataAndType(Uri.fromFile(tempFile), "image/*");//设置要裁剪的图片
				file = new File(path.get(i));
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				intent.putExtra("crop", "true");
				intent.putExtra("output",Uri.fromFile(file));//保存到原文件
				intent.putExtra("outputFormat", "JPEG");// 返回格式
				startActivityForResult(intent, 1);
			}
		});
		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//        if(et.getText() == null || et.getText().toString().trim().length()==0){
//        	Toast.makeText(Editview.this, "������Ҫ���������飡", 2000).show();
//        	et.setFocusable(true);
//        	 return ; 
//        }else{					
//       Intent it = new Intent();
//       it.setClass(Editview.this, Listview.class);
//       startActivityForResult(it, 200);
//        }
				Toast.makeText(Editview.this, "�����ڴ���", 2000).show();		
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK)
			if (requestCode == 1){
				iv.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));}
            if(requestCode == 200){
				if(db == null){
					db = new PictureDB(Editview.this);					
				}
				if(subjectFromGallery == null){
					 sharept = db.getItem(path.get(posit));
				}else {
			         sharept = db.getItem(path.get(i));
				}
				String xinqing = sharept.getXinqing();
				String picUrl = sharept.getPath();
			
				String title = sharept.getZhuti();
				String downUrl = "";
//				try {
//								
//            	int positon = data.getIntExtra("positionClick", 200);
//            	
//            	switch (positon) {
//				case 0:
//					ShareKaixin001.getInstance(Editview.this).share(
//					title, et.getText().toString(),  null, getString(R.string.app_name), picUrl);
//					break;
//				case 1:
//					ShareRenren.getInstance(Editview.this).shareFeed(
//					title, et.getText().toString(),  picUrl,
//					getString(R.string.app_name), downUrl);
//					break;
//				case 2:
//					ShareWeibo.getInstance(Editview.this, et.getText().toString(),
//					picUrl);
//					break;
//
//			     	}
//				} catch (Exception e) {
//					Toast.makeText(this, "δѡ�������", 2000).show();
//				}
            }
	}
	public void saveBitmap(Bitmap bitmap) throws IOException
    {
            File file = new File(path.get(i));
            FileOutputStream out;
            try{
                    out = new FileOutputStream(file);
                    if(bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) 
                   {
                            out.flush();
                            out.close();
                    }
            } 
           catch (FileNotFoundException e) 
           {
                    e.printStackTrace();
            } 
           catch (IOException e) 
           {
                    e.printStackTrace(); 
           }
    }
}
