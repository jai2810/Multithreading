package ThreadingRevision;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShopDemonstration {
    public static void main(String[] args) throws InterruptedException {
        BarberShop.runTest();
    }
}

class BarberShop {
    int waitingCustomers = 0;
    int hairCutsGiven = 0;
    int chairs;
    ReentrantLock lock = new ReentrantLock();
    Semaphore waitForCustomerToEnter = new Semaphore(0);
    Semaphore waitForBarberToGetReady = new Semaphore(0);
    Semaphore waitForCustomerToLeave = new Semaphore(0);
    Semaphore waitForBarberToCutHair = new Semaphore(0);


    public BarberShop(int chairs) {
        this.chairs = chairs;
    }

    private void customerWalksIn() throws InterruptedException {
        lock.lock();
        if (waitingCustomers == chairs) {
            System.out.println("All seats occupied. Hence the customer is leaving.");
            lock.unlock();
            return;
        }
        waitingCustomers++;
        lock.unlock();

        waitForCustomerToEnter.release();
        waitForBarberToGetReady.acquire();

        lock.lock();
        waitingCustomers--;
        lock.unlock();

        waitForBarberToCutHair.acquire();
        waitForCustomerToLeave.release();
    }

    private void barber() throws InterruptedException {
        while(true) {
            waitForCustomerToEnter.acquire();
            waitForBarberToGetReady.release();

            hairCutsGiven++;
            System.out.println("Barber cutting hair of customer " + hairCutsGiven);
            Thread.sleep(2000);
            waitForBarberToCutHair.release();

            waitForCustomerToLeave.acquire();
        }
    }

    public static void runTest() throws InterruptedException {
        BarberShop barberShop = new BarberShop(3);
        Set<Thread> allThreads = new HashSet<>();
        Thread barberThread = new Thread(() -> {
            try {
                barberShop.barber();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        barberThread.start();

        for (int i = 0; i<10;i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShop.customerWalksIn();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "Thread_" + (i+1));
            allThreads.add(t);
        }

        for (Thread t: allThreads) t.start();

        for (Thread t: allThreads) t.join();

        allThreads.clear();
        Thread.sleep(2000);

        for (int i = 0; i<5;i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShop.customerWalksIn();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "Thread_" + (i+10));
            allThreads.add(t);
        }

        for (Thread t: allThreads) t.start();

        barberThread.join();
    }

}
