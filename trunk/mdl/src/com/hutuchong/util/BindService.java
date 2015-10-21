package com.hutuchong.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.coolworks.handle.ActivityHandle;

public class BindService {
	//
	private ActivityHandle handler;
	//
	public Intent mIntent;
	/**
	 * 
	 */
	public BaseService service;

	/**
	 * 
	 * @param context
	 */
	public BindService(Context context, ActivityHandle handler, Class<?> cls) {
		if (context == null)
			return;
		this.handler = handler;
		//
		try {
			mIntent = new Intent();
			mIntent.setComponent(new ComponentName(context, cls));
			// Commond.showToast(context, "1111");
			context.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
			// Commond.showToast(context, "222");
			context.startService(mIntent);
			// Commond.showToast(context, "333");
		} catch (Exception ex) {
			// Commond.showToast(context, "ex:" + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param context
	 */
	public void unbindService(Context context) {
		context.unbindService(mConnection);
	}

	/**
	 * 
	 * @param context
	 */
	public void stopService(Context context) {
		context.stopService(mIntent);
	}

	/**
	 * 
	 */
	public ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			BindService.this.service = ((BaseService.MyBinder) service)
					.getService();
			handler.onBinddedService();
		}

		/**
		 * 
		 */
		public void onServiceDisconnected(ComponentName className) {
			// kaixinService = null;
		}
	};

}
