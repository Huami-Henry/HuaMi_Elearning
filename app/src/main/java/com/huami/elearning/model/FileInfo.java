package com.huami.elearning.model;

import java.io.Serializable;

/**
 * Created by Henry on 2017/7/17.
 */

public class FileInfo implements Serializable{
    private int asset_id;
    private String file_url;
    private String file_name;
    private int asset_type;
    private String create_time;
    private String file_path;
    private int click_count;
    public FileInfo(int asset_id,String file_url, String file_name, int asset_type,String file_path,int click_count,String create_time) {
        this.asset_id = asset_id;
        this.file_url = file_url;
        this.file_name = file_name;
        this.asset_type = asset_type;
        this.file_path = file_path;
        this.click_count = click_count;
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "asset_id=" + asset_id +
                ", file_url='" + file_url + '\'' +
                ", file_name='" + file_name + '\'' +
                ", asset_type=" + asset_type +
                ", create_time='" + create_time + '\'' +
                ", file_path='" + file_path + '\'' +
                ", click_count=" + click_count +
                '}';
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    public int getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(int asset_id) {
        this.asset_id = asset_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(int asset_type) {
        this.asset_type = asset_type;
    }
}
