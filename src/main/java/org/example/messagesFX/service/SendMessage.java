package org.example.messagesFX.service;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.messagesFX.model.Message;
import org.example.messagesFX.model.SendMessageResponse;

public class SendMessage extends Service<SendMessageResponse> {

    Message message;
    String toIdUser;

    public SendMessage(Message message, String toIdUser) {
        this.message = message;
        this.toIdUser = toIdUser;
    }

    @Override
    protected Task<SendMessageResponse> createTask() {
        return new Task<SendMessageResponse>() {
            @Override
            protected SendMessageResponse call() throws Exception {
                Gson gson = new Gson();
                String json = ServiceUtils.getResponse(NodeServer.getServer() + "/messages/" + toIdUser, gson.toJson(message), "POST");
                return gson.fromJson(json, SendMessageResponse.class);
            }
        };
    }

}
