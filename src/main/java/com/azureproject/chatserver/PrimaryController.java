package com.azureproject.chatserver;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class PrimaryController implements Initializable {
    public ListView<String> userList = new ListView<>();

    public ListView<String> serverLogs = new ListView<>();
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
