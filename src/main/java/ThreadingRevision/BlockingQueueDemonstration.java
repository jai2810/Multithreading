package ThreadingRevision;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueDemonstration {
    public static void main(String[] args) throws InterruptedException {
        //BlockingQueue.runTest();
        BlockingQueue.runTestWithMutexLock();
    }
}

class BlockingQueue<T> {
    private int capacity;

    private int count = 0;

    private int head = 0;

    private int tail = 0;

    private T[] queue;

    Object lock = new Object();

    Lock mutexLock = new ReentrantLock();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = (T[]) new Object[capacity];
    }

    private synchronized void enqueue(T item) throws InterruptedException {
        while(count == capacity) {
            this.wait();
        }
        count++;
        if (tail == capacity) {
            tail = 0;
        }
        queue[tail] = item;
        tail++;
        this.notifyAll();
    }

    private void enqueueUsingMutex(T item) {
        mutexLock.lock();
        while(count == capacity) {
            mutexLock.unlock();
            //
            mutexLock.lock();
        }

        count++;
        if (tail == capacity) {
            tail = 0;
        }
        queue[tail] = item;
        tail++;

        mutexLock.unlock();
    }

    private synchronized T dequeue() throws InterruptedException {
        T item = null;
        while(count == 0) {
            this.wait();
        }
        count--;
        if (head == capacity) {
            head = 0;
        }
        item = queue[head];
        queue[head] = null;
        head++;
        this.notifyAll();
        return item;
    }

    private T dequeueUsingMutex() {
        T item = null;
        mutexLock.lock();
        while(count == 0) {
            mutexLock.unlock();
            //
            mutexLock.lock();
        }

        count--;
        if (head == capacity) {
            head = 0;
        }
        item = queue[head];
        queue[head] = null;
        head++;
        mutexLock.unlock();
        return item;
    }

    public static void runTest() throws InterruptedException {
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(5);

        Thread p_thread = new Thread(() -> {
            for (int i = 1; i<=10;i++) {
                try {
                    blockingQueue.enqueue(i);
                    System.out.println("Enqueued " + i + " at " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Producer thread.");

        Thread c_thread = new Thread(() -> {
            for (int i = 1; i<=10; i++) {
                try {
                    System.out.println("Dequeued item: " + blockingQueue.dequeue() + " at " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        c_thread.start();
        Thread.sleep(2000);
        p_thread.start();

        p_thread.join();
        c_thread.join();

    }

    public static void runTestWithMutexLock() throws InterruptedException {
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(5);

        Thread p_thread = new Thread(() -> {
            for (int i = 1; i<=20;i++) {
                blockingQueue.enqueueUsingMutex(i);
                System.out.println("Enqueued " + i + " at " + System.currentTimeMillis());
            }
        }, "Producer thread.");

        Thread c1_thread = new Thread(() -> {
            for (int i = 1; i<=10; i++) {
                System.out.println("Dequeued item: " + blockingQueue.dequeueUsingMutex() + " at " + System.currentTimeMillis());
            }
        });

        Thread c2_thread = new Thread(() -> {
            for (int i = 11; i<=20; i++) {
                System.out.println("Dequeued item: " + blockingQueue.dequeueUsingMutex() + " at " + System.currentTimeMillis());
            }
        });

        p_thread.start();
        Thread.sleep(2000);
        c1_thread.start();
        c1_thread.join();
        c2_thread.start();
        p_thread.join();
        c2_thread.join();

    }
}
