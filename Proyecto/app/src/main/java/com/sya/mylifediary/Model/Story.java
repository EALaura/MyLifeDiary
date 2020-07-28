package com.sya.mylifediary.Model;

import java.io.Serializable;

/* Es el modelo para las historias, cada historia debe tener
   un título, una ubicación, una descripción y una foto
 */
public class Story implements Serializable {
    private String title;
    private String location;
    private String description;
    private String imageAddress;
    private double latitude;
    private double longitude;

    /* Métodos get y set para cada atributo */
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

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
