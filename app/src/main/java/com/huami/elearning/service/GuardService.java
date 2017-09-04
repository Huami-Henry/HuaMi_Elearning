package com.huami.elearning.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/9.
 */

public class GuardService extends Service {
    private Timer mTimer;
    private final long INTERVAL=1*1000*10;
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
        mTimer.scheduleAtFixedRate(new GuardService.MyTimerTask(),0, INTERVAL);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //下载媒资文件服务
            boolean serviceWork = isServiceRunning("com.inesanet.okgomulti.service.DownloadMediaServices");
            if (!serviceWork) {
                Intent intent = new Intent(GuardService.this, DownloadMediaServices.class);
                startService(intent);
            }
            //下载模板服务
            boolean serviceTemp = isServiceRunning("com.inesanet.okgomulti.service.DownTemplateService");
            if (!serviceTemp) {
                Intent intent = new Intent(GuardService.this, DownTemplateService.class);
                startService(intent);
            }
            boolean serviceFeed = isServiceRunning("com.inesanet.okgomulti.service.FeedBackServices");
            if (!serviceFeed) {
                Intent intent = new Intent(GuardService.this, FeedBackServices.class);
                startService(intent);
            }
            boolean serviceMainHeart = isServiceRunning("com.inesanet.okgomulti.service.MainHeartbeatService");
            if (!serviceMainHeart) {
                Intent intent = new Intent(GuardService.this, MainHeartbeatService.class);
                startService(intent);
            }

            boolean serviceRecord = isServiceRunning("com.inesanet.okgomulti.service.RecordService");
            if (!serviceRecord) {
                Intent intent = new Intent(GuardService.this, RecordService.class);
                startService(intent);
            }
            boolean serviceXml = isServiceRunning("com.inesanet.okgomulti.service.XmlToFileService");
            if (!serviceXml) {
                Intent intent = new Intent(GuardService.this, XmlToFileService.class);
                startService(intent);
            }
//            LogModelUtil.getInstance().doLog("我的服务",serviceWork+"-->"+serviceTemp+"-->"+serviceFeed+"-->"+serviceMainHeart+"-->"+serviceRecord+"-->"+serviceXml);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, GuardService.class);
        startService(intent);
    }
    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
