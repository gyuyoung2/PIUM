package com.example.deletelater;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Plant implements Serializable { //Plant Model
    private String name; //식물 이름
    private Bitmap imgsrc; //식물 img

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(Bitmap imgsrc) {
        this.imgsrc = imgsrc;
    }

    Plant(String name, Bitmap imgsrc){
        this.name = name;
        this.imgsrc = imgsrc;
    }
}
