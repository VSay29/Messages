package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.MyImage;
import org.example.messagesFX.model.UpdateUserResponse;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends Service<UpdateUserResponse> {

    Path image;

    public UpdateUser(Path image) {
        this.image = image;
    }

    @Override
    protected Task<UpdateUserResponse> createTask() {
        return new Task<UpdateUserResponse>() {
            @Override
            protected UpdateUserResponse call() throws Exception {
                Gson gson = new Gson();

                Map<String, MyImage> data = new HashMap<>();
                data.put("image", new MyImage(image));

                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/users", gson.toJson(data), "PUT");
                return gson.fromJson(json, UpdateUserResponse.class);
            }
        };
    }

}
