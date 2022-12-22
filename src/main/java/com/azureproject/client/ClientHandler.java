package com.azureproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import com.azureproject.SharedModels.AppMessage;
import com.azureproject.SharedModels.LoginData;
import com.azureproject.chatserver.OutputWorker;
import com.azureproject.chatserver.PrimaryController;
import com.azureproject.chatserver.ServerHandler;

import javafx.concurrent.Task;

public class ClientHandler extends Task<Void> {
    private final class OutputTask extends Task<Object> {
        ObjectOutputStream output;
        AppMessage message;

        public OutputTask(ObjectOutputStream output, AppMessage message) {
            this.output = output;
            this.message = message;
        }

        @Override
        protected Object call() throws Exception {
            // TODO Auto-generated method stub
            this.output.writeObject(message);
            return null;
        }
    }

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
        String username = "";
        int id = 0;
        try {
            System.out.println("Background thread for user created");
            // this.resources.dos.writeUTF("Write your username");
            // LoginData data = new LoginData("Gabo", "esm<3");
            // message = new AppMessage("LoginAttempt", data, 13);
            // this.resources.dos.writeObject(message);
            // this.resources.dos.flush();

            // logindata.toString();
            // ServerHandler.queuePrintUserList.put(username);

            OutputWorker worker = new OutputWorker();

            new Thread(worker).start();
            while (true) {
                System.out.println("Waiting for something");
                AppMessage ap = (AppMessage) this.resources.dis.readObject();
                System.out.println("We got something");
                System.out.println(ap.toString());
                if (ap.getAction().equals("LoginAttempt")) {
                    System.out.println("Login attempt");
                    LoginData ld = (LoginData) ap.getDataMessage();
                    ld.setMessage("ok");
                    ap.setDataMessage(ld);
                    sessionID = InMemoryClient.userCount;
                    username = ld.getUsername();
                    id = sessionID;
                    InMemoryClient.addClient(sessionID, resources);
                    ServerHandler.queuePrintCountUsers.put(String.valueOf(InMemoryClient.userCount));
                    ServerHandler.queuePrintServerLogs.put("User: ".concat(username)
                            .concat(" has connected with sessionID: ".concat(String.valueOf(id))));
                    ServerHandler.queuePrintUserList.put(username);
                    System.out.println("Adding to output queue");
                    worker.queue.put(new OutputTask(this.resources.dos, ap));
                }

            }

        } catch (IOException e) {
            System.out.println("Error from disconnect");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            System.out.println("User has disconnected, ID:".concat(String.valueOf(sessionID)));
            InMemoryClient.seeInMemory();
            ServerHandler.queueRemoveUserList.put(username);
            InMemoryClient.removeCLient(sessionID);
            ServerHandler.queuePrintCountUsers.put(String.valueOf(InMemoryClient.userCount));
            ServerHandler.queuePrintServerLogs.put("User: ".concat(username).concat(" has disconnected"));
            resources.finalize();
        }
        return null;
    }

}
