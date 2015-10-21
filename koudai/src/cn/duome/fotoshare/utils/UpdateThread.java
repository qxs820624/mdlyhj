package cn.duome.fotoshare.utils;

import java.util.ArrayList;

import cn.duome.fotoshare.entity.RequestDataEntity;

import com.hutuchong.inter_face.ActivityHandle;

/**
 * 
 * @author 3gqa.com
 * 
 */
public class UpdateThread extends Thread {

	private ActivityHandle handler;
	private boolean isForever = false;

	private boolean isRunning = true;

	private ArrayList<RequestDataEntity> entitys = new ArrayList<RequestDataEntity>(
			0);

	/**
	 * 
	 * @param handler
	 * @param urls
	 */
	public UpdateThread(ActivityHandle handler,
			boolean isLoop) {
		this.handler = handler;
		this.isForever = isLoop;
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
	public UpdateThread(ActivityHandle handler,
			RequestDataEntity dataEntity) {
		this.handler = handler;
		this.entitys.add(dataEntity);
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
					handler.onUpdateRequest(entity);
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
