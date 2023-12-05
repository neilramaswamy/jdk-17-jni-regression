package org.ramaswamy.jdk17;

public class RunnableWorker implements Runnable {
    private MessageBox messageBox;

    private int[] numbersToLog;

    public RunnableWorker(
            MessageBox messageBox,
            int startRange,
            int endRange) {
        this.messageBox = messageBox;

        this.numbersToLog = new int[endRange - startRange];
        for (int i = 0; i < endRange - startRange; i++) {
            this.numbersToLog[i] = startRange + i;
        }
    }

    @Override
    public void run() {
        for (int number : this.numbersToLog) {
            System.out.println("Trying to log " + number);

            messageBox.lock.lock();
            while (messageBox.message.length() != 0) {
                try {
                    messageBox.cond.await();
                } catch (Exception e) {
                }
            }

            assert messageBox.message.length() == 0;
            messageBox.message = "[ThreadId=" + Thread.currentThread().getId() + "] " + number;
            messageBox.cond.signalAll();
            messageBox.lock.unlock();

            // Sleep between 0 to 10ms
            double sleepAmount = Math.random() * 10.0;
            try {
                Thread.sleep(Math.round(sleepAmount));
            } catch (Exception e) {
                System.err.println("Error during sleeping: " + e);
            }
        }
    }
}
