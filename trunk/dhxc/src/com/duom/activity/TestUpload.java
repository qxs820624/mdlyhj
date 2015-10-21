package com.duom.activity;

import com.jingxunnet.cmusic.R;
import jw.http.ConnectPoolManager;
import jw.http.UploadData;
public class TestUpload {

	public ConnectPoolManager conn;

	public TestUpload(boolean isProxy) {
		conn = new ConnectPoolManager(isProxy);
	}

	public void release() {
		conn.releaseAll();
	}

	/**
	 * 上传文件和数据
	 * @param url
	 *            请求url
	 * @param formdata
	 *            key=value&key=value形式的post文本数据
	 * @param filename
	 *            需要上传的文件名
	 * @return
	 */
	public byte[] upload(String url, String formdata, String filename) {
		UploadData upload = new UploadData();
		upload.nType = 2;
		upload.strText = formdata;
		upload.strFilename = filename;
		upload.strType = "multipart/form-data";
		byte[] temp = conn.read(null, url, upload);
		return temp;
	}
}
