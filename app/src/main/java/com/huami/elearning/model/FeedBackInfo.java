package com.huami.elearning.model;

/**
 * Created by Henry on 2017/7/19.
 */

public class FeedBackInfo {
    public String feed_down_id;//汇报的id
    public int feed_state;//汇报的状态0未汇报 1已汇报
    public String create_date;
    public String file_name;
    private int down_state;
    public FeedBackInfo() {
    }
    public FeedBackInfo(String feed_down_id, int feed_state,String file_name,int down_state,String create_date) {
        this.feed_down_id = feed_down_id;
        this.feed_state = feed_state;
        this.file_name = file_name;
        this.create_date = create_date;
        this.down_state = down_state;
    }

    @Override
    public String toString() {
        return "FeedBackInfo{" +
                "feed_down_id='" + feed_down_id + '\'' +
                ", feed_state=" + feed_state +
                ", create_date='" + create_date + '\'' +
                ", file_name='" + file_name + '\'' +
                ", down_state=" + down_state +
                '}';
    }

    public int getDown_state() {
        return down_state;
    }

    public void setDown_state(int down_state) {
        this.down_state = down_state;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFeed_down_id() {
        return feed_down_id;
    }

    public void setFeed_down_id(String feed_down_id) {
        this.feed_down_id = feed_down_id;
    }

    public int getFeed_state() {
        return feed_state;
    }

    public void setFeed_state(int feed_state) {
        this.feed_state = feed_state;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }
}
