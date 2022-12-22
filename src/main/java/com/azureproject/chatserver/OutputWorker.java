package com.azureproject.chatserver;

import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.concurrent.Task;

public class OutputWorker extends Task<Void> {
    public BlockingQueue<Task<Object>> queue = new LinkedBlockingQueue<>();

    public OutputWorker() {
    }

    @Override
    protected Void call() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Login stuff");
        while (true) {
            try {
                Task<Object> task = queue.take();
                Thread t = new Thread(task);
                System.out.println("writing stuff");
                taskFinished(t);
            } catch (IllegalThreadStateException | InterruptedException e) {
                break;
            }
        }
        return null;
    }

    private void taskFinished(Thread t) throws IllegalThreadStateException {
        Boolean isCompleted = false;
        t.start();
        while (!isCompleted) {
            try {
                t.join();
                isCompleted = true;
            } catch (InterruptedException e) {
                isCompleted = false;
            }
        }

    }

}
