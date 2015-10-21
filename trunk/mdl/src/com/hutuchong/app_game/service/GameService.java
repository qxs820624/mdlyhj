package com.hutuchong.app_game.service;

import com.hutuchong.util.BaseService;
import com.hutuchong.util.ContantValue;
import com.hutuchong.util.ServiceHandle;

public class GameService extends BaseService implements ServiceHandle {
	public static String ServiceName = "com.hutuchong.app_game.service.GameService";

	public void initService(boolean isProxy) {
		super.initService(ContantValue.softUrl, "ddgame", isProxy);
	}
}