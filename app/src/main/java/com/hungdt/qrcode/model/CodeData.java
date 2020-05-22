package com.hungdt.qrcode.model;

public class CodeData {
    private String id;
    private String data;
    private String codeType;
    private String textType;
    private String createTime;
    private String createAt;
    private String save;
    private String like;
    private String note;
    private boolean isTicked = false;

    public CodeData(String id, String data, String codeType, String textType, String createTime, String createAt, String save, String like, String note) {
        this.id = id;
        this.data = data;
        this.codeType = codeType;
        this.textType = textType;
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
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

    public boolean isTicked() {
        return isTicked;
    }

    public void setTicked(boolean ticked) {
        isTicked = ticked;
    }
}

