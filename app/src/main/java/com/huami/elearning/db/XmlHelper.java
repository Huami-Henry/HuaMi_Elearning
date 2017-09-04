package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 利用数据库来记录xml信息
 */
public class XmlHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "xml.db";
	public static final String TB_NAME = "xml_info";
	private static final int DOWNLOAD_VERSION = 1;

	public XmlHelper(Context context) {
		super(context, DB_NAME, null, DOWNLOAD_VERSION);
	}
	/**
	 * xml_url 下载链接
	 * xml_name 下载文件名称
	 * xml_down_state 下载文件状态
	 * xml_render_state汇报状态
	 * xml_pri integer 下载文件优先级
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "
				+ TB_NAME
				+ "(downBoxId integer PRIMARY KEY, "
				+ "xml_url char ,xml_name char ,xml_down_state integer, xml_render_state integer ,xml_pri integer )");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
