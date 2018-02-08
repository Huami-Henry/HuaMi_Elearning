package com.huami.elearning.base;
import com.huami.elearning.util.CheckDisk;
/**
 * Created by Henry on 2017/5/3.
 */
public class BaseConsts {
    public static final String CONFIG_VERSION = "huaMi_config_version";
    public static final String BASE_FILE_CONFIG = "http://101.37.170.228/bt/fastdownload/update_version_whole.xml";
    public static final String BASE_APP_FILE_CONFIG = "http://101.37.170.228/bt/elearning_version.xml";
    public static final String BROADCAST_ACTION = "com.inesanet.dmedia";
    public static final String CONFIG_DOWNLOAD_PROGRESS = "CONFIG_DOWNLOAD_PROGRESS_";
    public static final String CONFIG_COMPRESS_PROGRESS = "CONFIG_COMPRESS_PROGRESS_";
//    public static final String BASE_URL = "http://101.37.77.231:8080/e-learningmer";//测试
    public static final String BASE_URL = "http://elearning-report.huami-tech.com:8080/e-learningmer";
    public static final String HEART_BEAT = BASE_URL+"/box/addBoxHeartBeat";//参数：mac    盒子心跳
    public static final String HEART_BOXFLAG = BASE_URL+"/box/updateDownBoxFlag";//参数：mac、downId   下载xml文件成功
    public static final String HEART_FILEFLAG = BASE_URL+"/box/addBoxDownSuccess";//    参数：mac、assetId、downId   下载一个媒质成功
    public static final String ClickLog = BASE_URL+"/boxclicklogdetail/addBoxClickLogDetail";//    参数：record json
    public static final String TEMPLATE_FEED = BASE_URL+"/tempLateBox/updateTemplateBoxFlag";//mac=12345678&templateId=1"    参数：record json
    public static final String BOX_MAC = CheckDisk.getMacAddress();
//    public static final String BOX_MAC = "50F0D3C1B5B9";
//    public static final String BOX_MAC = "B84D5685FC99";
    public static final String TEMPLATE_PATH = "template";
    public static final String TEMPLATE_XML_PATH = "template_xml";
    public static class SharedPrefrence{
        public static final String TEMPLATEID = "template_id";
        public static final String GO_UPDATE = "GO_UPDATE";
        public static final String box_language = "box_language";
    }

    public static final String BROAD_NET = "com.huami.elearning.speed";
    public static final String BROAD_UPDATE = "com.huami.elearning.update";
    public static final String DB_NAME = "file.db";
}
