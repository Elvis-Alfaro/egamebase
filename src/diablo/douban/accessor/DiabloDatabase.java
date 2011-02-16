package diablo.douban.accessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import diablo.douban.accessor.pojo.DoubanAuthData;

public class DiabloDatabase {
	public static final String TAG = "DoubanDiablo";
	private static final String DATABASE_NAME = "diablo.db";
	private static final String TABLE_NAME = "douban_users";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_DIABLO_TABLE = "CREATE TABLE "
			+ TABLE_NAME
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, username TEXT, token TEXT, secret TEXT, priority INTEGER, icon TEXT);";

	private static final String IMAGE_MAP = "image_map";
	private static final String CREATE_IMAGE_MAP = "CREATE TABLE image_map"
			+ "(img_url TEXT PRIMARY KEY, img_data BLOB);";

	private DatabaseHelper dbHelper;

	public DiabloDatabase(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void close(){
		if(dbHelper != null)
			dbHelper.close();
	}
	public void insert(String userid, String username, String token,
			String secret, String icon) {
		dbHelper.getWritableDatabase().execSQL(
				"INSERT INTO " + TABLE_NAME
						+ "(userid, username, token, secret, icon) VALUES ('"
						+ userid + "', '" + username + "', '" + token + "', '"
						+ secret + "', '"  + icon +  "')");
//		dbHelper.getWritableDatabase().close();
	}

	public DoubanAuthData queryFirst() {
		Cursor cursor = dbHelper.getReadableDatabase().query(TABLE_NAME,
				new String[] { "id", "userid", "username", "token", "secret", "icon" },
				null, null, null, null, null, "1");
		DoubanAuthData data = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Log
					.i("DoubanDiablo", " getColumnCount: "
							+ cursor.getColumnCount());

			data = new DoubanAuthData(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3), cursor
							.getString(4),  cursor.getString(5));
		}
		cursor.close();
//		dbHelper.getReadableDatabase().close();
		return data;
	}

	public DoubanAuthData query(String uid) {
		Cursor cursor = dbHelper.getReadableDatabase().query(TABLE_NAME,
				new String[] { "id", "userid", "username", "token", "secret", "icon" },
				"userid=?", new String[] { uid }, null, null, null, null);
		DoubanAuthData data = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Log.i("DoubanDiablo", "getColumnCount: " + cursor.getColumnCount());
			data = new DoubanAuthData(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3), cursor
							.getString(4), cursor.getString(5));
		}
		cursor.close();
//		dbHelper.getReadableDatabase().close();
		return data;
	}

	public List<DoubanAuthData> queryAll() {
		List<DoubanAuthData> list = new ArrayList<DoubanAuthData>();
		Cursor cursor = dbHelper.getReadableDatabase().query(TABLE_NAME,
				new String[] { "id", "userid", "username", "token", "secret", "icon" },
				null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				list.add(new DoubanAuthData(cursor.getInt(0), cursor
						.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4), cursor.getString(5)));
			}
		}
		cursor.close();
//		dbHelper.getReadableDatabase().close();
		return list;
	}

	public byte[] queryImage(String url) {
		try {
			Cursor cursor = dbHelper.getReadableDatabase().query("image_map",
					new String[] { "img_data" }, "img_url=?",
					new String[] { url }, null, null, null);
			if (cursor.moveToFirst()) {
				return cursor.getBlob(0);
			} else {
				return null;
			}
		} finally {
//			dbHelper.getReadableDatabase().close();
		}
	}

	public Map<String, byte[]> queryImages(String[] urls) {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		String where = "img_url in (";
		for (int i = 0; i < urls.length; i++) {
			where += "?,";
		}
		where = where.substring(0, where.length() - 1) + ")";
		Cursor cursor = dbHelper.getReadableDatabase().query("image_map",
				new String[] { "img_url", "img_data" }, where, urls, null,
				null, null);
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				map.put(cursor.getString(0), cursor.getBlob(1));
			}
		}
		cursor.close();
//		dbHelper.getReadableDatabase().close();
		return map;
	}

	public void insertImage(String url, byte[] data) {
		ContentValues map = new ContentValues();
		map.put("img_url", url);
		map.put("img_data", data);
		dbHelper.getWritableDatabase().execSQL(
				"INSERT OR IGNORE INTO " + IMAGE_MAP + " VALUES (?, ?)",
				new Object[] { url, data });
		// return dbHelper.getWritableDatabase().insert(IMAGE_MAP, null, map);
//		dbHelper.getWritableDatabase().close();
	}

	public class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DIABLO_TABLE);
			db.execSQL(CREATE_IMAGE_MAP);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS image_map ");
			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			Log.i("DoubanDiablo", "db opened");
			// TODO 每次成功打开数据库后首先被执行
		}
	}
}
