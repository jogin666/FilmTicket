package com.zy.filmticket.entity;

import java.io.Serializable;

public class CinemaEntity implements Serializable{

    private String cinemaId;
    private String cinemaName;
    private String city;
    private String cinemaAddrss;

    public CinemaEntity(){

    }

    public CinemaEntity(String cinemaId, String cinemaName, String city, String cinemaAddrss) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.city = city;
        this.cinemaAddrss = cinemaAddrss;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCinemaAddrss() {
        return cinemaAddrss;
    }

    public void setCinemaAddrss(String cinemaAddrss) {
        this.cinemaAddrss = cinemaAddrss;
    }
}
