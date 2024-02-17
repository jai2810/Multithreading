package ThreadingRevision;

import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilterWithDaemonThreadDemonstration {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketFilterWithDaemonThread.runTest();
    }
}

class TokenBucketFilterWithDaemonThread {
    private int capacity;

    private int tokens = 0;

    private final int ONE_SECOND = 1000;

    public TokenBucketFilterWithDaemonThread(int capacity) {
        this.capacity = capacity;
        Thread dt = new Thread(() -> {
            try {
                daemonThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        dt.setDaemon(true);
        dt.start();
    }

    private void daemonThread() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (tokens < capacity) {
                    tokens++;
                }
                this.notify();
            }
            Thread.sleep(ONE_SECOND);
        }
    }

    private void getToken() throws InterruptedException {
        synchronized (this) {
            while (tokens == 0) {
                this.wait();
            }
            tokens--;
        }
        System.out.println(
                "Granting " + Thread.currentThread().getName() + " token at " + System.currentTimeMillis() / 1000);
    }

    public static void runTest() throws InterruptedException {
        TokenBucketFilterWithDaemonThread bucketFilter = new TokenBucketFilterWithDaemonThread(10);
        Set<Thread> allThreads = new HashSet<>();

        long totalTime = System.currentTimeMillis();

        for (int i = 1; i<=25; i++) {
            Thread t = new Thread(()-> {
                try {
                    bucketFilter.getToken();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "Thread_" + i);
            allThreads.add(t);
        }

        //Thread.sleep(5000);

        for (Thread t: allThreads) t.start();

        for (Thread t: allThreads) t.join();

        totalTime = System.currentTimeMillis() - totalTime;

        System.out.println("Total time: " + totalTime);
    }
}
