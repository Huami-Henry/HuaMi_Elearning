package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.huami.elearning.TApplication;
import com.huami.elearning.model.PlayInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 点阵表正式表
 */
public class PlaySqlTool {
	private final String TAG = PlaySqlTool.class.getSimpleName();
	private static PlaySqlTool instance = null;
	private PlayHelper dbHelper = null;

	private PlaySqlTool(Context context) {
		dbHelper = new PlayHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new PlaySqlTool(context);
		}
	}

	public static PlaySqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}

	/**
	 * 将媒资信息保存到数据库
	 * @param info 数据集 先判断数据库是否有此数据
	 */
	public void insertInfo(PlayInfo info) {
		boolean exit = exit(info.getPlay_key());
		if (!exit) {
			try {
				SQLiteDatabase database = dbHelper.getWritableDatabase();
				String sql = "insert into " + PlayHelper.TB_NAME + "(play_key,play_file) values (?,?)";
				Object[] bindArgs = {info.getPlay_key(), info.getPlay_file()};
				database.execSQL(sql, bindArgs);
			} catch (Exception e) {
				Log.e("插入操作", "插入失败");
			}
		} else {
			update(info);
		}
	}
	public boolean exit(String key){
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "select * from "+ PlayHelper.TB_NAME+" where play_key=?";
			Cursor cursor = database.rawQuery(sql, new String[] {key});
			while (cursor.moveToNext()) {
				cursor.close();
				return true;
			}
		} catch (Exception e) {
			Log.e("我的查询是否存在", e.getMessage());
		}
		return false;
	}
	public PlayInfo getFileInfo(String key){
		PlayInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ PlayHelper.TB_NAME+" where play_key=?";
		Cursor cursor = database.rawQuery(sql, new String[] {key});
		while (cursor.moveToNext()) {
			info = new PlayInfo(cursor.getString(0),cursor.getString(1));
			cursor.close();
			return info;
		}
		return info;
	}
	public List<PlayInfo> getAllInfo(){
		List<PlayInfo> infos = new ArrayList<>();
		PlayInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ PlayHelper.TB_NAME;
		Cursor cursor = database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			info = new PlayInfo(cursor.getString(0),cursor.getString(1));
			infos.add(info);
		}
		cursor.close();
		return infos;
	}
	/** 更新数据库中的数据 */
	public void update(PlayInfo info) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql="update "+PlayHelper.TB_NAME+" set "+PlayHelper.PLAY_FILE+"='"+info.getPlay_file()+"' where "+PlayHelper.PLAY_KEY+"="+info.getPlay_key();
		database.execSQL(sql);
	}

	public int countRow() {
		return (int) allCaseNum();
	}
	/**
	 * 查询数据库中的总条数.
	 * @return
	 */
	public long allCaseNum(){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select count(*) from "+PlayHelper.TB_NAME;
		Cursor cursor = database.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}
}