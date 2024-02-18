package ThreadingRevision;

public class PrintingFooBarDemonstration {
    public static void main(String[] args) throws InterruptedException {
        PrintingFooBar.runTest();
    }
}

class PrintingFooBar {
    int count;
    int n;
    public PrintingFooBar(int n) {
        this.count = 1;
        this.n = n;
    }

    private void printFoo() throws InterruptedException {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while(count != 1) {
                    this.wait();
                }
                count++;
                System.out.println("Foo");
                this.notifyAll();
            }
        }
    }

    private void printBar() throws InterruptedException {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while(count != 2) {
                    this.wait();
                }
                System.out.println("Bar");
                count = 1;
                this.notifyAll();
            }
        }
    }

    public static void runTest() throws InterruptedException {
        PrintingFooBar printingFooBar = new PrintingFooBar(10);
        Thread t1 = new Thread(() -> {
            try {
                printingFooBar.printFoo();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                printingFooBar.printBar();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t2.start();
        t1.start();

        t2.join();
        t1.join();
    }

}
