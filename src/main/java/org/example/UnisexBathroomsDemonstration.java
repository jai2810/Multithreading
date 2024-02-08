package org.example;

import java.util.concurrent.Semaphore;

public class UnisexBathroomsDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathroom.runTest();
    }
}

class UnisexBathroom{
    static String MEN = "men";
    static String WOMEN = "women";

    static String NONE = "none";

    String inUseBy = NONE;

    int empInBathroom = 0;

    Semaphore maxEmployees = new Semaphore(3);

    void useBathRoom(String name) throws InterruptedException {
        System.out.println(name + " is using the bathroom. And the current number of employees in the bathroom are: " + empInBathroom + " at " + System.currentTimeMillis());
        Thread.sleep(5000);
        System.out.println(name + " done using bathroom at " + System.currentTimeMillis());
    }

    void maleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(inUseBy.equals(WOMEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            empInBathroom++;
            inUseBy = MEN;
        }

        useBathRoom(name);
        maxEmployees.release();

        synchronized (this) {
            empInBathroom--;
            if (empInBathroom == 0){
                inUseBy = NONE;
            }
            this.notifyAll();
        }
    }

    void femaleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while(inUseBy.equals(MEN)) {
                this.wait();
            }
            maxEmployees.acquire();
            empInBathroom++;
            inUseBy = WOMEN;
        }

        useBathRoom(name);
        maxEmployees.release();

        synchronized (this) {
            empInBathroom--;
            if (empInBathroom==0) {
                inUseBy = NONE;
            }
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        UnisexBathroom unisexBathroom = new UnisexBathroom();

        Thread female1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.femaleUseBathroom("Pragya");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.maleUseBathroom("Jai");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.maleUseBathroom("Nirmay");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread female2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.femaleUseBathroom("Riya");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.maleUseBathroom("Aditya");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread male4 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    unisexBathroom.maleUseBathroom("Durgesh");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        female1.start();
        male1.start();
        male2.start();
        male3.start();
        female2.start();
        male4.start();

        female1.join();
        male1.join();
        male2.join();
        male3.join();
        female2.join();
        male4.join();
    }
}
