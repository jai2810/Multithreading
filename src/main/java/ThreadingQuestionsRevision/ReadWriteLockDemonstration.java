package ThreadingQuestionsRevision;

public class ReadWriteLockDemonstration {
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock.runTest();
    }
}

class ReadWriteLock {
    int readers = 0;

    boolean isWriteLocked = false;

    private synchronized void acquireReadLock() throws InterruptedException {
        while (isWriteLocked) {
            wait();
        }
        readers++;
        notifyAll();
    }

    private synchronized void acquireWriteLock() throws InterruptedException {
        while (readers > 0 || isWriteLocked) {
            wait();
        }
        isWriteLocked = true;
    }

    private synchronized void releaseReadLock() {
        readers--;
        notifyAll();
    }

    private synchronized void releaseWriteLock() {
        isWriteLocked = false;
        notifyAll();
    }

    public static void runTest() throws InterruptedException {
        ReadWriteLock rwl = new ReadWriteLock();

        Thread t1 = new Thread(() -> {
            System.out.println("Acquiring write lock in t1 at: " + System.currentTimeMillis());
            try {
                rwl.acquireWriteLock();
                System.out.println("Acquired write lock in t1 at: " + System.currentTimeMillis());
                Thread.sleep(3000);
                rwl.releaseWriteLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println("Acquiring write lock in t2 at: " + System.currentTimeMillis());
            try {
                rwl.acquireWriteLock();
                System.out.println("Acquired write lock in t2 at: " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(() -> {
            System.out.println("Acquiring read lock in t3 at: " + System.currentTimeMillis());
            try {
                rwl.acquireReadLock();
                System.out.println("Acquired read lock in t3 at: " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t4 = new Thread(() -> {
            System.out.println("Acquiring read lock in t4 at: " + System.currentTimeMillis());
            try {
                rwl.acquireReadLock();
                System.out.println("Acquired read lock in t4 at: " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        Thread.sleep(50);
        t2.start();
        Thread.sleep(500);
        t4.start();
        t3.start();



        t4.join();
        t3.join();
        t1.join();
        t2.join();
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {

        }
    }
}

