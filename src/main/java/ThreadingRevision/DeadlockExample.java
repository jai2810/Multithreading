package ThreadingRevision;

public class DeadlockExample {
    public static void main(String[] args) throws InterruptedException {
        DeadLock.runTest();
    }
}

class DeadLock {
    Object lock1 = new Object();
    Object lock2 = new Object();

    void process1() {
        synchronized (lock1) {
            System.out.println("Lock1 acquired by process 1.");
            synchronized (lock2) {
                System.out.println("Lock2 acquired by process 1.");
            }
        }
    }

    void process2() {
        synchronized (lock2) {
            System.out.println("Lock2 acquired by process 2.");
            synchronized (lock1) {
                System.out.println("Lock1 acquired by process 2.");
            }
        }
    }

    public static void runTest() throws InterruptedException {
        DeadLock dl = new DeadLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                dl.process1();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                dl.process2();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
