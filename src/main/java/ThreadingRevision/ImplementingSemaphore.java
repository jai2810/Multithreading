package ThreadingRevision;

import java.util.concurrent.locks.ReentrantLock;

public class ImplementingSemaphore {
    public static void main(String[] args) throws InterruptedException {
        SemaphoreCustom.runTest();
    }
}

class SemaphoreCustom {
    int permits = 0;

    ReentrantLock lock = new ReentrantLock();

    int maxPermits;

    public SemaphoreCustom(int maxPermits) {
        this.maxPermits = maxPermits;
    }

    private void acquire() throws InterruptedException {
        synchronized (lock) {
            if (permits == maxPermits) {
                lock.wait();
            }
            permits++;
            lock.notifyAll();
            System.out.println("Semaphore acquired by " + Thread.currentThread().getName() + " at: " + System.currentTimeMillis()/1000);
            System.out.println("Total no of permits: " + permits);
        }
    }

    private void release() throws InterruptedException {
        synchronized (lock) {
            if (permits == 0) {
                lock.wait();
            }
            permits--;
            System.out.println("Semaphore released by " + Thread.currentThread().getName() + " at: " + System.currentTimeMillis()/1000);
            lock.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        SemaphoreCustom semaphore = new SemaphoreCustom(1);
//        Set<Thread> allThreads = new HashSet<>();
//        for (int i = 1; i<=10; i++) {
//            Thread t = new Thread(()->{
//                try {
//                    semaphore.acquire();
//                    Thread.sleep(3000);
//                    semaphore.release();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }, "Thread_" + i);
//            allThreads.add(t);
//        }
//
//        for (Thread t: allThreads) t.start();
//
//        for (Thread t: allThreads) t.join();

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        semaphore.acquire();
                        System.out.println("Ping " + i);
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        semaphore.release();
                        System.out.println("Pong " + i);
                    } catch (InterruptedException ie) {

                    }
                }
            }
        });

        t2.start();
        t1.start();
        t1.join();
        t2.join();
    }
}
