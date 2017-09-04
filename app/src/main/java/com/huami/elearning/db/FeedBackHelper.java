package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huami.elearning.base.BaseConsts;

/**
 * 利用数据库来记录反馈信息
 */
public class FeedBackHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "feedback_info.db";
	public static final String TB_NAME = "feedback_info";
	private static final int DOWNLOAD_VERSION = 1;
	public String FEED_BACK_DOWNID = "feed_down_id";//汇报的id
	public String FEED_BACK_STATE = "feed_state";//汇报的状态0未汇报 1已汇报
	public String FEED_DOWN_STATE = "down_state";//下载的状态0未下载 1已下载
	public String FEED_BACK_FILE_NAME = "file_name";//汇报的状态0未汇报 1已汇报
	public String CREATE_DATE = "create_date";
	public FeedBackHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table "
				+ TB_NAME
				+ "("+FEED_BACK_DOWNID + " char(20) PRIMARY KEY, " + FEED_BACK_STATE + " integer, " + FEED_BACK_FILE_NAME + " char(50), " + FEED_DOWN_STATE +" char(50), " + CREATE_DATE + " datetime)";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
