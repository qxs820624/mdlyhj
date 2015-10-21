package com.hutuchong.app_game.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

public class UpdateEntity {
	public int id = 0;
	public String saveFilePath = null;
	public String url = null;
	public String title = null;
	public Intent updateIntent;
	public PendingIntent updatePendingIntent;
	public Notification updateNotification;
}