package com.duom.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duom.tool.Picture;
import com.duom.tool.PictureDB;
import com.jingxunnet.cmusic.R;

public class Add extends Activity implements OnClickListener {
	// 定义数据库
	private PictureDB db;
	// 存储从日历点击跳转过来的月份和日；
	String month, day;
	// 自定义View，动态添加view组件
	View view;
	// bnt确定按钮
	Button bnt;	
	String date;
	// 定义数组 存储要显示的内容
	String bgname[] = { "木纹", "秋天", "蜗牛", "蓝天" };
	String musicname[] = { "无","浪漫梦幻", "怀念流年", "欢心鼓舞", "节日庆典" };
	String wethername[] = { "晴天","阴天",  "雪天", "雾霭", "雷雨" };
	String datename[] = { "上午", "中午", "下午", "晚上" };
	// 定义组件
	private ImageButton  back;
	private TextView showtime;
	private EditText subject, adress;
	// 定义索引，接受传过来的索引，来确定数组该显示的内容
	int index;
	int musicindex;
	int wetherindex;
	int dateindex;
	
	String route , routefromprompt , routefrommy;

	// 自定义XML布局，用来显示 选择的背景， 音乐， 天气 ，日期，后面 将这些添加到view中显示。
	private LinearLayout layoutbg, layoutmusic, layoutwether, layoutdate;
	// 用来显示 用户的选择
	public static TextView tvbg, tvmusic, tvwether, tvdate;

	// private int getYear , getMonth , getDay , getHour , getMinute ,
	// getSecond;
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);
		LinearLayout layout;

		// /找出控件
		layout = (LinearLayout) findViewById(R.id.layout);
		back = (ImageButton) findViewById(R.id.iconback);
		subject = (EditText) findViewById(R.id.etzhuti);
		adress = (EditText) findViewById(R.id.etadress);		
		back.setOnClickListener(this);
		
		Intent it = getIntent();		
		routefrommy = it.getStringExtra("route");
		routefromprompt = it.getStringExtra("routefromprompt");
		
		if(routefromprompt == null){			
		route = routefrommy ;
		Log.e("12345678", route+"");
		} else {		
		route = routefromprompt ;		
		Log.e("1111111111111111233", route);
		}	
		///获取焦点，hint消失。
		subject.setOnFocusChangeListener(new OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        EditText _v=(EditText)v;
		        if (!hasFocus) {// 失去焦点
		            _v.setHint(_v.getTag().toString());
		        } else {
		            String hint=_v.getHint().toString();
		            _v.setTag(hint);
		            _v.setHint("");
		        }
		    }
		});
		
		adress.setOnFocusChangeListener(new OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        EditText _v=(EditText)v;
		        if (!hasFocus) {// 失去焦点
		            _v.setHint(_v.getTag().toString());
		        } else {
		            String hint=_v.getHint().toString();
		            _v.setTag(hint);
		            _v.setHint("");
		        }
		    }
		});

		// 将自定义的 view 添加到main.xml布局 中去。
		view = new View(getApplicationContext());
		view = (View) this.getLayoutInflater().inflate(R.layout.viewbutton,
				null);
		// 设置 view 存放的位置
		LinearLayout.LayoutParams mparams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mparams.setMargins(0, 20, 0, 0);
		layout.addView(view, mparams);
		// 初始化
		layoutbg = (LinearLayout) findViewById(R.id.bg);
		layoutmusic = (LinearLayout) findViewById(R.id.music);
		layoutwether = (LinearLayout) findViewById(R.id.wether);
		layoutdate = (LinearLayout) findViewById(R.id.date);
		bnt = (Button) view.findViewById(R.id.sureadd);

		tvbg = (TextView) findViewById(R.id.bgtext);
		tvmusic = (TextView) findViewById(R.id.musictext);
		tvwether = (TextView) findViewById(R.id.wethertext);
		tvdate = (TextView) findViewById(R.id.datetext);
		// 监听
		layoutbg.setOnClickListener(this);
		layoutmusic.setOnClickListener(this);
		layoutwether.setOnClickListener(this);
		layoutdate.setOnClickListener(this);
	
		bnt.setOnClickListener(this);		
		if (db == null) {
			db = new PictureDB(this);
		}
	}
	@Override
	public void onClick(View v) {
		if (v == layoutbg) {
//			 跳转到 更换背景页面
			Intent intent = new Intent();
			intent.setClass(this, Choosebg.class);
			startActivityForResult(intent, 0);
		}
		if (v == layoutmusic) {
			// 跳转到更换音乐界面
			Intent intent = new Intent();
			intent.setClass(this, Choosemusic.class);
			startActivityForResult(intent, 1);
		}
		if (v == layoutwether) {
			// 跳转到更换天气界面，等带返回结果
			Intent intent = new Intent();
			intent.setClass(this, Choosewether.class);
			startActivityForResult(intent, 2);
		}
		if (v == layoutdate) {
			// 跳转到更换时间界面，等带返回结果
			Intent intent = new Intent();
			intent.setClass(this, Choosedate.class);
			startActivityForResult(intent, 3);
		}
		// 确定按钮，按下之后，把数据存储到数据库，并进入到浏览页面
		if (v == bnt) {
			// /判断 主题和地址 是否为空,如果为空提示
			if (subject.getText().toString() == null
					|| subject.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请填写主题！", Toast.LENGTH_SHORT).show();
				subject.requestFocus();
				return;
			}
			// /判断 主题和地址 是否为空  如果为空提示
			if (adress.getText().toString() == null
					|| adress.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请填写地址！", Toast.LENGTH_SHORT).show();
				adress.requestFocus();
				return;
			}
			// //把数据存储到数据库
			if (db == null) {
				db = new PictureDB(Add.this);
			}
//			boolean is = db.curry(zhuti);
//			//点击确定按钮后，将相册主题，背景等信息插入数据库
//			if(is == false){
			db.insert(new Picture(subject.getText().toString(), adress.getText()
					.toString(), musicindex, index, null, route,null));	
//			}else{		
//			    Picture pi = db.curryzhuti(route);
//				String sj = pi.getZhuti();
//				List<String> pa = new ArrayList<String>(); 
//				pa = db.querry(sj ,route);
//				for(int i = 0 ; i<pa.size() ; i++){
//				    Picture pc=  db.getItem(pa.get(i));
//				    pc.setZhuti(subject.getText().toString());
//				    pc.setBg(index);
//				    pc.setMusic(musicindex);
//					db.update(pc, pa.get(i));				  
//				}				
//               db.deleteAll(sj, route);
//				db.insert(new Picture(subject.getText().toString(), adress.getText()
//						.toString(), musicindex, index, null, route,null));	
//			}			
			// ///并进入到浏览页面
			Intent scan = new Intent();
			scan.putExtra("zhutiFormAdd", subject.getText().toString());
			scan.putExtra("musicFromAdd", musicindex);
			scan.putExtra("fromAddbg", index);
			scan.putExtra("routefromadd", route);
			scan.setClass(Add.this, Scan.class);
			startActivity(scan);		
			finish();
//		}
	}
		if (v == back) {
			finish();
		}
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	if (keyCode == KeyEvent.KEYCODE_BACK) {	
//		Intent toXiangmu = new Intent();
//		toXiangmu.setClass(Add.this, XiangmuActivity.class);
//		startActivity(toXiangmu);
//		finish();
//	  }
//	return true;
}
	@Override
	// //重载startactivityforresult 的方法 ，处理返回结果 ，根据用户选择后返回的结果，来设置每个Textview 上的字
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// /设置 选择的背景
		if (resultCode == 20) {
			index = data.getIntExtra("index", 20);
			tvbg.setText(bgname[index]);
		}
		// /设置 选择的音乐
		if (resultCode == 30) {
			musicindex = data.getIntExtra("musicindex", 30);
			tvmusic.setText(musicname[musicindex]);
		}
		// /设置 选择的天气
		if (resultCode == 40) {
			wetherindex = data.getIntExtra("wetherindex", 40);
			tvwether.setText(wethername[wetherindex]);
		}
		// /设置 选择的时间
		if (resultCode == 50) {
			dateindex = data.getIntExtra("dateindex", 50);
			tvdate.setText(datename[dateindex]);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
