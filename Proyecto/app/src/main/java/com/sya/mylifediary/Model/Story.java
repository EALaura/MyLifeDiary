package com.sya.mylifediary.Model;

import java.io.Serializable;

public class Story implements Serializable {

    String location, description;
    int photo;

    public Story(String location, String description, int photo) {
        this.location = location;
        this.description = description;
        this.photo = photo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
