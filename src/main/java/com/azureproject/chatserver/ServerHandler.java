package com.azureproject.chatserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.azureproject.client.ClientHandler;
import com.azureproject.servicesgui.ListPrinter;
import com.azureproject.servicesgui.ListRemover;

import javafx.concurrent.Task;

public class ServerHandler extends Task<Void> {

    PrimaryController pc;
    public static BlockingQueue<String> queuePrintUserList = new LinkedBlockingQueue<>();
    public static BlockingQueue<Integer> queueRemoveUserList = new LinkedBlockingQueue<>();
    public static int userCount = 0;

    public ServerHandler(PrimaryController pc) {
        this.pc = pc;
    }

    @Override
    public Void call() throws Exception {
        // TODO Auto-generated method stub

        ServerSocket server = new ServerSocket(76);
        System.out.println("Connectedd");
        Socket client = null;
        ListPrinter<String> printConnectedUsers = new ListPrinter<String>(this.pc.userList, queuePrintUserList);
        ListRemover<Integer, String> removeConnectedUsers = new ListRemover<Integer, String>(this.pc.userList,
                queueRemoveUserList);
        new Thread(printConnectedUsers).start();
        new Thread(removeConnectedUsers).start();
        while (true) {
            try {
                // Waiting for connections
                System.out.println("Waiting for connection");
                client = server.accept();
                System.out.println("Client connected");
                // Get client resources
                ObjectOutputStream dos = new ObjectOutputStream(client.getOutputStream());
                dos.flush();
                
                ObjectInputStream dis = new ObjectInputStream(client.getInputStream());

                // Create thread for handling user communication;

                Thread t = new Thread(new ClientHandler(dis, dos, pc));

                t.start();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
        System.out.println("Closing server...");
        server.close();
        return null;
    }
}
