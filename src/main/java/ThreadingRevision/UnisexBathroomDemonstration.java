package ThreadingRevision;

import java.util.concurrent.Semaphore;

public class UnisexBathroomDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathroom.runTest();
    }
}

class UnisexBathroom {
    String NONE = "none";
    String MAN = "man";

    String WOMEN = "women";

    String isUsedBy = NONE;

    int totalPerson = 0;

    Semaphore semaphore = new Semaphore(3);

    private void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " is using bathroom at:" + System.currentTimeMillis());
        Thread.sleep(3000);
    }

    private void manUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(isUsedBy.equals(WOMEN)) {
                wait();
            }
            semaphore.acquire();
            totalPerson++;
            isUsedBy = MAN;
        }

        useBathroom(name);
        semaphore.release();

        synchronized (this) {
            totalPerson--;
            if (totalPerson == 0) isUsedBy = NONE;
            notifyAll();
        }
    }

    private void womenUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(isUsedBy.equals(MAN)) {
                wait();
            }
            semaphore.acquire();
            totalPerson++;
            isUsedBy = WOMEN;
        }

        useBathroom(name);
        semaphore.release();

        synchronized (this) {
            totalPerson--;
            if (totalPerson == 0) isUsedBy = NONE;
            notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        UnisexBathroom unisexBathroom = new UnisexBathroom();

        Thread male1 = new Thread(() -> {
            try {
                unisexBathroom.manUseBathroom("Jai");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread female1 = new Thread(() -> {
            try {
                unisexBathroom.womenUseBathroom("Riya");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread male2 = new Thread(() -> {
            try {
                unisexBathroom.manUseBathroom("Jai 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread male3 = new Thread(() -> {
            try {
                unisexBathroom.manUseBathroom("Jai 3");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread male4 = new Thread(() -> {
            try {
                unisexBathroom.manUseBathroom("Jai 4");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread female2 = new Thread(() -> {
            try {
                unisexBathroom.womenUseBathroom("Riya 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        female1.start();
        male1.start();
        male2.start();
        Thread.sleep(1000);
        male3.start();
        male4.start();
        female2.start();

        female1.join();
        male1.join();
        male2.join();
        male3.join();
        male4.join();
        female2.join();

    }
}
