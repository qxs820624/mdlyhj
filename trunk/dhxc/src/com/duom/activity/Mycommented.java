package com.duom.activity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.duome.dkymarket.entity.AsyncImageLoader;
import cn.duome.dkymarket.entity.Mycommentinfo;
import com.jingxunnet.cmusic.R;

public class Mycommented extends HttpBaseActivity { 
	private ListView lv;
	List<Mycommentinfo> mycom;
	ProgressBar pb;
	private ImageButton back ;
	LinearLayout llcomment ;
	Scan sc = new Scan();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mycomment);

		lv = (ListView) findViewById(R.id.lvcomment);
		mycom = new ArrayList<Mycommentinfo>();
        back = (ImageButton)findViewById(R.id.backmycomment);
		pb = (ProgressBar)findViewById(R.id.progressmycomment);
		llcomment = (LinearLayout)findViewById(R.id.llcomment);
		
		lv.setAdapter(new adapter(this));
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
          if(Task1Activity.layuser.getVisibility() == View.GONE){
        	  Toast.makeText(this, "亲，你还没有登录哦！", 2000).show();
        	  return ;
          }
  		pb.setVisibility(View.VISIBLE);
		String url = "http://user.hutudan.com/car_map/mycomments.php?flg=json";
		StringBuffer data = new StringBuffer();
		//
		data.append("uid=");
		data.append(Login.uid);
		// 请求网络验证登陆
		HttpRequestTask request = new HttpRequestTask();
		request.execute(url, data.toString());

	}

	@Override
	void resultData(String result) {
		// TODO Auto-generated method stub
		String tip = null;
		try {		
			mycom.clear();
			JSONObject jsonChannel = new JSONObject(result);
			JSONArray jsonItems = jsonChannel.optJSONArray("items");
			if (jsonItems != null){
				pb.setVisibility(View.GONE); 
				for (int i = 0; i < jsonItems.length(); i++) {
					tip = "更新成功！";
					JSONObject jsonItem = jsonItems.getJSONObject(i);
					String comment = URLDecoder.decode(jsonItem
							.optString("comment"));
					String tl = URLDecoder.decode(jsonItem.optString("title"));
					String header = jsonItem.optString("thumb");
					//
					String title = "评论文章 " + "《" + tl + "》";
					Mycommentinfo item = new Mycommentinfo(comment, title, header);
					mycom.add(item);
				}
				if(mycom.size() == 0){
					llcomment.setVisibility(View.VISIBLE);
				}
			}
		} catch (Exception ex) {
			tip = ex.getMessage();
			ex.printStackTrace();
		}
		if (!TextUtils.isEmpty(tip)){
			Toast.makeText(Mycommented.this, tip, Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		lv.setAdapter(new adapter(this));
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
			return mycom.size();// 项目总数
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
			ViewHolder holder = null;
			View itemView = convertView;
			if (itemView == null) {
				itemView = LayoutInflater.from(context).inflate(
						R.layout.mycomitem, null);
				 holder = new ViewHolder();
				 holder.title = (TextView) itemView
						.findViewById(R.id.titlemycom);
			 	 holder.comment = (TextView) itemView
						.findViewById(R.id.commentmycom);
				 holder.imageheader = (ImageView) itemView
				 .findViewById(R.id.headermycom);
				itemView.setTag(holder);
			} else {
				holder = (ViewHolder) itemView.getTag();
			}
			Mycommentinfo item = mycom.get(position);
			itemView.setTag(itemView.getId(), item);
			//
			holder.comment.setText(item.getComment());
			holder.title.setText(item.getTitle());
			 holder.imageheader.setImageResource(R.drawable.iconseven);
			AsyncImageLoader imageLoader = new AsyncImageLoader();
			Drawable cachedDrawable = imageLoader.loadDrawable(item.getHeader(),
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
			return itemView;
		}
	}

	private class ViewHolder {
		 public ImageView imageheader;
		public TextView comment;
		public TextView title;
	}
}
