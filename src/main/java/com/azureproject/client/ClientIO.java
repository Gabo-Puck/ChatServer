package com.azureproject.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ClientIO {
    DataInputStream dis;
    DataOutputStream dos;

    public ClientIO(DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
    }

    public DataInputStream getDis() {
        return this.dis;
    }

    public DataOutputStream getDos() {
        return this.dos;
    }

}
