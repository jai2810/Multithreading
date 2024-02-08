package ThreadingQuestions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImplementingSemaphoresDemonstration {
    public static void main(String[] args) throws InterruptedException {
        ImplementingSemaphore.runTest();
    }
}

class ImplementingSemaphore{
    int maxPermits;

    int count = 0;

    Lock lock = new ReentrantLock();

    public ImplementingSemaphore(int maxPermits) {
        this.maxPermits = maxPermits;
    }

    void grantPermit() throws InterruptedException {
        synchronized (lock) {
            while(count == maxPermits) {
                lock.wait();
            }
            count++;
            lock.notifyAll();
        }
    }

    void releasePermit() throws InterruptedException {
        synchronized (lock) {
            while(count == 0) {
                lock.wait();
            }
            count--;
            lock.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        ImplementingSemaphore implementingSemaphore = new ImplementingSemaphore(1);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i<5;i++) {
                    try {
                        implementingSemaphore.grantPermit();
                        System.out.println("Acquiring --> " + (i+1) + " at: " + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i< 5; i++) {
                    try {
                        implementingSemaphore.releasePermit();
                        System.out.println("Releasing --> " + (i+1) + " at: " + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        t1.start();
        Thread.sleep(1000);
        t2.start();
        t1.join();
        t2.join();
    }

}

