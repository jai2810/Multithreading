package org.example;

import java.util.HashSet;
import java.util.Set;

public class DemonstrationTokenBucket {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketFilter.runTestMaxTokens();
    }
}


class TokenBucketFilter {
    private final int MAX_TOKENS;

    private long lastRequestedToken = System.currentTimeMillis();

    long possibleTokens = 0;

    public TokenBucketFilter (int maxTokens) {
        this.MAX_TOKENS = maxTokens;
    }

    synchronized void getTokens() throws InterruptedException{
        possibleTokens += (System.currentTimeMillis() - lastRequestedToken)/1000;

        if (possibleTokens > MAX_TOKENS) {
            possibleTokens = MAX_TOKENS;
        }

        if (possibleTokens == 0) {
            Thread.sleep(1000);
        } else {
            possibleTokens--;
        }

        lastRequestedToken = System.currentTimeMillis();

        System.out.println("Granting token to thread " + Thread.currentThread().getName() + " at " + System.currentTimeMillis()/1000);
    }

    public static void runTestMaxTokens() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        TokenBucketFilter tokenBucketFilter = new TokenBucketFilter(1);

        Thread.sleep(10000);

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tokenBucketFilter.getTokens();
                    } catch (InterruptedException e) {
                        System.out.println("Something went wrong.");
                    }
                }
            });
            t.setName("Thread_" + (i + 1));
            allThreads.add(t);
        }

        for (Thread t: allThreads) {
            t.start();
        }

        for (Thread t: allThreads) {
            t.join();
        }
    }
}
