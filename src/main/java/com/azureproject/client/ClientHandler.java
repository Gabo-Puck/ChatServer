package com.azureproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.azureproject.SharedEnum.EnumActions;
import com.azureproject.SharedInterfaces.AppDataMessage;
import com.azureproject.SharedInterfaces.Chat;
import com.azureproject.SharedModels.AppMessage;
import com.azureproject.SharedModels.LoginData;
import com.azureproject.SharedModels.Message;
import com.azureproject.SharedModels.User;
import com.azureproject.SharedModels.UsersList;
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

    private final class InputTask extends Task<Object> {
        ClientIO resources;
        AppMessage inputMessage;
        OutputWorker worker;

        public InputTask(ClientIO resources, AppMessage inputMessage,
                OutputWorker worker) {
            this.resources = resources;
            this.inputMessage = inputMessage;
            this.worker = worker;
        }

        @Override
        protected Object call() throws Exception {
            // TODO Auto-generated method stub
            System.out.println("We got something");
            System.out.println(inputMessage.toString());
            switch (EnumActions.toAction(inputMessage.getAction())) {
                case LOGIN_ATTEMPT: {
                    System.out.println("Login attempt");
                    LoginData ld = (LoginData) inputMessage.getDataMessage();
                    inputMessage.setStatus("ok");
                    inputMessage.setDataMessage(ld);
                    String username = ld.getUsername();
                    Integer sessionID = InMemoryClient.userCount;
                    this.resources.setSessionID(sessionID);
                    this.resources.setUsername(username);

                    InMemoryClient.addClient(sessionID, resources);
                    ServerHandler.queuePrintCountUsers.put(String.valueOf(InMemoryClient.userCount));
                    ServerHandler.queuePrintServerLogs.put("User: ".concat(username)
                            .concat(" has connected with sessionID: ".concat(String.valueOf(sessionID))));
                    this.resources.setSessionID(sessionID);
                    this.resources.setUsername(username);
                    ServerHandler.queuePrintUserList.put(username);

                    System.out.println("Adding to output queue");
                    System.out.println("Worker queue bef log: ".concat(worker.queue.toString()));
                    User newUser = new User(sessionID, username);
                    inputMessage.setDataMessage(newUser);
                    worker.queue.put(new OutputTask(this.resources.dos, inputMessage));
                    ConcurrentHashMap<Integer, ClientIO> map = new ConcurrentHashMap<>();

                    InMemoryClient.getClients().forEach((key, value) -> {
                        if (!sessionID.equals(key)) {
                            map.put(key, value);
                        }
                    });
                    distributeAppMessage(newUser, EnumActions.NEW_USER, map, worker);
                    System.out.println("Worker queue af log: ".concat(worker.queue.toString()));

                }

                    break;
                case GET_MAIN_SCREEN_DATA:
                    UsersList users = new UsersList();
                    ArrayList<String> userList = new ArrayList<String>();
                    userList.addAll(ServerHandler.pc.userList.getItems());
                    userList.remove(this.resources.username);
                    users.setUsers(userList);
                    inputMessage.setDataMessage(users);
                    inputMessage.toString();
                    inputMessage.setStatus("ok");
                    System.out.println("Worker queue bef list: ".concat(worker.queue.toString()));
                    worker.queue.put(new OutputTask(this.resources.dos, inputMessage));
                    System.out.println("Worker queue af list: ".concat(worker.queue.toString()));

                    break;

                case GET_CHAT_DATA:
                    // Get users data
                    Chat chatReciever = (Chat) inputMessage.getDataMessage();

                    chatReciever.getUsers().forEach((user) -> {
                        InMemoryClient.getClients().forEach((key, value) -> {
                            if (value.username.equals(user.getUsername())) {
                                user.setID(key);
                            }
                        });
                    });
                    inputMessage.setStatus("ok");

                    inputMessage.setDataMessage(chatReciever);
                    worker.queue.put(new OutputTask(this.resources.dos, inputMessage));
                    // do stuff to get the messages. not yet bc i'm lazy af :c

                    break;

                case NEW_MESSAGE_PEER:
                    Message messageSent = (Message) inputMessage.getDataMessage();
                    ConcurrentHashMap<Integer, ClientIO> newMap = new ConcurrentHashMap<>();
                    messageSent.getChatReciever().getUsers().forEach((user) -> {
                        ClientIO res = InMemoryClient.getClient(user.getID());
                        newMap.put(user.getID(), res);
                    });
                    ServerHandler.queuePrintServerLogs.put("User: ".concat(messageSent.getFrom().getUsername())
                            .concat(" says: ".concat(messageSent.getContent()).concat(" to ")
                                    .concat(messageSent.getChatReciever().getUsers().toString())));
                    distributeAppMessage(messageSent, EnumActions.NEW_MESSAGE_PEER, newMap, worker);
                    break;
                default:
                    System.out.println("Action not found");
                    inputMessage.setStatus("error");
                    System.out.println("Worker queue bef list: ".concat(worker.queue.toString()));
                    worker.queue.put(new OutputTask(this.resources.dos, inputMessage));
                    System.out.println("Worker queue af list: ".concat(worker.queue.toString()));
                    break;

            }
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

    public void distributeAppMessage(AppDataMessage message, EnumActions action,
            Map<Integer, ClientIO> clients, OutputWorker worker) {
        clients.forEach((key, value) -> {
            AppMessage appPackage = new AppMessage();
            appPackage.setDataMessage(message);
            appPackage.setAction(action);
            try {
                worker.queue.put(new OutputTask(value.getDos(), appPackage));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Void call() throws Exception {
        // TODO Auto-generated method stub

        OutputWorker worker = new OutputWorker();
        try {
            System.out.println("Background thread for user created");

            new Thread(worker).start();
            while (true) {
                System.out.println("Waiting for something");
                AppMessage ap = (AppMessage) this.resources.dis.readObject();
                new Thread(new InputTask(resources, ap, worker)).start();

            }

        } catch (IOException e) {
            System.out.println("Error from disconnect");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            InMemoryClient.seeInMemory();
            ServerHandler.queueRemoveUserList.put(this.resources.getUsername());
            InMemoryClient.removeCLient(this.resources.getSessionID());
            ServerHandler.queuePrintCountUsers.put(String.valueOf(InMemoryClient.userCount));
            ServerHandler.queuePrintServerLogs.put("User has ".concat(this.resources.getUsername())
                    .concat(" disconnected, ID: ".concat(String.valueOf(this.resources.getSessionID()))));
            String username = this.resources.getUsername();
            Integer sessionID = this.resources.getSessionID();
            User newUser = new User(sessionID, username);
            distributeAppMessage(newUser, EnumActions.LOG_OUT, InMemoryClient.getClients(), worker);
            // InMemoryClient.getClients().forEach((key, value) -> {
            // if (!this.resources.getSessionID().equals(key)) {
            // AppMessage notification = new AppMessage();
            // notification.setDataMessage(newUser);
            // notification.setAction(EnumActions.LOG_OUT);
            // try {
            // worker.queue.put(new OutputTask(value.getDos(), notification));
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            // });
            resources.finalize();
        }
        return null;
    }

}
