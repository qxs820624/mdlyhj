package com.hutuchong.app_mcdonalds;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.coolworks.util.Debug;

import com.hutuchong.app_mcdonalds.adapter.NavListItemAdapter;
import com.hutuchong.util.Commond;

public class NavListDialog {
	McdonaldsActivity mActivity;
	ProgressBar progressBar;
	Dialog dialog;
	double lat = 39.898148f;// 经度
	double lon = 116.488423f;// 纬度
	RSSChannel mChannel;

	/** Called when the activity is first created. */

	public NavListDialog(McdonaldsActivity activity) {
		mActivity = activity;
		dialog = Commond
				.createDialog(mActivity, R.layout.app_mcdonalds_navlist);
		progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
		// 获得当前位置的坐标
		LocationManager locationManager = (LocationManager) mActivity
				.getSystemService(Context.LOCATION_SERVICE);// 获取LocationManager的一个实例
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 0, locationListener);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location != null) {
			lat = location.getLatitude();
			lon = location.getLongitude();
			Debug.d(lat + ":" + lon);
		}
		//
		View v = dialog.findViewById(R.id.btn_cancel);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		//
		activity.requestNavList(lat, lon);
		progressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	public void loadData(RSSChannel channel) {
		progressBar.setVisibility(View.GONE);
		if (channel == null)
			return;
		mChannel = channel;
		//
		TextView tv = (TextView) dialog.findViewById(R.id.tv_title);
		tv.setText(mChannel.getTitle());
		//
		ListView listView = (ListView) dialog.findViewById(R.id.list_view);
		NavListItemAdapter navAdapter = new NavListItemAdapter(mActivity,
				R.layout.app_mcdonalds_navlist_item, channel);
		listView.setAdapter(navAdapter);
	}

	/**
	 * 
	 */
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
			if (location != null) {
				lat = location.getLatitude();// 经度
				lon = location.getLongitude();// 纬度
				Debug.d(lat + ":" + lon);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 显示
	 */
	public void show() {
		dialog.show();
	}
}
