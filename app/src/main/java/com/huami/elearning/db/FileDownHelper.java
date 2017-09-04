package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huami.elearning.base.BaseConsts;

/**
 * 利用数据库来记录xml信息
 */
public class FileDownHelper extends SQLiteOpenHelper {
	public static final String TB_NAME = "filedown";
	public static final String DB_NAME = "filedown.db";
	private static final int DOWNLOAD_VERSION = 1;
	public FileDownHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+TB_NAME+"(assert_id integer,file_url varchar(50),file_name varchar(20),md5 varchar(50),file_type integer,file_pri integer,file_length integer,file_progress integer,down_id integer,down_state integer,render_state integer)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
