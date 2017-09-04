package com.huami.elearning.util;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.huami.elearning.TApplication;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;

/**
 * Created by henry on 2017/8/10.
 */
public class DownFileUtil {
    public static void downFile(final View progress, String url, final String folder, String name) {
        OkGo.<File>get(url)//
                .tag(url)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new FileCallback(folder,name) {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        progress.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onSuccess(Response<File> response) {
                        progress.setVisibility(View.GONE);
                        openFile(response.body());
                    }
                    @Override
                    public void onError(Response<File> response) {
                    }
                    @Override
                    public void downloadProgress(Progress progress) {

                    }
                });
    }
    /**
     * 通过文件安装apk
     *
     * @param file
     */
    private static void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        TApplication.getContext().startActivity(intent);
    }

}
