package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.DeleteMessageResponse;

public class DeleteMessage extends Service<DeleteMessageResponse> {

    String idMessage;

    public DeleteMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    @Override
    protected Task<DeleteMessageResponse> createTask() {
        return new Task<DeleteMessageResponse>() {
            @Override
            protected DeleteMessageResponse call() throws Exception {
                Gson gson = new Gson();
                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/messages/" + idMessage, null, "DELETE");
                return gson.fromJson(json, DeleteMessageResponse.class);
            }
        };
    }

}
