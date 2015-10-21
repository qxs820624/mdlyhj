package com.duom.tool;
public class Const {
	////////////创建表 用到的一些常量， 和建表语句 ////////////
	public static final String DATABASE_NAME = "picture1";
	public static final String TABLE_NAME = "tb_picture1";
	public static final String KEY_ID = "_id" ;
	public static final String KEY_ZHUTI = "zhuti";
	public static final String KEY_DIZHI = "dizhi";
	public static final String KEY_ROUTE = "route";
	public static final String KEY_PATH = "path";
	public static final String KEY_MUSIC = "music";
	public static final String KEY_BG = "bg";
	public static final String KEY_HUMOR = "humor";
	public static final int VERSION = 1;
	public static final String  CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " integer primary key autoincrement," + KEY_ZHUTI + " text," + KEY_DIZHI + " text," + KEY_MUSIC + " int," + KEY_BG + " int," + KEY_PATH + " text," + KEY_ROUTE +" text," + KEY_HUMOR + " text)";	
}
