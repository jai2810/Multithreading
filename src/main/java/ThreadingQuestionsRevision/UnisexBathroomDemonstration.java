package ThreadingQuestionsRevision;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class UnisexBathroomDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathroom.runTest();
    }
}

class UnisexBathroom {
    String MEN = "men";

    String WOMEN = "women";

    String NONE = "none";

    String isUsedBy = NONE;

    Semaphore maxEmployees= new Semaphore(3);

    int noOfEmployeesInBathroom = 0;

    private void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " is using the bathroom. Total no of employees in the bathroom are: " + noOfEmployeesInBathroom);
        Thread.sleep(3000);
    }

    private void menUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(isUsedBy.equals(WOMEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            noOfEmployeesInBathroom++;
            isUsedBy = MEN;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            noOfEmployeesInBathroom--;
            if (noOfEmployeesInBathroom == 0)isUsedBy = NONE;
            this.notifyAll();
        }
    }

    private void womenUseBathroom (String name) throws InterruptedException {
        synchronized (this) {
            while(isUsedBy.equals(MEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            noOfEmployeesInBathroom++;
            isUsedBy = WOMEN;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            noOfEmployeesInBathroom--;
            if (noOfEmployeesInBathroom == 0)isUsedBy = NONE;
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        UnisexBathroom unisexBathroom = new UnisexBathroom();
        Thread m1 = new Thread(() -> {
            try {
                unisexBathroom.menUseBathroom("jai 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread f1 = new Thread(() -> {
            try {
                unisexBathroom.womenUseBathroom("riya 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread f2 = new Thread(() -> {
            try {
                unisexBathroom.womenUseBathroom("riya 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread m2 = new Thread(() -> {
            try {
                unisexBathroom.menUseBathroom("Jai 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread m3 = new Thread(() -> {
            try {
                unisexBathroom.menUseBathroom("Jai 3");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread m4 = new Thread(() -> {
            try {
                unisexBathroom.menUseBathroom("Jai 4");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        f1.start();
        f2.start();
        Thread.sleep(1000);

        m1.start();
        m2.start();
        m3.start();
        m4.start();

        f1.join();
        f2.join();
        m1.join();
        m2.join();
        m3.join();
        m4.join();
    }
}

