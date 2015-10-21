package com.hutuchong.app_mcdonalds.service;

import com.hutuchong.util.BaseService;
import com.hutuchong.util.ServiceHandle;

public class McdonaldsService extends BaseService implements ServiceHandle {
	public static String ServiceName = "com.hutuchong.app_mcdonalds.service.McdonaldsService";

	/**
	 * 
	 */
	public void initService(boolean isProxy) {
		super.initService("http://ddnews.hutuchong.com/3g/category.php",
				"ddmcdonalds", isProxy);
	}
}
