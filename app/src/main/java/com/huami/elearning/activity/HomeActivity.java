package com.huami.elearning.activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huami.elearning.R;
import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.BaseNetDataBiz;
import com.huami.elearning.base.BaseActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FileDownSqlTool;
import com.huami.elearning.db.FileSqlTool;
import com.huami.elearning.db.PlaySqlTool;
import com.huami.elearning.db.RecordSqlTool;
import com.huami.elearning.db.TemplateSqlTool;
import com.huami.elearning.db.XmlSqlTool;
import com.huami.elearning.model.APPVersionInfo;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.model.FileInfo;
import com.huami.elearning.model.PlayInfo;
import com.huami.elearning.model.RecordInfo;
import com.huami.elearning.model.TemplateInfo;
import com.huami.elearning.model.XmlDownInfo;
import com.huami.elearning.service.DownTemplateService;
import com.huami.elearning.service.MainHeartbeatService;
import com.huami.elearning.service.RecordService;
import com.huami.elearning.service.XmlToFileService;
import com.huami.elearning.ui.OpenFileUtil;
import com.huami.elearning.util.CheckDisk;
import com.huami.elearning.util.DownFileUtil;
import com.huami.elearning.util.LanguageManager;
import com.huami.elearning.util.SPCache;
import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.squareup.okhttp.Request;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class HomeActivity extends BaseActivity implements ChromeClientCallbackManager.ReceivedTitleCallback,BaseNetDataBiz.RequestListener {
    private AgentWeb mAgentWeb;
    private RelativeLayout myLayout;
    private String path = CheckDisk.checkState();
    private BaseNetDataBiz biz_down_config;
    private static final String DOWN_FILE_CONFIG = "DOWN_FILE_CONFIG";
    private static final String DOWN_APP_CONFIG = "DOWN_APP_CONFIG";
    private String app_version_url;
    private TimerTask task_check;
    private Timer timer_check = new Timer();
    private String template;
    private View progress_view;
    private TextView update;
    private TextView language_change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TApplication.activityList.add(this);
        if (isApkDebug(this)) {
            showToast(BaseConsts.BOX_MAC);
        }
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        update = (TextView) findViewById(R.id.update);
        language_change = (TextView) findViewById(R.id.language_change);
        if ("CN".equals(language_type)) {
            language_change.setText("中文");
        } else if ("EN".equals(language_type)) {
            language_change.setText("English");
        }

        if (TextUtils.isEmpty(path)) {
            showToast(LanguageManager.getInstance().getLanguageTip("device_exception", language_type));//弹出一个页面提示我的机器没有插盒子
        } else {
            if (isNetworkConnected(this)) {
                downLoadAppConfig();
            }
            initView();
            setWebView();
            initListener();
        }
    }
    public boolean isApkDebug(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {
        }
        return false;
    }
    private void initListener() {
        language_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language_type.equals("CN")) {
                    SPCache.putString(BaseConsts.SharedPrefrence.box_language, "EN");
                    language_type = "EN";
                    language_change.setText("英文");
                } else if (language_type.equals("EN")) {
                    SPCache.putString(BaseConsts.SharedPrefrence.box_language, "CN");
                    language_type = "CN";
                    language_change.setText("中文");
                }
            }
        });
    }

    private updateReceiver receiver;

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = new updateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BaseConsts.BROAD_UPDATE);
            registerReceiver(receiver, filter);
        }
    }

    private void downLoadAppConfig() {
        biz_down_config = new BaseNetDataBiz(this);
        biz_down_config.downloadFile(BaseConsts.BASE_APP_FILE_CONFIG, DOWN_APP_CONFIG);
    }

    private void initView() {
        progress_view = findViewById(R.id.progress_view);
        setLocalData();

    }

    public void getLocalTemplate() {

        TemplateInfo compressTemplate = TemplateSqlTool.getInstance().getCompressTemplate();
        if (compressTemplate != null) {
            template = String.valueOf(compressTemplate.getTemplate_id());
        }
        if (TextUtils.isEmpty(template)) {
            template = "1";
        }
    }

    private String html_url;

    private void setLocalData() {
        getLocalTemplate();
        //没有任何更新信息
        File file = new File(path + template + "/index.html");
        if (file.exists()) {
            html_url = "file://" + path + template + "/index.html?tplId=" + template;
        }
    }

    private void setWebView() {
        mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(myLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .closeProgressBar()// 使用默认进度条
                .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                .setWebChromeClient(null)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()
                .go(html_url);
        WebView mWebView = mAgentWeb.getWebCreator().get();
        WebSettings webSettings= mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.addJavascriptInterface(new DMediaJavaScriptInterface(), "DMedia");
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }
    };

    @Override
    public void onReceivedTitle(WebView view, String title) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        showBackDialog(LanguageManager.getInstance().getLanguageTip("make_sure_back_elearning", language_type));
    }

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

    private void downLoadInfoFile() {
        startService();
    }

    private Intent intent_heart, intent_xml_to_file, intent_log, intent_template;

    public void startService() {
        boolean serviceMainHeartbeatService = isServiceRunning("com.huami.elearning.service.MainHeartbeatService");
        if (!serviceMainHeartbeatService) {
            intent_heart = new Intent(HomeActivity.this, MainHeartbeatService.class);
            startService(intent_heart);
        }
        boolean serviceXmlToFileService = isServiceRunning("com.huami.elearning.service.XmlToFileService");
        if (!serviceXmlToFileService) {
            intent_xml_to_file = new Intent(HomeActivity.this, XmlToFileService.class);
            startService(intent_xml_to_file);
        }
        boolean serviceRecordService = isServiceRunning("com.huami.elearning.service.RecordService");
        if (!serviceRecordService) {
            intent_log = new Intent(HomeActivity.this, RecordService.class);
            startService(intent_log);
        }
        boolean serviceDownTemplateService = isServiceRunning("com.huami.elearning.service.DownTemplateService");
        if (!serviceDownTemplateService) {
            intent_template = new Intent(HomeActivity.this, DownTemplateService.class);
            startService(intent_template);
        }
    }

    public AlertDialog dialog_tip;

    public void showDownloadDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle(LanguageManager.getInstance().getLanguageTip("media_update_reminder", language_type));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(LanguageManager.getInstance().getLanguageTip("go_update", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_tip = null;
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
                Intent intent = new Intent(HomeActivity.this, UpMediaActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(LanguageManager.getInstance().getLanguageTip("check_later", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog_tip = null;
                SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, true);
            }
        });
        try {
            dialog_tip = builder.create();
            dialog_tip.show();
        } catch (Exception e) {
        }
    }

    public void showBackDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle(LanguageManager.getInstance().getLanguageTip("back_reminder", language_type));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(LanguageManager.getInstance().getLanguageTip("sure_back", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                finish();
            }
        });
        builder.setNegativeButton(LanguageManager.getInstance().getLanguageTip("cancel_back", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        try {
            builder.create().show();
        } catch (Exception e) {

        }
    }

    private void showAppUpdateDialog(final APPVersionInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle(LanguageManager.getInstance().getLanguageTip("upgrade_software", language_type));
        builder.setCancelable(false);
        builder.setMessage(LanguageManager.getInstance().getLanguageTip("current_version", language_type) + ":" + getVersion() + "\n" + LanguageManager.getInstance().getLanguageTip("recent_version", language_type) + ":" + info.getVersion().getLatestversion());
        builder.setPositiveButton(LanguageManager.getInstance().getLanguageTip("down_now", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                app_version_url = info.getVersion().getUrl();
                if (isNetworkConnected(HomeActivity.this)) {
                    DownFileUtil.downFile(progress_view, app_version_url, path + "appDown", "elearning.apk");
                } else {
//                    showToast("网络休息了,唤醒它再来下载吧");
                }
            }
        });
        builder.setNegativeButton(LanguageManager.getInstance().getLanguageTip("check_later", language_type), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downLoadInfoFile();
            }
        });
        builder.create().show();
    }

    @Override
    public void OnFailure(Request r, IOException o) {

    }

    class DMediaJavaScriptInterface {
        @JavascriptInterface
        public void open(String target) {
            PlayInfo fileInfo = PlaySqlTool.getInstance().getFileInfo(target);
            FileInfo filePath = FileSqlTool.getInstance().getInfo(fileInfo.getPlay_file());
            if (filePath != null) {
                String file_path = CheckDisk.checkState() + filePath.getFile_path();
                String file_name = filePath.getFile_name();
                saveRecord(file_name);
                switch (filePath.getAsset_type()) {
                    case 1:
                        saveAndPlay(file_name, file_path);
                        break;
                    case 2:
                        Intent intent = OpenFileUtil.openPic(file_path);
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent1 = OpenFileUtil.OpenAudio(file_path);
                        startActivity(intent1);
                        break;
                    case 4:
                        try {
                            Intent intent2 = OpenFileUtil.OpenPPt(file_path);
                            startActivity(intent2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 打开视频
     *
     * @param file_name
     * @param path
     */
    private void saveAndPlay(String file_name, String path) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.destroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
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
        if (intent_template != null) {
            stopService(intent_template);
        }
    }

    class updateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<FileDownInfo> maxPris = FileDownSqlTool.getInstance().getMaxPris(0);
            if (maxPris.size() > 0) {
                if (isNetworkConnected(HomeActivity.this)) {
                    if (dialog_tip == null) {
                        boolean need = SPCache.getBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, false);
                        if (!need) {
                            showDownloadDialog(LanguageManager.getInstance().getLanguageTip("update_media_documents", language_type));
                        }
                    }
                }
            }
        }
    }
}
