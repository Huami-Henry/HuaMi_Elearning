package com.huami.elearning.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.huami.elearning.TApplication;
import com.huami.elearning.model.TemplateInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 */
public class TemplateSqlTool {

	private static final String TAG = TemplateSqlTool.class.getSimpleName();
	private static TemplateSqlTool instance = null;
	private TemplateHelper dbHelper = null;

	private TemplateSqlTool(Context context) {
		dbHelper = new TemplateHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new TemplateSqlTool(context);
		}
	}

	public static TemplateSqlTool getInstance() {
		if (instance == null) {
			syncInit(TApplication.getContext());
		}
		return instance;
	}


	/**
	 * 将媒资信息保存到数据库
	 * @param info 数据集 先判断数据库是否有此数据
	 */
	public void insertInfo(TemplateInfo info) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "insert into " + TemplateHelper.TB_NAME + "("+
					TemplateHelper.TEMPLATE_ID+","+
					TemplateHelper.TEMPLATE_PATH+","+
					TemplateHelper.TEMPLATE_URL+","+
					TemplateHelper.TEMPLATE_STATE+","+
					TemplateHelper.TEMPLATE_DOWNSTATE+") values (?,?,?,?,?)";
			Object[] bindArgs = {info.getTemplate_id(), info.getTemplate_path(),info.getTemplate_url(),info.getTemplate_state(),info.getTemplate_downState()};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
		}
	}

	/**
	 * 获取模板的下载状态
	 * @param template_id
	 * @return -1模板列表中还没有此模板 0未下载  1已下载
	 */
	public int getTemplateDownState(int template_id) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+template_id});
		while (cursor.moveToNext()) {
			int state=cursor.getInt(5);
			cursor.close();
			return state;
		}
		return -1;
	}

	/**
	 * 获取一条需要下载的数据
	 * @param t_id
	 * @return
	 */
	public TemplateInfo getFileInfo(int t_id){
		TemplateInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_state=0 and template_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+t_id});
		while (cursor.moveToNext()) {
			info = new TemplateInfo(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
			cursor.close();
			return info;
		}
		return info;
	}

	/**
	 * 获取第一条未解压的模板
	 * @param template_id 模板id
	 * @return 0未下载  1正在下载  2下载完成
	 */
	public int checkDownState(int template_id) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_id=? and template_state=0 limit 1";
		Cursor cursor = database.rawQuery(sql, new String[] {""+template_id});
		while (cursor.moveToNext()) {
			int state = cursor.getInt(5);
			cursor.close();
			return state;
		}
		return 0;
	}

	/**
	 * 获取解压状态
	 * @param template_id 模板id
	 * @return 0未解压  1正在解压 2解压完成
	 */
	public int checkDecompressionState(int template_id) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+template_id});
		while (cursor.moveToNext()) {
			int state = cursor.getInt(4);
			cursor.close();
			return state;
		}
		return 0;
	}

	/**
	 * 获取第一个未解压的文件
	 * @return
	 */
	public TemplateInfo getDownTemplate() {
		TemplateInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_down_state=0 limit 1";
		Cursor cursor = database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			info = new TemplateInfo(
					cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5));
			cursor.close();
			return info;
		}
		return info;
	}
	/**
	 * 获取第一个未解压的文件
	 * @return
	 */
	public TemplateInfo getCompressTemplate() {
		TemplateInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME+" where template_state=2 order by id desc limit 1";
		Cursor cursor = database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			info = new TemplateInfo(
					cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5));
			cursor.close();
			return info;
		}
		return info;
	}
	/**
	 * 获取第一个未解压的文件
	 * @return
	 */
	public List<TemplateInfo> getAllTemplate() {
		List<TemplateInfo> infos = new ArrayList<>();
		TemplateInfo info = null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select * from "+ TemplateHelper.TB_NAME;
		Cursor cursor = database.rawQuery(sql,null);
		while (cursor.moveToNext()) {
			info = new TemplateInfo(
					cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5));
			infos.add(info);
		}
		cursor.close();
		return infos;
	}

	/**
	 * 更新当前的下载状态
	 * @param id        主键id
	 * @param downState 下载状态
	 */
	public void updateDownState(int id, int downState) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "update " + TemplateHelper.TB_NAME + " set " + TemplateHelper.TEMPLATE_DOWNSTATE + " = " + downState + " where id= " + id;
		database.execSQL(sql);
	}
	/**
	 * 更新当前的解压状态
	 * @param id        主键id
	 * @param compressState 下载状态
	 */
	public void updateCompressState(int id, int compressState) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update " + TemplateHelper.TB_NAME + " set " + TemplateHelper.TEMPLATE_STATE + " = " + compressState + " where id= " + id;
			database.execSQL(sql);
		} catch (Exception e) {
			Log.e("我的更新操作", e.getMessage());
		}
	}
}