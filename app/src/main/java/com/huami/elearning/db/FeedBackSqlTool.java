package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huami.elearning.TApplication;
import com.huami.elearning.model.FeedBackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 */
public class FeedBackSqlTool{
	private final String TAG = FeedBackSqlTool.class.getSimpleName();
	private static FeedBackSqlTool instance = null;
	private FeedBackHelper dbHelper = null;
	private FeedBackSqlTool(Context context) {
		dbHelper = new FeedBackHelper(context);
	}
	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new FeedBackSqlTool(context);
		}
	}
	public static FeedBackSqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}

	/** 将反馈信息保存到数据库 */
	public void insertInfos(List<FeedBackInfo> infos) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (FeedBackInfo info : infos) {
			boolean b = exitData(info.getFeed_down_id());
			if (!b) {
				String sql = "insert into " + FeedBackHelper.TB_NAME + "(" + dbHelper.FEED_BACK_DOWNID + "," + dbHelper.FEED_BACK_STATE + "," + dbHelper.FEED_BACK_FILE_NAME + "," + dbHelper.FEED_DOWN_STATE+","+dbHelper.CREATE_DATE + ") values (?,?,?,?,?)";
				Object[] bindArgs = {info.getFeed_down_id(), info.getFeed_state(), info.getFile_name(), info.getDown_state(),info.getCreate_date()};
				database.execSQL(sql, bindArgs);
			} else {
				updateFeedState(info.getFeed_down_id(), 1);
			}
		}
	}
	/** 将反馈信息保存到数据库 */
	public void insertInfo(FeedBackInfo info) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		boolean b = exitData(info.getFeed_down_id());
		if (!b) {
			String sql = "insert into " + FeedBackHelper.TB_NAME + " ("+dbHelper.FEED_BACK_DOWNID+","+dbHelper.FEED_BACK_STATE+","+dbHelper.FEED_BACK_FILE_NAME+","+dbHelper.FEED_DOWN_STATE+","+dbHelper.CREATE_DATE+") values (?,?,?,?,?)";
			Object[] bindArgs = {info.getFeed_down_id(),info.getFeed_state(),info.getFile_name(),info.getDown_state(),info.getCreate_date()};
			database.execSQL(sql, bindArgs);
		}
	}
	public List<FeedBackInfo> getRenderData(int state){
		List<FeedBackInfo> infos = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM "+FeedBackHelper.TB_NAME+" WHERE "+dbHelper.FEED_DOWN_STATE+"=1 and "+dbHelper.FEED_BACK_STATE+"=?";
		Cursor cursor =database.rawQuery(sql, new String[]{""+state});
		while (cursor.moveToNext()) {
			FeedBackInfo info = new FeedBackInfo(cursor.getString(0),
					cursor.getInt(1), cursor.getString(2),cursor.getInt(3),cursor.getString(4));
			infos.add(info);
		}
		cursor.close();
		return infos;
	}
	/**
	 * 判断数据库是否有此数据
	 * @param down_id
	 * @return
	 */
	public boolean exitData(String down_id) {
		return getInfo(down_id);
	}
	private boolean getInfo(String down_id) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from "+ FeedBackHelper.TB_NAME+" where "+dbHelper.FEED_BACK_DOWNID+"=?";
		Cursor cursor = database.rawQuery(sql, new String[] {down_id});
		while (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		return false;
	}
	/** 获取xml信息 */
	public List<FeedBackInfo> getAllInfos() {
		List<FeedBackInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		Cursor cursor = database.query(FeedBackHelper.TB_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			FeedBackInfo info = new FeedBackInfo(
					cursor.getString(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getInt(3),cursor.getString(4));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/**
	 * 获取保存过的文件
	 * @param file_name
	 * @return
	 */
	public List<FeedBackInfo> getInfoWithFileName(String file_name) {
		List<FeedBackInfo> infos = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from " + FeedBackHelper.TB_NAME + " where " + dbHelper.FEED_BACK_FILE_NAME + "=?";
		Cursor cursor = database.rawQuery(sql, new String[]{file_name});
		while (cursor.moveToNext()) {
			FeedBackInfo info = new FeedBackInfo(
					cursor.getString(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getInt(3),cursor.getString(4)
			);
			infos.add(info);
		}
		cursor.close();
		return infos;
	}

	/** 关闭数据库 */
	public void closeDb() {
		dbHelper.close();
	}
	/** 删除数据库中的数据 */
	public void delete(String down_id) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.delete(FeedBackHelper.TB_NAME, dbHelper.FEED_BACK_DOWNID+"=?", new String[] {down_id });
	}
	/** 删除数据库中的数据 */
	public void deleteWithFile(String fileName) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.delete(FeedBackHelper.TB_NAME, dbHelper.FEED_BACK_FILE_NAME+"=?", new String[] {fileName });
	}
	/**
	 * 更新数据库信息
	 * @param down_id
	 * @param state 0 (默认)代表没有汇报 1代表汇报完成
	 */
	public void updateFeedState(String down_id, int state) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+FeedBackHelper.TB_NAME+" set "+dbHelper.FEED_BACK_STATE+"="+state+" where "+dbHelper.FEED_BACK_DOWNID+"='"+down_id+"' and "+dbHelper.FEED_BACK_STATE+"=0";
			database.execSQL(sql);
			delete(down_id);
		} catch (Exception e) {
		}
	}
	/**
	 * 更新数据库信息
	 * @param fileName
	 * @param down_state 0 (默认)代表没有下载完成 1代表下载完成
	 */
	public void updateDownState(String fileName, int down_state) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+FeedBackHelper.TB_NAME+" set "+dbHelper.FEED_DOWN_STATE+"="+down_state+" where "+dbHelper.FEED_BACK_FILE_NAME+"='"+fileName+"' and "+dbHelper.FEED_DOWN_STATE+"=0";
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}
}