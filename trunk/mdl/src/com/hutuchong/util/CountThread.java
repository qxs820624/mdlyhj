package com.hutuchong.util;

import cn.coolworks.handle.CountThreadHandle;

/**
 * 
 * @author 3gqa.com
 * 
 */
public class CountThread extends Thread {
	private int loopCount = 0;
	private boolean isRunning = false;
	public boolean isRunning() {
		return isRunning;
	}

	private CountThreadHandle mHandler;

	public CountThread(CountThreadHandle handler) {
		mHandler = handler;
	}

	/**
	 * 
	 * @return
	 */
	public boolean startRequest() {
		loopCount = 0;
		isRunning = true;
		this.start();
		return true;
	}

	/**
	 * 
	 */
	public void stopRunning() {
		loopCount = 0;
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				mHandler.onCount(loopCount);
				loopCount++;
				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
				isRunning = false;
			} finally {
			}
		}
	}
}
