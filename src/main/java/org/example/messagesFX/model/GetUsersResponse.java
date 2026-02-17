package org.example.messagesFX.model;

import java.util.List;

public class GetUsersResponse {

    private boolean ok;
    private String error;
    private List<User> users;

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public List<User> getUsers() {
        return users;
    }

}
