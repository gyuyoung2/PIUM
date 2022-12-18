package com.example.deletelater;

import java.io.Serializable;

public class UserList implements Serializable {
    public String name;
    public String ptimage;

    public UserList(){
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }


    public String getPtimage() {
        return ptimage;
    }
    public void setPtimage(String ptimage) {
        this.ptimage = ptimage;
    }

    public UserList(String plantName, String imgsrc){
        this.name = plantName;
        this.ptimage = imgsrc;
    }

}
