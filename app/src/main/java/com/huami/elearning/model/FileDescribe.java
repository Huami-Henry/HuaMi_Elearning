package com.huami.elearning.model;

/**
 * Created by Henry on 2017/8/18.
 */

public class FileDescribe {
    private int asset_id;//媒资的id
    private int asset_type;//记录文件的类型
    private String fileName;//记录文件的名字
    private String fileUrl;//记录文件的url
    private long fileLength;//记录文件的总长度
    private int progress;//记录下载的进度
    private int downCount;//记录下载次数
    private String createTime;
    private String md5;
    private int downState;

    public FileDescribe(int asset_id, int asset_type, String fileName, String fileUrl, long fileLength, int progress, int downCount, String createTime, String md5, int downState) {
        this.asset_id = asset_id;
        this.asset_type = asset_type;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileLength = fileLength;
        this.progress = progress;
        this.downCount = downCount;
        this.createTime = createTime;
        this.md5 = md5;
        this.downState = downState;
    }

    public int getDownState() {
        return downState;
    }

    public void setDownState(int downState) {
        this.downState = downState;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public int getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(int asset_id) {
        this.asset_id = asset_id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(int asset_type) {
        this.asset_type = asset_type;
    }
    @Override
    public String toString() {
        return "FileDescribe{" +
                ", asset_id=" + asset_id +
                ", asset_type=" + asset_type +
                ", fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", fileUrl='" + fileUrl + '\'' +
                ", progress=" + progress +
                ", downCount=" + downCount +
                '}';
    }
}
