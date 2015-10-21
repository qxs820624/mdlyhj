package com.example.constants;

/** 音乐播放操作常量和对音乐播放状态的记录 */
public class Settings {

	// 来自UI界面发送给音乐播放service的操作码
	public static final int OP_UNKNOWN = -1;
	public static final int OP_STOP = 0;
	public static final int OP_RESUME = 1;
	public static final int OP_PLAY = 2;
	public static final int OP_PAUSE = 3;

	// 来自音乐播放service发送给UI界面的更新界面操作码
	public static final int BK_UPDATE_NULL = -100;
	public static final int BK_UPDATE_BTN_PLAY = 101;
	public static final int BK_UPDATE_ALL = 102;
	public static final int BK_UPDATE_SB_PROGRESS = 103;
	public static final int BK_NEXT_MUSIC = 104;
	public static final int BK_LAST_MUSIC = 105;

	// 记录音乐播放service的播放状态的状态值
	public static final int STATUS_PLAYING = 10;
	public static final int STATUS_PAUSED = 11;
	public static final int STATUS_STOPED = 12;

	// 歌曲状态记录
	public static String path = "";
	public static String name = "";
	public static int position = 0;
	public static int seekto = 0;
	public static int status = STATUS_STOPED;
	public static boolean allowedUpdateUI = true;

};