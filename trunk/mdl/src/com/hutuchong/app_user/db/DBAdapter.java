package com.hutuchong.app_user.db;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TITLE = "title";
	public static final String KEY_LINK = "link";
	public static final String KEY_DESC = "desc";
	public static final String KEY_IMAGE1 = "image1";
	public static final String KEY_IMAGE2 = "image2";
	public static final String KEY_FAVLINK = "favlink";
	public static final String KEY_PUBDATE = "pubDate";
	public static final String KEY_READ = "read";
	//
	public static final String KEY_KEY = "meta_key";
	public static final String KEY_VALUE = "meta_value";
	public static final String LAST_TIME = "lasttime";
	//
	private static final String DATABASE_NAME = "userdb";
	private static final String DATABASE_TABLE_CONFIG = "config";
	private static final String DATABASE_TABLE_MSG = "selfmsg";
	private static final int DATABASE_VERSION = 1;
	//
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//
			String msgsql = "create table "
					+ DATABASE_TABLE_MSG
					+ " (_id integer primary key autoincrement, "
					+ " type text not null,title text not null, link text null, "
					+ KEY_DESC + " text null," + KEY_IMAGE1 + " text null,"
					+ KEY_IMAGE2 + " text null," + KEY_FAVLINK + " text null,"
					+ " pubDate text not null,"
					+ " read boolean not null default 0 );";
			db.execSQL(msgsql);
			//
			String confsql = "create table " + DATABASE_TABLE_CONFIG
					+ " (_id integer primary key autoincrement, "
					+ " meta_key text not null, meta_value text not null);";
			db.execSQL(confsql);
			// 初始化一条记录,以后就只需要更新即可
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_KEY, LAST_TIME);
			initialValues.put(KEY_VALUE, "0");
			db.insert(DATABASE_TABLE_CONFIG, null, initialValues);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS selfmsg");
			onCreate(db);
		}
	}

	/**
	 * 打开数据库
	 * 
	 * @return
	 * @throws SQLException
	 */

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * 关闭数据库
	 */

	public void close() {
		DBHelper.close();
	}

	/**
	 * 向数据库中插入一个标题
	 * 
	 * @param itemid
	 * @param type
	 * @param title
	 * @param link
	 * @param pubDate
	 * @return
	 */

	public long insertItem(String type, String title, String link, String desc,
			String image1, String image2, String favlink, String pubDate) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_LINK, link);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_IMAGE1, image1);
		initialValues.put(KEY_IMAGE2, image2);
		initialValues.put(KEY_FAVLINK, favlink);
		initialValues.put(KEY_PUBDATE, pubDate);
		return db.insert(DATABASE_TABLE_MSG, null, initialValues);
	}

	/**
	 * 标记已读/未读
	 * 
	 * @param rowId
	 * @param read
	 * @return
	 */

	public boolean setReaded(String rowId, boolean read) {
		ContentValues args = new ContentValues();
		args.put(KEY_ROWID, rowId);
		args.put(KEY_READ, read ? "1" : "0");
		open();
		boolean result = db.update(DATABASE_TABLE_MSG, args, KEY_ROWID + "="
				+ rowId, null) > 0;
		close();
		return result;
	}

	/**
	 * 检索列表
	 * 
	 * @return
	 */
	public int getUnReadCount() {
		int count = 0;
		String where = KEY_READ + "=0";
		open();
		Cursor c = db.query(DATABASE_TABLE_MSG, new String[] { KEY_ROWID },
				where, null, null, null, null);
		if (c != null) {
			count = c.getCount();
		}
		close();
		return count;
	}

	/**
	 * 删除一个指定标题
	 * 
	 * @param rowId
	 * @return
	 */

	public boolean deleteItem(long rowId) {
		open();
		boolean result = db.delete(DATABASE_TABLE_MSG, KEY_ROWID + "=" + rowId,
				null) > 0;
		close();
		return result;
	}

	/**
	 * 检索列表
	 * 
	 * @return
	 */
	public RSSChannel getItems(int start, int count) {
		open();
		RSSChannel channel = null;
		Cursor c = db.query(DATABASE_TABLE_MSG, new String[] { KEY_ROWID,
				KEY_TYPE, KEY_TITLE, KEY_LINK, KEY_DESC, KEY_IMAGE1,
				KEY_IMAGE2, KEY_FAVLINK, KEY_PUBDATE, KEY_READ }, null, null,
				null, null, null);
		if (c != null) {
			channel = new RSSChannel();
			boolean has = c.moveToFirst();
			while (has) {
				RSSItem item = new RSSItem();
				item.setId(c.getString(0));
				item.setCategory(c.getString(1));
				item.setTitle(c.getString(2));
				item.setLink(c.getString(3));
				item.setDescription(c.getString(4));
				item.setImageUrl(c.getString(5));
				item.setThumbailUrl(c.getString(6));
				item.setFavLink(c.getString(7));
				item.setPubDate(c.getString(8));
				item.setReaded(c.getShort(9) == 0 ? false : true);
				//
				channel.addItem(item);
				//
				has = c.moveToNext();
			}
		}
		close();
		return channel;
	}

	/**
	 * 获取最大的条目编号
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Cursor getLastTime() throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE_CONFIG, new String[] {
				KEY_ROWID, KEY_KEY, KEY_VALUE, }, KEY_KEY + "='" + LAST_TIME
				+ "'", null, null, null, null, null);
		return mCursor;
	}

	/**
	 * 修改最后获取到的时间
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean updateLastDate(String lasttime) throws SQLException {
		ContentValues args = new ContentValues();
		args.put(KEY_VALUE, lasttime);
		return db.update(DATABASE_TABLE_CONFIG, args, KEY_KEY + "='"
				+ LAST_TIME + "'", null) > 0;
	}
}
