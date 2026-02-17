package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.GetUsersResponse;
import org.example.messagesFX.model.User;

import java.util.List;

public class GetUsers extends Service<List<User>> {

    @Override
    protected Task<List<User>> createTask() {
        return new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/users", null, "GET");
                Gson gson = new Gson();
                GetUsersResponse resp = gson.fromJson(json, GetUsersResponse.class);
                return resp.getUsers();
            }
        };
    }

}
