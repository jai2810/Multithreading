package ThreadingQuestionsRevision;

public class CountingSemaphoresDemonstration {
    public static void main(String[] args) throws InterruptedException {
        CountingSemaphore.runTest();
    }
}

class CountingSemaphore {
    private int maxPermits;

    private int usedPermits = 0;

    public CountingSemaphore(int maxPermits) {
        this.maxPermits = maxPermits;
    }

    private synchronized void acquire() throws InterruptedException {
        while (usedPermits == maxPermits) {
            wait();
        }
        usedPermits++;
        notifyAll();
    }

    private synchronized void release() throws InterruptedException {
        while (usedPermits == 0) {
            wait();
        }
        usedPermits--;
        notifyAll();
    }

    public static void runTest() throws InterruptedException {
        CountingSemaphore countingSemaphore = new CountingSemaphore(1);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    countingSemaphore.acquire();
                    System.out.println("Ping " + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    countingSemaphore.release();
                    System.out.println("Pong " + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }
}
