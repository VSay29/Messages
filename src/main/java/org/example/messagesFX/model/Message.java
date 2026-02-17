package org.example.messagesFX.model;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("_id")
    String id;
    String from;
    String to;
    String message, image, sent;

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

    @Override
    public String toString() {
        return "From: " + from + ", To: " + to + ", Message: " + message + ", Image: " + image + ", Sent: " + sent;
    }

}
