package cn.coolworks.util;

import java.util.ArrayList;

import cn.coolworks.handle.ActivityHandle;

import com.hutuchong.util.BaseService;

/**
 * 
 * @author 3gqa.com
 * 
 */
public class UpdateThread extends Thread {

	private ActivityHandle handler;
	private boolean isForever = false;

	private boolean isRunning = true;
	private BaseService mService;

	private ArrayList<RequestDataEntity> entitys = new ArrayList<RequestDataEntity>(
			0);

	/**
	 * 
	 * @param handler
	 * @param urls
	 */
	public UpdateThread(ActivityHandle handler, BaseService service,
			boolean isLoop) {
		this.handler = handler;
		this.isForever = isLoop;
		this.mService = service;
	}

	/**
	 * 
	 * @param handler
	 * @param urls
	 */
	public UpdateThread(ActivityHandle handler,
			ArrayList<RequestDataEntity> urls) {
		this.handler = handler;
		this.entitys = urls;
	}

	/**
	 * 
	 * @param handler
	 * @param dataEntity
	 */
	public UpdateThread(ActivityHandle handler, BaseService service,
			RequestDataEntity dataEntity) {
		this.handler = handler;
		this.entitys.add(dataEntity);
		this.mService = service;
	}

	/**
	 * 
	 * @param dataEntity
	 */
	public void addRequest(RequestDataEntity dataEntity) {
		this.entitys.add(dataEntity);
	}

	/**
	 * 
	 * @return
	 */
	public boolean startRequest() {
		isRunning = true;
		this.start();
		return true;
	}

	/**
	 * 
	 */
	public void stopRequest() {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				//
				if (entitys.size() > 0) {
					RequestDataEntity entity = this.entitys.get(0);
					handler.onUpdateRequest(this.mService, entity);
					entitys.remove(entity);
					entity = null;
				} else {
					if (!isForever) {
						break;
					}
				}
				sleep(100);
			} catch (Exception ex) {
				isRunning = false;
				ex.printStackTrace();
			} finally {

			}
		}
		entitys.clear();
	}
}
