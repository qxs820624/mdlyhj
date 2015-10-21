package com.hutuchong.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import mobi.domore.mcdonalds.R;
import cn.coolworks.handle.ActivityHandle;

/**
 * 
 * 
 * @author 3gqa.com
 * 
 */
public class WebDialog extends BaseActivity implements ActivityHandle {
	WebView mWebView;

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
		this.setContentView(R.layout.dialog_webview);
		//
		mWebView = (WebView) this.findViewById(R.id.webview);
		loadBtnBack(mWebView, R.id.btn_left, View.VISIBLE);
		loadPreNext(R.id.btn_preitem, R.id.btn_nextitem, View.VISIBLE);
		//
		TextView textView = (TextView) findViewById(R.id.title_text);
		if (textView != null)
			textView.setVisibility(View.VISIBLE);
		//
		// Commond.loadWebView(this, R.id.webview);
		View progressImage = findViewById(R.id.progressImage);
		Commond.loadWebView(this, mWebView, textView, null, null, null, null,
				null, progressImage);
		// Commond.loadZoom(this, R.id.webview);
		//
		Intent data = getIntent();
		if (data != null) {
			String title = data.getStringExtra(ContantValue.EXTRA_TITLE);
			if (!TextUtils.isEmpty(title)) {
				if (textView != null)
					textView.setText(title);
				else
					this.setTitle(title);
			}
			//
			String message = data.getStringExtra("message");
			if (!TextUtils.isEmpty(message)) {
				Commond.loadHtml(mWebView, message);
			} else {
				String url = data.getStringExtra(ContantValue.EXTRA_URL);
				Uri uri = data.getData();
				if (uri != null) {
					url = uri.toString();
				}
				if (!TextUtils.isEmpty(url)) {
					mWebView.loadUrl(Commond.appendUrl(url));
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && goBack(mWebView)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}