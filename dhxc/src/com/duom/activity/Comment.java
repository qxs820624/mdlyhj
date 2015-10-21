package com.duom.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.duome.dkymarket.entity.AsyncImageLoader;
import cn.duome.dkymarket.entity.Commentitem;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.RouteOverlay;
import com.jingxunnet.cmusic.R;

public class Comment extends com.baidu.mapapi.MapActivity implements
		LocationListener {

	// ��ͼ����
	public BMapManager mapmanger;
	// ��ͼ������
	MapController mapController;
	// ����
	TextView tv;
	ListView suggestion;
	// �ݳ������Ľ��
	static MKDrivingRouteResult mkResult;
	public static boolean is = false;
	// ��ȡ�ص�
	public ImageButton back;
	// ������
	public MKSearch mKSearch;
	// map��ͼ
	private MapView mapView;
	// ��ʾ���
	private Gallery gv;
	// ƽ��
	private RatingBar rt;
	// ����
	private ListView lv;
	// ���� ��������ۣ� �ղ���·
	private Button share, pinglun, shoucang ;
	static int position;
	static String st, ed, route;
	static int id;
	public MKPlanNode start;
	public MKPlanNode end;
	static List<String> ls = new ArrayList<String>();
	public List<Commentitem> itemlist = new ArrayList<Commentitem>();
	HttpBaseActivity hb;
	static boolean isRefresh = true;
	ProgressDialog pd;
	// ����
	String title;
	// ���� �� ����
	int music, bg;

	Scan sc = new Scan();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment);
		lv = (ListView) findViewById(R.id.lvcomment);
		back = (ImageButton) findViewById(R.id.backcomment);
		tv = (TextView) findViewById(R.id.tvcomment);
		// nulltv = (TextView) findViewById(R.id.nulltv);
		share = (Button) findViewById(R.id.sharecomment);
		pinglun = (Button) findViewById(R.id.pingluncomment);
		shoucang = (Button) findViewById(R.id.savecomment);	
		View view = new View(getApplicationContext());
		view = getLayoutInflater().inflate(R.layout.header, null);
		lv.addHeaderView(view);
		gv = (Gallery) view.findViewById(R.id.glcomment);
		rt = (RatingBar) view.findViewById(R.id.barcomment);
		rt.setRating((float) 1);
		
		if (Task1Activity.fromshouye == true) {
			Intent it = getIntent();
			st = it.getStringExtra("start");
			ed = it.getStringExtra("end");
			id = it.getIntExtra("id", 10);
			route = it.getStringExtra("route");
			position = it.getIntExtra("position", 9);
			String ss = st + "------------" + ed;
			if (MostNew.routes.contains(ss)) {
				shoucang.setBackgroundResource(R.drawable.yishoucang);
			}
			start = new MKPlanNode();
			end = new MKPlanNode();
			start.name = st.toString();
			end.name = ed.toString();
		}
		Task1Activity.fromshouye = true;
		tv.setText(route);

		// pd = new ProgressDialog(this);
		// pd.show();
		pd = new ProgressDialog(Comment.this);
		pd.setMessage("���ڻ�ȡ���ۺ�ͼƬ������");
		pd.show();

		lv.setAdapter(new adapter(this));
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Commentitem item = itemlist.get(position-1);
				String pinglun = item.getComment() ;
				Intent it = new Intent(); 
				it.putExtra("pinglun", pinglun);
				it.setClass(Comment.this, Showall.class);
				startActivity(it);
			}
		});

		mapmanger = new BMapManager(this);
		mapmanger.init("F071BA1F1CA6651A37F9D455F2A9A5256C72399C", null);
		super.initMapActivity(mapmanger);
		// ���õ�ͼ����֧������
		mapView = (MapView) findViewById(R.id.bdmap);
		mapView.setBuiltInZoomControls(true);
		// ��ͼ������
		mapController = mapView.getController();
		mapController.setZoom(12);
		// ��ʼ��������
		mKSearch = new MKSearch();
		mKSearch.init(mapmanger, new MySearchListener());
		mKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mKSearch.drivingSearch("�й�", start, "�й�", end);
		mapView.invalidate(); // ˢ�µ�ͼ

		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("title", title);
				it.putExtra("music", music);
				it.putExtra("bg", bg);
				it.putExtra("routetoshowpic", route);
				it.putExtra("position", position);
				it.setClass(Comment.this, Showpicture.class);
				startActivity(it);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		pinglun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("position", id);
				it.setClass(Comment.this, Pinglun.class);
				startActivity(it);
				finish();
			}
		});
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("position", position);
				it.setClass(Comment.this, Listview.class);
				startActivity(it);
			}
		});
		shoucang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ���屣����·�ĸ�ʽ
				String ss = st.toString() + "------------" + ed.toString();
				// ���ϵ�ȥ�ط������жϼ����Ƿ��ss�� ������룬���򲻼���
				if (!MostNew.routes.contains(ss)) {
					MostNew.routes.add(ss);
					Toast.makeText(Comment.this, "�ղسɹ���", 2000).show();
					v.setBackgroundResource(R.drawable.yishoucang);
					Log.e("dfs1111", MostNew.routes + "");
				} else {
					MostNew.routes.remove(ss);
					Log.e("dfs1111", MostNew.routes + "");
					v.setBackgroundResource(R.drawable.save11);
					Toast.makeText(Comment.this, "��ȡ���ղ�!", 2000).show();
				}
			}
		});
		String url = "http://user.hutudan.com/car_map/routedetail.php?flg=json";
		StringBuffer data = new StringBuffer();
		//
		data.append("id=");
		data.append(id);
		// ����������֤��½
		HttpRequestTask request = new HttpRequestTask();
		request.execute(url, data.toString());
	}

	public void resultData(String result1) {
		String tip = null;
		try {
			pd.cancel();
			JSONObject json = new JSONObject(result1);
			int ret = json.optInt("ret", -1);
			if (ret == 1) {
				title = URLDecoder.decode(json.optString("title"));
				music = json.optInt("music", -2);
				bg = json.optInt("bgpic", -3);
				Log.e("00000", result1 + "  ");
			} else {
				Toast.makeText(Comment.this, "û��ͼƬ", 2000).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			itemlist.clear();
			JSONObject jsonChannel = new JSONObject(result1);
			JSONArray jsonItems = jsonChannel.optJSONArray("comments_items");
			if (jsonItems != null) {
				for (int i = 0; i < jsonItems.length(); i++) {
					JSONObject jsonItem = jsonItems.getJSONObject(i);
					String title = URLDecoder.decode(jsonItem
							.optString("title"));
					String pubdate = URLDecoder.decode(jsonItem
							.optString("pubdate"));
					String header = jsonItem.optString("header");
					Commentitem item = new Commentitem(title, pubdate, header);
					itemlist.add(item);
					lv.setAdapter(new adapter(this));
				}
			} else if (jsonItems == null) {
				TextView tv = new TextView(this);
				tv.setText("�������ۣ�");
				tv.setHeight(100);
				tv.setWidth(460);
				lv.addView(tv);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ls.clear();
			JSONObject jsonChanne = new JSONObject(result1);
			JSONArray Items = jsonChanne.optJSONArray("pic_items");
			if (Items != null) {
				for (int i = 0; i < Items.length(); i++) {
					JSONObject jsonItem = Items.getJSONObject(i);
					String pic = URLDecoder.decode(jsonItem.optString("url"));					
					// Task1Activity tt = new Task1Activity();
					// Bitmap bit = tt.makeBitmap(pic);
					ls.add(pic);
					gv.setAdapter(new ImageAdapter(this));
				}
			} else {
				Toast.makeText(Comment.this, "û��ͼƬ", 2000).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (mapmanger != null) {
			mapmanger.start();
		}
		super.onResume();
	}

	// ʵ��MKsearchListener�ӿڡ�
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		// �ݳ�����·�ߵĽ��
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 != 0 || arg0 == null) {
				Toast.makeText(Comment.this, "������δ�ҵ��ɹ�", Toast.LENGTH_LONG)
						.show();
				return;
			}
			// ��·��ͼ����������ʾ�ڵ�ͼ��
			mkResult = arg0;
			mapView.getOverlays().clear();
			RouteOverlay routeOverlay = new RouteOverlay(Comment.this, mapView);
			routeOverlay.setData(mkResult.getPlan(0).getRoute(0));
			mapView.getOverlays().add(routeOverlay);
			mapView.invalidate();
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		// ���ؽ���
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	protected void onDestroy() {
		mapmanger.destroy();
		mapmanger = null;
		is = false;
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mapmanger != null) {
			mapmanger.stop();
		}
		// TODO Auto-generated method stub
		super.onPause();
	}

	// Ϊgallery ����������
	class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		// ����items�ĸ������Դ����ݵĸ���
		public int getCount() {
			// TODO Auto-generated method stub
			return ls.size();
			// return Integer.MAX_VALUE;
		}

		// �õ�һ��item
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// �õ�item��Ӧ�ؼ���id
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// �õ�һ��Item��Ӧ�Ŀؼ� : ImageView
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView view = new ImageView(context);
			view.setImageResource(R.drawable.icontwelve);
			// view.setImageBitmap(ls.get(position));
			AsyncImageLoader imageLoader = new AsyncImageLoader();
			Drawable cachedDrawable = imageLoader.loadDrawable(
					ls.get(position), view,
					new AsyncImageLoader.ImageDownloadCallBack() {
						@Override
						public void imageLoaded(String imgURL,
								Drawable downloadedDrawable, ImageView imageView) {
							// TODO Auto-generated method stub
							if (downloadedDrawable != null) {
								BitmapDrawable bd = (BitmapDrawable) downloadedDrawable;
								Bitmap bm = bd.getBitmap();
								Bitmap bit = sc.getgallery(bm);
								// imageView.setImageDrawable(downloadedDrawable);
								imageView.setImageBitmap(bit);
							}
						}
					});
			// ˵����ͼƬ�ѱ����ز�����
			if (cachedDrawable != null) {
				BitmapDrawable bd = (BitmapDrawable) cachedDrawable;
				Bitmap bm = bd.getBitmap();
				Bitmap bit = sc.getgallery(bm);
				view.setImageBitmap(bit);
			}
			// ����ͼƬ�����Ż�������ģʽ
			view.setScaleType(ScaleType.FIT_CENTER);
			view.setPadding(5, 5, 5, 5);
			view.setBackgroundColor(Color.parseColor("#FFFFFF"));
			return view;
		}
	}

	private class adapter extends BaseAdapter

	{
		private Context context;

		public adapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount()

		{
			return itemlist.size();// ��Ŀ����
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
			// �Զ����view ��ͼ����������Դ
			ViewHolder holder = null;
			View itemView = convertView;
			if (itemView == null) {
				itemView = LayoutInflater.from(context).inflate(
						R.layout.itemlist, null);
				holder = new ViewHolder();
				holder.contentitem = (TextView) itemView
						.findViewById(R.id.textcontentitem);
				holder.dateitem = (TextView) itemView
						.findViewById(R.id.textdateitem);
				holder.headeritem = (ImageView) itemView
						.findViewById(R.id.headeritem);
				itemView.setTag(holder);
			} else {
				holder = (ViewHolder) itemView.getTag();
			}
			Commentitem item = itemlist.get(position);
			itemView.setTag(itemView.getId(), item);
			//
			holder.contentitem.setText(item.getComment());
			holder.dateitem.setText(item.getDate());
			 holder.headeritem.setImageResource(R.drawable.iconsix);
			AsyncImageLoader imageLoader = new AsyncImageLoader();
			Drawable cachedDrawable = imageLoader.loadDrawable(
					item.getHeader(), holder.headeritem,
					new AsyncImageLoader.ImageDownloadCallBack() {
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
			// ˵����ͼƬ�ѱ����ز�����
			if (cachedDrawable != null) {
//				holder.headeritem.setImageDrawable(cachedDrawable);
				BitmapDrawable bd = (BitmapDrawable) cachedDrawable;
				Bitmap bm = bd.getBitmap();
				Bitmap bit = sc.getlistitem(bm);
				holder.headeritem.setImageBitmap(bit);
			}
			return itemView;
		}
	}

	private class ViewHolder {
		public ImageView headeritem;
		public TextView contentitem;
		public TextView dateitem;
	}

	class HttpRequestTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPostExecute(String result) {
			resultData(result);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HashMap<String, String> data = new HashMap<String, String>();
				if (params.length > 1) {
					String[] keyvalues = params[1].split("&");
					for (String keyvalue : keyvalues) {
						String[] tmp = keyvalue.split("=");
						data.put(tmp[0], tmp[1]);
					}
				}
				
				HttpUriRequest request = getRequest(params[0], data, "POST");
				HttpResponse response = client.execute(request);
				//
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = EntityUtils.toString(response.getEntity());
				}
			} catch (Exception ex) {
				result = ex.getMessage();
				ex.printStackTrace();
			}
			return result;
		}

		/**
		 * 
		 * 
		 * @param url
		 * @param params
		 * @param method
		 * @return
		 */
		public HttpUriRequest getRequest(String url,
				Map<String, String> params, String method) {
			if (method.equals("POST")) {
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				if (params != null) {
					for (String name : params.keySet()) {
						listParams.add(new BasicNameValuePair(name, params
								.get(name)));
					}
				}
				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							listParams);
					HttpPost request = new HttpPost(url);
					request.setEntity(entity);
					return request;
				} catch (UnsupportedEncodingException e) {
					// Should not come here, ignore me.
					throw new java.lang.RuntimeException(e.getMessage(), e);
				}
			} else {
				if (url.indexOf("?") < 0) {
					url += "?";
				}
				if (params != null) {
					for (String name : params.keySet()) {
						url += "&" + name + "="
								+ URLEncoder.encode(params.get(name));
					}
				}
				HttpGet request = new HttpGet(url);
				return request;
			}
		}
	}
}
