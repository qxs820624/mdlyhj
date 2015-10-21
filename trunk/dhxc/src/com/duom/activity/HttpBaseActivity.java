package com.duom.activity;

import java.io.UnsupportedEncodingException;
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

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.jingxunnet.cmusic.R;

/**
 * 
 * 
 * @author dky
 * 
 */
public abstract class HttpBaseActivity extends Activity {
	 ProgressBar pro;
	String mHost="http://user.hutudan.com";
	abstract void resultData(String result);

	class HttpRequestTask extends AsyncTask<String, Integer, String> {

		 // 执行预处理
		 @Override
		 protected void onPreExecute() {
		  super.onPreExecute();
		  // 显示进度条
		  pro = new ProgressBar(HttpBaseActivity.this);
		  pro.setVisibility(View.VISIBLE);	
		 }

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
				Log.e("011111111", data.toString());
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
			 pro.setVisibility(View.GONE);	
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