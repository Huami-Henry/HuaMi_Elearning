package com.huami.elearning.model;

import java.util.List;

/**
 * Created by Henry on 2017/7/25.
 */

public class FeedRecordRoot {
    private String mac;
    private List<FeedRecordInfo> file;
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<FeedRecordInfo> getFile() {
        return file;
    }

    public void setFile(List<FeedRecordInfo> file) {
        this.file = file;
    }
}
