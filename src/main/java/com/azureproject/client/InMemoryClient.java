package com.azureproject.client;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryClient {
    public static ConcurrentHashMap<Integer, ClientIO> clients = new ConcurrentHashMap<>();

    public static void addClient(int id, ClientIO clientResources) {
        clients.put(id, clientResources);
    }

    public static ClientIO getClient(int id) {
        return clients.get(id);
    }

}
