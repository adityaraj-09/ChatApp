package com.example.insien;

import java.util.ArrayList;

public class UserStatus {
    private String name,profileImage;
    private  long lastupdated;
    private ArrayList<Status > statuses;

    public UserStatus() {
    }

    public UserStatus(String name, String profileImage, long lastupdated, ArrayList<Status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastupdated = lastupdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
