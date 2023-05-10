package com.example.insien.ModelClass;

public class Blocked {

    String BlockingStatus;
    String userid;


    public Blocked() {
    }

    public Blocked(String blockingStatus, String userid) {
        this.BlockingStatus = blockingStatus;
        this.userid = userid;
    }

    public String getBlockingStatus() {
        return BlockingStatus;
    }

    public void setBlockingStatus(String blockingStatus) {
        BlockingStatus = blockingStatus;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
