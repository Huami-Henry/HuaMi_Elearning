package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.huami.elearning.model.RecordInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 */
public class RecordSqlTool {

	private static RecordSqlTool instance = null;
	private RecordHelper dbHelper = null;
	private final String TAG = RecordSqlTool.class.getSimpleName();

	private RecordSqlTool(Context context) {
		dbHelper = new RecordHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new RecordSqlTool(context);
		}
	}

	public static RecordSqlTool getInstance(Context context) {
		if (instance == null) {
			syncInit(context);
		}
		return instance;
	}

	/**
	 * 将媒资信息保存到数据库
	 * @param info 数据集 先判断数据库是否有此数据
	 */
	public void insertInfo(RecordInfo info) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "insert into " + RecordHelper.TB_NAME + "(record_file,record_time) values (?,?)";
			Object[] bindArgs = {info.getRecord_file(),info.getRecord_time()};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			Log.e("插入操作", "插入失败");
		}
	}
	/** 获取xml信息 */
	public List<RecordInfo> getAllInfos() {
		List<RecordInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		Cursor cursor = database.query(RecordHelper.TB_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			RecordInfo info = new RecordInfo(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/** 清空数据库中的数据 */
	public void delete(int id) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			database.delete(RecordHelper.TB_NAME, "id=?", new String[]{String.valueOf(id)});
		} catch (Exception e) {
			Log.e("删除记录信息", "出错啦" + e.getMessage());
		}
	}
}