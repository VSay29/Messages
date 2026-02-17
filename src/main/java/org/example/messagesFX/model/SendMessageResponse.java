package org.example.messagesFX.model;

public class SendMessageResponse {

    private boolean ok;
    private String newId;
    private String error;

    public boolean isOk() {
        return ok;
    }

    public String getNewId() {
        return newId;
    }

    public String getError() {
        return error;
    }

}
