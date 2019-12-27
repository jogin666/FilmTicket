package com.zy.filmticket.entity;

import java.io.Serializable;

public class UserEntity implements Serializable{
    private String useraccount;
    private String username;
    private String agender;
    private String password;
    private String headImg;
    private String brithday;
    private String selfIntroduction;

    public UserEntity(){}

    public UserEntity(String useraccount, String password) {
        this.useraccount = useraccount;
        this.password = password;
    }

    public UserEntity(String useraccount, String username, String agender,
                      String headImg, String brithday, String selfIntroduction) {
        this.useraccount = useraccount;
        this.username = username;
        this.agender = agender;
        this.headImg = headImg;
        this.brithday = brithday;
        this.selfIntroduction = selfIntroduction;
    }

    public String getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(String useraccount) {
        this.useraccount = useraccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAgender() {
        return agender;
    }

    public void setAgender(String agender) {
        this.agender = agender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    @Override
    public String toString() {
        return "useraccount="+ useraccount + "&username="+ username +"&gender=" + agender +
                "&headImg=" + headImg +"&brithday=" + brithday + "&selfIntroduction=" + selfIntroduction;
    }
}
