package com.hutuchong.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import mobi.domore.mcdonalds.R;

/**
 * 
 * 
 * @author 3gqa.com
 * 
 */
public class AboutActivity extends Activity {
	TextView mContent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Commond.userInfo.isFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		//
		//
		this.setContentView(R.layout.about);
		//
		TextView vtTitle = (TextView) findViewById(R.id.title_text);
		WebView mWebView = (WebView) this.findViewById(R.id.webview);
		View progressImage = this.findViewById(R.id.progressImage);
		Commond.loadWebView(this, mWebView, vtTitle, null, null, null, null,
				null, progressImage);
		//
		// String abouturl = "file:///android_asset/about.html";
		// String abouturl = "http://foto.hutuchong.com/3g/m.php";
		mWebView.loadUrl("file:///android_asset/about.html");
		//

		vtTitle.setVisibility(View.VISIBLE);
		vtTitle.setText(R.string.company);
		//
		Commond.setAbout(this, R.id.btn_phone, R.string.phone, R.id.btn_sms);
		// 返回
		View btnBack = this.findViewById(R.id.btn_left);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//
		View nav_share = this.findViewById(R.id.btn_right);
		nav_share.setVisibility(View.INVISIBLE);
		//
	}
}