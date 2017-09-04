package com.huami.elearning.model;

/**
 * Created by Henry on 2017/8/1.
 */

public class TemplateInfo {
    private int id;
    private int template_id;
    private String template_path;
    private String template_url;
    private int template_state;
    private int template_downState;
    public TemplateInfo(int id,int template_id, String template_path, String template_url, int template_state, int template_downState) {
        this.template_id = template_id;
        this.template_path = template_path;
        this.template_url = template_url;
        this.template_state = template_state;
        this.template_downState = template_downState;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }

    public String getTemplate_path() {
        return template_path;
    }

    public void setTemplate_path(String template_path) {
        this.template_path = template_path;
    }

    public String getTemplate_url() {
        return template_url;
    }

    public void setTemplate_url(String template_url) {
        this.template_url = template_url;
    }

    public int getTemplate_state() {
        return template_state;
    }

    public void setTemplate_state(int template_state) {
        this.template_state = template_state;
    }

    public int getTemplate_downState() {
        return template_downState;
    }

    public void setTemplate_downState(int template_downState) {
        this.template_downState = template_downState;
    }

    @Override
    public String toString() {
        return "TemplateInfo{" +
                "id=" + id +
                ", template_id=" + template_id +
                ", template_path='" + template_path + '\'' +
                ", template_url='" + template_url + '\'' +
                ", template_state=" + template_state +
                ", template_downState=" + template_downState +
                '}';
    }
}
