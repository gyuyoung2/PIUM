package com.example.deletelater;

import java.io.Serializable;

public class User implements Serializable {
    public String name; //식물 닉네임
    public String date; //식물 심은날짜
    public String plantDetail; //식물 정보
    public String ptimage; //식물 사진
    public String ptnumber;

    public String getPtnumber() {
        return ptnumber;
    }

    public void setPtnumber(String ptnumber) {
        this.ptnumber = ptnumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlantDetail(String plantDetail) {this.plantDetail = plantDetail;}

//    public void setPtnumber(String ptnumber) {this.ptnumber = ptnumber;}

    public void setPtimage(String ptimage) {this.ptimage = ptimage;}


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getPlantDetail() { return plantDetail; }

//    public String getPtnumber() { return ptnumber; }

    public String getPtimage() { return ptimage; }

    public User(String name, String date, String plantDetail, String ptimage) {
        this.name = name;
        this.date = date;
        this.plantDetail = plantDetail;
//        this.ptnumber = ptnumber;
        this.ptimage = ptimage;
    }

    public User(){

    }


}