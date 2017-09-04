package com.huami.elearning;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.huami.elearning.activity.HomeActivity;
import com.huami.elearning.base.BaseActivity;
import com.huami.elearning.base.BaseConsts;
import com.huami.elearning.util.SPCache;
import com.tomandjerry.coolanim.lib.CoolAnimView;

public class LoadingActivity extends BaseActivity {
    private CoolAnimView mCoolAnimView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loadding);
        SPCache.putBoolean(BaseConsts.SharedPrefrence.GO_UPDATE, false);
        mCoolAnimView = (CoolAnimView) findViewById(R.id.cool_view);
        mCoolAnimView.setOnCoolAnimViewListener(new CoolAnimView.OnCoolAnimViewListener() {
            @Override
            public void onAnimEnd() {
                Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCoolAnimView.stopAnim();
            }
        }, 4000);
    }
}
