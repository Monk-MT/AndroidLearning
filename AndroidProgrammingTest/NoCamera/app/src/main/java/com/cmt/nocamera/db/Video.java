package com.cmt.nocamera.db;

import android.graphics.Bitmap;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-03-29-11:28
 */
public class Video {
    int id;
    private String name;
    private String url;
    private long createtime;
    private long size;
    private long time;
    private int image;

    public Video() {
    }

    public Video(int id, String name, String url, long createtime, long size, long time, int image) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.createtime = createtime;
        this.size = size;
        this.time = time;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
