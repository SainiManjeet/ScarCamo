package com.scarcamo.pojo;

/**
 * Created by Ravinder on 2/26/2018.
 */

public class User {

    private String username, email , password, deviceType, deviceToken;

    public User(String username, String email, String password, String deviceType, String deviceToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.deviceType = deviceType;
        this.deviceToken = deviceToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
