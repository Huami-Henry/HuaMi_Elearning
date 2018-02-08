package com.huami.elearning.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

/**
 * Created by Henry on 2017/8/24.
 */

public class OpenFileUtil {
    private static String className = "cn.wps.moffice.documentmanager.PreStartActivity2";
    private static String packageName = "cn.wps.moffice_eng";
    /**
     * 打开ppt
     * @param path
     */
    public static Intent OpenPPt(String path) throws Exception{
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("OpenMode", "ReadMode");
//        bundle.putBoolean("ClearBuffer", true);
//        bundle.putBoolean("ClearTrace", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(packageName, className);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        return intent;
    }
    /**
     * 打开音频
     * @param path
     */
    public static Intent OpenAudio(String path){
        File file = new File(path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }
    /**
     * 创建一个可以打开图片的Intent
     * @param path
     * @return
     */
    public static Intent openPic(String path){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
}
