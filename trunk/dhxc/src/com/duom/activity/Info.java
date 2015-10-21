package com.duom.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.jingxunnet.cmusic.R;

/*
 * 更换头像功能，从相册选取图片，作为自己的头像 ，跟从相册添加相片功能一样的。
 * 
 * */
public class Info extends Activity {
	private ImageView iv;
	private ImageButton back;
	private EditText nicheng, qianming;
	private Button boy, gril, save;
	private String nick, sign;
	TestUpload tul;
	static Bitmap bitinfo;
	static String path;
    static String sexinfo ;
    private ProgressDialog pd ;
    private SharedPreferences prefs;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.info);

		iv = (ImageView) findViewById(R.id.gravatar);
		nicheng = (EditText) findViewById(R.id.nicheng);
		qianming = (EditText) findViewById(R.id.qianming);

		boy = (Button) findViewById(R.id.boy);
		gril = (Button) findViewById(R.id.gril);
		save = (Button) findViewById(R.id.saveinfo);
		back = (ImageButton) findViewById(R.id.backinfo);

		gril.setBackgroundResource(R.drawable.grilbnt);
		boy.setBackgroundResource(R.drawable.boybnt);
		tul = new TestUpload(false);

		Task1Activity.qidongyemian = false;
		if(Login.nick == null){
			if(!Task1Activity.nick.equalsIgnoreCase("")){
				nicheng.setText(Task1Activity.nick.toString());
				}else{
					nicheng.setHint("无");
				}
		}else{
			if(!Login.nick.equalsIgnoreCase("")){
				nicheng.setText(Login.nick.toString());
				}else{
					nicheng.setHint("无");
				}
		}
		
		if( Login.header1!= null || Task1Activity.header1!= null){
			BitmapDrawable bitmapDrawable = new BitmapDrawable(Info.bitinfo);
			iv.setBackgroundDrawable(bitmapDrawable);
		}else{
			iv.setBackgroundResource(R.drawable.defaultgravater);
		}
	
		if(Login.sex.equalsIgnoreCase("0")){
				boy.setBackgroundResource(R.drawable.boyselected);
				sexinfo ="0";
				}else{
					gril.setBackgroundResource(R.drawable.grilselected);
					}
		////
		if(Login.humor == null){
			if(!Task1Activity.humor.equalsIgnoreCase("")){
				if(Task1Activity.registinfo == false){
					qianming.setText(Task1Activity.humor);
				 }else{
						qianming.setHint("还没有签名哦！");
						Task1Activity.registinfo = false; 
				 }		
				}
		}else{
			if(!Login.humor.equalsIgnoreCase("")){
				
					qianming.setText(Login.humor);
				  }else{ 
						qianming.setHint("还没有签名哦！");	
					}								
		}

		boy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boy.setBackgroundResource(R.drawable.boyselected);
				gril.setBackgroundResource(R.drawable.grilbnt);
				sexinfo = "0";
			}
		});

		gril.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boy.setBackgroundResource(R.drawable.boybnt);
				gril.setBackgroundResource(R.drawable.grilselected);
				sexinfo = "1";
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent it = new Intent();
//				it.setClass(Info.this, Task1Activity.class);
//				startActivity(it);
				finish();
//				 Task1Activity.headerfresh = true ;
				Task1Activity.info = true;
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Login.header1 = null ;
				String url = "http://user.hutudan.com/car_map/userinfo.php?flg=json";
				StringBuffer data = new StringBuffer();
				
				// /判断是否为空  如果为空提示
				if (nicheng.getText().toString() == null
						|| nicheng.getText().toString().trim().length() == 0) {
					Toast.makeText(Info.this, "请填写昵称！", Toast.LENGTH_SHORT).show();
					nicheng.requestFocus();
					return;
				}
				if (qianming.getText().toString() == null
						|| qianming.getText().toString().trim().length() == 0) {
					Toast.makeText(Info.this, "请填写签名！", Toast.LENGTH_SHORT).show();
					qianming.requestFocus();
					return;
				}
				if (!sexinfo.equalsIgnoreCase("0")&& !sexinfo.equalsIgnoreCase("1")) {
					Toast.makeText(Info.this, "请选择性别！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (bitinfo == null) {
					Toast.makeText(Info.this, "请选择头像！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				pd = new ProgressDialog(Info.this);
				pd.setMessage("正在登录。。。");
				pd.show();
				//
				data.append("nick=");
				nick = nicheng.getText().toString();
				data.append(URLEncoder.encode(nick));
				//
				data.append("&uid=");
				data.append(Login.uid);
				
				data.append("&sex=");
				data.append(URLEncoder.encode(sexinfo.toString()));
				//
				data.append("&sign=");
				data.append(URLEncoder.encode(qianming.getText().toString()));
		    	
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bitinfo.compress(CompressFormat.PNG, 100, os);
				byte[] bytes = os.toByteArray();
				Log.e("999999999", bytes+"");
			   
				File file = byteToFile(bytes);	
				Log.e("666669999", file.getAbsolutePath()+file+"");
				byte [] bt =tul.upload(url,data.toString(), file.getAbsolutePath().toString());
				String ss = new String(bt);
				Log.e("110110110", ss);                
				String tip = null;
				try {					
					JSONObject json = new JSONObject(ss);
					pd.cancel() ;
					int ret = json.optInt("ret", -1);
					if (ret == 1) {
						tip = URLDecoder.decode(json.optString("tip"));
						Login.header1 = URLDecoder.decode(json.optString("header"));
						Login.humor = qianming.getText().toString();
						Login.nick = nicheng.getText().toString();
						Login.sex =  sexinfo.toString();
						saveDate();
						// 进入到下一个页面
//			                Intent it = new Intent();
//							it.setClass(Info.this, Task1Activity.class);
//							startActivity(it);
							Task1Activity.info = true;
							 Task1Activity.headerfresh = true ;
							finish();
					} else {
						tip = "登陆请求失败,请重试!";
					}
				} catch (Exception ex) {
					tip = ex.getMessage();
					ex.printStackTrace();
				}
				if (!TextUtils.isEmpty(tip))
					Toast.makeText(Info.this, tip, Toast.LENGTH_SHORT)
							.show();

			}
		});
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				// 启动activity并等待返回结果
				startActivityForResult(intent, 2);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
			Intent it = new Intent();
			it.setClass(Info.this, Task1Activity.class);
			startActivity(it);
			Task1Activity.info = true;
			finish();
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;

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
				Photo po = new Photo();
				 path = cursor.getString(column_index);
				// Bitmap bit = po.getBitmap(path, 100);
				// Bitmap getSmallbit = po.getSmallBitmap(bit);
				Scan sc = new Scan();
				bitinfo = sc.getBitmap(path, 149);
				if (bitinfo.getWidth() < bitinfo.getHeight()) {
					// bit = sc.getBitmap(path.get(forScan), 200);
					bitinfo = sc.getBigBitmapWithWidth(bitinfo, 149);
				} else if (bitinfo.getHeight() < bitinfo.getWidth()) {
					// bit = sc.getBitmapw(path.get(forScan), 220);
					bitinfo = sc.getBigBitmapWithHight(bitinfo, 149);
				}
				//将bitmap 转换为 drawable
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bitinfo);      
				iv.setBackgroundDrawable(bitmapDrawable);
			} catch (Exception e) {
				Toast.makeText(this, "选取头像失败，请重新选取！", 2000).show();
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
    public File byteToFile(byte[] bytes){
		File file = null;
		String path = Environment.getExternalStorageDirectory()
				.getPath() + File.separator + "header.PNG";
		BufferedOutputStream stream = null;
		try {
			file = new File(path);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(bytes);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
    }
	private void saveDate() {
		// String ss= paCalendar.getInstance();
		prefs = getSharedPreferences("data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("uid", Login.uid);
		editor.putString("nick", Login.nick);
		editor.putString("sex", Login.sex);
		editor.putString("header1", Login.header1);
		editor.putString("humor", Login.humor);
		editor.commit(); 
	}
}
