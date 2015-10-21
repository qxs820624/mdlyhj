package com.hutuchong.app_game;

import org.gnu.stealthp.rsslib.RSSItem;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import cn.coolworks.util.StringUtil;

public class AppInfo {
	/**
	 * 
	 */
	// private static final long serialVersionUID = -6902619869657075833L;
	public String appName = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	public float size = 0.0f;
	public String sizeStr = "";
	public Drawable appIcon = null;
	/* java.io.NotSerializableException:android.graphics.Bitmap */
	public Bitmap appBitmap = null;
	public String desc = "";
	public String downCount = "0";

	public String linkUrl = null;
	public String apkUrl = null;
	public String iconUrl = null;

	/**
	 * 
	 */
	public AppInfo() {

	}

	/**
	 * 
	 * @param item
	 */
	public AppInfo(RSSItem item) {
		item.setTag(this);
		//
		linkUrl = item.getLink();
		packageName = item.getCategory();
		apkUrl = item.getImageUrl();
		String title = item.getTitle();
		String[] tmps = title.split("_");
		if (tmps.length >= 3) {
			appName = tmps[0];
			sizeStr = StringUtil.readableFileSize(tmps[2]);

		}
		iconUrl = item.getThumbailUrl();
		versionName = item.getFavLink();
	}
}
