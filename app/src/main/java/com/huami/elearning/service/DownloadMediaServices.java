package com.huami.elearning.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.huami.elearning.DownLoadActivity;
import com.huami.elearning.TApplication;
import com.huami.elearning.activity.UpMediaActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FileSqlTool;
import com.huami.elearning.model.FileInfo;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.SPCache;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Henry on 2017/7/17.
 */
public class DownloadMediaServices extends Service{
    private enum Media_Type {
        VIDEO,AUDIO, IMAGE, TEXT, OTHER
    }
    private String BASE_PATH = CheckDisk.checkState();
    private Timer mTimer;
    //    private final long INTERVAL=40*1000*60;
    private final long INTERVAL=30*1000;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        LogModelUtil.getInstance().doLog("下载服务线程开启","现在开启");
        // 如果已经存在，则先取消
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new DownloadMediaServices.MyTimerTask(),0, INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
    private AlertDialog noticeDialog;
    public void showDownloadDialog(String message){
        if (noticeDialog != null) {
            if (!noticeDialog.isShowing()) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TApplication.activityList.get(0));// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
                    builder.setTitle("媒资更新提醒");
                    builder.setMessage(message);
                    builder.setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                            SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
                            Intent intent = new Intent(DownloadMediaServices.this, DownLoadActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
                        }
                    });
                    noticeDialog = builder.create();
                    noticeDialog.show();
                } catch (Exception e) {
                }
            }
        }
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
        }
    }
}
