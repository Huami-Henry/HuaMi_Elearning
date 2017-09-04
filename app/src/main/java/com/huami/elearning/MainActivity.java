package com.huami.elearning;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.gson.Gson;
import com.huami.elearning.acceptNet.BaseNetDataBiz;
import com.huami.elearning.base.BaseActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.RecordSqlTool;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.model.APPVersionInfo;
import com.huami.elearning.model.RecordInfo;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.service.DownTemplateService;
import com.huami.elearning.service.FeedBackServices;
import com.huami.elearning.service.MainHeartbeatService;
import com.huami.elearning.service.RecordService;
import com.huami.elearning.service.XmlToFileService;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.DownFileUtil;
import com.huami.elearning.util.SPCache;
import com.squareup.okhttp.Request;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends BaseActivity implements BaseNetDataBiz.RequestListener{
    private String template;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TApplication.activityList.add(this);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        showAvailableSize();
        if (TextUtils.isEmpty(path)) {
            showToast("设备存储异常,请重启！");//弹出一个页面提示我的机器没有插盒子
        } else {
            if (isNetworkConnected(this)) {
                downLoadAppConfig();
            }
            initView();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            _layout.removeView(mWebView);
            mWebView.stopLoading();
            mWebView.destroy();
        }
        stopService();
        if (task_check != null) {
            try {
                task_check.cancel();
                timer_check.cancel();
            } catch (Exception e) {
            }
        }
    }
    private static final String DOWN_FILE_CONFIG = "DOWN_FILE_CONFIG";
    private static final String DOWN_APP_CONFIG = "DOWN_APP_CONFIG";
    public RelativeLayout progress_view;
    private RoundCornerProgressBar progress;
    private TextView tv_progress;
    private String path = CheckDisk.checkState();
    private WebView mWebView;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private FrameLayout _layout;
    private MyHandler myHandler;
    private boolean mediaplaying = false;
    private void downLoadAppConfig() {
        biz_down_config = new BaseNetDataBiz(this);
        biz_down_config.downloadFile(BaseConsts.BASE_APP_FILE_CONFIG, DOWN_APP_CONFIG);
    }

    private void initView() {
        mWebView = getView(R.id.webview);
        _layout = getView(R.id.framelayout);
        progress_view = getView(R.id.progress_view);
        progress = getView(R.id.progress);
        tv_progress = getView(R.id.tv_progress);
        initWebView();
    }

    private void initWebView() {
        myHandler = new MyHandler(this);
        mediaPlayer = new MediaPlayer();
        surfaceView = new SurfaceView(MainActivity.this);
        surfaceView.getHolder().addCallback(new myCallback());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mWebView.loadUrl("javascript:playEnded()");
            }
        });
        setWebView();
        setLocalData();

    }
    public void getLocalTemplate(){
        TemplateInfo compressTemplate = TemplateSqlTool.getInstance().getCompressTemplate();
        if (compressTemplate != null) {
            template = String.valueOf(compressTemplate.getTemplate_id());
        }
        if (TextUtils.isEmpty(template)) {
            template = "1";
        }
    }
    private void setLocalData() {
        getLocalTemplate();
        //没有任何更新信息
        File file = new File(path + template + "/index.html");
        if (file.exists()) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("type", 0x01);
            b.putString("url", "file://" + path + template + "/index.html?tplId=" + template);
            msg.setData(b);
            myHandler.sendMessage(msg);
            mWebView.setVisibility(View.VISIBLE);
        }
    }
    private void setWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        String appCacheDir = this.getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        mWebView.setWebChromeClient(mChromeClient);
        mWebView.addJavascriptInterface(new DMediaJavaScriptInterface(), "DMedia");
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private Intent intent_heart,intent_xml_to_file,intent_log,intent_feed,intent_template,intent_file;
    public void startService(){
        boolean serviceMainHeartbeatService = isServiceRunning("com.huami.elearning.service.MainHeartbeatService");
        if (!serviceMainHeartbeatService) {
            intent_heart = new Intent(MainActivity.this, MainHeartbeatService.class);
            startService(intent_heart);
        }
        boolean serviceXmlToFileService = isServiceRunning("com.huami.elearning.service.XmlToFileService");
        if (!serviceXmlToFileService) {
            intent_xml_to_file = new Intent(MainActivity.this, XmlToFileService.class);
            startService(intent_xml_to_file);
        }
        boolean serviceRecordService = isServiceRunning("com.huami.elearning.service.RecordService");
        if (!serviceRecordService) {
            intent_log = new Intent(MainActivity.this, RecordService.class);
            startService(intent_log);
        }
        boolean serviceFeedBackServices = isServiceRunning("com.huami.elearning.service.FeedBackServices");
        if (!serviceFeedBackServices) {
            intent_feed = new Intent(MainActivity.this, FeedBackServices.class);
            startService(intent_feed);
        }
        boolean serviceDownTemplateService = isServiceRunning("com.huami.elearning.service.DownTemplateService");
        if (!serviceDownTemplateService) {
            intent_template = new Intent(MainActivity.this, DownTemplateService.class);
            startService(intent_template);
        }

//        if (task_check == null) {
//            task_check=new TimerTask() {
//                @Override
//                public void run() {
//                    List<FileInfo> maxPriInfos = FileSqlTool.getInstance().getMaxPriInfos(0);
//                    if (maxPriInfos.size() > 0) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (isNetworkConnected(MainActivity.this)) {
//                                    if (dialog_tip == null) {
//                                        showDownloadDialog("有需要更新的媒资文件");
//                                    }
//                                }
//                            }
//                        });
//                    }
//                }
//            };
//            timer_check.schedule(task_check,10000,5000);
//        }
    }

    public AlertDialog dialog_tip;
    public void showDownloadDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("媒资更新提醒");
        builder.setMessage(message);
        builder.setPositiveButton("去更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_tip = null;
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
                Intent intent = new Intent(MainActivity.this, DownLoadActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog_tip = null;
                SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
            }
        });
        dialog_tip = builder.create();
        dialog_tip.show();
    }
    public void stopService() {
        if (intent_heart != null) {
            stopService(intent_heart);
        }
        if (intent_xml_to_file != null) {
            stopService(intent_xml_to_file);
        }
        if (intent_log != null) {
            stopService(intent_log);
        }
        if (intent_feed != null) {
            stopService(intent_feed);
        }
        if (intent_template != null) {
            stopService(intent_template);
        }
        if (intent_file != null) {
            stopService(intent_file);
        }
    }

    //下载完配置信息文件之后来判断是否需要更新
    public void downLoadInfoFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService();
            }
        }).start();
    }
    protected String app_version_url;//app要个新的版本
    protected void showAppUpdateDialog(final APPVersionInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("软件版本更新");
        builder.setMessage("当前版本：" + getVersion() + "\n" + "最新版本：" + info.getVersion().getLatestversion());
        builder.setPositiveButton("开始下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                app_version_url = info.getVersion().getUrl();
                if (isNetworkConnected(MainActivity.this)) {
                    DownFileUtil.downFile(progress_view,app_version_url,path+"appDown","elearning.apk");
                } else {
                    showToast("网络休息了,唤醒它再来下载吧");
                }
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downLoadInfoFile();
            }
        });
        builder.create().show();
    }
    private BaseNetDataBiz biz_down_config;
    class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            Bundle b = msg.getData();
            int type = b.getInt("type", 0);
            switch (type) {
                case 1:
                    String urlString = b.getString("url");
                    try {
                        theActivity.mWebView.loadUrl(urlString);
                    } catch (Exception e) {
                    }
                    break;
                case 5:
                    String newurlString = b.getString("url");
                    try {
                        theActivity.mWebView.loadUrl(newurlString);
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    }
    /**
     * http下载配置文件的响应
     *
     * @param model
     */
    @Override
    public void onResponse(BaseNetDataBiz.Model model) {
        try {
            String xml_str = model.getJson();
            String json = xml2JSON(xml_str);
            final APPVersionInfo info = new Gson().fromJson(json, APPVersionInfo.class);
            boolean needUpdate = CheckDisk.checkAppVersion(info.getVersion().getVersioncode());
            if (needUpdate) {
                showAppUpdateDialog(info);
            } else {
                downLoadInfoFile();
            }
        } catch (Exception e) {
            showToast("版本号获取失败,请重启尝试重新获取。");
        }
    }
    @Override
    public void OnFailure(Request r, IOException o) {
        if (!r.tag().equals(DOWN_FILE_CONFIG)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("更新失败,请检查网络配置情况,稍后尝试!");
                }
            });
        }
    }
    public void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 123456, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 1秒钟后重启应用
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    private class myCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mediaPlayer.setDisplay(surfaceView.getHolder());
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                if (mediaplaying)
                    mediaPlayer.stop();
            } catch (Exception e) {
                Log.d("stopVideo", "Exception", e);
            }
            mediaPlayer.release();
        }
    }
    private View myView = null;
    private WebChromeClient mChromeClient = new WebChromeClient() {
        private CustomViewCallback myCallback = null;
        // 配置权限 （在WebChromeClinet中实现）
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        // Android 使WebView支持HTML5 Video（全屏）播放的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            parent.removeView(mWebView);
            parent.addView(view);
            myView = view;
            myCallback = callback;
            mChromeClient = this;
        }
        @Override
        public void onHideCustomView() {
            if (myView != null) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);
                parent.addView(mWebView);
                mWebView.requestFocus();
                myView = null;
            }
        }
    };

    class DMediaJavaScriptInterface {

        DMediaJavaScriptInterface() {
        }

        @JavascriptInterface
        public void videoInit(int width, int height, int left, int top, float rotation) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("type", 0x02);
            b.putInt("width", width);
            b.putInt("height", height);
            b.putInt("left", left);
            b.putInt("top", top);
            b.putFloat("rotation", rotation);
            msg.setData(b);
            myHandler.sendMessage(msg);
        }

        @JavascriptInterface
        public void open(String target) {
//            PlayInfo fileInfo = PlaySqlTool.getInstance().getFileInfo(target);
//            FileInfo filePath = FileSqlTool.getInstance().getFilePath(fileInfo.getPlay_file());
//            if (filePath != null) {
//                String file_path = filePath.getFile_path();
//                String file_name = filePath.getFile_name();
//                switch (filePath.getAsset_type()) {
//                    case 1:
//                        saveAndPlay(file_name, file_path);
//                        break;
//                    case 2:
//                        Intent intent = openPic(file_path);
//                        startActivity(intent);
//                        break;
//                    case 3:
//                        OpenAudio(file_path);
//                        break;
//                    case 4:
//                        OpenPPt(file_path);
//                        break;
//                    default:
//                        break;
//                }
//            }
        }
    }

    private int i = 0;
    /**
     * 打开ppt
     * @param path
     */
    public void OpenPPt(String path){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("OpenMode", "ReadMode");
        bundle.putBoolean("ClearBuffer", true);
        bundle.putBoolean("ClearTrace", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(packageName, className);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
        startActivity(intent);
    }
    /**
     * 打开音频
     * @param path
     */
    public void OpenAudio(String path){
        File file = new File(path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }
    /**
     * 创建一个可以打开图片的Intent
     * @param path
     * @return
     */
    public Intent openPic(String path){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    /**
     * 打开视频
     * @param file_name
     * @param path
     */
    private void saveAndPlay(String file_name,String path) {
        saveRecord(file_name);
        Uri uri = Uri.parse(path);
        //调用系统自带的播放器
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }
    /**
     * 保存记录
     *
     * @param filename
     */
    public void saveRecord(String filename) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RecordInfo recordInfo = new RecordInfo(filename, format.format(new Date()));
        RecordSqlTool.getInstance(TApplication.getContext()).insertInfo(recordInfo);
    }
    public Intent getPPTFileIntent(String Path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName(packageName, className);
        File file = new File(Path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        Bundle bundle = new Bundle();
        bundle.putString("OPEN_MODE", "Normal");
        bundle.putBoolean("ClearBuffer", true); //关闭文件时是否清空临时文件
        bundle.putBoolean("ClearTrace", true);  //关闭文件时是否删除使用记录
        intent.putExtras(bundle);
        return intent;
    }

    private String className = "cn.wps.moffice.documentmanager.PreStartActivity2";
    private String packageName = "cn.wps.moffice_eng";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mWebView != null) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            try {
                mWebView.getClass().getMethod("onPause")
                        .invoke(mWebView, (Object[]) null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            mChromeClient.onHideCustomView();
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            try {
                mWebView.getClass().getMethod("onResume")
                        .invoke(mWebView, (Object[]) null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            mWebView.onResume();
            mWebView.resumeTimers();
        }
    }

    private Timer timer_check = new Timer();
    private TimerTask task_check;
}
