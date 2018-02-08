package com.huami.elearning.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.BaseNetDataBiz;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.PlaySqlTool;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.db.TemporarySqlTool;
import com.huami.elearning.db.XmlSqlTool;
import com.huami.elearning.model.BoxDownListInfo;
import com.huami.elearning.model.DoShakeListInfo;
import com.huami.elearning.model.MainRoot;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.TemplateBoxInfo;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.model.TemporaryInfo;
import com.huami.elearning.model.XmlDownInfo;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/7/13.
 * 此服务开启后将30分钟执行一次
 * 主要用来下载xml列表、点阵表
 */
public class MainHeartbeatService extends Service implements BaseNetDataBiz.RequestListener{
    private static final long INTERVAL = 5 * 1000 * 60;
//    private static final long INTERVAL = 1000 * 60;
    private Handler handler = new Handler();
    private Timer mTimer;
    private BaseNetDataBiz biz = new BaseNetDataBiz(this);
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 如果已经存在，则先取消
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new MainHeartbeatService.MyTimerTask(), 0, INTERVAL);
    }

    private List<BoxDownListInfo> boxDownList;
    private List<DoShakeListInfo> shakeListInfos;
    private List<TemplateBoxInfo> templateBoxInfos;
    private List<XmlDownInfo> xmlDownInfos = new ArrayList<>();
    @Override
    public void onResponse(BaseNetDataBiz.Model model) {
        String json = model.getJson();
        Gson gson = new Gson();
        String tag = model.getTag();
        switch (tag) {
            case BaseConsts.HEART_BEAT:
                if (json != null) {
                    MainRoot mainRoot = gson.fromJson(json, MainRoot.class);
                    boxDownList = mainRoot.getBoxDownList();
                    shakeListInfos = mainRoot.getDotshakeList();
                    templateBoxInfos = mainRoot.getTemplateBoxList();
                    //先将点阵表的信息写到临时表里面  此时做个判断
                    setTemporary();
                    setTemplate();
                    setDownList();
                }
                break;
        }
    }

    private void setTemporary() {
        if (shakeListInfos != null) {
            for (DoShakeListInfo info : shakeListInfos) {
                TemporaryInfo playInfo =
                        new TemporaryInfo(info.getTemplate_id(),info.getKey_only(),info.getValue());
                TemporarySqlTool.getInstance().insertInfo(playInfo);
            }
            shakeListInfos.clear();
            shakeListInfos = null;
        }
    }
    private void setTemplate() {
        if (templateBoxInfos != null) {
            for (TemplateBoxInfo info : templateBoxInfos) {
                TemplateInfo playInfo =
                        new TemplateInfo(
                                info.getId(),
                                info.getTemplateId(),
                                String.valueOf(info.getTemplateId()),
                                info.getUrl(),
                                0,0);
                TemplateSqlTool.getInstance().insertInfo(playInfo);
            }
            templateBoxInfos.clear();
            templateBoxInfos = null;
        }
    }
    /**
     * 设置下载列表
     */
    private void setDownList() {
        if (boxDownList != null) {
            for (BoxDownListInfo info : boxDownList) {
                XmlDownInfo info_xml =
                        new XmlDownInfo(
                                info.getId(),
                                info.getUrl(),
                                info.getName(),
                                0,
                                0,
                                info.getPri()
                        );
                xmlDownInfos.add(info_xml);
            }
            if (xmlDownInfos.size() != 0) {
                //将获得的xml数据保存到数据库中
                XmlSqlTool.getInstance(TApplication.getContext()).insertInfos(xmlDownInfos);
                xmlDownInfos.clear();
                boxDownList.clear();
                boxDownList = null;
            }
        } else {
            Toast.makeText(this, "盒子已被注销", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnFailure(Request r, IOException o) {
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(runnable);
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isNetworkConnected()) {
                //开启请求xml
                biz.downloadFile(BaseConsts.HEART_BEAT+"?mac="+BaseConsts.BOX_MAC, BaseConsts.HEART_BEAT);
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
}
