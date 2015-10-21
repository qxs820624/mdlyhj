package com.jw.http;

import android.text.TextUtils;

import com.hutuchong.util.BaseCommond;
import com.jw.http.ConnectPoolManager;
import com.jw.http.ProgressListener;
import com.jw.http.UploadData;

public class HotInterface {

	// -----------------------------------------------------
	final static String POST_URLTYPE = "application/x-www-form-urlencoded";
	final static String POST_PHOTOTYPE = "multipart/form-data";

	public ConnectPoolManager conn;

	public HotInterface(boolean isProxy) {
		conn = new ConnectPoolManager(isProxy);
	}

	public int getProgress() {
		return conn.getProgress();
	}

	public void release() {
		conn.releaseAll();
	}

	/**
	 * 
	 * @param email
	 * @param pass
	 * @return
	 */
	public byte[] read(ProgressListener l, String url) {
		// Debug.d("hotinterface read....");
		byte[] temp = null;
		temp = conn.read(l, BaseCommond.appendUrl(url));
		return temp;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public byte[] image(ProgressListener l, String url) {
		//
		byte[] temp = conn.read(l, url);
		return temp;
	}

	/**
	 * 
	 * @param obj
	 * @param l
	 * @param url
	 * @param uid
	 * @param ver
	 * @return
	 */
	public byte[] image(String filename, Object obj, ProgressListener l,
			String url) {
		//
		UploadData upload = new UploadData();
		upload.tag = obj;
		upload.responseFileName = filename;
		upload.progressListener = l;
		upload.strType = "application/octet-stream";
		// upload.strFilename = url;
		upload.nType = 3;
		byte[] temp = conn.read(l, BaseCommond.appendUrl(url), upload);
		return temp;
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public byte[] read(ProgressListener l, String url, String data) {
		//
		byte[] temp = null;
		if (!TextUtils.isEmpty(data)) {
			UploadData upload = new UploadData();
			upload.progressListener = l;
			upload.strType = "application/x-www-form-urlencoded";
			upload.nType = 1;
			upload.strText = data;
			temp = conn.read(l, BaseCommond.appendUrl(url), upload);
		} else
			temp = conn.read(l, BaseCommond.appendUrl(url));
		return temp;
	}

	/**
	 * 
	 * @param obj
	 * @param l
	 * @param url
	 * @param uid
	 * @param imei
	 * @param ver
	 * @param title
	 * @param filename
	 * @param data
	 * @return
	 */
	public byte[] postPhoto(Object obj, ProgressListener l, String url,
			String title, String filename) {
		String newurl = BaseCommond.appendUrl(url);
		UploadData upload = new UploadData();
		upload.tag = obj;
		upload.progressListener = l;
		upload.nType = 2;
		upload.strText = title;
		upload.strFilename = filename;
		upload.strType = POST_PHOTOTYPE;
		byte[] temp = conn.read(l, newurl, upload);
		return temp;
	}

	/**
	 * 
	 * @param url
	 * @param formData
	 * @return
	 */
	public byte[] multipartForm(ProgressListener l, String url, String formData) {
		UploadData upload = new UploadData();
		upload.progressListener = l;
		upload.nType = 2;
		upload.strText = formData;
		upload.strType = POST_PHOTOTYPE;
		byte[] temp = conn.read(l, url, upload);
		return temp;
	}
}
