package com.huami.elearning.util;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.listener.NetFileCheckAllCallBack;
import com.huami.elearning.model.FileDescribe;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by Henry on 2017/8/21.
 */
public class NetFileLengthManager{
    private final int count = 3;
    private List<FileDescribe> describes;
    private ExecutorService executor;
    private NetFileCheckAllCallBack callBack;
    public NetFileLengthManager(List<FileDescribe> describes,NetFileCheckAllCallBack callBack) {
        this.describes = describes;
        this.callBack = callBack;
        executor = Executors.newFixedThreadPool(count);// 限制线程池大小为count的线程池;
    }
    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response = OkHttp.mOkHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                return contentLength == 0 ? 0 : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 将所有的下载任务保存下来
     */
    private Map<String,Runnable> current_task = new HashMap<>();
    /**
     * 下载的服务
     */
    private MyRunnable runnable;
    class MyRunnable implements Runnable {
        private FileDescribe describe;
        public MyRunnable(FileDescribe describe) {
            this.describe = describe;
        }
        @Override
        public void run() {
            long contentLength = getContentLength(describe.getFileUrl());
            Log.e("我的文件大小", describe.getFileUrl() + "-->" + (contentLength / 1024 / 1024) + "MB");
            describe.setFileLength(contentLength);
            Message message = new Message();
            message.obj = describe.getFileUrl();
            handler.sendMessage(message);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String url = (String) msg.obj;
            remove(url);
        }
    };
    public void remove(String key){
        current_task.remove(key);
        if (current_task.size() == 0) {
            callBack.checkSuccess();
            reset();
        }
    }
    public void start(){
        for (FileDescribe describe : describes) {
            runnable = new MyRunnable(describe);
            current_task.put(describe.getFileUrl(), runnable);
            executor.execute(runnable);
        }
    }
    private void reset(){
        executor.shutdown();
    }
}
