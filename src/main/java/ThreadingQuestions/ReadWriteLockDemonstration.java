package ThreadingQuestions;

import java.awt.*;

public class ReadWriteLockDemonstration {
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock.runTest();
    }
}

class ReadWriteLock{
    int readers = 0;

    boolean isWriteLocked = false;

    synchronized void acquireReadLock() throws InterruptedException {
        while(isWriteLocked) {
            wait();
        }
        readers++;
    }

    synchronized void releaseReadLock() {
        readers--;
        notify();
    }

    synchronized void acquireWriteLock() throws InterruptedException {
        while(isWriteLocked || readers != 0) {
            wait();
        }
        isWriteLocked = true;
    }

    synchronized void releaseWriteLock() {
        isWriteLocked = false;
        notify();
    }

    public static void runTest() throws InterruptedException {
        ReadWriteLock readWriteLock = new ReadWriteLock();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Acquiring write lock at " + System.currentTimeMillis());
                try {
                    readWriteLock.acquireWriteLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Write lock acquired at " + System.currentTimeMillis());
            }
        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Acquiring read lock at " + System.currentTimeMillis());
                try {
                    readWriteLock.acquireReadLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Read lock acquired at " + System.currentTimeMillis());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                readWriteLock.releaseReadLock();
                System.out.println("Read lock released at " + System.currentTimeMillis());
            }
        });

        Thread readerThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Acquiring read lock at " + System.currentTimeMillis());
                try {
                    readWriteLock.acquireReadLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Read lock acquired at " + System.currentTimeMillis());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                readWriteLock.releaseReadLock();
                System.out.println("Read lock released at " + System.currentTimeMillis());
            }
        });

        readerThread.start();
        Thread.sleep(10);
        t.start();
        readerThread2.start();
        readerThread.join();
        t.join();
        readerThread.join();
    }
}
