package ThreadingRevision;

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
    PriorityQueue<Callback> pq = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    void registerCallback(Callback callback) {
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
                if (sleepFor > 0) {
                    newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
                } else break;
            }
            Callback cb = pq.poll();
            System.out.println(
                    "Executed at " + System.currentTimeMillis() / 1000 + " required at " + cb.executeAt / 1000
                            + ": message:" + cb.message);
            if (cb.isPeriodic) {
                cb.executeAt = System.currentTimeMillis() + (1000*cb.executePeriodicallyAfter);
                registerCallback(cb);
            }
            lock.unlock();
        }
    }




    public static void runTest() throws InterruptedException {
        DeferredCallback dcb = new DeferredCallback();
        Thread service = new Thread(() -> {
            try {
                dcb.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        service.start();

        Thread earlyThread = new Thread(new Runnable() {
            public void run() {
                dcb.registerCallback(new Callback("Hello this is callback submitted second",1, true));
            }
        });
        earlyThread.start();
        earlyThread.join();

    }

    static class Callback {
        String message;
        long executeAt;

        long executePeriodicallyAfter;

        boolean isPeriodic;

        public Callback(String name, long executeAfterSecs, boolean isPeriodic) {
            this.message = name;
            this.executeAt = System.currentTimeMillis() + (1000 * executeAfterSecs);
            this.isPeriodic = isPeriodic;
            this.executePeriodicallyAfter = executeAfterSecs;
        }
    }


}
