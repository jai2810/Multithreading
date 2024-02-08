package ThreadingQuestions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallbackDemonstration {
    public static void main(String[] args) throws InterruptedException {
        DeferredCallback.runTest();
    }
}

class DeferredCallback {

    ReentrantLock lock = new ReentrantLock();

    Condition newCallbackArrived = lock.newCondition();
    PriorityQueue<Callback> pq = new PriorityQueue<>(new Comparator<Callback>() {
        @Override
        public int compare(Callback o1, Callback o2) {
            return (int)(o1.executeAt - o2.executeAt);
        }
    });

    void registerCallback(Callback callback) throws InterruptedException {
        lock.lock();
        pq.add(callback);
        newCallbackArrived.signal();
        lock.unlock();
    }

    long findSleepDuration() {
        return pq.peek().executeAt - System.currentTimeMillis();
    }

    void start() throws InterruptedException {
        long sleepFor = 0;
        while(true) {
            lock.lock();

            while(pq.isEmpty()) {
                newCallbackArrived.await();
            }

            while(!pq.isEmpty()) {
                sleepFor = findSleepDuration();
                if (sleepFor <= 0){
                    break;
                }
                else {
                    newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
                }
            }

            Callback cb = pq.poll();
            System.out.println(
                    "Executed at " + System.currentTimeMillis() / 1000 + " required at " + cb.executeAt / 1000
                            + ": message:" + cb.message);
            lock.unlock();
        }
    }

    static class Callback {
        String message;
        long executeAt;

        public Callback(long executeAfter, String  message) {
            this.executeAt = System.currentTimeMillis() + (1000*executeAfter);
            this.message = message;
        }
    }

    public static void runTest() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<Thread>();
        final DeferredCallback deferredCallbackExecutor = new DeferredCallback();

        Thread service = new Thread(new Runnable() {
            public void run() {
                try {
                    deferredCallbackExecutor.start();
                } catch (InterruptedException ie) {
                    //
                }
            }
        });

        service.start();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    Callback cb = new Callback(1, "Hello this is " + Thread.currentThread().getName());
                    try {
                        deferredCallbackExecutor.registerCallback(cb);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.setName("Thread_" + (i + 1));
            thread.start();
            allThreads.add(thread);
            Thread.sleep(1000);
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }
}
