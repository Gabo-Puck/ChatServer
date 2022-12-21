package com.azureproject.client;

import java.util.concurrent.ConcurrentHashMap;

import com.azureproject.chatserver.ServerHandler;

public class InMemoryClient {
    public static ConcurrentHashMap<Integer, ClientIO> clients = new ConcurrentHashMap<>();

    public static void addClient(int id, ClientIO clientResources) {
        ServerHandler.userCount++;
        clients.put(id, clientResources);
    }

    public static void removeCLient(int id) {
        ServerHandler.userCount--;
        clients.remove(id);
    }

    public static ClientIO getClient(int id) {
        return clients.get(id);
    }

}
