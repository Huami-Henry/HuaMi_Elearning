package com.huami.elearning.model;
/**
 * Created by Henry on 2017/7/25.
 */

public class RecordInfo {
    private String record_file;
    private String record_time;
    private int id;

    public RecordInfo(String record_file, String record_time) {
        this.record_file = record_file;
        this.record_time = record_time;
    }

    public RecordInfo(int id, String record_file, String record_time) {
        this.record_file = record_file;
        this.record_time = record_time;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecord_file() {
        return record_file;
    }

    public void setRecord_file(String record_file) {
        this.record_file = record_file;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "record_file='" + record_file + '\'' +
                ", record_time='" + record_time + '\'' +
                '}';
    }
}
