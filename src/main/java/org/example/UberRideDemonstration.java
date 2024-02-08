package org.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class UberRideDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UberRide.runTest();
    }
}

class UberRide{
    int democrats = 0;

    int republicans = 0;

    Semaphore demsWaiting = new Semaphore(0);

    Semaphore repubsWaiting = new Semaphore(0);

    CyclicBarrier barrier = new CyclicBarrier(4);

    ReentrantLock lock = new ReentrantLock();

    void drive() {
        System.out.println("Uber Ride on Its way... with ride leader " + Thread.currentThread().getName()
                + " at: " + System.currentTimeMillis());
        System.out.flush();
    }

    void seated() {
        System.out.println(Thread.currentThread().getName() + "  seated");
        System.out.flush();
    }

    void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();

        democrats++;

        if (democrats == 4) {
            demsWaiting.release(3);
            democrats-=4;
            rideLeader = true;
        } else if (democrats == 2 && republicans>=2) {
            demsWaiting.release(1);
            repubsWaiting.release(2);
            rideLeader = true;
            democrats-=2;
            republicans-=2;
        } else {
            lock.unlock();
            demsWaiting.acquire();
        }

        seated();
        barrier.await();

        if (rideLeader) {
            drive();
            lock.unlock();
        }

    }

    void seatRepublican() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();

        republicans++;

        if (republicans == 4) {
            repubsWaiting.release(3);
            republicans-=4;
            rideLeader = true;
        } else if (republicans == 2 && democrats >= 2) {
            demsWaiting.release(2);
            repubsWaiting.release(1);
            rideLeader = true;
            democrats-=2;
            republicans-=2;
        } else {
            lock.unlock();
            repubsWaiting.acquire();
        }

        seated();
        barrier.await();

        if (rideLeader) {
            drive();
            lock.unlock();
        }
    }

    public static void runTest() throws InterruptedException {
        final UberRide uberRide = new UberRide();

        Set<Thread> allThreads = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    uberRide.seatDemocrat();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            t.setName("Democrat_" + (i+1));
            allThreads.add(t);
        }

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    uberRide.seatRepublican();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            t.setName("Republican_" + (i+1));
            allThreads.add(t);
            Thread.sleep(50);
        }

        for (Thread t: allThreads)t.start();

        for (Thread t: allThreads)t.join();
    }
}
