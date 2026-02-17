package org.example.messagesFX.model;

import java.util.List;

public class GetMessageResponse {

    private boolean ok;
    private String error;
    private List<Message> messages;

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public List<Message> getMessages() {
        return messages;
    }

}
