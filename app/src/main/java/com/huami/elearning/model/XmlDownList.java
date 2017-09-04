package com.huami.elearning.model;
import java.util.List;
/**
 * Created by Administrator on 2017/7/14.
 */
public class XmlDownList {
    private String downname;
    private int downId;
    private String url;
    private int assetCount;
    private List<XmlAsset> asset;

    public String getDownname() {
        return downname;
    }

    public void setDownname(String downname) {
        this.downname = downname;
    }

    public int getDownId() {
        return downId;
    }

    public void setDownId(int downId) {
        this.downId = downId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }

    public List<XmlAsset> getAsset() {
        return asset;
    }

    public void setAsset(List<XmlAsset> asset) {
        this.asset = asset;
    }

    @Override
    public String toString() {
        return "XmlDownList{" +
                "downname='" + downname + '\'' +
                ", downId=" + downId +
                ", url='" + url + '\'' +
                ", assetCount=" + assetCount +
                ", asset=" + asset +
                '}';
    }
}
