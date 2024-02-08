package org.example;

public class CountingSemaphoreDemonstration{
    public static void main(String[] args) throws InterruptedException {
        CountingSemaphore.runCountingSemaphores();
    }
}
class CountingSemaphore {
    int usedPermits = 0;
    int maxCount;

    public CountingSemaphore(int count) {
        this.maxCount = count;
    }

    public synchronized void acquire() throws InterruptedException {
        while(usedPermits == maxCount) {
            wait();
        }
        usedPermits++;
        notify();
    }

    public synchronized void release() throws InterruptedException {
        while(usedPermits == 0) {
            wait();
        }
        usedPermits--;
        notify();
    }

    public static void runCountingSemaphores() throws InterruptedException {
        CountingSemaphore cs = new CountingSemaphore(0);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        cs.acquire();
                    } catch (InterruptedException e) {
                        //
                    }
                    System.out.println("Acquiring " + i);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        cs.release();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Releasing " + i);
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

}
