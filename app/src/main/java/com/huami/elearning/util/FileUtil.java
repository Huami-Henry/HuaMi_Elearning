package com.huami.elearning.util;

import java.io.File;

/**
 * Created by Henry on 2017/8/21.
 */

public class FileUtil {
    public static void creatFolder(String path){
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
