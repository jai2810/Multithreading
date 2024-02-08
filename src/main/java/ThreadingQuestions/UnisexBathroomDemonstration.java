package ThreadingQuestions;

import java.util.concurrent.Semaphore;

public class UnisexBathroomDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathroom.runTest();
    }
}

class UnisexBathroom {
    String NONE = "none";
    String MEN = "men";
    String WOMEN = "women";

    String inUseBy = NONE;

    int totalEmployees = 0;

    Semaphore maxEmployees = new Semaphore(3);

    void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " is using bathroom. Current no. of employees are:  " + totalEmployees);
        Thread.sleep(3000);
        System.out.println(name + " came out of the bathroom at: " + System.currentTimeMillis());
    }

    void menUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(inUseBy.equals(WOMEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            totalEmployees++;
            inUseBy = MEN;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            totalEmployees--;
            if (totalEmployees == 0) {
                inUseBy = NONE;
            }
            this.notifyAll();
        }

    }

    void womenUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(inUseBy.equals(MEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            totalEmployees++;
            inUseBy = WOMEN;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            totalEmployees--;
            if (totalEmployees == 0) {
                inUseBy = NONE;
            }
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        UnisexBathroom unisexBathroom = new UnisexBathroom();
        Thread male1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Jai is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.menUseBathroom("Jai");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Jai 2 is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.menUseBathroom("Jai 2");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Jai 3 is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.menUseBathroom("Jai 3");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male4 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Jai 4 is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.menUseBathroom("Jai 4");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread female1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Riya is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.womenUseBathroom("Riya");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread female2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Riya 2 is in the queue at: " + System.currentTimeMillis());
                    unisexBathroom.womenUseBathroom("Riya 2");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        male1.start();
        male2.start();
        female1.start();
        male3.start();
        female2.start();
        male4.start();

        male1.join();
        male2.join();
        male3.join();
        male4.join();
        female1.join();
        female2.join();


    }
}
