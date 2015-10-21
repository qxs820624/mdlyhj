package cn.coolworks.util;

import cn.coolworks.handle.ActivityHandle;

import com.hutuchong.util.BaseService;

/**
 * 
 * @author 3gqa.com
 * 
 */
public class LoopUpdateThread extends Thread {
	private ActivityHandle handler;
	private RequestDataEntity entity;
	private boolean isRunning = false;
	private BaseService mService;

	public LoopUpdateThread(ActivityHandle handler, BaseService service,
			RequestDataEntity dataEntity) {
		this.handler = handler;
		this.entity = dataEntity;
		this.mService = service;
	}

	/**
	 * 
	 * @return
	 */
	public boolean startRequest() {
		if (this.entity == null || isRunning)
			return false;
		isRunning = true;
		this.start();
		return true;
	}
	/**
	 * 
	 */
	public void stopRunning() {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				handler.onUpdateRequest(this.mService,this.entity);
				Thread.sleep(5000);
			} catch (Exception ex) {
				ex.printStackTrace();
				isRunning = false;
			} finally {
			}
		}
	}
}
