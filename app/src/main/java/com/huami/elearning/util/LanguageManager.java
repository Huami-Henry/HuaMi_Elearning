package com.huami.elearning.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Henry on 2017/8/25.
 */
public class LanguageManager {
    private static Map<String, String> cn_language = new HashMap<>();
    private static Map<String, String> en_language = new HashMap<>();
    private static LanguageManager instance = null;
    private LanguageManager() {
    }
    private static synchronized void syncInit() {
        if (instance == null) {
            setCN_language();
            setEN_language();
            instance = new LanguageManager();
        }
    }
    public static LanguageManager getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }
    private static void setCN_language(){
        cn_language.put("upgrade_software", "软件版本更新");
        cn_language.put("current_version", "当前版本");
        cn_language.put("recent_version", "最新版本");
        cn_language.put("check_later", "以后再说");
        cn_language.put("down_now", "开始下载");
        cn_language.put("updating", "更新中...");
        cn_language.put("go_update", "去更新");
        cn_language.put("media_update_reminder", "媒资更新提醒");
        cn_language.put("update_media_documents", "有媒资文件需要更新");
        cn_language.put("make_sure_back", "确定要退出下载媒资页面么?");
        cn_language.put("make_sure_back_elearning", "确定要退出E_learning么?");
        cn_language.put("back_reminder", "退出提醒");
        cn_language.put("sure_back", "确认");
        cn_language.put("cancel_back", "取消");
        cn_language.put("continue_download", "继续下载");
        cn_language.put("detecting_media_files", "正在检测需要更新的媒资文件");
        cn_language.put("restart_detecting_media_files", "文件下载完成,重新检测需要更新的媒资文件");
        cn_language.put("device_exception", "设备存储异常,请重启！");
        cn_language.put("current_net_speed", "当前网速");
        cn_language.put("need_to_return_ok", "媒资下载完成是否需要返回主页？");
    }
    private static void setEN_language(){
        en_language.put("upgrade_software", "Software Version Upgrade");
        en_language.put("current_version", "Current Version");
        en_language.put("recent_version", "Latest Version");
        en_language.put("check_later", "Do Later");
        en_language.put("down_now", "Download Start");
        en_language.put("updating", "Updating...");
        en_language.put("go_update", "Go Update");
        en_language.put("media_update_reminder", "Media Update Reminder");
        en_language.put("update_media_documents", "There is a need to update the media documents");
        en_language.put("make_sure_back", "Make sure you exit the download interface");
        en_language.put("back_reminder", "Exit Reminder");
        cn_language.put("make_sure_back_elearning", "Are you sure you want to quit E_learning?");
        en_language.put("sure_back", "Confirm Exit");
        en_language.put("cancel_back", "Cancel");
        en_language.put("continue_download", "Continue Download");
        en_language.put("detecting_media_files", "Detecting media files that need to be updated");
        en_language.put("restart_detecting_media_files", "Down Over,Detecting media files that need to be updated");
        en_language.put("device_exception", "Device Exception！");
        en_language.put("current_net_speed", "Current Speed");
        cn_language.put("need_to_return_ok", "Do you need to return to the home page for download?");
    }
    public String getLanguageTip(String key,String type){
        try {
            switch (type) {
                case "CN":
                    return cn_language.get(key);
                case "EN":
                    return en_language.get(key);
            }
        } catch (Exception e) {

        }
        return null;
    }
}
