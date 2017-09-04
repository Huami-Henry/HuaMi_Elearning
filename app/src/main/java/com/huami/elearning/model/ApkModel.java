package com.huami.elearning.model;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ApkModel implements Serializable {
    private static final long serialVersionUID = 2072893447591548402L;

    public String name;
    public String url;
    public String iconUrl;
    public int priority;

    public ApkModel() {
        Random random = new Random();
        priority = random.nextInt(100);
    }
}
