package ThreadingQuestions;

import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilterDemonstration {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketFilter.runTest();
    }
}

class TokenBucketFilter{
    int maxTokens;

    int tokensAvailable = 0;

    long lastRequestedTime = System.currentTimeMillis();

    public TokenBucketFilter(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    synchronized void getToken() throws InterruptedException {
        tokensAvailable += (int) ((System.currentTimeMillis() - lastRequestedTime)/1000);

        if (tokensAvailable > maxTokens) {
            tokensAvailable = maxTokens;
        }

        if (tokensAvailable == 0) {
            Thread.sleep(1000);
        } else {
            tokensAvailable--;
        }

        lastRequestedTime = System.currentTimeMillis();
        System.out.println("Granting token to thread " + Thread.currentThread().getName() + " at: " + System.currentTimeMillis()/1000);

    }

    public static void runTest() throws InterruptedException {
        TokenBucketFilter tbf = new TokenBucketFilter(5);
        Thread.sleep(10000);
        Set<Thread> allThreads = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tbf.getToken();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "Thread_" + (i+1));
            allThreads.add(t);
        }

        for (Thread t: allThreads)t.start();
        for (Thread t: allThreads)t.join();
    }
}
