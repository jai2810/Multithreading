package org.example;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallbackDemonstration {
    public static void main(String[] args) throws InterruptedException {
        DeferredCallbackExecutor.runTestCallback();
    }
}

class DeferredCallbackExecutor {
    PriorityQueue<Callback> q = new PriorityQueue<>(new Comparator<Callback>() {
        @Override
        public int compare(Callback o1, Callback o2) {
            return (int) (o1.executeAt - o2.executeAt);
        }
    });

    // Lock to guard critical conditions.
    ReentrantLock lock = new ReentrantLock();


    Condition newCallbackArrived = lock.newCondition();

    private long findSleepDuration() {
        return q.peek().executeAt - System.currentTimeMillis();
    }

    public void start() throws InterruptedException {
        long sleepFor = 0;

        while(true) {
            lock.lock();
            while(q.size() == 0) {
                newCallbackArrived.await();
            }

            while(q.size() != 0) {
                sleepFor = findSleepDuration();
                if (sleepFor <=0){
                    break;
                }
                newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
            }
            Callback cb = q.poll();
            System.out.println("Executed at " + System.currentTimeMillis()/1000 + "required at " + cb.executeAt/1000
                    + "message: " + cb.message);
            lock.unlock();
        }
    }

    public void registerCallback(Callback callback) {
        lock.lock();
        q.add(callback);
        newCallbackArrived.signal();
        lock.unlock();
    }

    static class Callback{
        long executeAt;
        String message;

        public Callback(long executeAfter, String message) {
            this.executeAt = System.currentTimeMillis() + (executeAfter*1000);
            this.message = message;
        }

    }

    public static void runTestCallback() throws InterruptedException{
        Set<Thread> allThreads = new HashSet<>();
        DeferredCallbackExecutor deferredCallbackExecutor = new DeferredCallbackExecutor();

        Thread service = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    deferredCallbackExecutor.start();
                } catch (InterruptedException e) {
                    // Swallow exception.
                }
            }
        });

        service.start();

        for (int i = 0; i<10;i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Callback cb = new Callback(1, "This is " + Thread.currentThread().getName());
                    deferredCallbackExecutor.registerCallback(cb);
                }
            });
            t.setName("Thread_" + (i+1));
            t.start();
            allThreads.add(t);
            Thread.sleep(1000);
        }

        for (Thread t: allThreads) {
            t.join();
        }
    }
}


