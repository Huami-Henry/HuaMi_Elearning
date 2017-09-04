package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 利用数据库来记录xml信息
 */
public class PlayHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "play.db";
	public static final String TB_NAME = "play_info";
	private static final int DOWNLOAD_VERSION = 1;
	public final static String PLAY_KEY = "play_key";
	public final static String PLAY_FILE = "play_file";
	public PlayHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TB_NAME + "(" + PLAY_KEY + " char(20) , " + PLAY_FILE + " char(50))";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
