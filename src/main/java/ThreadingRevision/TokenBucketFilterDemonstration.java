package ThreadingRevision;

import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilterDemonstration {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketFilter.runTest();
    }
}

class TokenBucketFilter {
    int capacity;

    int count = 0;

    long lastRequestedAt = System.currentTimeMillis();

    public TokenBucketFilter(int capacity) {
        this.capacity = capacity;
    }

    private synchronized void getToken(long requestedAt) throws InterruptedException {
        count += (int) ((System.currentTimeMillis() - lastRequestedAt)/1000);
        if (count > capacity)count = capacity;
        if (count == 0) {
            Thread.sleep(1000);
            count++;
        }
        lastRequestedAt = System.currentTimeMillis();
        count--;
        System.out.println("Token requested at " + requestedAt/1000 +   " and acquired by thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis()/1000);
    }

    public static void runTest() throws InterruptedException {
        TokenBucketFilter bucketFilter = new TokenBucketFilter(10);
        Set<Thread> allThreads = new HashSet<>();

        long totalTime = System.currentTimeMillis();

        for (int i = 1; i<=25; i++) {
            Thread t = new Thread(()-> {
                try {
                    bucketFilter.getToken(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "Thread_" + i);
            allThreads.add(t);
        }

        Thread.sleep(5000);

        for (Thread t: allThreads) t.start();

        for (Thread t: allThreads) t.join();

        totalTime = System.currentTimeMillis() - totalTime;

        System.out.println("Total time: " + totalTime);

    }

}