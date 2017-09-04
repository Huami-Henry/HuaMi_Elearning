package com.huami.elearning.model;

import java.io.Serializable;

/**
 * Created by Henry on 2017/8/10.
 */

public class FileDownInfo implements Serializable{
    private int assert_id;
    private String file_url;
    private String file_name;
    private String md5;
    private int file_type;
    private int file_pri;
    private long file_length;
    private int file_progress;
    private int down_id;
    private int down_state;
    private int render_state;
    public FileDownInfo(int assert_id, String file_url, String file_name, String md5, int file_type, int file_pri, long file_length, int file_progress, int down_id, int down_state, int render_state) {
        this.assert_id = assert_id;
        this.file_url = file_url;
        this.file_name = file_name;
        this.md5 = md5;
        this.file_type = file_type;
        this.file_pri = file_pri;
        this.file_length = file_length;
        this.file_progress = file_progress;
        this.down_id = down_id;
        this.down_state = down_state;
        this.render_state = render_state;
    }

    public int getAssert_id() {
        return assert_id;
    }

    public void setAssert_id(int assert_id) {
        this.assert_id = assert_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public int getFile_pri() {
        return file_pri;
    }

    public void setFile_pri(int file_pri) {
        this.file_pri = file_pri;
    }

    public long getFile_length() {
        return file_length;
    }

    public void setFile_length(long file_length) {
        this.file_length = file_length;
    }

    public int getFile_progress() {
        return file_progress;
    }

    public void setFile_progress(int file_progress) {
        this.file_progress = file_progress;
    }

    public int getDown_id() {
        return down_id;
    }

    public void setDown_id(int down_id) {
        this.down_id = down_id;
    }

    public int getDown_state() {
        return down_state;
    }

    public void setDown_state(int down_state) {
        this.down_state = down_state;
    }

    public int getRender_state() {
        return render_state;
    }

    public void setRender_state(int render_state) {
        this.render_state = render_state;
    }

    @Override
    public String toString() {
        return "FileDownInfo{" +
                "assert_id=" + assert_id +
                ", file_url='" + file_url + '\'' +
                ", file_name='" + file_name + '\'' +
                ", md5='" + md5 + '\'' +
                ", file_type=" + file_type +
                ", file_pri=" + file_pri +
                ", file_length=" + file_length +
                ", file_progress=" + file_progress +
                ", down_id=" + down_id +
                ", down_state=" + down_state +
                ", render_state=" + render_state +
                '}';
    }
}
