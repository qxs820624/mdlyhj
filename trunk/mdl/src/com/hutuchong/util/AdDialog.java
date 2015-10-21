package com.hutuchong.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.hutuchong.http.HotInterface;
import com.jw.http.ProgressListener;

/**
 * 
 * 
 * @author 3gqa.com
 * 
 */
public class AdDialog extends Activity implements ProgressListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setVisible(false);
		// this.setContentView(R.layout.webview);
		//
		Intent intent = getIntent();
		if (intent != null) {
			//
			Uri uri = intent.getData();
			// WebView v = (WebView) this.findViewById(R.id.webview);
			if (uri != null) {
				final String url = uri.toString();
				// v.loadUrl(url);
				Thread test = new Thread(new Runnable() {
					public void run() {
						HotInterface inter = new HotInterface(false);
						inter.image(AdDialog.this, url);
					}
				});
				test.start();
				finish();
			}
		}
	}

	@Override
	public void onProgress(Object tag, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBaseUrl(String url) {
		// TODO Auto-generated method stub

	}
}