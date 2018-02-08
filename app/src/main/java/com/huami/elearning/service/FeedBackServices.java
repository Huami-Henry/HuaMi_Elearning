package com.huami.elearning.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.huami.elearning.TApplication;
import com.huami.elearning.acceptNet.BaseNetDataBiz;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.db.FeedBackSqlTool;
import com.huami.elearning.db.FileSqlTool;
import com.huami.elearning.db.XmlSqlTool;
import com.huami.elearning.model.FeedBackInfo;
import com.huami.elearning.model.XmlDownInfo;
import com.squareup.okhttp.Request;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2017/7/17.
 */

public class FeedBackServices extends Service implements BaseNetDataBiz.RequestListener{
    private Timer mTimer;
    private final long INTERVAL=1*1000*30;
    private BaseNetDataBiz biz = new BaseNetDataBiz(this);
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 如果已经存在，则先取消
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new FeedBackServices.MyTimerTask(),0, INTERVAL);
    }

    @Override
    public void onResponse(BaseNetDataBiz.Model model) {
        String json = model.getJson();
        String tag = model.getTag();
        if (!TextUtils.isEmpty(tag)) {
            String tag_tag = tag.split(",")[0];
            switch (tag_tag) {
                case BaseConsts.HEART_BOXFLAG:
                    try {
                        JSONObject object = new JSONObject(json);
                        int code = object.getInt("code");
                        int down_id = Integer.valueOf(tag.split(",")[1]);
                        if (code == 0) {
                            XmlSqlTool.getInstance(TApplication.getContext()).updateXmlRenderState(down_id);
                        }
                    } catch (JSONException e) {
                        Log.e("我的上传结果HEART_BOXFLAG", "失败");
                    }
                    break;
                case BaseConsts.HEART_FILEFLAG:
                    try {
                        JSONObject object = new JSONObject(json);
                        int code = object.getInt("code");
                        if (code == 0) {
                            Log.e("我的上传结果HEART_FILEFLAG", "成功");
                            FeedBackSqlTool.getInstance().updateFeedState(tag.split(",")[1],1);
                        }
                    } catch (JSONException e) {
                        Log.e("我的上传结果HEART_FILEFLAG", "失败");
                    }
                    break;
            }
        }
    }

    @Override
    public void OnFailure(Request r, IOException o) {

    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //xml汇报
            List<XmlDownInfo> feedBacks= XmlSqlTool.getInstance(TApplication.getContext()).getPriDownloadInfos(1);

            for (XmlDownInfo info : feedBacks) {
                Log.e("我的xml汇报", feedBacks.toString());
                biz.downloadFile(BaseConsts.HEART_BOXFLAG + "?mac=" + BaseConsts.BOX_MAC + "&downId=" + info.getDownBoxId(), BaseConsts.HEART_BOXFLAG + "," + info.getDownBoxId());
            }
            //媒资文件汇报
            List<FeedBackInfo> renderData = FeedBackSqlTool.getInstance().getRenderData(0);
            for (FeedBackInfo info : renderData) {
                String assetId = info.getFeed_down_id().split("_")[1];
                biz.downloadFile(BaseConsts.HEART_FILEFLAG+"?mac="+BaseConsts.BOX_MAC+"&assetId="+assetId+"&downId="+info.getFeed_down_id().split("_")[0],BaseConsts.HEART_FILEFLAG+","+info.getFeed_down_id());
            }
        }
    }
}
