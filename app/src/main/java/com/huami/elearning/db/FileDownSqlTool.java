package com.huami.elearning.db;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.huami.elearning.TApplication;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 */
public class FileDownSqlTool {
	private final String TAG = FileDownSqlTool.class.getSimpleName();
	private static FileDownSqlTool instance = null;
	private FileDownHelper dbHelper = null;

	private FileDownSqlTool(Context context) {
		dbHelper = new FileDownHelper(context);
	}
	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new FileDownSqlTool(context);
		}
	}
	public static FileDownSqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}

	/**
	 * 按优先级查询资源文件
	 * @param down_state 0未下载的状态
	 * @return
	 */
	public List<FileDownInfo> getMaxPris(int down_state) {
		List<FileDownInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM "+FileDownHelper.TB_NAME+" WHERE render_state=0 and down_state=?";
		Cursor cursor =database.rawQuery(sql, new String[]{""+down_state});
		while (cursor.moveToNext()) {
			FileDownInfo info = new FileDownInfo(
					cursor.getInt(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5),
					cursor.getInt(6),
					cursor.getInt(7),
					cursor.getInt(8),
					cursor.getInt(9),
					cursor.getInt(10));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/**
	 * 按优先级查询资源文件
	 * @return
	 */
	public List<FileDownInfo> getAll() {
		List<FileDownInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM "+FileDownHelper.TB_NAME;
		Cursor cursor =database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			FileDownInfo info = new FileDownInfo(
					cursor.getInt(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5),
					cursor.getInt(6),
					cursor.getInt(7),
					cursor.getInt(8),
					cursor.getInt(9),
					cursor.getInt(10));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/** 将下载信息保存到数据库 */
	public void insertInfo(FileDownInfo info) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "insert into " + FileDownHelper.TB_NAME + "(assert_id,file_url,file_name,md5,file_type,file_pri,file_length,file_progress,down_id,down_state,render_state) values (?,?,?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = {
					info.getAssert_id(),
					info.getFile_url(),
					info.getFile_name(),
					info.getMd5(),
					info.getFile_type(),
					info.getFile_pri(),
					info.getFile_length(),
					info.getFile_progress(),
					info.getDown_id(),
					info.getDown_state(),
					info.getRender_state()
			};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {

		}
	}
	/**
	 * 判断文件是否存在 存在返回文件长度 不存在返-1
	 */
	public int checkLength(String url){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from " + FileDownHelper.TB_NAME + " where file_url=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+url});
		while (cursor.moveToNext()) {
			int length = cursor.getInt(7);
			return length;
		}
		return -1;
	}
	/**
	 * 返回文件的大小
	 */
	public int checkDownState(String url,int down_id){
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "select * from " + FileDownHelper.TB_NAME + " where down_id=? and file_url=?";
			Cursor cursor = database.rawQuery(sql, new String[] {String.valueOf(down_id),url});
			while (cursor.moveToNext()) {
				int length = cursor.getInt(7);
				return length;
			}
		} catch (Exception e) {

		}
		return -1;
	}

	/**
	 * 删除表中汇报状态为2的
	 * @param render_state
	 */
	public void delete(int render_state) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			database.delete(FileDownHelper.TB_NAME, "render_state=?", new String[]{String.valueOf(render_state)});
		} catch (Exception e) {

		}
	}

	/**
	 * 更新文件的下载状态
	 * @param url 文件的地址
	 * @param down_state 文件的下载状态
	 */
	public void updateDownState(String url,int down_state){
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update " + FileDownHelper.TB_NAME + " set down_state=" + down_state + " where file_url='" + url + "'";
			database.execSQL(sql);
		} catch (Exception e) {
			Log.e("我的更新","更新出错"+e.getMessage());
		}
	}
	/**
	 * 更新文件的下载状态
	 * @param url 文件的地址
	 * @param size 文件的下载状态
	 */
	public void updateFileSize(String url,long size){
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update " + FileDownHelper.TB_NAME + " set file_length=" + size+" where file_url='"+url+"'";
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}

	/**
	 * 更新文件的汇报状态
	 * @param url 文件的url
	 * @param render_state 文件的汇报状态
	 * @param down_id 文件的下载id
	 */
	public void updateRenderState(String url,int render_state,int down_id){
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update " + FileDownHelper.TB_NAME + " set down_state=" + render_state+" where down_id="+down_id+" and file_url='"+url+"'";
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}
}