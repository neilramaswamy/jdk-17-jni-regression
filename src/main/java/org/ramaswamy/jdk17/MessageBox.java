package org.ramaswamy.jdk17;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageBox {
    public String message = "";
    public Lock lock = new ReentrantLock();
    public Condition cond = lock.newCondition();

    public MessageBox() {
    }
}
