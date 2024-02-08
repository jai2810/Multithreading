package ThreadingQuestions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueMutexDemonstration {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueueWithMutex.runTest();
    }
}class BlockingQueueWithMutex<T>{
    int capacity;

    T[] array;

    int head = 0;

    int tail = 0;

    int count = 0;

    Lock lock = new ReentrantLock();

    public BlockingQueueWithMutex(int capacity) {
        this.array = (T[])new Object[capacity];
        this.capacity = capacity;
    }

    void enqueue(T item) {
        lock.lock();
        while(count == capacity) {
            lock.unlock();

            lock.lock();
        }
        count++;
        if (tail == capacity) {
            tail = 0;
        }
        array[tail] = item;
        tail++;
        lock.unlock();
    }

    T dequeue() {
        T item = null;
        lock.lock();
        while(count == 0) {
            lock.unlock();

            lock.lock();
        }
        count--;

        if (head == capacity) {
            head = 0;
        }
        item = array[head];
        array[head] = null;
        head++;
        lock.unlock();
        return item;
    }

    public static void runTest() throws InterruptedException {
        final BlockingQueueWithMutex<Integer> q = new BlockingQueueWithMutex<Integer>(5);

        Thread producer1 = new Thread(new Runnable() {
            public void run() {
                int i = 1;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 1 enqueued " + i);
                    i++;
                }
            }
        });

        Thread producer2 = new Thread(new Runnable() {
            public void run() {
                int i = 5000;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 2 enqueued " + i);
                    i++;
                }
            }
        });

        Thread producer3 = new Thread(new Runnable() {
            public void run() {
                int i = 100000;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 3 enqueued " + i);
                    i++;
                }
            }
        });

        Thread consumer1 = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("Consumer thread 1 dequeued " + q.dequeue());
                }
            }
        });

        Thread consumer2 = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("Consumer thread 2 dequeued " + q.dequeue());
                }
            }
        });

        Thread consumer3 = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("Consumer thread 3 dequeued " + q.dequeue());
                }
            }
        });

        producer1.setDaemon(true);
        producer2.setDaemon(true);
        producer3.setDaemon(true);
        consumer1.setDaemon(true);
        consumer2.setDaemon(true);
        consumer3.setDaemon(true);

        producer1.start();
        producer2.start();
        producer3.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();

        Thread.sleep(1000);
    }


}



