package com.huami.elearning.db;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.huami.elearning.TApplication;
import com.huami.elearning.model.FeedBackInfo;
import com.huami.elearning.model.FileInfo;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.TemporaryInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 数据库操作工具类
 */
public class FileSqlTool {
	private final String TAG = FileSqlTool.class.getSimpleName();
	private static FileSqlTool instance = null;
	private FileHelper dbHelper = null;

	private FileSqlTool(Context context) {
		dbHelper = new FileHelper(context);
	}
	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new FileSqlTool(context);
		}
	}
	public static FileSqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}
	/** 将媒资信息保存到数据库 */
	public void insertFile(FileInfo info) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			boolean b = exitData(info.getFile_name());
			if (!b) {
				String sql = "insert into " + FileHelper.TB_NAME + "(asset_id,file_url,file_name,asset_type,file_path,click_count,create_time) values (?,?,?,?,?,?,?)";
				Object[] bindArgs = {
						info.getAsset_id(),
						info.getFile_url(),
						info.getFile_name(),
						info.getAsset_type(),
						info.getFile_path(),
						info.getClick_count(),
						info.getCreate_time()
				};
				database.execSQL(sql, bindArgs);
			}
		} catch (Exception e) {
			Log.e("我的异常是啥", e.getMessage());
		}
	}
	/**
	 * 判断数据库是否有此数据
	 * @param file_name
	 * @return
	 */
	private  boolean exitData(String file_name) {
		FileInfo info = getInfo(file_name);
		if (info != null) {
			return true;
		}
		return false;
	}
	public FileInfo getInfo(String fileName) {
		FileInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ FileHelper.TB_NAME+" where file_name=?";
		Cursor cursor = database.rawQuery(sql, new String[] {fileName});
		while (cursor.moveToNext()) {
			info = new FileInfo(cursor.getInt(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getInt(3),
					cursor.getString(4),
					cursor.getInt(5),
					cursor.getString(6));
			cursor.close();
		}
		return info;
	}
}