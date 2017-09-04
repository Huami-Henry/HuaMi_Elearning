package com.huami.elearning.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 利用数据库来记录xml信息
 */
public class RecordHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "record.db";
	public static final String TB_NAME = "record_info";
	private static final int DOWNLOAD_VERSION = 1;
	private final String RECORD_ID= "id";
	private final String RECORD_FILE = "record_file";
	private final String RECORD_TIME = "record_time";
	public RecordHelper(Context context) {
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
		String sql = "create table " + TB_NAME + "(" + RECORD_ID +" integer PRIMARY KEY AUTOINCREMENT,"+ RECORD_FILE + " varchar(20) , " + RECORD_TIME + " varchar(50))";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
