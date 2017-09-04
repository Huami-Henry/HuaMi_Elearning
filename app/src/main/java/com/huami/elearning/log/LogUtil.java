package com.huami.elearning.log;

import android.os.Handler;
import android.os.Message;

import com.huami.elearning.util.CheckDisk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Henry on 2017/8/4.
 */

public class LogUtil {
    private Handler handler;
    private static LogUtil instance;
    private LogUtil() {
        String path = CheckDisk.checkState();
        initFile(new File(path+"log.txt"));
    }
    public static LogUtil getInstance() {
        if (instance == null) {
            instance = new LogUtil();
        }
        return instance;
    }
    /**
     * 收集Log日志
     * @param type
     * @param msg
     */
    public void doLog(String type, String msg) {
//        String obj = type + "--->" + msg;
//        Message message = new Message();
//        message.obj = obj;
//        handler.sendMessage(message);
    }
    /**
     * 一行一行写入文件，解决写入中文字符时出现乱码
     * 流的关闭顺序：先打开的后关，后打开的先关，
     * @throws IOException
     */
    private void writeToFile(String message) {
        try {
            bw.write(message+"\n");
            bw.flush();
            osw.flush();
            fos.flush();
        } catch (IOException e) {

        }
    }

    private FileOutputStream fos;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private void initFile(File file){
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String message = (String) msg.obj;
                writeToFile(message);
            }
        };
    }

}
