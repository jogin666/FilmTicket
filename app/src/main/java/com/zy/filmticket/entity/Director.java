package com.zy.filmticket.entity;

public class Director {

    private String codeId;
    private String engName;
    private String cnName;

    public Director(String codeId, String engName, String cnName) {
        this.codeId = codeId;
        this.engName = engName;
        this.cnName = cnName;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }
}
