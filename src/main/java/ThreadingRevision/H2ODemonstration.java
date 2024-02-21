package ThreadingRevision;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class H2ODemonstration {
    public static void main(String[] args) throws InterruptedException {
        H2O.runTest();
    }
}

class H2O {
    int hydrogenCount = 0;

    int oxygenCount = 0;

    Semaphore hSem = new Semaphore(0);

    Semaphore oSem = new Semaphore(0);

    CyclicBarrier barrier = new CyclicBarrier(3);

    ReentrantLock lock = new ReentrantLock();

    private void hAccumulate() throws InterruptedException, BrokenBarrierException {
        boolean isAtom = false;
        lock.lock();
        hydrogenCount++;
        if (hydrogenCount == 2 && oxygenCount >=1) {
            isAtom = true;
            hydrogenCount -= 2;
            oxygenCount -= 1;
            hSem.release(1);
            oSem.release(1);
        } else {
            lock.unlock();
            hSem.acquire();
        }

        barrier.await();

        if (isAtom) {
            System.out.println("HHO");
            lock.unlock();
        }
    }

    private void oAccumulate() throws InterruptedException, BrokenBarrierException {
        boolean isAtom = false;
        lock.lock();
        oxygenCount++;
        if (hydrogenCount>=2 && oxygenCount == 1) {
            isAtom = true;
            hydrogenCount-=2;
            oxygenCount -= 1;
            hSem.release(2);
        } else {
            lock.unlock();
            oSem.acquire();
        }

        barrier.await();

        if (isAtom) {
            System.out.println("HHO");
            Thread.sleep(1000);
            lock.unlock();
        }
    }

    public static void runTest() throws InterruptedException {
        H2O h2O = new H2O();
        Set<Thread> allThreads = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(() -> {
                try {
                    h2O.hAccumulate();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            allThreads.add(t);
        }

        for (int i = 0; i < 9; i++) {
            Thread t = new Thread(() -> {
                try {
                    h2O.oAccumulate();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            allThreads.add(t);
        }

        for (Thread t: allThreads)t.start();

        for (Thread t: allThreads)t.join();
    }
}
