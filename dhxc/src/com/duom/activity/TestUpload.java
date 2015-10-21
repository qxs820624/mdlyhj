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
	 * �ϴ��ļ�������
	 * @param url
	 *            ����url
	 * @param formdata
	 *            key=value&key=value��ʽ��post�ı�����
	 * @param filename
	 *            ��Ҫ�ϴ����ļ���
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
