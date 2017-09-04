package com.huami.elearning.model;

/**
 * Created by Administrator on 2017/8/1.
 */

public class DoShakeListInfo {
    private String key_only;
    private String value;
    private int templateId;

    public int getTemplate_id() {
        return templateId;
    }

    public void setTemplate_id(int template_id) {
        this.templateId = template_id;
    }

    public String getKey_only() {
        return key_only;
    }

    public void setKey_only(String key_only) {
        this.key_only = key_only;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
