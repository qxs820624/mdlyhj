package com.hutuchong.util;

import java.util.ArrayList;

import org.gnu.stealthp.rsslib.RSSItem;

import android.graphics.Bitmap;

/**
 * 
 * @author 3gqa.com
 * 
 */
public interface ServiceHandle {
	public Bitmap getCacheBitmap(String url);

	//public byte[] getCacheData(String url);

	public String getExitFileName(String url);

	public long getFileSize(String url);

	//public ArrayList<RSSItem> getShowItemList();
}