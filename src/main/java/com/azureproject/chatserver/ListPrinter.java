package com.azureproject.chatserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ListPrinter extends PrintWorker {

    public ListPrinter(ListView<String> usersConnected, BlockingQueue<String> queue) {
        super(usersConnected, queue);

    }

    @Override
    public void print(String text, Control target) {
        // TODO Auto-generated method stub
        ((ListView<String>) target).getItems().add(text);
    }

}
