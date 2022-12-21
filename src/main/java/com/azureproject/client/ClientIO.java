package com.azureproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientIO {

    ObjectInputStream dis;
    ObjectOutputStream dos;

    public ClientIO(ObjectInputStream dis, ObjectOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
    }

    public ObjectInputStream getDis() {
        return this.dis;
    }

    public ObjectOutputStream getDos() {
        return this.dos;
    }

    protected void finalize() {
        System.out.println("Garbage collector destroying client resources");
        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
