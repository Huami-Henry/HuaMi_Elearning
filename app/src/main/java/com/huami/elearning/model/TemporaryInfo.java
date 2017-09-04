package com.huami.elearning.model;

/**
 * Created by Henry on 2017/7/31.
 */
public class TemporaryInfo {
    private int template_id;
    private String temporary_key;
    private String temporary_value;
    public int getTemplate_id() {
        return template_id;
    }
    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }
    public String getTemporary_key() {
        return temporary_key;
    }
    public void setTemporary_key(String temporary_key) {
        this.temporary_key = temporary_key;
    }
    public String getTemporary_value() {
        return temporary_value;
    }
    public void setTemporary_value(String temporary_value) {
        this.temporary_value = temporary_value;
    }
    public TemporaryInfo(int template_id, String temporary_key, String temporary_value) {
        this.template_id = template_id;
        this.temporary_key = temporary_key;
        this.temporary_value = temporary_value;
    }
    @Override
    public String toString() {
        return "TemporaryInfo{" +
                "template_id=" + template_id +
                ", temporary_key='" + temporary_key + '\'' +
                ", temporary_value='" + temporary_value + '\'' +
                '}';
    }
}
