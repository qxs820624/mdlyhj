package com.jw.http;


public interface ProgressListener {

	public void onProgress(Object tag, int progress);

	public void onError(String msg);

	public void onBaseUrl(String url);
}