package com.huami.elearning.service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FileDownSqlTool;
import com.huami.elearning.db.XmlSqlTool;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.XmlAsset;
import com.huami.elearning.model.XmlDownInfo;
import com.huami.elearning.model.XmlDownList;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.DownUtil;
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
import java.util.Timer;
import java.util.TimerTask;
/**
 * 主要用于解析xml内部数据并且保存到媒资数据库中
 * Created by Henry on 2017/7/13.
 */
public class XmlToFileService extends Service implements Callback{
    @Override
    public void onFailure(Request request, IOException e) {
    }
    @Override
    public void onResponse(Response response) throws IOException {
        try {
            XmlDownInfo downInfo = (XmlDownInfo) response.request().tag();
            String[] split = downInfo.getXml_url().split("/");
            String fileOut = downXml(response, split[split.length - 1]);
            XmlDownList list = xmlParser(fileOut);
            for (XmlAsset asset : list.getAsset()) {
                Log.e("我的xml文件信息", asset.toString());
                long contentLength = 0;
                FileDownInfo info_down=new FileDownInfo(
                        asset.getAsset_id(),
                        list.getUrl()+asset.getFilename(),
                        asset.getFilename(),
                        asset.getMd5(),
                        asset.getAsset_type(),
                        downInfo.getXml_pri(),
                        contentLength,0,list.getDownId(),0,0
                        );
                FileDownSqlTool.getInstance().insertInfo(info_down);
            }
            XmlSqlTool.getInstance(TApplication.getContext()).updateXmlState(downInfo.getXml_url(), 1);
            List<FileDownInfo> maxPris = FileDownSqlTool.getInstance().getMaxPris(0);
            if (maxPris.size() > 0) {
                //提醒媒资需要更新
                Intent intent = new Intent();
                intent.setAction(BaseConsts.BROAD_UPDATE);
                sendBroadcast(intent);
            }
        } catch (IOException e) {

        }
    }

    @NonNull
    private String downXml(Response response, String s) throws IOException {
        InputStream is = response.body().byteStream();
        String fileOut = CheckDisk.checkState() + BaseConsts.TEMPLATE_XML_PATH + File.separator+ s;
        OutputStream os = new FileOutputStream(fileOut);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        os.close();
        is.close();
        return fileOut;
    }

    //    private static final long INTERVAL = 40 * 1000* 60;
    private static final long INTERVAL = 5 * 1000;
    private Handler handler = new Handler();
    private Timer mTimer;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 如果已经存在，则先取消
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new XmlToFileService.MyTimerTask(), 0, INTERVAL);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(runnable);
        }
    }
    private List<XmlDownInfo> priMaxInfos;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取数据库的数据优先级高的先获取
            priMaxInfos = XmlSqlTool.getInstance(TApplication.getContext()).getPriMaxInfos(0);
            if (priMaxInfos.size() != 0) {
                File file = new File(CheckDisk.checkState() + BaseConsts.TEMPLATE_XML_PATH + File.separator);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (isNetworkConnected()) {
                    //下载xml 并且获取内部的数据
                    for (XmlDownInfo info : priMaxInfos) {
                        Log.e("我的步骤", "开始解析xml-->"+info.getXml_url());
                        OkHttp.asyncPost(BaseConsts.BASE_URL + info.getXml_url(),info,XmlToFileService.this);
                    }
                }
            }
        }
    };
    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }
    /**
     * 检查网络链接状态
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) TApplication.getContext()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
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
}
