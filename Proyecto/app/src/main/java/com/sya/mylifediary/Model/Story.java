package com.sya.mylifediary.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Story implements Serializable {
    private String title;
    private String location;
    private String description;
    private transient Bitmap photo;     // para que sea compatible con serializable

    public Story(String title, String location, String description, Bitmap photo) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
