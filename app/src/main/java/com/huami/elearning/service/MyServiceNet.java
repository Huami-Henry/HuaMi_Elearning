package com.huami.elearning.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import com.huami.elearning.TApplication;
import com.huami.elearning.base.BaseConsts;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/5/11.
 */
public class MyServiceNet extends Service {
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
        mTimer.scheduleAtFixedRate(new MyTimerTask(), 0, INTERVAL);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            // 新开一个线程执行
            handler.post(runnable);
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isNetworkConnected()) {
                Intent intent_bro = new Intent();
                intent_bro.setAction(BaseConsts.BROADCAST_ACTION);
                sendBroadcast(intent_bro);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
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
