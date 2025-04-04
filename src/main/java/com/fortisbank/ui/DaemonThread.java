package com.fortisbank.ui;

import java.util.Date;

public class DaemonThread extends Thread {
    private final Runnable task;
    private final long delay;

    public DaemonThread(Runnable task, long delay) {
        this.task = task;
        this.delay = delay;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                task.run();
                // log the task execution
                System.out.println("Daemon Task executed at: " + new Date());
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // log the interruption
                System.out.println("Daemon Task interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
