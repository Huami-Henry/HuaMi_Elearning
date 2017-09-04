package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 利用数据库来记录xml信息
 */
public class DownloadHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "download.db";
	public static final String TB_NAME = "download";
	private static final int DOWNLOAD_VERSION = 1;

	public DownloadHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "
				+ TB_NAME
				+ "(asset_id integer PRIMARY KEY, "
				+"asset_type integer ,"
				+"file_name char(50), "
				+ "file_url char(50) ," +
				"filelength integer ," +
				"progress integer ," +
				"downCount integer ," +
				"file_mdFive char(50) ," +
				"create_time datetime," +
				"downState integer)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
