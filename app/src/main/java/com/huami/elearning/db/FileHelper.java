package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huami.elearning.base.BaseConsts;

/**
 * 利用数据库来记录xml信息
 */
public class FileHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "media_info.db";
	public static final String TB_NAME = "media_info";
	private static final int DOWNLOAD_VERSION = 1;
	public FileHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "
				+ TB_NAME
				+ "(asset_id integer , "
				+ "file_url char(50) ," +
				"file_name char(50) PRIMARY KEY, " +
				"asset_type integer ," +
				"file_path char(50) ," +
				"click_count integer ," +
				"create_time datetime)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
