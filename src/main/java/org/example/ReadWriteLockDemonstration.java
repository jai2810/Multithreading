package org.example;

public class ReadWriteLockDemonstration {
    public static void main(String[] args) {

    }
}

class ReadWriteLock{

    boolean isWriteLocked = false;
    int readers = 0;
    public synchronized void acquireReadLock() throws InterruptedException {
        while(isWriteLocked) {
            wait();
        }
        readers++;
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while(isWriteLocked || readers != 0) {
            wait();
        }
        isWriteLocked = true;
    }

    public synchronized void releaseReadLock() {
        readers--;
        notify();
    }

    public synchronized void releaseWriteLock() {
        isWriteLocked = false;
        notify();
    }

    public static void runTest() {
        ReadWriteLock rwt = new ReadWriteLock();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


}
