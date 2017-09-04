package com.huami.elearning.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/7/19.
 */

public class LogService extends Service {
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
        mTimer.scheduleAtFixedRate(new LogService.MyTimerTask(),0, INTERVAL);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
        }
    }
}
