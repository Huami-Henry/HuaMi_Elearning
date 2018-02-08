package com.huami.elearning.db;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.internal.Excluder;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.activity.HomeActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.TemporaryInfo;
import com.huami.elearning.model.XmlAsset;
import com.huami.elearning.model.XmlDownInfo;
import com.huami.elearning.model.XmlDownList;
import com.huami.elearning.model.XmlRoot;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.DownUtil;
import com.huami.elearning.util.ExampleUtil;
import com.huami.elearning.util.LanguageManager;
import com.huami.elearning.util.SPCache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * 数据库操作工具类
 */
public class XmlSqlTool {

	private static final String TAG = XmlSqlTool.class.getSimpleName();
	private static XmlSqlTool instance = null;
	private XmlHelper dbHelper = null;

	private XmlSqlTool(Context context) {
		dbHelper = new XmlHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new XmlSqlTool(context);
		}
	}

	public static XmlSqlTool getInstance(Context context) {
		if (instance == null) {
			syncInit(context);
		}
		return instance;
	}

	/**
	 * 将媒资信息保存到数据库
	 * @param infos 数据集 先判断数据库是否有此数据
	 */
	public void insertInfos(List<XmlDownInfo> infos) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (final XmlDownInfo info : infos) {
			boolean b = exitData(info.getDownBoxId());
			if (!b) {
				String sql = "insert into " + XmlHelper.TB_NAME + "(downBoxId,xml_url,xml_name,xml_down_state,xml_render_state,xml_pri) values (?,?,?,?,?,?)";
				Object[] bindArgs = {info.getDownBoxId(), info.getXml_url(), info.getXml_name(),
						info.getXml_down_state(), info.getXml_render_state(), info.getXml_pri()};
				database.execSQL(sql, bindArgs);
			} else {
				//如果数据库已经存在xml 解析xml然后
				OkHttp.asyncPost(BaseConsts.BASE_URL + info.getXml_url(), info.getXml_url(), new Callback() {
					@Override
					public void onFailure(Request request, IOException e) {
					}
					@Override
					public void onResponse(Response response) throws IOException {
						try {
							String url = (String) response.request().tag();
							String[] split = url.split("/");
							InputStream is = response.body().byteStream();
							String fileOut = CheckDisk.checkState() + BaseConsts.TEMPLATE_XML_PATH + File.separator + split[split.length - 1];
							OutputStream os = new FileOutputStream(fileOut);
							byte[] buffer = new byte[1024];
							int len;
							while ((len = is.read(buffer)) != -1) {
								os.write(buffer, 0, len);
							}
							os.flush();
							os.close();
							is.close();
							XmlDownList list = xmlParser(fileOut);
							for (XmlAsset asset : list.getAsset()) {
								long contentLength = DownUtil.getContentLength(list.getUrl());
								FileDownInfo info_down=new FileDownInfo(
										asset.getAsset_id(),
										list.getUrl()+asset.getFilename(),
										asset.getFilename(),
										asset.getMd5(),
										asset.getAsset_type(),
										info.getXml_pri(),
										contentLength,0,list.getDownId(),0,0
								);
								FileDownSqlTool.getInstance().insertInfo(info_down);
							}
							XmlSqlTool.getInstance(TApplication.getContext()).updateXmlState(info.getXml_url(), 1);
							List<FileDownInfo> maxPris = FileDownSqlTool.getInstance().getMaxPris(0);
							if (maxPris.size() > 0) {
								//提醒媒资需要更新
								Intent intent = new Intent();
								intent.setAction(BaseConsts.BROAD_UPDATE);
								TApplication.getContext().sendBroadcast(intent);
							}
						} catch (Exception e) {

						}
					}
				});
			}
		}
	}
    private XmlDownList xmlParser(String xmlPath) {
        XmlDownList downlist=null;
        List<XmlAsset> assets = new ArrayList<>();
        XmlAsset asset = null;
        InputStream inputStream=null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            inputStream=new FileInputStream(xmlPath);
            xmlParser.setInput(inputStream, "utf-8");
            int evtType=xmlParser.getEventType();
            while(evtType!=XmlPullParser.END_DOCUMENT){
                switch(evtType){
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        if (tag.equalsIgnoreCase("downlist")) {
                            downlist = new XmlDownList();
                            int attributeCount = xmlParser.getAttributeCount();
                            for (int i=0;i<attributeCount;i++) {
                                String attributeName = xmlParser.getAttributeName(i);
                                String attributeNamespace = xmlParser.getAttributeNamespace(i);
                                String attributeValue = xmlParser.getAttributeValue(attributeNamespace, attributeName);
                                if (attributeName.equals("downname")) {
                                    downlist.setDownname(attributeValue);
                                } else if (attributeName.equals("assetCount")) {
                                    downlist.setAssetCount(Integer.parseInt(attributeValue));
                                } else if (attributeName.equals("downId")) {
                                    downlist.setDownId(Integer.parseInt(attributeValue));
                                } else if (attributeName.equals("url")) {
                                    downlist.setUrl(attributeValue);
                                }
                            }
                        }else if(downlist!=null){
                            switch (tag.toLowerCase()) {
                                case "asset":
                                    asset = new XmlAsset();
                                    break;
                                case "asset_id":
                                    try {
                                        int text = Integer.parseInt(xmlParser.nextText());
                                        asset.setAsset_id(text);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "showName":
                                    String name = xmlParser.nextText();
                                    asset.setShowName(name);
                                    break;
                                case "filename":
                                    String filename = xmlParser.nextText();
                                    asset.setFilename(filename);
                                    break;
                                case "asset_type":
                                    try {
                                        int asset_type = Integer.parseInt(xmlParser.nextText());
                                        asset.setAsset_type(asset_type);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "type_name":
                                    String asset_type_name =xmlParser.nextText();
                                    asset.setType_name(asset_type_name);
                                    break;
                                case "md5":
                                    String md5 = xmlParser.nextText();
                                    asset.setMd5(md5);
                                    break;
                                case "filesize":
                                    try {
                                        int filesize = Integer.parseInt(xmlParser.nextText());
                                        asset.setFilesize(filesize);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "playtime":
                                    try {
                                        int playtime = Integer.parseInt(xmlParser.nextText());
                                        asset.setPlaytime(playtime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String end = xmlParser.getName();
                        switch (end.toLowerCase()) {
                            case "downlist":
                                downlist.setAsset(assets);
                                break;
                            case "asset":
                                assets.add(asset);
                                break;
                        }
                        break;
                }
                evtType=xmlParser.next();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return downlist;
    }
	/** 获取xml信息 */
	public XmlDownInfo getInfo(String xml_url) {
		XmlDownInfo info=null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select downBoxId,xml_url,xml_name,xml_down_state,xml_render_state,xml_pri from "+ XmlHelper.TB_NAME+" where xml_url=?";
		Cursor cursor = database.rawQuery(sql, new String[] {xml_url});
		while (cursor.moveToNext()) {
			info = new XmlDownInfo(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
			cursor.close();
			return info;
		}
		return info;
	}
	/** 获取xml信息 */
	public XmlDownInfo getInfo(int down_id) {
		XmlDownInfo info=null;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select downBoxId,xml_url,xml_name,xml_down_state,xml_render_state,xml_pri from "+ XmlHelper.TB_NAME+" where downBoxId=?";
		Cursor cursor = database.rawQuery(sql, new String[] {""+down_id});
		try {
			while (cursor.moveToNext()) {
				info = new XmlDownInfo(cursor.getInt(0),
						cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
				cursor.close();
				return info;
			}
		} catch (Exception e) {

		}
		return info;
	}
	/** 获取xml信息 */
	public List<XmlDownInfo> getAllInfos() {
		List<XmlDownInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		Cursor cursor = database.query(XmlHelper.TB_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			XmlDownInfo info = new XmlDownInfo(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/** 获取未下载xml信息 */
	public List<XmlDownInfo> getPriMaxInfos(int xml_down_state) {
		List<XmlDownInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM "+XmlHelper.TB_NAME+" WHERE xml_pri=(SELECT MAX(xml_pri) FROM "+XmlHelper.TB_NAME+" WHERE xml_down_state=?)";
		Cursor cursor =database.rawQuery(sql, new String[]{""+xml_down_state});
		while (cursor.moveToNext()) {
			XmlDownInfo info = new XmlDownInfo(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));
			list.add(info);
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取已完成下载xml
	 * @param xml_down_state 1
	 * @return
	 */
	public List<XmlDownInfo> getPriDownloadInfos(int xml_down_state) {
		List<XmlDownInfo> list = new ArrayList<>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM "+XmlHelper.TB_NAME+" WHERE xml_render_state=0 and xml_down_state=?";
		Cursor cursor =database.rawQuery(sql, new String[]{""+xml_down_state});
		while (cursor.moveToNext()) {
			XmlDownInfo info = new XmlDownInfo(cursor.getInt(0),
					cursor.getString(2), cursor.getString(1),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));
			list.add(info);
		}
		cursor.close();
		return list;
	}
	/** 获取xml信息 */
	public int getDown_id(String xml_url) {
		int downBoxId = -1;
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select downBoxId from "+ XmlHelper.TB_NAME+" where xml_url=?";
		Cursor cursor = database.rawQuery(sql, new String[] {xml_url});
		while (cursor.moveToNext()) {
			downBoxId = cursor.getInt(0);
		}
		cursor.close();
		return downBoxId;
	}

	/**
	 * 更新数据库信息
	 * @param xml_url
	 * @param state 0 (默认)代表没有下载  1代表下载完成
	 */
	public void updateXmlState(String xml_url,int state) {
		Log.e("我的更新", xml_url + "---->" + state);
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+XmlHelper.TB_NAME+" set xml_down_state="+state+" where xml_url='"+xml_url+"' and xml_down_state=0";
			database.execSQL(sql);
		} catch (Exception e) {
			Log.e("我的汇报", e.getMessage());
		}
	}
	/**
	 * 更新数据库信息
	 * @param down_id
	 */
	public void updateXmlRenderState(int down_id) {
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			String sql = "update "+XmlHelper.TB_NAME+" set xml_render_state=1 where downBoxId="+down_id+" and xml_render_state=0";
			database.execSQL(sql);
		} catch (Exception e) {

		}
	}

	/**
	 * 判断数据库是否有此数据
	 * @param down_id
	 * @return
	 */
	public boolean exitData(int down_id) {
		XmlDownInfo info = getInfo(down_id);
		if (info != null) {
			return true;
		}
		return false;
	}
	/** 关闭数据库 */
	public void closeDb() {
		dbHelper.close();
	}
	/** 删除表 */
	public void dropDb() {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.execSQL("drop table "+XmlHelper.TB_NAME);
	}

	/** 删除数据库中的数据 */
	public void deleteSingle(int downBoxId) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.delete(FileHelper.TB_NAME, "downBoxId=?", new String[] {""+downBoxId });
	}
}