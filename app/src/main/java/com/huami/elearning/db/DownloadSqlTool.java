package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huami.elearning.TApplication;
import com.huami.elearning.model.FileDescribe;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 数据库操作工具类
 */
public class DownloadSqlTool {
	private final String TAG = DownloadSqlTool.class.getSimpleName();
	private static DownloadSqlTool instance = null;
	private DownloadHelper dbHelper = null;

	private DownloadSqlTool(Context context) {
		dbHelper = new DownloadHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new DownloadSqlTool(context);
		}
	}

	public static DownloadSqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}

	/** 将媒资信息保存到数据库 */
	public void insertInfos(List<FileDescribe> infos) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (FileDescribe info : infos) {
			boolean b = exitData(info.getAsset_id());
			if (!b) {
				String sql = "insert into " + DownloadHelper.TB_NAME + "(asset_id,file_url,file_name,asset_type,file_mdFive,downCount,progress,filelength,create_time,downState) values (?,?,?,?,?,?,?,?,?,?)";
				Object[] bindArgs = {
						info.getAsset_id(),
						info.getFileUrl(),
						info.getFileName(),
						info.getAsset_type(),
						info.getMd5(),
						info.getDownCount(),
						info.getProgress(),
						info.getFileLength(),
						info.getCreateTime(),
						info.getDownState()
				};
				database.execSQL(sql, bindArgs);
			}
		}
	}
	/** 将媒资信息保存到数据库 */
	public void insertInfos(FileDescribe info) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		boolean b = exitData(info.getAsset_id());
		if (!b) {
			String sql = "insert into " + DownloadHelper.TB_NAME + "(asset_id,file_url,file_name,asset_type,file_mdFive,downCount,progress,filelength,create_time,downState) values (?,?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = {
					info.getAsset_id(),
					info.getFileUrl(),
					info.getFileName(),
					info.getAsset_type(),
					info.getMd5(),
					info.getDownCount(),
					info.getProgress(),
					info.getFileLength(),
					info.getCreateTime(),
					info.getDownState()
			};
			database.execSQL(sql, bindArgs);
		}
	}

	private boolean exitData(int asset_id) {
		FileDescribe info = getInfo(asset_id);
		if (info == null) {
			return false;
		}
		return true;
	}

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public FileDescribe getInfo(String file_url) {
		FileDescribe info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ DownloadHelper.TB_NAME+" where file_url=?";
		Cursor cursor = database.rawQuery(sql, new String[] {file_url});
		while (cursor.moveToNext()) {
			info = new FileDescribe(cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getLong(4),
					cursor.getInt(5),
					cursor.getInt(6),
					cursor.getString(7),
					cursor.getString(8),
					cursor.getInt(9)
			);
			cursor.close();
			return info;
		}
		return info;
	}
	private FileDescribe getInfo(int asset_id) {
		FileDescribe info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ DownloadHelper.TB_NAME+" where asset_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+asset_id});
		while (cursor.moveToNext()) {
			info =new FileDescribe(cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getLong(4),
					cursor.getInt(5),
					cursor.getInt(6),
					cursor.getString(7),
					cursor.getString(8),
					cursor.getInt(9)
			);
			cursor.close();
			return info;
		}
		return info;
	}

	/**
	 * 更新数据库进度信息
	 * @param assets_id
	 * @param progress
	 */
	public void updateFileProgress(int assets_id,int progress) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+DownloadHelper.TB_NAME+" set progress="+progress+" where asset_id="+assets_id;
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}

	/**
	 * 更新数据库进度信息
	 * @param assets_id
	 * @param downState
	 */
	public void updateDownState(int assets_id,int downState) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+DownloadHelper.TB_NAME+" set downState="+downState+" where asset_id="+assets_id;
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}
	/**
	 * 更新失败下载次数
	 * @param assets_id
	 * @param count
	 */
	public void updateDowncount(int assets_id,int count) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+DownloadHelper.TB_NAME+" set downCount="+count+" where asset_id="+assets_id;
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}
}