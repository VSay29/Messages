package org.example.messagesFX.model;

import com.google.gson.annotations.SerializedName;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public ImageView getImageView() {
        return new ImageView(base64_img(this.image));
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nImage: " + image;
    }

    private Image base64_img(String base64) {
        byte[] bytes = java.util.Base64.getDecoder().decode(base64);
        return new javafx.scene.image.Image(new java.io.ByteArrayInputStream(bytes));
    }

}
