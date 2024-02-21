package ThreadingQuestionsRevision;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallbackDemonstration {
    public static void main(String[] args) throws InterruptedException {
        DeferredCallback.runTest();
    }
}

class DeferredCallback {

    PriorityQueue<Callback> pq = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    ReentrantLock lock = new ReentrantLock();
    Condition newCallbackRegistered = lock.newCondition();

    private long sleepFor = 0;
    private void registerCallback(Callback cb) {
        lock.lock();
        pq.add(cb);
        newCallbackRegistered.signal();
        lock.unlock();
    }

    private void execute() throws InterruptedException {
        while (true) {
            lock.lock();
            while (pq.isEmpty()) {
                newCallbackRegistered.await();
            }
            while (!pq.isEmpty()) {
                sleepFor = pq.peek().executeAt - System.currentTimeMillis();
                if (sleepFor < 0) {
                    break;
                } else {
                    Thread.sleep(sleepFor);
                }
            }
            Callback cb = pq.poll();
            System.out.println(cb.message + " required at " + cb.executeAt/1000 + " executed at: " + System.currentTimeMillis()/1000);
            if (cb.isPeriodic) {
                registerCallback(new Callback(cb.message, cb.executeAfter, true));
            }
            lock.unlock();
        }
    }

    public static void runTest() throws InterruptedException {
        DeferredCallback deferredCallback = new DeferredCallback();

        Thread start = new Thread(() -> {
            try {
                deferredCallback.execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        start.start();

        Thread earlyThread = new Thread(() -> {
            deferredCallback.registerCallback(new Callback("Task 1", 3, false));
        });

        Thread lateThread = new Thread(() -> {
            deferredCallback.registerCallback(new Callback("Task 2", 1, true));
        });

        earlyThread.start();
        lateThread.start();

        earlyThread.join();
        lateThread.join();
    }

    static class Callback {
        private long executeAt;

        private String message;

        private boolean isPeriodic;

        private long executeAfter;

        public Callback(String message, long executeAfter, boolean isPeriodic) {
            this.message = message;
            this.executeAt = (System.currentTimeMillis() + executeAfter*1000);
            this.isPeriodic = isPeriodic;
            this.executeAfter = executeAfter;
        }
    }
}
