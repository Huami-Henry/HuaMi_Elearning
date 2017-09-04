package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 模板信息表
 */
public class TemplateHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "template.db";
	public static final String TB_NAME = "template_info";
	private static final int DOWNLOAD_VERSION = 1;
	public static final String ID = "id";
	public static final String TEMPLATE_ID = "template_id";
	public static final String TEMPLATE_PATH = "template_path";//1111
	public static final String TEMPLATE_URL = "template_url";
	public static final String TEMPLATE_STATE = "template_state";//解压状态 0  1
	public static final String TEMPLATE_DOWNSTATE = "template_down_state";//下载状态 0  1
	public TemplateHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TB_NAME + "(" + ID+" integer PRIMARY KEY AUTOINCREMENT ,"+TEMPLATE_ID + " integer , " + TEMPLATE_PATH + " char(50) ,"+ TEMPLATE_URL +" char(50) ,"+ TEMPLATE_STATE + " integer ,"+ TEMPLATE_DOWNSTATE + " integer)";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
