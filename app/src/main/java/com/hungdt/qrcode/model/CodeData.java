package com.hungdt.qrcode.model;

public class CodeData {
    private String id;
    private String data;
    private String type;
    private String createTime;
    private String createAt;
    private String save;
    private String like;
    private String note;

    public CodeData(String id, String data, String type, String createTime, String createAt, String save, String like, String note) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.createTime = createTime;
        this.createAt = createAt;
        this.save = save;
        this.like = like;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

