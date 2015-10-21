package com.hutuchong.app_user.service;

import com.hutuchong.util.BaseService;
import com.hutuchong.util.ContantValue;
import com.hutuchong.util.ServiceHandle;

public class UserService extends BaseService implements ServiceHandle {
	public static String ServiceName = "com.hutuchong.app_user.service.UserService";

	/**
	 * 
	 */
	public void initService(boolean isProxy) {
		super.initService(ContantValue.selfmsgUrl, "ddmsg", isProxy);

	}

}
