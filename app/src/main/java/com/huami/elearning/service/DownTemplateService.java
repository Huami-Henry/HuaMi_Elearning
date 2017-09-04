package com.huami.elearning.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.huami.elearning.MainActivity;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.activity.HomeActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.db.TemporarySqlTool;
import com.huami.elearning.model.CompressStatus;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.squareup.okhttp.Callback;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/8/2.
 */
public class DownTemplateService extends Service{
    private Timer mTimer;
    private final long INTERVAL=1000*20;
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
        mTimer.scheduleAtFixedRate(new DownTemplateService.MyTimerTask(),0, INTERVAL);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }
    private int teplate_id;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TemplateInfo templateInfo = TemplateSqlTool.getInstance().getDownTemplate();
            if (templateInfo != null) {
//                LogModelUtil.getInstance().doLog("我的模板", templateInfo.toString());
                String path = CheckDisk.checkState();
                //模板列表中有数据
                //1先判断有没有下载
                teplate_id = templateInfo.getTemplate_id();
                String name = templateInfo.getTemplate_url();
                if (name != null&& !TextUtils.isEmpty(name)) {
                    String[] file_name = name.split("/");
                    downZip(
                            templateInfo.getId(),
                            BaseConsts.BASE_URL+templateInfo.getTemplate_url(),
                            path+BaseConsts.TEMPLATE_PATH,
                            file_name[file_name.length-1],
                            path+templateInfo.getTemplate_id());
                }
            }
        }
    };
    private MyHandler handler_zip;
    private int id;
    /**
     * 开始下载模板文件
     * @param id
     * @param url
     * @param folder
     * @param name
     */
    public void downZip(final int id,String url,final String folder,String name,final String compress) {
        this.id = id;
        OkGo.<File>get(url)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new FileCallback(folder,name) {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        TemplateSqlTool.getInstance().updateDownState(id, 1);
                    }
                    @Override
                    public void onSuccess(Response<File> response) {
                        if (handler_zip == null) {
                            handler_zip = new MyHandler();
                        }
                        TemplateSqlTool.getInstance().updateDownState(id, 2);
                        downSuccessRender(id);
                        boolean empty = TemporarySqlTool.getInstance().checkEmpty(teplate_id);
                        Log.e("我的模板是否为空", "--->" + empty);
                        if (empty) {
                            ZipUtil.upzipFile(response.body(),compress,handler_zip,true);
                        }
                    }
                    @Override
                    public void onError(Response<File> response) {
                        TemplateSqlTool.getInstance().updateDownState(id,0);
                    }
                    @Override
                    public void downloadProgress(Progress progress) {

                    }
                });
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CompressStatus.COMPLETED:
                    //解压成功
                    TemplateSqlTool.getInstance().updateCompressState(id,2);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendIntent();
                        }
                    }, 1000);
                    //切换模板
                    break;
                case CompressStatus.ERROR:
                    //解压失败

                    break;
            }
        }
    }
    public void sendIntent(){
        try {
            Intent intent = new Intent(TApplication.activityList.get(0), HomeActivity.class);
            TApplication.activityList.get(0).startActivity(intent);
            TApplication.activityList.get(0).finish();
        }catch (Exception e){

        }

    }
    /**
     * 模板文件下载反馈
     * @param template_id
     */
    public void downSuccessRender(int template_id) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", BaseConsts.BOX_MAC);
        map.put("templateId", String.valueOf(template_id));
        OkHttp.asyncPost(BaseConsts.TEMPLATE_FEED, map, BaseConsts.TEMPLATE_FEED, new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

            }
        });
    }
}
