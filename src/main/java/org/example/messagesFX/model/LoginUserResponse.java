package org.example.messagesFX.model;

public class LoginUserResponse {

    private boolean ok;
    private String error;
    private String token;
    private String _id;
    private String name;
    private String image;

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

}
