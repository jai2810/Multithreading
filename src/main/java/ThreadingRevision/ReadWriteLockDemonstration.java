package ThreadingRevision;

public class ReadWriteLockDemonstration {
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock.runTest();
    }
}

class ReadWriteLock {

    int readers = 0;

    boolean isWriteLocked = false;

    private synchronized void acquiredReadLock() throws InterruptedException {
        while(isWriteLocked) {
            wait();
        }
        readers++;
    }

    private synchronized void releaseReadLock() throws InterruptedException {
        while (readers == 0) {
            wait();
        }
        readers--;
        notifyAll();
    }

    private synchronized void acquiredWriteLock() throws InterruptedException {
        while(isWriteLocked || readers!=0) {
            wait();
        }
        isWriteLocked = true;
    }

    private synchronized void releaseWriteLock() throws InterruptedException {
        isWriteLocked = false;
        notifyAll();
    }

    public static void runTest() throws InterruptedException {
        final ReadWriteLock rwl = new ReadWriteLock();

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    System.out.println("Attempting to acquire write lock in t1: " + System.currentTimeMillis());
                    rwl.acquiredWriteLock();
                    System.out.println("write lock acquired t1: " + +System.currentTimeMillis());

                    // Simulates write lock being held indefinitely
                    for (; ; ) {
                        Thread.sleep(500);
                    }

                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    System.out.println("Attempting to acquire write lock in t2: " + System.currentTimeMillis());
                    rwl.acquiredWriteLock();
                    System.out.println("write lock acquired t2: " + System.currentTimeMillis());

                } catch (InterruptedException ie) {

                }
            }
        });

        Thread tReader1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    rwl.acquiredReadLock();
                    System.out.println("Read lock acquired: " + System.currentTimeMillis());

                } catch (InterruptedException ie) {

                }
            }
        });

        Thread tReader2 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Read lock about to release: " + System.currentTimeMillis());
                try {
                    rwl.releaseReadLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Read lock released: " + System.currentTimeMillis());
            }
        });

        tReader1.start();
        t1.start();
        Thread.sleep(3000);
        tReader2.start();
        Thread.sleep(1000);
        t2.start();
        tReader1.join();
        tReader2.join();
        t2.join();
    }
}
