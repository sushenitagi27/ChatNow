package com.example.chatnow;

public class userprofile {
    public String username,userid,phonenumber;

    public userprofile() {
    }

    public userprofile(String username, String userid) {
        this.username = username;
        this.userid = userid;
    }

    public userprofile(String username, String userid, String phonenumber) {
        this.username = username;
        this.userid = userid;
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
