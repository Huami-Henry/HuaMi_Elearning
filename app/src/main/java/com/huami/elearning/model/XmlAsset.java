package com.huami.elearning.model;

/**
 * Created by Administrator on 2017/7/14.
 */

public class XmlAsset {
    private int asset_id;
    private String type_name;
    private String md5;
    private String filename;
    private int filesize;
    private String showName;
    private int asset_type;
    private int playtime;

    public int getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(int asset_id) {
        this.asset_id = asset_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(int asset_type) {
        this.asset_type = asset_type;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    @Override
    public String toString() {
        return "XmlAsset{" +
                "asset_id=" + asset_id +
                ", type_name='" + type_name + '\'' +
                ", md5='" + md5 + '\'' +
                ", filename='" + filename + '\'' +
                ", filesize=" + filesize +
                ", showName='" + showName + '\'' +
                ", asset_type=" + asset_type +
                ", playtime=" + playtime +
                '}';
    }
}
