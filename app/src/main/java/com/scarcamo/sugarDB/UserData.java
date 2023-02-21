package com.scarcamo.sugarDB;

import com.orm.SugarRecord;

/**
 * Created by Ravinder on 4/4/2018.
 */

public class UserData extends SugarRecord<UserData> {
    String shadeId;
    String color;
    String date;


    public UserData() {

    }
    public UserData(String id,String color, String date) {
        this.shadeId = id;
        this.color = color;
        this.date = date;
    }

    public String getShadeId() {
        return shadeId;
    }

    public void setShadeId(String shadeId) {
        this.shadeId = shadeId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
