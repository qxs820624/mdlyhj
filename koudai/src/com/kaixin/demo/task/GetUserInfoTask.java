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
package com.kaixin.demo.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kaixin.connect.Kaixin;
import com.kaixin.connect.Util;
import com.kaixin.connect.exception.KaixinError;
import com.kaixin.demo.Constant;
import com.kaixin.demo.item.UserInfo;

public class GetUserInfoTask extends AsyncTask<Object, Void, Integer> {
	private static final String TAG = "GetUserInfoTask";
	private static final String RESTAPI_INTERFACE_GETUSERINFO = "/users/me.json";
	private ArrayList<UserInfo> resultContainer;
	private Handler handler;

	@SuppressWarnings("unchecked")
	protected Integer doInBackground(Object... params) {
		if (params == null || params.length == 0 || params.length != 4) {
			handler.sendEmptyMessage(Constant.RESULT_FAILED_ARG_ERR);
			return 0;
		}

		Kaixin kaixin = (Kaixin) params[0];
		handler = (Handler) params[1];
		resultContainer = (ArrayList<UserInfo>) params[2];
		Context context = (Context) params[3];

		Bundle bundle = new Bundle();
		bundle.putString("fields", "uid,name,gender,logo50,hometown,city,status");

		try {
			// 获取当前登录用户的资料
			String jsonResult = kaixin.request(context,
					RESTAPI_INTERFACE_GETUSERINFO, bundle, "GET");
			if (jsonResult == null) {
				handler.sendEmptyMessage(Constant.RESULT_FAILED_NETWORK_ERR);
			} else {
				KaixinError kaixinError = Util.parseRequestError(jsonResult);
				if (kaixinError != null) {
					Message msg = Message.obtain();
					msg.what = Constant.RESULT_FAILED_REQUEST_ERR;
					msg.obj = kaixinError;
					handler.sendMessage(msg);
				} else {
					ArrayList<UserInfo> userInfoList = getUserInfo(jsonResult);
					if (null != userInfoList) {
						resultContainer.addAll(userInfoList);
					}
					handler.sendEmptyMessage(Constant.RESULT_GET_USERINFO_OK);
				}
			}
		} catch (MalformedURLException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_MALFORMEDURL_ERR);
		} catch (JSONException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_JSON_PARSE_ERR);
		} catch (IOException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_NETWORK_ERR);
		} catch (Exception e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED);
		}
		return 1;
	}

	private ArrayList<UserInfo> getUserInfo(String jsonResult)
			throws JSONException {
		JSONObject jsonObj = new JSONObject(jsonResult);
		if (jsonObj == null) {
			return null;
		}
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>(7);
		String uid = jsonObj.getString("uid");
		userInfoList.add(new UserInfo("ID", uid));

		String name = jsonObj.getString("name");
		userInfoList.add(new UserInfo("姓名", name));

		String gender = jsonObj.getString("gender");
		userInfoList.add(new UserInfo("性别", gender.equals("0") ? "男" : "女"));

		String logo50 = jsonObj.getString("logo50");
		userInfoList.add(new UserInfo("头像", logo50));

		String hometown = jsonObj.getString("hometown");
		userInfoList.add(new UserInfo("家乡", hometown));

		String city = jsonObj.getString("city");
		userInfoList.add(new UserInfo("城市", city));

		String status = jsonObj.getString("status");
		userInfoList.add(new UserInfo("状态", status));

		return userInfoList;
	}

	protected void onPostExecute(Integer result) {
	}
}