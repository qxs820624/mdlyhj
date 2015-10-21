package com.duom.activity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.duome.dkymarket.entity.AsyncImageLoader;
import cn.duome.dkymarket.entity.Item;

import com.jingxunnet.cmusic.R;
import com.duom.activity.HttpBaseActivity.HttpRequestTask;
import com.duom.activity.MostHost.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
public class Area extends HttpBaseActivity {
	static ListView listarea = null;
	static Button tianjialuxian;
	public List<Item> it = new ArrayList<Item>();
	int i = 0;
	static boolean iswait;
	public ProgressBar pgb;
	//收藏
	static List<String> saveRoute ;
	static List<String> routes = new ArrayList<String>();
	ViewHolder holder = null;
	Task1Activity tt = new Task1Activity();
	Scan sc = new Scan();
	SharedPreferences prefs ;
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.area);
	
	listarea = (ListView) findViewById(R.id.listarea);
	tianjialuxian = (Button) findViewById(R.id.tianjialuxianarea);
	pgb = (ProgressBar)findViewById(R.id.progressarea);
	pgb.setVisibility(View.VISIBLE);
	String result = "";
	String url = "http://user.hutudan.com/car_map/routelist.php?flg=json";
	StringBuffer data = new StringBuffer();
	//
	data.append("uid=");
	data.append(Login.uid);
	//
	data.append("&mark=");
	data.append(1);
	// 请求网络验证登陆
	HttpRequestTask request = new HttpRequestTask();
	request.execute(url, data.toString());
	Log.e("1111122222", request + " ");


	listarea.setAdapter(new adapter(this));
	it.clear();
	tianjialuxian.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent it = new Intent();
			it.setClass(Area.this, Navigation.class);
			startActivity(it);
		}
	});
	listarea.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			Item item = it.get(position);
			String rout = item.getRoute();
			String[] routes = rout.split("--");
			String start = routes[0];
			String end = routes[1];

			Intent it = new Intent();
			it.putExtra("start", start);
			it.putExtra("end", end);
			it.putExtra("route", rout);
			it.putExtra("position", position);
			it.putExtra("id", item.getId());
			it.setClass(Area.this, Comment.class);
			startActivity(it);
			Comment.isRefresh = true;
		}
	});
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
			new AlertDialog.Builder(Area.this)
			.setTitle("友情提示：")
			.setMessage("确定退出？")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
							saveDate();
						}
					}).show();
		}
		return true;
	}
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	Task1Activity.tv0.setBackgroundResource(R.drawable.music_migu_btn_normal);
		Task1Activity.tv2.setBackgroundResource(R.drawable.daohang);
		Task1Activity.tv.setBackgroundResource(R.drawable.zhuye);
		Task1Activity.tv3.setBackgroundResource(R.drawable.xiangcepress);
		if(Task1Activity.layuser.getVisibility() == View.VISIBLE){
			
		}	
		if(MostNew.isfresh == true){
			fresh();
			listarea.setAdapter(new adapter(this));	
			MostNew.isfresh = false;
		}
    	super.onResume();
    }
	public class adapter extends BaseAdapter

	{
		private Context context;

		public adapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount()

		{
			return it.size();// 项目总数
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 自定义的view 视图，适配器资源
			
			View itemView = convertView;
			ImageView imageView = null;
			Drawable cachedDrawable = null ;
			if (itemView == null) {
				itemView = LayoutInflater.from(context).inflate(
						R.layout.itemnew, null);
				holder = new ViewHolder();
				holder.route = (TextView) itemView.findViewById(R.id.textroute);
				holder.subject = (TextView) itemView
						.findViewById(R.id.textsubject);
				holder.imageheader = (ImageView) itemView
						.findViewById(R.id.imageheader);
				itemView.setTag(holder);
			}
			holder = (ViewHolder) itemView.getTag();
			Item item = it.get(position);
			itemView.setTag(itemView.getId(), item);
			//
			holder.route.setText(item.getRoute());
			holder.subject.setText(item.getTitle());
			 holder.imageheader.setImageResource(R.drawable.iconseven);
			AsyncImageLoader imageLoader = new AsyncImageLoader();
			cachedDrawable = imageLoader.loadDrawable(item.getHeader(),
					holder.imageheader, new AsyncImageLoader.ImageDownloadCallBack() {
						@Override
						public void imageLoaded(String imgURL,
								Drawable downloadedDrawable, ImageView imageView) {
							// TODO Auto-generated method stub
							if (downloadedDrawable != null) {
								BitmapDrawable bd = (BitmapDrawable) downloadedDrawable;
								Bitmap bm = bd.getBitmap();
								Bitmap bit = sc.getlistitem(bm);
								// imageView.setImageDrawable(downloadedDrawable);
								imageView.setImageBitmap(bit);
							}
						}
					});
	      //说明此图片已被下载并缓存   
          if(cachedDrawable != null){ 
        	  BitmapDrawable bd = (BitmapDrawable) cachedDrawable;
				Bitmap bm = bd.getBitmap();
				Bitmap bit = sc.getlistitem(bm);
				holder.imageheader.setImageBitmap(bit);
    	   }
//			Bitmap bit = tt.makeBitmap(item.getHeader());
//			holder.imageheader.setImageBitmap(bit);
			return itemView;
		}
	}
    private class ViewHolder {
		public ImageView imageheader;
		public TextView subject;
		public TextView route;
	}
    String tip ;
	@Override
	void resultData(String result) {
		// TODO Auto-generated method stub
		tip = null ;
		pgb.setVisibility(View.GONE);
		try {								 
			JSONObject jsonChannel = new JSONObject(result);
			JSONArray jsonItems = jsonChannel.optJSONArray("items");
			if (jsonItems != null)
				tip = "获取成功！";
				for (int i = 0; i < jsonItems.length(); i++) {
					JSONObject jsonItem = jsonItems.getJSONObject(i);
					int uid = jsonItem.optInt("uid");
					int id = jsonItem.optInt("id");
					String start = URLDecoder.decode(jsonItem
							.optString("baddress"));
					String end = URLDecoder.decode(jsonItem
							.optString("eaddress"));
					String title = URLDecoder.decode(jsonItem
							.optString("title"));
					String header = jsonItem.optString("thumb");
					//
					String route = start + "--" + end;
//					Task1Activity tt = new Task1Activity();
//					Bitmap bit = tt.makeBitmap(header);
					Item item = new Item(route, title, header,id);
					it.add(item);      
				 }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(tip)){
			Toast.makeText(Area.this, tip, Toast.LENGTH_SHORT).show();
//			 pb.setVisibility(View.GONE);
		}
	}
	
	public void fresh(){
		pgb.setVisibility(View.VISIBLE);
		String result = "";
		String url = "http://user.hutudan.com/car_map/routelist.php?flg=json";
		StringBuffer data = new StringBuffer();
		//
		data.append("uid=");
		data.append(Login.uid);
		//
		data.append("&mark=");
		data.append(1);
		// 请求网络验证登陆
		HttpRequestTask request = new HttpRequestTask();
		request.execute(url, data.toString());
	}
	public void saveDate() {
		// String ss= paCalendar.getInstance();
		prefs = getSharedPreferences("data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if(Task1Activity.login.getVisibility() == View.VISIBLE){
		editor.putInt("uid", 0);
		editor.putString("nick", "");
		editor.putString("sex", "");
		editor.putString("header1","");
		editor.putString("humor", "");
		}else{
			editor.putInt("uid", Login.uid);
			editor.putString("nick", Login.nick);
			editor.putString("sex", Login.sex);
			editor.putString("header1",Login.header1);
			editor.putString("humor", Login.humor);
		}
		editor.commit(); 
	}
}
