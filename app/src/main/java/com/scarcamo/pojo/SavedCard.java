package com.scarcamo.pojo;

/**
 * Created by Ravinder on 3/11/2018.
 */

public class SavedCard {
    private String uid, shade_id;

    public SavedCard(String id, String uID) {
        this.shade_id = id;
        this.uid = uID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getShade_id() {
        return shade_id;
    }

    public void setShade_id(String shade_id) {
        this.shade_id = shade_id;
    }


}
