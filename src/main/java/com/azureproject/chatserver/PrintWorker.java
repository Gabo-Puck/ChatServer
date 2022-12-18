package com.azureproject.chatserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;

public abstract class PrintWorker extends Task<Void> {
    /*
     * (non-Javadoc)
     * 
     * @see javafx.concurrent.Task#call()
     */

    public Control target;
    public BlockingQueue<String> queue;

    public PrintWorker(Control target, BlockingQueue<String> queue) {
        this.target = target;
        this.queue = queue;
    }

    public abstract void print(String text, Control target);

    @Override
    protected Void call() throws Exception {
        // TODO Auto-generated method stub

        while (true) {
            try {
                String t = queue.take();
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    print(t, target);
                    latch.countDown();
                });
                if (isCancelled()) {
                    break;
                }

                latch.await();

            } catch (InterruptedException ex) {
                if (isCancelled()) {
                    break;
                }
            }
        }
        return null;
    }

}
