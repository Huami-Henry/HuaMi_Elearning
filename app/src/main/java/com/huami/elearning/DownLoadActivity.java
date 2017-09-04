package com.huami.elearning;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.adapter.DownLoadAdapter;
import com.huami.elearning.base.BaseActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FeedBackSqlTool;
import com.huami.elearning.db.FileSqlTool;
import com.huami.elearning.db.PlaySqlTool;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.db.TemporarySqlTool;
import com.huami.elearning.model.CompressStatus;
import com.huami.elearning.model.FileDescribe;
import com.huami.elearning.model.FileInfo;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.model.TemporaryInfo;
import com.huami.elearning.service.Net_Service;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.SPCache;
import com.huami.elearning.util.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class DownLoadActivity extends BaseActivity implements Callback{
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private TimerTask time_task;
    private Timer timer = new Timer();
    private DownLoadAdapter adapter;
    private List<FileDescribe> describes = new ArrayList<>();
    private String BaseUrl = CheckDisk.checkState();
    private String file_Path;
    private ExecutorService executor_one;
    private ExecutorService executor_second;
    private int id;
    private View progress_view;
    private ProgressDialog progressDialog;
    private TextView down_speed;
    private enum Media_Type {
        VIDEO, AUDIO, IMAGE, TEXT, OTHER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_down_loading);
        initSDCard();
        down_speed = (TextView) findViewById(R.id.down_speed);
        progress_view = findViewById(R.id.progress_view);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
//        adapter = new DownLoadAdapter(describes, this);
//        recyclerView.setAdapter(adapter);
        getAllLength();
    }

    @Override
    public void onBackPressed() {
        showAppUpdateDialog("点击退出将会重启应用");
    }

    protected void showAppUpdateDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("退出提醒");
        builder.setMessage(message);
        builder.setPositiveButton("我要退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                finishDown();
            }
        });
        builder.setNegativeButton("继续下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void finishDown() {
       finish();
    }

    /**
     * 初始化下载路径
     */
    public void initSDCard() {
        String url;
        File file;
        url = BaseUrl + Media_Type.VIDEO + File.separator;
        file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
        url = BaseUrl + Media_Type.AUDIO + File.separator;
        file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
        url = BaseUrl + Media_Type.IMAGE + File.separator;
        file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
        url = BaseUrl + Media_Type.TEXT + File.separator;
        file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
        url = BaseUrl + Media_Type.OTHER + File.separator;
        file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 首先获取所有的媒资文件的长度
     */
    public void getAllLength() {
        //1:判断网络弹出进度对话框
//        if (isNetworkConnected(this)) {
////            final List<FileInfo> maxPriInfos = FileSqlTool.getInstance().getMaxPriInfos(0);
//            //显示ProgressDialog
//            progressDialog = ProgressDialog.show(DownLoadActivity.this, "Loading...", "正在检测需要更新的媒资文件", true, false);
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected Void doInBackground(Void... params) {
//                    for (FileInfo fileInfo : maxPriInfos) {
//                        long contentLength = getContentLength(fileInfo.getFile_url());
//                        if (contentLength > 0) {
//                            FileDescribe des = new FileDescribe(
//                                    fileInfo.getAsset_id(),
//                                    fileInfo.getAsset_type(),
//                                    fileInfo.getFile_name(),
//                                    fileInfo.getFile_url(),
//                                    contentLength, 0,0,
//                                    fileInfo.getCreate_time(),
//                                    fileInfo.getFile_mdFive(),
//                                    0);
//                            describes.add(des);
//                        }
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    progressDialog.dismiss();
//                    progress_view.setVisibility(View.VISIBLE);
//                    adapter.notifyDataSetChanged();
//                    if (time_task == null) {
//                        time_task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                });
//                            }
//                        };
//                        timer.schedule(time_task, 0, 2000);
//                    }
//                    registNetReceiver();
//                    initExecutorService();
//                    ExecutorServiceThread();
//                }
//            }.execute();
//        } else {
//            showToast("网络异常请退出重新检查网络");
//        }
    }

    private Intent intent_net;
    private MyReceiver receiver;

    private void registNetReceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BaseConsts.BROAD_NET);
        registerReceiver(receiver, filter);
        intent_net = new Intent(this, Net_Service.class);
        startService(intent_net);
    }

    private static final int count = 2;

    private void initExecutorService() {
        executor_one = Executors.newFixedThreadPool(count);// 限制线程池大小为count的线程池
        executor_second = Executors.newFixedThreadPool(count);// 限制线程池大小为count的线程池
    }

    /**
     * 将所有的下载任务保存下来
     */
    private Map<String, Runnable> current_task = new HashMap<>();

    private void ExecutorServiceThread() {
        if (describes.size() >= 2) {
            int i = describes.size() / 2;
            int j = 0;
            for (final FileDescribe describe : describes) {
                if (j < i) {
                    runnable = new MyRunnable(describe);
                    current_task.put(describe.getFileUrl(), runnable);
                    executor_one.execute(runnable);
                } else {
                    runnable_second = new MyRunnableSecond(describe);
                    current_task.put(describe.getFileUrl(), runnable_second);
                    executor_second.execute(runnable_second);
                }
                j++;
            }
        } else {
            for (final FileDescribe describe : describes) {
                runnable = new MyRunnable(describe);
                current_task.put(describe.getFileUrl(), runnable);
                executor_one.execute(runnable);
            }
        }
    }

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
            long contentLength = describe.getFileLength();
            String[] spilt = describe.getFileUrl().split("/");
            int fileType = describe.getAsset_type();
            switch (fileType) {
                case 1:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.VIDEO + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 2:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.AUDIO + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 3:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.IMAGE + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 4:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.TEXT + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                default:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.OTHER + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
            }
        }
    }

    /**
     * 下载的服务
     */
    private MyRunnableSecond runnable_second;

    class MyRunnableSecond implements Runnable {
        private FileDescribe describe;

        public MyRunnableSecond(FileDescribe describe) {
            this.describe = describe;
        }

        @Override
        public void run() {
            long contentLength = describe.getFileLength();
            String[] spilt = describe.getFileUrl().split("/");
            int fileType = describe.getAsset_type();
            switch (fileType) {
                case 1:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.VIDEO + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 2:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.AUDIO + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 3:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.IMAGE + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                case 4:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.TEXT + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
                default:
                    startDownApp(describe.getFileUrl(), BaseUrl + Media_Type.OTHER + File.separator + spilt[spilt.length - 1], contentLength);
                    break;
            }
        }
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
     * 开始下载文件
     */
    public void startDownApp(String url, String file_Path, long netSize) {
        File file = new File(file_Path);
        long length = 0;
        if (file.exists()) {
            length = file.length();
        }
        if (length > 0 && length < netSize) {
            if (length < netSize) {
                Request request = new Request.Builder().addHeader("RANGE", "bytes=" + length + "-" + netSize)
                        .url(url).tag(url).build();
                OkHttp.mOkHttpClient.newCall(request).enqueue(this);
            } else if (length > netSize) {
                Request request = new Request.Builder().url(url).tag(url).build();
                OkHttp.mOkHttpClient.newCall(request).enqueue(this);
            }
        } else if (length == 0) {
            Request request = new Request.Builder().url(url).tag(url).build();
            OkHttp.mOkHttpClient.newCall(request).enqueue(this);
        } else if (length == netSize) {
            current_task.remove(url);
            Message message = new Message();
            message.obj = url;
            message.what = 0;
            handler.sendMessage(message);
        } else {
            file.delete();
            Request request = new Request.Builder().url(url).tag(url).build();
            OkHttp.mOkHttpClient.newCall(request).enqueue(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (describes != null) {
            describes.clear();
        }
        if (timer != null) {
            try {
                time_task.cancel();
                timer.cancel();
            } catch (Exception e) {

            }
        }
        for (String key : current_task.keySet()) {
            OkHttp.mOkHttpClient.cancel(key);
        }
        if (intent_net != null) {
            stopService(intent_net);
            unregisterReceiver(receiver);
        }
        SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, false);
    }
    @Override
    public void onFailure(Request request, IOException e) {
    }
    @Override
    public void onResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            String tag = (String) response.request().tag();
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            // 储存下载文件的目录
            try {
                is = response.body().byteStream();
                String[] spilt = tag.split("/");
                for (FileDescribe describe : describes) {
                    if (tag.equals(describe.getFileUrl())) {
                        switch (describe.getAsset_type()) {
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
                        break;
                    }
                }
                File file = new File(file_Path + spilt[spilt.length - 1]);
                long length = 0;
                while ((len = is.read(buf)) != -1) {
                    if (fos == null) {
                        fos = new FileOutputStream(file, true);
                    }
                    fos.write(buf, 0, len);
                    length += len;
                    if (length > 1024 * 1024) {
                        fos.flush();
                        fos.close();
                        fos = null;
                        length = 0;
                    }
                }
                // 下载完成
                Message message = new Message();
                message.obj = tag;
                message.what = 0;
                handler.sendMessage(message);
                current_task.remove(tag);
                if (fos != null) {
                    fos.flush();
                }
            } catch (Exception e) {
                Message message_error = new Message();
                message_error.obj = tag;
                message_error.what = 1;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        final String tag_ok = (String) msg.obj;
//                        FileSqlTool.getInstance().updateFileState(tag_ok, 2);
                        FileInfo fileInfo=null;
//                        fileInfo = FileSqlTool.getInstance().getFileInfo(tag_ok);
                        int fileType = fileInfo.getAsset_type();
                        String filePath;
                        switch (fileType) {
                            case 1:
                                filePath = BaseUrl + Media_Type.VIDEO + File.separator + fileInfo.getFile_name();
                                break;
                            case 2:
                                filePath = BaseUrl + Media_Type.AUDIO + File.separator + fileInfo.getFile_name();
                                break;
                            case 3:
                                filePath = BaseUrl + Media_Type.IMAGE + File.separator + fileInfo.getFile_name();
                                break;
                            case 4:
                                filePath = BaseUrl + Media_Type.TEXT + File.separator + fileInfo.getFile_name();
                                break;
                            default:
                                filePath = BaseUrl + Media_Type.OTHER + File.separator + fileInfo.getFile_name();
                                break;
                        }
                        doSuccess(tag_ok, filePath, fileInfo.getFile_name());
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };
    /**
     * 文件下载成功后的处理
     */
    private Handler handler_zip;

    public void doSuccess(String url, String file_Path, String fileName) {
        if (handler_zip == null) {
            handler_zip = new MyHandler();
        }
//        FileSqlTool.getInstance().updateFilePath(file_Path, url);
        //更新状态
        FeedBackSqlTool.getInstance().updateDownState(fileName, 1);
        //2修改临时表中数据做查询操作插入或者替换正式表中的数据
        List<TemporaryInfo> fileKey = TemporarySqlTool.getInstance().getFileKey(fileName);
        for (TemporaryInfo info : fileKey) {
            Log.e("我的临时模板", info.toString());
        }
        if (fileKey.size() != 0) {
            for (TemporaryInfo info : fileKey) {
                boolean exit = PlaySqlTool.getInstance().exit(info.getTemporary_key());
                Log.e("我的临时模板", "存不存在"+exit);
                if (exit) {
                    PlayInfo info_play = new PlayInfo(info.getTemporary_key(), info.getTemporary_value());
                    PlaySqlTool.getInstance().update(info_play);
                    TemporarySqlTool.getInstance().delLines(info);
                    checkEmpty(info, exit);

                } else {
                    PlayInfo info_play = new PlayInfo(info.getTemporary_key(), info.getTemporary_value());
                    PlaySqlTool.getInstance().insertInfo(info_play);
                    TemporarySqlTool.getInstance().delLines(info);
                    checkEmpty(info, exit);
                }
            }
        }
    }
    private void checkEmpty(TemporaryInfo info, boolean exit) {
        //3检查临时表中的t_id是否为空
        boolean empty = TemporarySqlTool.getInstance().checkEmpty(info.getTemplate_id());
        Log.e("我的临时模板", "是否为空"+empty);
        //设置模板表的状态
        //查看模板表的下载情况和解压情况
        if (empty) {
            int downState = TemplateSqlTool.getInstance().checkDownState(info.getTemplate_id());
            //当下载状态为0
            TemplateInfo fileInfo = TemplateSqlTool.getInstance().getFileInfo(info.getTemplate_id());
            Log.e("我的临时模板", "TemplateInfo-->"+fileInfo.toString());
            if (fileInfo != null) {
                id = fileInfo.getId();
                Log.e("我的路径", "--下载状态->"+downState);
                if (downState == 0 || downState == 1) {
                    String path = CheckDisk.checkState();
                    if (fileInfo.getTemplate_url() != null) {
                        String[] split = fileInfo.getTemplate_url().split("/");
                        downZip(info.getTemplate_id(), fileInfo.getTemplate_url(), path + BaseConsts.TEMPLATE_PATH, split[split.length - 1], path + info.getTemplate_id());
                    }
                } else {
                    String path = CheckDisk.checkState();
                    if (fileInfo.getTemplate_url() != null) {
                        String[] split = fileInfo.getTemplate_url().split("/");
                        File file_zip = new File(path + BaseConsts.TEMPLATE_PATH + File.separator + split[split.length - 1]);
                        Log.e("我的路径", path + info.getTemplate_id() + File.separator + split[split.length - 1]);
                        if (file_zip.exists()) {
                            ZipUtil.upzipFile(file_zip, path + info.getTemplate_id(), handler_zip, true);
                        }
                    }
                }
            }
        }
    }

    private int template_id;

    /**
     * 开始下载模板文件
     *
     * @param id
     * @param url
     * @param folder
     * @param name
     */
    public void downZip(final int id, String url, final String folder, String name, final String compress) {
        Log.e("我的临时模板", "下载模板");
        template_id = id;
        OkGo.<File>get(url)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new FileCallback(folder, name) {
                    @Override
                    public void onStart(com.lzy.okgo.request.base.Request<File, ? extends com.lzy.okgo.request.base.Request> request) {
                    }

                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                        TemplateSqlTool.getInstance().updateDownState(id, 2);
                        downSuccessRender(id);
                        //解压当前文件
                        ZipUtil.upzipFile(response.body(), compress, handler_zip, true);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<File> response) {
                    }

                    @Override
                    public void downloadProgress(Progress progress) {

                    }
                });
    }

    private Map<String, Boolean> error = new HashMap<>();

    /**
     * 模板文件下载反馈
     *
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

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CompressStatus.COMPLETED:
                    //解压成功
                    TemplateSqlTool.getInstance().updateCompressState(id, 2);
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

    public void sendIntent() {
        Intent intent = new Intent(TApplication.activityList.get(0), MainActivity.class);
        TApplication.activityList.get(0).startActivity(intent);
        TApplication.activityList.get(0).finish();
    }
    private int i = 0;
    /**
     * 网速监听
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int speed = intent.getIntExtra("speed", 0);
            down_speed.setText("正在下载" + speed + "kb/s");
            if (speed == 0) {
                i++;
                if (i > 12) {
                    i = 0;
                    if (isNetworkConnected(DownLoadActivity.this)) {
                        for (String key : current_task.keySet()) {
                            OkHttp.mOkHttpClient.cancel(key);
                        }
                        for (String key : current_task.keySet()) {
                            Runnable runnable = current_task.get(key);
                            if (runnable instanceof MyRunnable) {
                                executor_one.submit(runnable);
                            } else {
                                executor_second.submit(runnable);
                            }
                        }
                    }
                }
            }
        }
    }
}
