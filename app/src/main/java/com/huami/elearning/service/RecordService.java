package com.huami.elearning.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.BaseNetDataBiz;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.RecordSqlTool;
import com.huami.elearning.model.FeedRecordInfo;
import com.huami.elearning.model.FeedRecordRoot;
import com.huami.elearning.model.RecordInfo;
import com.squareup.okhttp.Request;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/7/13.
 * 此服务开启后将30分钟执行一次
 * 主要用来下载xml列表、阵列表
 */
public class RecordService extends Service implements BaseNetDataBiz.RequestListener{
    private static final long INTERVAL = 30* 1000 * 1;
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
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new RecordService.MyTimerTask(), 0, INTERVAL);
    }
    @Override
    public void onResponse(BaseNetDataBiz.Model model) {
        String json = model.getJson();
        try {
            JSONObject object = new JSONObject(json);
            if (object.getInt("code") == 0) {
                if (allInfos != null) {
                    for (RecordInfo info : allInfos) {
                        RecordSqlTool.getInstance(TApplication.getContext()).delete(info.getId());
                    }
                }
            }
        } catch (JSONException e) {

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
    private List<RecordInfo> allInfos;
    private Gson gson = new Gson();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isNetworkConnected()) {
                //1:获取记录数据库数据
                allInfos = RecordSqlTool.getInstance(TApplication.getContext()).getAllInfos();
                if (allInfos.size() != 0) {
                    FeedRecordRoot record = new FeedRecordRoot();
                    record.setMac(BaseConsts.BOX_MAC);
                    List<FeedRecordInfo> infos = new ArrayList<>();
                    for (RecordInfo info : allInfos) {
                        FeedRecordInfo recordInfo = new FeedRecordInfo();
                        recordInfo.setFile_name(info.getRecord_file());
                        recordInfo.setClick_time(info.getRecord_time());
                        infos.add(recordInfo);
                    }
                    record.setFile(infos);
                    String json = gson.toJson(record);
                    Map<String, String> map = new HashMap<>();
                    map.put("record", json);
                    //开启请求xml
                    biz.uploadJson(BaseConsts.ClickLog,map, BaseConsts.ClickLog);
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
}
