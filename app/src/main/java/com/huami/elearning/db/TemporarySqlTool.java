package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.huami.elearning.TApplication;
import com.huami.elearning.model.TemporaryInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * 点阵表正式表
 */
public class TemporarySqlTool {

	private static final String TAG = TemporarySqlTool.class.getSimpleName();
	private static TemporarySqlTool instance = null;
	private TemporaryHelper dbHelper = null;

	private TemporarySqlTool(Context context) {
		dbHelper = new TemporaryHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new TemporarySqlTool(context);
		}
	}

	public static TemporarySqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}

	/**
	 * 将媒资信息保存到数据库
	 * @param info 数据集 先判断数据库是否有此数据
	 */
	public void insertInfo(TemporaryInfo info) {
		boolean exit = exit(info);
		if (!exit) {
			try {
				SQLiteDatabase database = dbHelper.getWritableDatabase();
				String sql = "insert into " + TemporaryHelper.TB_NAME + "(template_id,temporary_key,temporary_value) values (?,?,?)";
				Object[] bindArgs = {info.getTemplate_id(), info.getTemporary_key(), info.getTemporary_value()};
				database.execSQL(sql, bindArgs);
			} catch (Exception e) {
				Log.e("插入操作", "插入失败");
			}
		} else {
			//更新数据
			update(info);
		}
	}
	/** 更新数据库中的数据 */
	public void update(TemporaryInfo info) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql="update "+TemporaryHelper.TB_NAME+" set "+TemporaryHelper.TEMPORARY_VALUE+"='"+info.getTemporary_value()+"' where "+TemporaryHelper.TEMPORARY_KEY+"="+info.getTemporary_key();
		database.execSQL(sql);
	}
	public boolean exit(TemporaryInfo info){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemporaryHelper.TB_NAME+" where temporary_key=?";
		Cursor cursor = database.rawQuery(sql, new String[] {info.getTemporary_key()});
		while (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		return false;
	}
	public boolean exit(String temporary_key){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemporaryHelper.TB_NAME+" where temporary_key=?";
		Cursor cursor = database.rawQuery(sql, new String[] {temporary_key});
		while (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		return false;
	}

	public List<TemporaryInfo> getFileKey(String fileName) {
		List<TemporaryInfo> infos = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemporaryHelper.TB_NAME+" where temporary_value=?";
		Cursor cursor = database.rawQuery(sql, new String[] {fileName});
		while (cursor.moveToNext()) {
			TemporaryInfo info = new TemporaryInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
			infos.add(info);
		}
		cursor.close();
		return infos;
	}
	public List<TemporaryInfo> getAll() {
		List<TemporaryInfo> infos = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemporaryHelper.TB_NAME;
		Cursor cursor = database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			TemporaryInfo info = new TemporaryInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
			infos.add(info);
		}
		cursor.close();
		return infos;
	}
	public boolean checkEmpty(int t_id) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "select * from "+ TemporaryHelper.TB_NAME+" where template_id=?";
			Cursor cursor = database.rawQuery(sql, new String[] {""+t_id});
			while (cursor.moveToNext()) {
				cursor.close();
				return false;
			}
		} catch (Exception e) {
			Log.e("我的临时模板检控", "--->"+e.getMessage());
		}
		return true;
	}

	public void delLines(TemporaryInfo info) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			database.delete(TemporaryHelper.TB_NAME, "temporary_key=?", new String[]{info.getTemporary_key()});
			Log.e("我的操作", "开始删除临时信息");
		} catch (Exception e) {
			Log.e("删除模板信息", "出错啦" + e.getMessage());
		}
	}
}