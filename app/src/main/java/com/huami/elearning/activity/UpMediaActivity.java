package com.huami.elearning.activity;
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
import com.huami.elearning.R;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.adapter.DownLoadAdapter;
import com.huami.elearning.base.BaseActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FileDownSqlTool;
import com.huami.elearning.db.FileSqlTool;
import com.huami.elearning.db.PlaySqlTool;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.db.TemporarySqlTool;
import com.huami.elearning.listener.FileDownListener;
import com.huami.elearning.manager.DownloadTask;
import com.huami.elearning.model.CompressStatus;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.FileInfo;
import com.huami.elearning.model.Media_Type;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.model.TemporaryInfo;
import com.huami.elearning.service.Net_Service;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.DownUtil;
import com.huami.elearning.util.LanguageManager;
import com.huami.elearning.util.SPCache;
import com.huami.elearning.util.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
public class UpMediaActivity extends BaseActivity implements FileDownListener{
    private DownloadTask task;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private TimerTask time_task;
    private Timer timer = new Timer();
    private DownLoadAdapter adapter;
    private List<FileDownInfo> describes = new ArrayList<>();
    private String BaseUrl = CheckDisk.checkState();
    private int id;
    private View progress_view;
    private TextView down_speed;
    private ProgressDialog progressDialog;

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
        adapter = new DownLoadAdapter(describes);
        recyclerView.setAdapter(adapter);
        getDownList();
    }

    private void getDownList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(UpMediaActivity.this, "Loading...", "正在检测需要更新的媒资文件", true, false);
            }
            @Override
            protected Void doInBackground(Void... params) {
                if (isNetworkConnected(UpMediaActivity.this)) {
                    setData();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
                startDown();
            }
        }.execute();
    }

    private void setData() {
        List<FileDownInfo> maxPris = FileDownSqlTool.getInstance().getMaxPris(0);
        for (FileDownInfo info : maxPris) {
            long contentLength=info.getFile_length();
            if (info.getFile_length() == 0) {
                contentLength = DownUtil.getContentLength(info.getFile_url());
                FileDownSqlTool.getInstance().updateFileSize(info.getFile_url(), contentLength);
            }
            if (contentLength > 0) {
                FileDownInfo des = new FileDownInfo(
                        info.getAssert_id(),
                        info.getFile_url(),
                        info.getFile_name(),
                        info.getMd5(),
                        info.getFile_type(),
                        info.getFile_pri(),
                        contentLength,
                        info.getFile_progress(),
                        info.getDown_id(),
                        info.getDown_state(),
                        info.getRender_state());
                describes.add(des);
            }
        }
    }

    private void startDown() {
        if (isNetworkConnected(this)) {
            if (task == null) {
                task = new DownloadTask(describes, this);
            }
            task.startDown();
        }
        progress_view.setVisibility(View.VISIBLE);
        if (time_task == null) {
            time_task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            };
            timer.schedule(time_task, 0, 2000);
        }
        registNetReceiver();
    }

    @Override
    public void onBackPressed() {
        showAppUpdateDialog(LanguageManager.getInstance().getLanguageTip("make_sure_back",language_type));
    }

    protected void showAppUpdateDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle(LanguageManager.getInstance().getLanguageTip("back_reminder",language_type));
        builder.setMessage(message);
        builder.setPositiveButton(LanguageManager.getInstance().getLanguageTip("sure_back",language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                finishDown();
            }
        });
        builder.setNegativeButton(LanguageManager.getInstance().getLanguageTip("continue_download",language_type), new DialogInterface.OnClickListener() {
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

    private Intent intent_net;
    private MyReceiver receiver;

    private void registNetReceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BaseConsts.BROAD_NET);
        registerReceiver(receiver, filter);
        if (intent_net == null) {
            intent_net = new Intent(this, Net_Service.class);
            startService(intent_net);
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
        if (task != null) {
            task.cancelDown();
        }
        if (intent_net != null) {
            stopService(intent_net);
            unregisterReceiver(receiver);
        }
        SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, false);
    }
    /**
     * 文件下载成功后的处理
     */
    private Handler handler_zip=new MyHandler();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    public void doSuccess(FileDownInfo file_info) {
        try {
            Log.e("我的fileName",file_info.toString());
            String path = DownUtil.getFilePath(file_info.getFile_type())+file_info.getFile_name();
            Log.e("执行到着了么", "获取路径");
            FileInfo info = new FileInfo(
                    file_info.getAssert_id(),
                    file_info.getFile_url(), file_info.getFile_name(), file_info.getFile_type(), path, 0, format.format(new Date()));
            FileSqlTool.getInstance().insertFile(info);
            Log.e("执行到着了么", "是的执行到了");
            Map<String, String> map = new HashMap<>();
            map.put("mac", BaseConsts.BOX_MAC);
            map.put("assetId", String.valueOf(file_info.getAssert_id()));
            map.put("downId", String.valueOf(file_info.getDown_id()));

            FileDownSqlTool.getInstance().updateDownState(file_info.getFile_url(),1);

            OkHttp.asyncPost(BaseConsts.HEART_FILEFLAG, map, file_info.getFile_name() + file_info.getAssert_id() + file_info.getDown_id(), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }
                @Override
                public void onResponse(Response response) throws IOException {
                }
            });
            //1:查询临时模板表中是否有对应的key
            List<TemporaryInfo> fileKey = TemporarySqlTool.getInstance().getFileKey(file_info.getFile_name());
            if (fileKey!=null) {
                for (TemporaryInfo info_key : fileKey) {
                    boolean exit = PlaySqlTool.getInstance().exit(info_key.getTemporary_key());
                    if (exit) {
                        PlayInfo info_play = new PlayInfo(info_key.getTemporary_key(), info_key.getTemporary_value());
                        PlaySqlTool.getInstance().update(info_play);
                    } else {
                        PlayInfo info_play = new PlayInfo(info_key.getTemporary_key(), info_key.getTemporary_value());
                        PlaySqlTool.getInstance().insertInfo(info_play);
                    }
                    TemporarySqlTool.getInstance().delLines(info_key);
                    checkEmpty(info_key);
                }
            }
        } catch (Exception e) {

        }
    }
    private void checkEmpty(TemporaryInfo info) {
        //3检查临时表中的t_id是否为空
        boolean empty = TemporarySqlTool.getInstance().checkEmpty(info.getTemplate_id());
        Log.e("临时模板是否为空","--->"+empty);
        //设置模板表的状态
        //查看模板表的下载情况和解压情况
        if (empty) {
            int downState = TemplateSqlTool.getInstance().checkDownState(info.getTemplate_id());
            //当下载状态为0
            TemplateInfo fileInfo = TemplateSqlTool.getInstance().getFileInfo(info.getTemplate_id());
            if (fileInfo != null) {
                Log.e("临时模板是否为空","--->"+fileInfo.toString());
                id = fileInfo.getId();
                if (downState == 0 || downState == 1) {
                    String path = CheckDisk.checkState();
                    if (fileInfo.getTemplate_url() != null) {
                        String[] split = fileInfo.getTemplate_url().split("/");
                        downZip(fileInfo.getId(),fileInfo.getTemplate_id(), fileInfo.getTemplate_url(), path + BaseConsts.TEMPLATE_PATH, split[split.length - 1], path + info.getTemplate_id());
                    }
                } else {
                    String path = CheckDisk.checkState();
                    if (fileInfo.getTemplate_url() != null) {
                        String[] split = fileInfo.getTemplate_url().split("/");
                        File file_zip = new File(path + BaseConsts.TEMPLATE_PATH + File.separator + split[split.length - 1]);
                        if (file_zip.exists()) {
                            ZipUtil.upzipFile(file_zip, path + info.getTemplate_id(), handler_zip, true);
                        }
                    }
                }
            }
        } else {
            if (task != null) {
                boolean have = task.haveNoAsset();
                if (!have) {
                    //1：继续检测是否有新的媒资需要下载
                    boolean b = checkNewAsset();
                    if (b) {
                        getMoreList();
                    } else {
                        //2：弹出对话框告诉用户新的媒资文件下载完成。
                        backDialog(LanguageManager.getInstance().getLanguageTip("need_to_return_ok",language_type));
                    }
                }
            }
        }
    }

    /**
     * 用户返回确认
     */
    public void backDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle(LanguageManager.getInstance().getLanguageTip("back_reminder",language_type));
        builder.setMessage(message);
        builder.setPositiveButton(LanguageManager.getInstance().getLanguageTip("sure_back",language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                finishDown();
            }
        });
        builder.setNegativeButton(LanguageManager.getInstance().getLanguageTip("cancel_back",language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void getMoreList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                unregisterReceiver(receiver);
                describes.clear();
                progress_view.setVisibility(View.GONE);
                progressDialog = ProgressDialog.show(UpMediaActivity.this, "Loading...", LanguageManager.getInstance().getLanguageTip("restart_detecting_media_files",language_type), true, false);
            }
            @Override
            protected Void doInBackground(Void... params) {
                if (isNetworkConnected(UpMediaActivity.this)) {
                    setData();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
                startDown();
            }
        }.execute();
    }
    private boolean checkNewAsset() {
        List<FileDownInfo> maxPris = FileDownSqlTool.getInstance().getMaxPris(0);
        if (maxPris.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 开始下载模板文件
     * @param id
     * @param url
     * @param folder
     * @param name
     */
    public void downZip(final int id, final int templateId, String url, final String folder, String name, final String compress) {
        Log.e("我的操作", "--->开始下载模板" + url + "-->" + folder + "--->" + name);
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
                        downSuccessRender(templateId);
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

    @Override
    public void downSuccess(FileDownInfo info) {
        doSuccess(info);
    }
    @Override
    public void downError(FileDownInfo info) {
    }
    @Override
    public void downFailure(FileDownInfo info) {
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
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private int i = 0;
    /**
     * 网速监听
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int speed = intent.getIntExtra("speed", 0);
            down_speed.setText(LanguageManager.getInstance().getLanguageTip("current_net_speed",language_type) + speed + "kb/s");
            if (speed == 0) {
                i++;
                if (i > 12) {
                    i = 0;
                    if (isNetworkConnected(UpMediaActivity.this)) {
                        if (task != null) {
                            task.restartDown();
                        }
                    }
                }
            }
        }
    }
}
