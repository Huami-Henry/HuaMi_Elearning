package com.huami.elearning.model;
/**
 * Created by Henry on 2017/7/14.
 */
public class XmlRoot {
    private XmlDownList downlist;
    public XmlDownList getDownlist() {
        return downlist;
    }
    public void setDownlist(XmlDownList downlist) {
        this.downlist = downlist;
    }

    @Override
    public String toString() {
        return "XmlRoot{" +
                "downlist=" + downlist +
                '}';
    }
}
