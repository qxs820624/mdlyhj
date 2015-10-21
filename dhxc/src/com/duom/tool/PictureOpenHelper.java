package com.duom.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PictureOpenHelper extends SQLiteOpenHelper{

	public PictureOpenHelper(Context context) {
		super(context, Const.DATABASE_NAME, null, Const.VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Const.CREATE_TABLE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
