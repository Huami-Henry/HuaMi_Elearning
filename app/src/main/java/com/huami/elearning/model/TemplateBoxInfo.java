package com.huami.elearning.model;

/**
 * Created by Henry on 2017/8/2.
 */

public class TemplateBoxInfo {
    private int id;
    private int  templateId;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TemplateBoxInfo{" +
                "id=" + id +
                ", templateId=" + templateId +
                ", url='" + url + '\'' +
                '}';
    }
}
