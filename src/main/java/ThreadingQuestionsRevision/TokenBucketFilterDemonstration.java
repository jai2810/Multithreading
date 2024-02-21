package ThreadingQuestionsRevision;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketFilterDemonstration {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketFilter.runTest();
    }
}

class TokenBucketFilter {
    long lastRequestedAt;

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    int maxTokens;

    long tokensAvailable = 0;

    public TokenBucketFilter(int maxTokens) {
        this.maxTokens = maxTokens;
        this.lastRequestedAt = System.currentTimeMillis();
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
        while(true) {
            synchronized (this) {
                if (tokensAvailable < maxTokens) {
                    tokensAvailable++;
                }
                this.notifyAll();
            }
            Thread.sleep(1000);
        }
    }

    private void getTokenDaemonThread() throws InterruptedException {
        synchronized (this) {
            while (tokensAvailable == 0) {
                this.wait();
            }
            tokensAvailable--;
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " acquired token at: " + System.currentTimeMillis()/1000);
    }

    private synchronized void getToken() throws InterruptedException {
        //tokensAvailable += (System.currentTimeMillis() - lastRequestedAt)/1000;
        if (tokensAvailable > maxTokens) {
            tokensAvailable = maxTokens;
            Thread.sleep(1000);
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " acquired token at: " + System.currentTimeMillis()/1000);
        if (tokensAvailable < 0) {
            Thread.sleep(1000);
        } else {
            tokensAvailable--;
        }
        lastRequestedAt = System.currentTimeMillis();
    }

    public static void runTest() throws InterruptedException {
        TokenBucketFilter tbf = new TokenBucketFilter(5);
        Set<Thread> allThreads = new HashSet<>();

//        ExecutorService executorService = Executors.newFixedThreadPool(20);
//        try {
//            executorService.submit(() -> {
//                for (int i = 0; i < 20; i++) {
//                    try {
//                        tbf.getToken();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        } finally {
//            executorService.shutdown();
//            executorService.awaitTermination(1, TimeUnit.HOURS);
//        }

        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(() -> {
                try {
                    tbf.getTokenDaemonThread();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "Thread_" + i);
            allThreads.add(t);
        }

        Thread.sleep(3000);
        for (Thread t: allThreads) t.start();

        for (Thread t: allThreads)t.join();

    }
}
