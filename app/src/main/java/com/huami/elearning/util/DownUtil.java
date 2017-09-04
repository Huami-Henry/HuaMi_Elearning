package com.huami.elearning.util;
import com.huami.elearning.acceptNet.OkHttp;
import com.huami.elearning.model.Media_Type;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
/**
 * Created by Henry on 2017/8/24.
 */

public class DownUtil {
    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    public static long getContentLength(String downloadUrl) {
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
     * 获取路径
     * @return
     */
    public static String getFilePath(int asset_type){
        String file_path = null;
        switch (asset_type) {
            case 1:
                file_path =  Media_Type.VIDEO + File.separator;
                break;
            case 2:
                file_path =  Media_Type.AUDIO + File.separator;
                break;
            case 3:
                file_path =  Media_Type.IMAGE + File.separator;
                break;
            case 4:
                file_path =  Media_Type.TEXT + File.separator;
                break;
            default:
                file_path =  Media_Type.OTHER + File.separator;
                break;
        }
        return file_path;
    }
}
