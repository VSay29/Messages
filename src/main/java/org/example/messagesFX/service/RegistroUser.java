package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.RegisterUserResponse;
import org.example.messagesFX.model.User;

import java.util.HashMap;
import java.util.Map;

public class RegistroUser extends Service<RegisterUserResponse> {

    User user;

    public RegistroUser(User user) {
        this.user = user;
    }

    @Override
    protected Task<RegisterUserResponse> createTask() {
        return new Task<RegisterUserResponse>() {
            @Override
            protected RegisterUserResponse call() throws Exception {
                Gson gson = new Gson();

                Map<String, String> data = new HashMap<>();
                data.put("name", user.getName());
                data.put("password", user.getPassword());
                data.put("image", user.getImage());

                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/register", gson.toJson(data), "POST");
                return gson.fromJson(json, RegisterUserResponse.class);
            }
        };
    }

}
