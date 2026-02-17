package org.example.messagesFX.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    String id;
    String name, password, image;

    public User(String name, String password, String image) {
        this.name = name;
        this.password = password;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nImage: " + image;
    }

}
