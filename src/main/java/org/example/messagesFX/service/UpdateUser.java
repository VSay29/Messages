package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.UpdateUserResponse;

import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends Service<UpdateUserResponse> {

    String image;

    public UpdateUser(String image) {
        this.image = image;
    }

    @Override
    protected Task<UpdateUserResponse> createTask() {
        return new Task<UpdateUserResponse>() {
            @Override
            protected UpdateUserResponse call() throws Exception {
                Gson gson = new Gson();

                Map<String, String> data = new HashMap<>();
                data.put("image", image);

                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/users", gson.toJson(data), "PUT");
                return gson.fromJson(json, UpdateUserResponse.class);
            }
        };
    }

}
