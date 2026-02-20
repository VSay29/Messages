package org.example.messagesFX.model;

import com.google.gson.annotations.SerializedName;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Message {

    @SerializedName("_id")
    String id;
    String from;
    String to;
    String message, sent, image;

    public Message(String to, String message) {
        this.to = to;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSent() {
        return sent;
    }

    public ImageView getImageView() {
        return new ImageView(base64_img(this.image));
    }

    @Override
    public String toString() {
        return "From: " + from + ", To: " + to + ", Message: " + message + ", Image: " + image + ", Sent: " + sent;
    }

    private Image base64_img(String base64) {
        byte[] bytes = java.util.Base64.getDecoder().decode(base64);
        return new javafx.scene.image.Image(new java.io.ByteArrayInputStream(bytes));
    }

}
