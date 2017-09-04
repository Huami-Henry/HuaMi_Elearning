package com.huami.elearning.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Environment;

import com.huami.elearning.TApplication;

import java.io.File;

/**
 * Created by Administrator on 2017/5/10.
 */

public class CheckDisk {
    public static String path = "";
    //    先判断
    private static String udisk0 = "/mnt/usb_storage/USB_DISK0/udisk0/";
    //    是否存在，如果存在则赋值给path,如果不存在则判断
    private static  String USB_DISK1_udisk0 = "/mnt/usb_storage/USB_DISK1/udisk0/";
    //    是否存在，如果存在则赋值给path,如果不存在则判断
    private static String USB_DISK0_udisk1 = "/mnt/usb_storage/USB_DISK0/(1)/";
    //    是否存在，如果存在则赋值给path,如果不存在则判断
    private static String USB_DISK0_udisk2 = "/mnt/usb_storage/USB_DISK0/(2)/";
    //    是否存在，如果存在则赋值给path,如果不存在提示“设备存储空间异常，请重启设备”。

    public static String checkState(){
        File file = new File(udisk0);
        if (file.exists() && file.isDirectory()) {
            path = udisk0;
        } else {
            file = new File(USB_DISK1_udisk0);
            if (file.exists() && file.isDirectory()) {
                path = USB_DISK1_udisk0;
            } else {
                file = new File(USB_DISK0_udisk1);
                if (file.exists() && file.isDirectory()) {
                    path = USB_DISK0_udisk1;
                } else {
                    file = new File(USB_DISK0_udisk2);
                    if (file.exists() && file.isDirectory()) {
                        path = USB_DISK0_udisk2;
                    }
                }
            }
        }
//        手机测试用
        if ("".equals(path)) {
            path = Environment.getExternalStorageDirectory()+"/";
        }
        return path;
    }
    public static String getMacAddress(){
        try {
            WifiManager wm = (WifiManager) TApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String macAddress = wm.getConnectionInfo().getMacAddress();
            String[] split = macAddress.split(":");
            StringBuilder builder = new StringBuilder();
            for (String s : split) {
                builder.append(s);
            }
            return builder.toString().toUpperCase();
        } catch (Exception e) {
        }
        return null;
    }
    public static boolean checkAppVersion(int version){
        int present_version = getVersion();
        if (present_version>=version) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    private static int getVersion() {
        int version = 0;
        PackageManager manager = TApplication.getContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo("com.huami.elearning", 0);
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }
}
