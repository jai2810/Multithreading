package ThreadingQuestionsRevision;

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

class UberRide {
    int democrats = 0;

    int republicans = 0;

    Semaphore demSemaphore = new Semaphore(0);

    Semaphore repubSemaphore = new Semaphore(0);

    CyclicBarrier barrier = new CyclicBarrier(4);

    ReentrantLock lock = new ReentrantLock();

    void drive() {
        System.out.println("Uber Ride on Its wayyyy... with ride leader " + Thread.currentThread().getName());
        System.out.flush();
    }

    void seated() {
        System.out.println(Thread.currentThread().getName() + "  seated");
        System.out.flush();
    }

    private void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean isRideLeader = false;
        lock.lock();
        democrats++;
        if (democrats == 4) {
            democrats-=4;
            isRideLeader = true;
            demSemaphore.release(3);
        } else if (democrats == 2 && republicans>=2) {
            isRideLeader = true;
            democrats-=2;
            republicans-=2;
            demSemaphore.release(1);
            repubSemaphore.release(2);
        } else {
            lock.unlock();
            demSemaphore.acquire();
        }

        seated();
        barrier.await();

        if (isRideLeader) {
            drive();
            lock.unlock();
        }

    }

    private void seatRepublican() throws InterruptedException, BrokenBarrierException {
        boolean isRideLeader = false;
        lock.lock();
        republicans++;
        if (republicans == 4) {
            republicans-=4;
            isRideLeader = true;
            repubSemaphore.release(3);
        } else if (democrats >= 2 && republicans==2) {
            isRideLeader = true;
            democrats-=2;
            republicans-=2;
            demSemaphore.release(2);
            repubSemaphore.release(1);
        } else {
            lock.unlock();
            repubSemaphore.acquire();
        }

        seated();
        barrier.await();

        if (isRideLeader) {
            drive();
            lock.unlock();
        }
    }

    public static void runTest() throws InterruptedException {
        UberRide uberRide = new UberRide();
        Set<Thread> allThreads = new HashSet<>();

        for (int i = 0; i<10;i++) {
            Thread t = new Thread(() -> {
                try {
                    uberRide.seatDemocrat();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            t.setName("Democrate_" + i);
            allThreads.add(t);
        }

        for (int i = 0; i<12;i++) {
            Thread t = new Thread(() -> {
                try {
                    uberRide.seatRepublican();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            t.setName("Republican_" + i);
            allThreads.add(t);
        }

        for (Thread t: allThreads) t.start();
        for(Thread t: allThreads) t.join();
    }

}
