package org.ramaswamy.jdk17;

public class RunnableLogger implements Runnable {

    private volatile boolean isActive = true;

    private MessageBox messageBox;

    public RunnableLogger(MessageBox messageBox) {
        this.messageBox = messageBox;
    }

    public void deactivate() {
        System.out.println("deactivating");
        isActive = false;
        messageBox.cond.signalAll();
    }

    @Override
    public void run() {
        System.out.println("Starting to run logger");

        messageBox.lock.lock();
        while (isActive) {
            System.out.println("Entering loop again");
            while (messageBox.message.length() == 0) {
                System.out.println("In loop once again...");
                try {
                    System.out.println("Awaiting cond");
                    messageBox.cond.await();
                    System.out.println("waking up from sleep");

                    // Could be woken up if we're shutting down
                    if (!isActive) {
                        messageBox.lock.unlock();
                        return;
                    }
                } catch (Exception e) {
                }
            }
            System.out.println("Exiting loop");

            // Sanity check
            assert messageBox.message.length() != 0;

            // Logging logic
            System.out.println("[LOGGING THREAD] " + messageBox.message);
            messageBox.message = "";
            messageBox.cond.signalAll();
        }
        messageBox.lock.unlock();
    }

}