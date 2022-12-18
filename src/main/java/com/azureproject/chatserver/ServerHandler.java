package com.azureproject.chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.azureproject.client.ClientHandler;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;

public class ServerHandler extends Task<Void> {

    PrimaryController pc;
    public static BlockingQueue<String> queuePrintUserList = new LinkedBlockingQueue<>();

    public ServerHandler(PrimaryController pc) {
        this.pc = pc;
    }

    @Override
    public Void call() throws Exception {
        // TODO Auto-generated method stub

        try {
            ServerSocket server = new ServerSocket(76);
            System.out.println("Connectedd");
            Socket client = null;
            ListPrinter lp = new ListPrinter(this.pc.userList, queuePrintUserList);
            new Thread(lp).start();
            while (true) {
                // Waiting for connections
                System.out.println("Waiting for connection");
                client = server.accept();

                // Get client resources
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                // Create thread for handling user communication;

                // tt.addEventHandler(, getOnCancelled());
                Thread t = new Thread(new ClientHandler(dis, dos, pc));

                t.start();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
