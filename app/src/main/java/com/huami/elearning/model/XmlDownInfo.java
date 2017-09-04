package com.huami.elearning.model;

import java.io.Serializable;

/**
 * Created by Henry on 2017/7/18.
 */

public class XmlDownInfo implements Serializable{
    private int downBoxId;
    private String xml_url;
    private String xml_name;
    private int xml_down_state;
    private int xml_render_state;
    private int xml_pri;

    @Override
    public String toString() {
        return "XmlDownInfo{" +
                "downBoxId=" + downBoxId +
                ", xml_url='" + xml_url + '\'' +
                ", xml_name='" + xml_name + '\'' +
                ", xml_down_state=" + xml_down_state +
                ", xml_render_state=" + xml_render_state +
                ", xml_pri=" + xml_pri +
                '}';
    }

    public XmlDownInfo(int downBoxId, String xml_url, String xml_name, int xml_down_state, int xml_render_state, int xml_pri) {
        this.downBoxId = downBoxId;
        this.xml_url = xml_url;
        this.xml_name = xml_name;
        this.xml_down_state = xml_down_state;
        this.xml_render_state = xml_render_state;
        this.xml_pri = xml_pri;
    }

    public int getDownBoxId() {
        return downBoxId;
    }

    public void setDownBoxId(int downBoxId) {
        this.downBoxId = downBoxId;
    }

    public String getXml_url() {
        return xml_url;
    }

    public void setXml_url(String xml_url) {
        this.xml_url = xml_url;
    }

    public String getXml_name() {
        return xml_name;
    }

    public void setXml_name(String xml_name) {
        this.xml_name = xml_name;
    }

    public int getXml_down_state() {
        return xml_down_state;
    }

    public void setXml_down_state(int xml_down_state) {
        this.xml_down_state = xml_down_state;
    }

    public int getXml_render_state() {
        return xml_render_state;
    }

    public void setXml_render_state(int xml_render_state) {
        this.xml_render_state = xml_render_state;
    }

    public int getXml_pri() {
        return xml_pri;
    }

    public void setXml_pri(int xml_pri) {
        this.xml_pri = xml_pri;
    }
}
