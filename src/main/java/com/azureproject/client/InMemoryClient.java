package com.azureproject.client;

import java.util.concurrent.ConcurrentHashMap;

import com.azureproject.chatserver.ServerHandler;

public class InMemoryClient {
    public static ConcurrentHashMap<Integer, ClientIO> clients = new ConcurrentHashMap<>();
    public static int userCount = 0;

    public static void seeInMemory() {
        System.out.println(clients);
    }

    public static void addClient(int id, ClientIO clientResources) {
        InMemoryClient.userCount++;
        clients.put(id, clientResources);
    }

    public static void removeCLient(Integer id) {
        InMemoryClient.userCount--;
        clients.remove(id);
    }

    public static ClientIO getClient(int id) {
        return clients.get(id);
    }

}
