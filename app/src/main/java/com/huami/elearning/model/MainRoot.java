package com.huami.elearning.model;

import java.util.List;

/**
 * Created by Henry on 2017/7/13.
 */

public class MainRoot {
    private int code;
    private String msg;
    private List<BoxDownListInfo> boxDownList;
    private List<DoShakeListInfo> dotshakeList;
    private List<TemplateBoxInfo> templateBoxList;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<BoxDownListInfo> getBoxDownList() {
        return boxDownList;
    }

    public void setBoxDownList(List<BoxDownListInfo> boxDownList) {
        this.boxDownList = boxDownList;
    }

    public List<DoShakeListInfo> getDotshakeList() {
        return dotshakeList;
    }

    public void setDotshakeList(List<DoShakeListInfo> dotshakeList) {
        this.dotshakeList = dotshakeList;
    }

    public List<TemplateBoxInfo> getTemplateBoxList() {
        return templateBoxList;
    }

    public void setTemplateBoxList(List<TemplateBoxInfo> templateBoxList) {
        this.templateBoxList = templateBoxList;
    }
}
