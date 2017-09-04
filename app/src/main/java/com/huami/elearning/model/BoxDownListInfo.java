package com.huami.elearning.model;

/**
 * Created by Administrator on 2017/7/13.
 */

public class BoxDownListInfo {
    /**
     * flag : 1
     * company_id : 1
     * operator_id : 1
     * is_box_state : 0
     * downBoxId : 1
     * existBegin : {"date":27,"hours":19,"seconds":24,"month":5,"nanos":0,"timezoneOffset":-480,"year":117,"minutes":53,"time":1498564404000,"day":2}
     * url : /xml/1/x14m2hsizn.xml
     * last_mod : {"date":13,"hours":11,"seconds":59,"month":6,"nanos":0,"timezoneOffset":-480,"year":117,"minutes":53,"time":1499918039000,"day":4}
     * name : 133135
     * id : 1
     * assetCount : 2
     * state : 1
     * create_date : {"date":12,"hours":16,"seconds":21,"month":6,"nanos":0,"timezoneOffset":-480,"year":117,"minutes":6,"time":1499846781000,"day":3}
     * existEnd : {"date":6,"hours":19,"seconds":27,"month":7,"nanos":0,"timezoneOffset":-480,"year":117,"minutes":53,"time":1502020407000,"day":0}
     * md5 : 490c905af1c141e43e17a3ec76df82d4
     */

    private int flag;
    private int company_id;
    private int operator_id;
    private int is_box_state;
    private int downBoxId;
    private ExistBeginInfo existBegin;
    private String url;
    private LastModInfo last_mod;
    private String name;
    private int id;
    private int assetCount;
    private int state;
    private int pri;
    private CreateDateInfo create_date;
    private ExistEndInfo existEnd;
    private String md5;

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(int operator_id) {
        this.operator_id = operator_id;
    }

    public int getIs_box_state() {
        return is_box_state;
    }

    public void setIs_box_state(int is_box_state) {
        this.is_box_state = is_box_state;
    }

    public int getDownBoxId() {
        return downBoxId;
    }

    public void setDownBoxId(int downBoxId) {
        this.downBoxId = downBoxId;
    }

    public ExistBeginInfo getExistBegin() {
        return existBegin;
    }

    public void setExistBegin(ExistBeginInfo existBegin) {
        this.existBegin = existBegin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LastModInfo getLast_mod() {
        return last_mod;
    }

    public void setLast_mod(LastModInfo last_mod) {
        this.last_mod = last_mod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public CreateDateInfo getCreate_date() {
        return create_date;
    }

    public void setCreate_date(CreateDateInfo create_date) {
        this.create_date = create_date;
    }

    public ExistEndInfo getExistEnd() {
        return existEnd;
    }

    public void setExistEnd(ExistEndInfo existEnd) {
        this.existEnd = existEnd;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
