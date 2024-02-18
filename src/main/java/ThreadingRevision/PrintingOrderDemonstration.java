package ThreadingRevision;

public class PrintingOrderDemonstration {
    public static void main(String[] args) throws InterruptedException {
        PrintingOrder.runTest();
    }
}

class PrintingOrder {
    int count;

    public PrintingOrder () {
        this.count = 1;
    }

    private void printFirst() throws InterruptedException {
        synchronized (this) {
            while(count != 1) {
                this.wait();
            }
            count++;
            System.out.println("First");
            this.notifyAll();
        }
    }

    private void printSecond() throws InterruptedException {
        synchronized (this) {
            while(count != 2) {
                this.wait();
            }
            System.out.println("Second");
            count++;
            this.notifyAll();
        }
    }

    private void printThird() throws InterruptedException {
        synchronized (this) {
            while(count != 3) {
                this.wait();
            }
            System.out.println("Third");
            count = 1;
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {
        PrintingOrder printingOrder = new PrintingOrder();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i<10; i++) {
                try {
                    printingOrder.printFirst();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i<10; i++) {
                try {
                    printingOrder.printSecond();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i = 0; i<10; i++) {
                try {
                    printingOrder.printThird();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t2.start();
        t1.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}
