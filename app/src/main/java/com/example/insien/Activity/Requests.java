package com.example.insien.Activity;

public class Requests {
    String RUid,RequestStatus,RName,RProfile_img,RAbout;

    public Requests() {
    }

    public Requests(String RUid, String RequestStatus, String RName, String RProfile_img, String RAbout) {
        this.RUid = RUid;
        this.RequestStatus = RequestStatus;
        this.RName = RName;
        this.RProfile_img = RProfile_img;
        this.RAbout = RAbout;
    }

    public String getRUid() {
        return RUid;
    }

    public void setRUid(String RUid) {
        this.RUid = RUid;
    }

    public String getRequestStatus() {
        return RequestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        RequestStatus = RequestStatus;
    }

    public String getRName() {
        return RName;
    }

    public void setRName(String RName) {
        this.RName = RName;
    }

    public String getRProfile_img() {
        return RProfile_img;
    }

    public void setRProfile_img(String RProfile_img) {
        this.RProfile_img = RProfile_img;
    }

    public String getRAbout() {
        return RAbout;
    }

    public void setRAbout(String RAbout) {
        this.RAbout = RAbout;
    }
}
