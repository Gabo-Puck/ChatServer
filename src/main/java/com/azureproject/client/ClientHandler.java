package com.azureproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.azureproject.SharedModels.AppMessage;
import com.azureproject.SharedModels.LoginData;
import com.azureproject.chatserver.PrimaryController;
import com.azureproject.chatserver.ServerHandler;

import javafx.concurrent.Task;

public class ClientHandler extends Task<Void> {
    ClientIO resources;
    PrimaryController pc;

    public ClientHandler(ObjectInputStream dis, ObjectOutputStream dos, PrimaryController pc) {
        this.resources = new ClientIO(dis, dos);
        this.pc = pc;
    }

    public ClientHandler() {
    }

    @Override
    protected Void call() throws Exception {
        // TODO Auto-generated method stub
        int sessionID = 0;
        try {
            System.out.println("Background thread for user created");
            // this.resources.dos.writeUTF("Write your username");
            AppMessage message;
            LoginData data = new LoginData("Gabo", "esm<3");
            message = new AppMessage("LoginAttempt", data, 13);
            this.resources.dos.writeObject(message);
            this.resources.dos.flush();

            // logindata.toString();
            sessionID = ServerHandler.userCount;
            ServerHandler.userCount++;
            InMemoryClient.addClient(sessionID, resources);
            // ServerHandler.queuePrintUserList.put(username);
            while (true) {
                AppMessage ap = (AppMessage) this.resources.dis.readObject();
                System.out.println(ap.toString());
                // ServerHandler.queuePrintUserList.put(this.resources.dis.readUTF());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ServerHandler.queueRemoveUserList.put(sessionID);
            InMemoryClient.removeCLient(sessionID);
            resources.finalize();
        }
        return null;
    }

}
