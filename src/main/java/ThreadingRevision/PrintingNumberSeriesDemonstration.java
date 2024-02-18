package ThreadingRevision;

import java.util.concurrent.Semaphore;

public class PrintingNumberSeriesDemonstration {
    public static void main(String[] args) throws InterruptedException {
        PrintingNumberSeries.runTest();
    }
}

class PrintingNumberSeries {
    int count;

    int n;

    int number;

    Semaphore semZero, semEven, semOdd;

    public PrintingNumberSeries(int n) {
        this.count = 1;
        this.n = n;
        this.number = 1;
        this.semZero = new Semaphore(1);
        this.semOdd = new Semaphore(0);
        this.semEven = new Semaphore(0);
    }

    private void printZeroSem() throws InterruptedException {
        for (int i=0; i<n; i++) {
            semZero.acquire();
            System.out.println("0");
            if (i%2 == 1) {
                semEven.release();
            } else {
                semOdd.release();
            }
        }
    }

    private void printEvenSem() throws InterruptedException {
        for (int i = 2; i<=n; i+=2) {
            semEven.acquire();
            System.out.println(i);
            semZero.release();
        }
    }

    private void printOddSem() throws InterruptedException {
        for (int i = 1; i<=n;i+=2) {
            semOdd.acquire();
            System.out.println(i);
            semZero.release();
        }
    }

    private void printZero() throws InterruptedException {
        synchronized (this) {
            while(count%2 != 1) {
                this.wait();
            }
            System.out.println("0");
            count++;
            this.notifyAll();
        }
    }

    private void printEven() throws InterruptedException {
        synchronized (this) {
            while(count%4 != 2) {
                this.wait();
            }
            System.out.println(number);
            number++;
            count++;
            this.notifyAll();
        }
    }

    private void printOdd() throws InterruptedException {
        synchronized (this) {
            while(count%4 != 0) {
                this.wait();
            }
            System.out.println(number);
            number++;
            count++;
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        PrintingNumberSeries printingNumberSeries = new PrintingNumberSeries(10);
        Thread t0 = new Thread(() -> {
            try {
                printingNumberSeries.printZeroSem();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t1 = new Thread(() -> {
            try {
                printingNumberSeries.printOddSem();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                printingNumberSeries.printEvenSem();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t2.start();
        t1.start();
        t0.start();

        t1.join();
        t2.join();
        t0.join();
    }
}