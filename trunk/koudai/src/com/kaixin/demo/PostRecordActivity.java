/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaixin.demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.chuangyu.music.R;

import com.hutuchong.share.LoaderImageView;
import com.kaixin.demo.task.PostRecordTask;

public class PostRecordActivity extends BaseActivity {

	// 当前页面可能的状态：
	private static final int STATE_INITIAL = 0;
	private static final int STATE_UPLOADING = 1;
	private static final int STATE_FINSHED = 2;

	EditText mEontentEdit;
	private int activityState = STATE_INITIAL;
	private PostRecordTask getDataTask;

	String mTitle;
	String mContent;
	String mLink;
	String mPicUrl;
	String mPicPath;
	String mActionName;
	String mActionLink;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kaixin001_postrecord);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		// dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
		lp.dimAmount = 0.7f;
		//
		Intent intent = getIntent();
		mTitle = intent.getStringExtra("title");
		mContent = intent.getStringExtra("content");
		if (mContent != null && mContent.length() > 100) {
			mContent = mContent.substring(0, 100);
			mContent += "...";
		}
		mLink = intent.getStringExtra("link");
		mPicUrl = intent.getStringExtra("picUrl");
		mPicPath = intent.getStringExtra("picPath");
		mActionName = intent.getStringExtra("actionName");
		mActionLink = intent.getStringExtra("actionLink");

		Button postBtn = (Button) findViewById(R.id.kaixin001_postrecord_post_button);
		mEontentEdit = (EditText) findViewById(R.id.kaixin001_postrecord_content_edit);
		mEontentEdit.setText(mContent);

		LoaderImageView ivPhotoUrl = (LoaderImageView) findViewById(R.id.kaixin001_postrecord_photo_url);
		if (TextUtils.isEmpty(mPicUrl) && TextUtils.isEmpty(mPicPath)) {
			ivPhotoUrl.setVisibility(View.GONE);
		} else {
			ivPhotoUrl.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(mPicPath))
				ivPhotoUrl.setImageDrawable(mPicPath);
			else
				ivPhotoUrl.setImageDrawable(mPicUrl);
		}

		postBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = String.valueOf(mEontentEdit.getText()).trim();
				if (content.length() == 0) {
					Toast.makeText(getApplicationContext(),
							R.string.post_record_empty_content,
							Toast.LENGTH_LONG).show();
					return;
				}

				activityState = STATE_UPLOADING;
				showDialog(Constant.DIALOG_ID_UPLOADING);
				// InputStream in =
				// getResources().openRawResource(R.raw.photo1);
				InputStream in = null;
				// System.out.println("mPicPath:" + mPicPath);
				if (!TextUtils.isEmpty(mPicPath)) {
					try {
						in = new FileInputStream(mPicPath);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (!TextUtils.isEmpty(mPicUrl)) {
					try {
						in = (java.io.InputStream) new java.net.URL(mPicUrl)
								.getContent();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				getDataTask = new PostRecordTask();
				getDataTask.execute(kaixin, handler, content, in,
						PostRecordActivity.this);
			}
		});
	}

	@Override
	public void doOnResume() {
		// System.out.println("doOnResume:" + activityState);
		if (activityState == STATE_INITIAL) {
			// activityState = STATE_UPLOADING;
			// stateInitOnCreate();
		} else if (activityState == STATE_UPLOADING) {
			stateInitOnCreate();
		} else if (activityState == STATE_FINSHED) {
			setStateOnFetchFinished();
			finish();
		}
	}

	@Override
	protected void doOnPause() {
		// System.out.println("doOnPause:" + activityState);
		if (activityState == STATE_UPLOADING) {
			dismissDialog(Constant.DIALOG_ID_UPLOADING);
			getDataTask.cancel(true);
		} else if (activityState == STATE_FINSHED) {
			// do nothing
			finish();
		}
	}

	@SuppressWarnings("unchecked")
	protected void dealWithMessage(Message msg) {
		dismissDialog(Constant.DIALOG_ID_UPLOADING);

		if (msg.what == Constant.RESULT_POST_RECORD_OK) {
			if (activityState == STATE_UPLOADING) {
				activityState = STATE_FINSHED;

				Toast.makeText(getApplicationContext(),
						R.string.post_record_success, Toast.LENGTH_LONG).show();
				finish();
			}
		} else if (msg.what == Constant.RESULT_POST_RECORD_FAILED) {
			if (activityState == STATE_UPLOADING) {
				activityState = STATE_FINSHED;
				Toast.makeText(getApplicationContext(),
						R.string.post_record_fail, Toast.LENGTH_LONG).show();
			}
		} else {
			super.dealWithMessage(msg);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == Constant.DIALOG_ID_UPLOADING) {
			return ProgressDialog.show(this, "", getString(R.string.uploading),
					true, true, null);
		} else {
			return super.onCreateDialog(id);
		}
	}

	protected void stateInitOnCreate() {
	}

	protected void setStateOnFetchFinished() {

	}
}