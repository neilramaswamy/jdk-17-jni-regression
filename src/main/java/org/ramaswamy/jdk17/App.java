package org.ramaswamy.jdk17;

public class App {
    public static void main(String[] args) {
        MessageBox box = new MessageBox();

        RunnableWorker runnableWorker0 = new RunnableWorker(box, 0, 1000);
        RunnableWorker runnableWorker1 = new RunnableWorker(box, 1000, 2000);
        RunnableLogger loggingWorker = new RunnableLogger(box);

        Thread workerThread0 = new Thread(runnableWorker0);
        Thread workerThread1 = new Thread(runnableWorker1);
        Thread loggerThread = new Thread(loggingWorker);

        System.out.println("Starting to run workers");
        workerThread0.start();
        workerThread1.start();
        System.out.println("Starting to run logger");
        loggerThread.start();

        try {
            workerThread0.join();
            workerThread1.join();

            loggingWorker.deactivate();
            loggerThread.join();
            System.out.println("joined");
        } catch (Exception e) {
        }

    }
}
