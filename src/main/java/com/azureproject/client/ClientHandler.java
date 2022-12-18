package com.azureproject.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.azureproject.chatserver.PrimaryController;
import com.azureproject.chatserver.ServerHandler;

import javafx.concurrent.Task;

public class ClientHandler extends Task<Void> {
    DataInputStream dis;
    DataOutputStream dos;
    PrimaryController pc;

    public ClientHandler(DataInputStream dis, DataOutputStream dos, PrimaryController pc) {
        this.dis = dis;
        this.dos = dos;
        this.pc = pc;
    }

    public ClientHandler() {
    }

    @Override
    protected Void call() throws Exception {
        // TODO Auto-generated method stub
        try {
            System.out.println("Background thread for user created");
            dos.writeUTF("Write your username");
            String username = dis.readUTF();
            ServerHandler.queuePrintUserList.put(username);

            while (true) {
                dos.writeUTF("Write an user");
                ServerHandler.queuePrintUserList.put(dis.readUTF());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
