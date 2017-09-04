package com.huami.elearning.acceptNet;
import android.text.TextUtils;

import com.huami.elearning.TApplication;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.framed.Header;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class OkHttp {
    private static final String TAG = "OkHttp";
    public static final OkHttpClient mOkHttpClient = new OkHttpClient();
    /**
     * 设置的缓存大小
     */
    private static int cacheSize = 30 * 1024 * 1024; // 20 MiB
    // timeout
    static {
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(TApplication.getContext()), CookiePolicy.ACCEPT_ALL));
        /**
         * 当你的应用在被用户卸载后，SDCard/Android/data/你的应用的包名/ 这个目录下的所有文件都会被删除，不会留下垃圾信息
         */
        mOkHttpClient.setCache(new Cache(TApplication.getContext().getExternalCacheDir(), cacheSize));
    }
    // post without file with tag
    public static void asyncPost(String url, String authHead, String value, Map<String,String> body,Callback callback) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        for (String key : body.keySet()) {
            if (TextUtils.isEmpty(body.get(key))) {
                return;
            }
            formEncodingBuilder.add(key, body.get(key));
        }
        RequestBody formBody = formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .header(authHead, value)
                .post(formBody)
                .build();

        enqueue(request, callback);
    }
    // post without file with tag
    public static void asyncPost(String url, String authHead, String value,Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader(authHead, value)
                .build();
        enqueue(request, callback);
    }
    // post without file with tag
    public static void asyncPost(String url,Map<String,String> map, String tag,Callback callback) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        for (String key : map.keySet()) {
            if (TextUtils.isEmpty(map.get(key))) {
                return;
            }
            formEncodingBuilder.add(key, map.get(key));
        }
        RequestBody formBody = formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .tag(tag)
                .build();
        enqueue(request, callback);
    }
    // post without file with tag
    public static void asyncPost(String url,String tag,Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        enqueue(request, callback);
    }
    // post without file with tag
    public static void asyncPost(String url,Object tag,Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        enqueue(request, callback);
    }
    // post without file with tag
    public static void asyncPost(String url, Header head, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .head()
                .build();
        enqueue(request, callback);
    }
    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    private static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

}
