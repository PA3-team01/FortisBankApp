package com.fortisbank.business.bll_utils;

import java.util.Date;

/**
 * A daemon thread that repeatedly executes a given task after a specified delay.
 */
public class DaemonThread extends Thread {
    private final Runnable task;
    private final long delay;

    /**
     * Constructs a DaemonThread with the specified task and delay.
     *
     * @param task the task to be executed
     * @param delay the delay between task executions in milliseconds
     */
    public DaemonThread(Runnable task, long delay) {
        this.task = task;
        this.delay = delay;
        setDaemon(true);
    }

    /**
     * Runs the daemon thread, executing the task repeatedly with the specified delay.
     * Logs the execution time and handles interruptions.
     */
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