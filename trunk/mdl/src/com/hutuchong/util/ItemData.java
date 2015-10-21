package com.hutuchong.util;

public class ItemData {
	public final static int STATUS_PLAYING = 0;
	public final static int STATUS_STOP = 1;
	public String progress;
	public int status = STATUS_STOP;
	public String tag;
	public boolean downloaded = false;
}