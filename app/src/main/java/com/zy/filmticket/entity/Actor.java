package com.zy.filmticket.entity;

public class Actor {

    private String nameEng; //英文名
    private String name; //中文名
    private String id; //id

    public Actor(String nameEng, String name, String id) {
        this.nameEng = nameEng;
        this.name = name;
        this.id = id;
    }

    public void setNameEng(String nameEng) {
         this.nameEng = nameEng;
     }

    public String getNameEng() {
         return nameEng;
     }

    public void setName(String name) {
         this.name = name;
     }

    public String getName() {
         return name;
     }

    public void setId(String id) {
         this.id = id;
     }

    public String getId() {
         return id;
     }

}