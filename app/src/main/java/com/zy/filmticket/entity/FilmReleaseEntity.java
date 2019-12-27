package com.zy.filmticket.entity;

public class FilmReleaseEntity {

    private int id;
    private String filmId;
    private String cinemaId;
    private String releaseDate;
    private int releasePosition;
    private String cinemaName;
    private String releaseTime;
    private String filmName;
    private int releaseNum;

    public FilmReleaseEntity(){

    }

    public FilmReleaseEntity(int id, String filmId, String cinemaId, String releaseDate, int releasePosition,
                             String cinemaName, String releaseTime, String filmName, int releaseNum) {
        this.id = id;
        this.filmId = filmId;
        this.cinemaId = cinemaId;
        this.releaseDate = releaseDate;
        this.releasePosition = releasePosition;
        this.cinemaName = cinemaName;
        this.releaseTime = releaseTime;
        this.filmName = filmName;
        this.releaseNum = releaseNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getReleasePosition() {
        return releasePosition;
    }

    public void setReleasePosition(int releasePosition) {
        this.releasePosition = releasePosition;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public int getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(int releaseNum) {
        this.releaseNum = releaseNum;
    }
}
