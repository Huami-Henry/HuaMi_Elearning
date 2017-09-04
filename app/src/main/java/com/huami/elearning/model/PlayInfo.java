package com.huami.elearning.model;

/**
 * Created by Henry on 2017/7/31.
 */

public class PlayInfo {
    private String play_key;
    private String play_file;

    public PlayInfo(String play_key, String play_file) {
        this.play_key = play_key;
        this.play_file = play_file;
    }

    public String getPlay_key() {
        return play_key;
    }

    public void setPlay_key(String play_key) {
        this.play_key = play_key;
    }

    public String getPlay_file() {
        return play_file;
    }

    public void setPlay_file(String play_file) {
        this.play_file = play_file;
    }

    @Override
    public String toString() {
        return "PlayInfo{" +
                "play_key='" + play_key + '\'' +
                ", play_file='" + play_file + '\'' +
                '}';
    }
}
