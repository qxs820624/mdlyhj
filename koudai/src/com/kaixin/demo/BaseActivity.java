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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.chuangyu.music.R;
import com.kaixin.connect.Kaixin;
import com.kaixin.connect.exception.KaixinError;

/**
 * 此类为可以处理授权的基类，子类可以继承它，以简化页面逻辑
 */
public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";

	protected Kaixin kaixin = Kaixin.getInstance();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (kaixin.isSessionValid()) {
			doOnResume();
		}
	}

	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dealWithMessage(msg);
		}
	};

	/**
	 * 当Kaixin实例初始化完成，才开始运行页面逻辑
	 */
	protected void doOnResume() {

	}

	/**
	 * 当Kaixin实例初始化完成，才开始运行页面逻辑
	 */
	protected void doOnPause() {

	}

	/**
	 * 处理Message，子类可以调用此方法来简化异常处理
	 * 
	 * @param msg
	 *            Message
	 */
	protected void dealWithMessage(Message msg) {
		switch (msg.what) {
		case Constant.RESULT_FAILED_NETWORK_ERR:
			Toast.makeText(getApplicationContext(),
					R.string.task_failed_network_err, Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.RESULT_FAILED_JSON_PARSE_ERR:
			Toast.makeText(getApplicationContext(),
					R.string.task_failed_json_parse_err, Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.RESULT_FAILED_ARG_ERR:
			Toast.makeText(getApplicationContext(),
					R.string.task_failed_arg_err, Toast.LENGTH_SHORT).show();
			break;
		case Constant.RESULT_FAILED_MALFORMEDURL_ERR:
			Toast.makeText(getApplicationContext(),
					R.string.task_failed_malformed_url_err, Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.RESULT_FAILED_ENCODER_ERR:
			Toast.makeText(getApplicationContext(),
					R.string.task_failed_encoder_err, Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.RESULT_FAILED:
			Toast.makeText(getApplicationContext(), R.string.task_failed,
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.RESULT_FAILED_REQUEST_ERR:
			KaixinError kaixinError = (KaixinError) msg.obj;
			Toast.makeText(getApplicationContext(), kaixinError.toString(),
					Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (kaixin.isSessionValid()) {
			doOnPause();
		}
	}
}