package ThreadingQuestions;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class UberRidesDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UberRide.runTest();
    }
}

class UberRide {
    int democrats = 0;
    int republicans = 0;

    CyclicBarrier cb = new CyclicBarrier(4);

    ReentrantLock lock = new ReentrantLock();

    Semaphore demsWaiting = new Semaphore(0);

    Semaphore repubsWaiting = new Semaphore(0);

    void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean isRideLeader = false;
        lock.lock();
        democrats++;

        if (democrats == 4) {
            isRideLeader = true;
            democrats-=4;
            demsWaiting.release(3);
        } else if (democrats == 2 && republicans>=2) {
            isRideLeader = true;
            democrats-=2;
            republicans-=2;
            demsWaiting.release(1);
            repubsWaiting.release(2);
        } else {
            lock.unlock();
            demsWaiting.acquire();
        }

        seated();
        cb.await();

        if (isRideLeader) {
            drive();
            lock.unlock();
        }

    }

    void seatRepublican() throws InterruptedException, BrokenBarrierException {
        boolean isRideLeader = false;

        lock.lock();
        republicans++;

        if (republicans == 4) {
            isRideLeader = true;
            republicans-=4;
            repubsWaiting.release(3);
        } else if (republicans == 2 && democrats>=2) {
            isRideLeader = true;
            republicans-=2;
            democrats-=2;
            repubsWaiting.release(1);
            demsWaiting.release(2);
        } else {
            lock.unlock();
            repubsWaiting.acquire();
        }

        seated();
        cb.await();

        if (isRideLeader) {
            drive();
            lock.unlock();
        }
    }

    void seated() {
        System.out.println(Thread.currentThread().getName() + "  seated");
        System.out.flush();
    }

    void drive() {
        System.out.println("Uber ride on its way... With ride leader " + Thread.currentThread().getName());
        System.out.flush();
    }

    public static void runTest() throws InterruptedException {
        UberRide uberRide = new UberRide();
        Set<Thread> allThreads = new HashSet<>();

        // Adding democrats thread
        for (int i = 0; i < 10; i++) {
            Thread dem = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uberRide.seatDemocrat();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "Democrat_" + (i+1));
            allThreads.add(dem);
            Thread.sleep(20);
        }

        for (int i = 0; i < 18; i++) {
            Thread repub = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uberRide.seatRepublican();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "Republican_" + (i+1));
            allThreads.add(repub);
            Thread.sleep(50);
        }

        for(Thread t: allThreads) {
            t.start();
        }

        for (Thread t: allThreads) {
            t.join();
        }

    }

}

