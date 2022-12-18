package com.azureproject.chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentMap;

import com.azureproject.client.ClientHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.concurrent.Task;

public class PrimaryController implements Initializable {
    public ListView userList = new ListView();

    public ListView serverLogs = new ListView();
    public Label usersConnected = new Label("");
    public Label usersRegistered = new Label("");
    public Label messagesSent = new Label("");

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        System.out.println("Init");
        ServerHandler sh = new ServerHandler(this);
        Thread d = new Thread(sh);
        d.setDaemon(true);
        d.start();

    }
}
