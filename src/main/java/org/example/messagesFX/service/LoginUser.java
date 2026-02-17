package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.LoginUserResponse;

import java.util.HashMap;
import java.util.Map;

public class LoginUser extends Service<LoginUserResponse> {

    private final String username;
    private final String password;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected Task<LoginUserResponse> createTask() {
        return new Task<LoginUserResponse>() {
            @Override
            protected LoginUserResponse call() throws Exception {
                Map<String, String> data = new HashMap<>();
                data.put("name", username);
                data.put("password", password);

                Gson gson = new Gson();
                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/login", gson.toJson(data), "POST");

                return gson.fromJson(json, LoginUserResponse.class);
            }
        };
    }
}
