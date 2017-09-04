package com.huami.elearning.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import com.huami.elearning.util.SPCache;
import java.util.Locale;
import cn.jpush.android.api.JPushInterface;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by Henry on 2017/7/24.
 */

public class BaseActivity extends AppCompatActivity {
    protected String language_type=SPCache.getString(BaseConsts.SharedPrefrence.box_language, "CN");
    private final SparseArray<View> views = new SparseArray<>();

    public void showToast(String message) {
        BaseToast.showToast(this, message);
    }
    protected boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);
        SPCache.putString(BaseConsts.SharedPrefrence.box_language, language);
    }
    /**
     * 检查网络链接状态
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 显示存储的剩余空间
     */
    public long showAvailableSize(){
        return getAvailSpace(Environment.getDataDirectory().getAbsolutePath());//手机内部存储大小
    }
    /**
     * 获取某个目录的可用空间
     */
    private long getAvailSpace(String path){
        StatFs statfs = new StatFs(path);
        long size = statfs.getBlockSize();//获取分区的大小
        long count = statfs.getAvailableBlocks();//获取可用分区块的个数
        return size*count;
    }
    /**
     * 将xml转换成json
     * @param xml
     * @return
     */
    protected String xml2JSON(String xml) {
        try {
            XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
            return xmlToJson.toJson().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    protected String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
        }
        return "";
    }
    /**
     * 返回一个具体的view对象
     * 这个就是借鉴的base-adapter-helper中的方法
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public  <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }
}
