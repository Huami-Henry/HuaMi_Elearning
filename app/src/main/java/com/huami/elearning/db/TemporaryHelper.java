package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 临时点阵表
 */
public class TemporaryHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "temporary.db";
	public static final String TB_NAME = "temporary_info";
	private static final int DOWNLOAD_VERSION = 1;
	public static final String TEMPORARY_KEY = "temporary_key";
	public static final String TEMPLATE_ID = "template_id";
	public static final String TEMPORARY_VALUE = "temporary_value";
	public TemporaryHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TB_NAME + "(" + TEMPLATE_ID + " integer , " + TEMPORARY_KEY + " char(50) ,"+ TEMPORARY_VALUE +" char(50))";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
