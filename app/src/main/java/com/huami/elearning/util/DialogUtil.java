package com.huami.elearning.util;
/**
 * Created by Administrator on 2017/8/17.
 */

public class DialogUtil {
    private DialogUtil(){}
    private static DialogUtil instance;
    public static DialogUtil getInstance(){
        if (instance == null) {
            instance = new DialogUtil();
        }
        return instance;
    }


}
