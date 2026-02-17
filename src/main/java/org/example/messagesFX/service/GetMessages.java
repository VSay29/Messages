package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.GetMessageResponse;
import org.example.messagesFX.model.Message;

import java.util.List;

public class GetMessages extends Service<List<Message>> {

    @Override
    protected Task<List<Message>> createTask() {
        return new Task<>() {
            @Override
            protected List<Message> call() throws Exception {
                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/messages", null, "GET");
                Gson gson = new Gson();
                GetMessageResponse resp = gson.fromJson(json, GetMessageResponse.class);
                return resp.getMessages();
            }
        };
    }

}
