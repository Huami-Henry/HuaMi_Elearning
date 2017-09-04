package com.huami.elearning.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huami.elearning.LoadingActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent sayHelloIntent=new Intent(context,LoadingActivity.class);
            sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sayHelloIntent);
        }
    }
}
