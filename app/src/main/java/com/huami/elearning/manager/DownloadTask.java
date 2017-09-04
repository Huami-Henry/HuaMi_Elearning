package com.huami.elearning.manager;
import android.os.Handler;
import android.os.Message;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.listener.FileDownListener;
import com.huami.elearning.listener.NetFileCheckAllCallBack;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.Media_Type;
import com.huami.elearning.util.CheckDisk;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by Henry on 2017/8/24.
 */
public class DownloadTask implements Callback{
    private final int SUCCESS = 0;
    private final int ERROR = 1;
    private final int FAILURE = 2;
    private static final int count = 2;
    private DownRunnable runnable;
    private Map<String,DownRunnable> runnableMap = new HashMap();//存储正在加入队列的线程
    private String BaseUrl = CheckDisk.checkState();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileDownInfo obj = (FileDownInfo) msg.obj;
            switch (msg.what) {
                case SUCCESS:
                    listener.downSuccess(obj);
                    break;
                case ERROR:
                    listener.downError(obj);
                    break;
                case FAILURE:
                    listener.downFailure(obj);
                    break;
            }
        }
    };
    private List<FileDownInfo> describes;
    private FileDownListener listener;
    private ExecutorService executor;
    public DownloadTask(List<FileDownInfo> describes, FileDownListener listener) {
        this.describes = describes;
        this.listener = listener;
        initExecutorService();
    }
    private void initExecutorService() {
        executor = Executors.newFixedThreadPool(count);// 限制线程池大小为count的线程池 可以稳定的执行当前线程
    }
    /**
     * 开始下载
     */
    public void startDown(){
        for (FileDownInfo info : describes) {
            if (!runnableMap.containsKey(info.getFile_url())) {
                runnable = new DownRunnable(info);
                runnableMap.put(info.getFile_url(), runnable);
                executor.execute(runnable);
            }
        }
    }
    /**[h[
     * 开始下载
     */
    public void restartDown(){
        cancelDown();
        if (describes != null) {
            for (String key : runnableMap.keySet()) {
                DownRunnable downRunnable = runnableMap.get(key);
                executor.submit(downRunnable);
            }
        }
    }

    /**
     * 检测任务列表是否都清空
     * @return
     */
    public boolean haveNoAsset(){
        if (runnableMap.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 终结正在下载的链接
     */
    public void cancelDown(){
        if (describes != null) {
            for (FileDownInfo info : describes) {
                OkHttp.mOkHttpClient.cancel(info);
            }
        }
    }
    /**
     * 开始下载文件
     */
    private void startDownApp(FileDownInfo info) {
        int file_type = info.getFile_type();
        String file_Path = null;
        switch (file_type) {
            case 1:
                file_Path = BaseUrl + Media_Type.VIDEO + File.separator + info.getFile_name();
                break;
            case 2:
                file_Path = BaseUrl + Media_Type.AUDIO + File.separator + info.getFile_name();
                break;
            case 3:
                file_Path = BaseUrl + Media_Type.IMAGE + File.separator + info.getFile_name();
                break;
            case 4:
                file_Path = BaseUrl + Media_Type.TEXT + File.separator + info.getFile_name();
                break;
            default:
                file_Path = BaseUrl + Media_Type.OTHER + File.separator + info.getFile_name();
                break;
        }
        try {
            File file = new File(file_Path);
            long length = 0;
            if (file.exists()) {
                length = file.length();
            }
            if (length > 0 && length < info.getFile_length()) {
                if (length < info.getFile_length()) {
                    Request request = new Request.Builder().addHeader("RANGE", "bytes=" + length + "-" + info.getFile_length())
                            .url(info.getFile_url()).tag(info).build();
                    OkHttp.mOkHttpClient.newCall(request).enqueue(this);
                } else if (length > info.getFile_length()) {
                    Request request = new Request.Builder().url(info.getFile_url()).tag(info).build();
                    OkHttp.mOkHttpClient.newCall(request).enqueue(this);
                }
            } else if (length == 0) {
                Request request = new Request.Builder().url(info.getFile_url()).tag(info).build();
                OkHttp.mOkHttpClient.newCall(request).enqueue(this);
            } else if (length == info.getFile_length()) {
                runnableMap.remove(info.getFile_url());
                Message message = new Message();
                message.obj = info;
                message.what = SUCCESS;
                handler.sendMessage(message);
            } else {
                file.delete();
                Request request = new Request.Builder().url(info.getFile_url()).tag(info).build();
                OkHttp.mOkHttpClient.newCall(request).enqueue(this);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Message message = new Message();
        message.what = FAILURE;
        message.obj = request.tag();
        handler.sendMessage(message);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            FileDownInfo info = (FileDownInfo) response.request().tag();
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            // 储存下载文件的目录
            try {
                is = response.body().byteStream();
                String[] spilt = info.getFile_url().split("/");
                String file_Path = null;
                switch (info.getFile_type()) {
                    case 1:
                        file_Path = BaseUrl + Media_Type.VIDEO + File.separator;
                        break;
                    case 2:
                        file_Path = BaseUrl + Media_Type.AUDIO + File.separator;
                        break;
                    case 3:
                        file_Path = BaseUrl + Media_Type.IMAGE + File.separator;
                        break;
                    case 4:
                        file_Path = BaseUrl + Media_Type.TEXT + File.separator;
                        break;
                    default:
                        file_Path = BaseUrl + Media_Type.OTHER + File.separator;
                        break;
                }
                File file = new File(file_Path + spilt[spilt.length - 1]);
                fos = new FileOutputStream(file, true);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                if (fos != null) {
                    fos.flush();
                }
//                // 下载完成
                Message message = new Message();
                message.obj = info;
                message.what = SUCCESS;
                handler.sendMessage(message);
                runnableMap.remove(info.getFile_url());
            } catch (Exception e) {
                Message message_error = new Message();
                message_error.obj = info;
                message_error.what = ERROR;
                handler.sendMessage(message_error);
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
    /**
     * 加入下载队列
     */
    class DownRunnable implements Runnable{
        private FileDownInfo info;
        public DownRunnable(FileDownInfo info) {
            this.info = info;
        }
        @Override
        public void run() {
            startDownApp(info);
        }
    }
}
