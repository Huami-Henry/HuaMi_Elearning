package com.huami.elearning.service;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.huami.elearning.base.BaseConsts;
/**
 * Created by Henry on 2017/8/19.
 */

public class Net_Service extends Service {
    private long total_data = TrafficStats.getTotalRxBytes();
    private Handler mHandler;
    //几秒刷新一次
    private final int count = 5;
    /**
     * 定义线程周期性地获取网速
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //定时器
            mHandler.postDelayed(mRunnable, count * 1000);
            Message msg = mHandler.obtainMessage();
            msg.what = 1;
            msg.arg1 = getNetSpeed();
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Intent intent  = new Intent();
                    intent.setAction(BaseConsts.BROAD_NET);
                    intent.putExtra("speed",msg.arg1 / 1024);
                    sendBroadcast(intent);
                }
            }
        };
    }

    /**
     * 核心方法，得到当前网速
     * @return
     */
    private int getNetSpeed() {
        long traffic_data = TrafficStats.getTotalRxBytes() - total_data;
        total_data = TrafficStats.getTotalRxBytes();
        return (int)traffic_data /count ;
    }

    /**
     * 启动服务时就开始启动线程获取网速
     */
    @Override
    public void onStart(Intent intent, int startId) {
        mHandler.postDelayed(mRunnable, 0);
    }
    /**
     * 在服务结束时删除消息队列
     */
    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
