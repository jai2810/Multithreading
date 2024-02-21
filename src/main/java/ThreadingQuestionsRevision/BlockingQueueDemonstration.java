package ThreadingQuestionsRevision;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueDemonstration {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue.runTest();
    }
}

class BlockingQueue<T> {

    private int capacity;
    private T[] array;

    private int head = 0;

    private int tail = 0;

    private int count = 0;

    private ReentrantLock lock = new ReentrantLock();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.array = (T[])new Object[capacity];
    }

    private synchronized void enqueue(T item) throws InterruptedException {
        while(count == capacity) {
            wait();
        }
        count++;
        if (tail == capacity) {
            tail = 0;
        }
        array[tail] = item;
        System.out.println("Current count ->" + count);
        tail++;
        notifyAll();
    }

//    private void enqueueSemaphore(T item) throws InterruptedException {
//        while (count == capacity) {
//
//        }
//        count++;
//        if (tail == capacity) {
//            tail = 0;
//        }
//        array[tail] = item;
//        System.out.println("Current count ->" + count);
//        tail++;
//        semaphore.release();
//    }

    private void enqueueLock(T item) throws InterruptedException {
        lock.lock();
        while(count == capacity) {
            lock.unlock();
            //
            lock.lock();
        }
        count++;
        if (tail == capacity) {
            tail = 0;
        }
        array[tail] = item;
        System.out.println("Current count ->" + count);
        tail++;
        lock.unlock();
    }

    private synchronized T dequeue() throws InterruptedException {
        T item = null;
        while(count == 0) {
            wait();
        }
        count--;
        if (head == capacity) {
            head = 0;
        }
        item = array[head];
        array[head] = null;
        head++;
        notifyAll();
        return item;
    }

    private T dequeueLock() throws InterruptedException {
        T item = null;
        lock.lock();
        while(count == 0) {
            lock.unlock();
            //
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
        BlockingQueue<Integer> bq = new BlockingQueue<>(1);
        Runnable p_runnable = () -> {
            for (int i=0;i<20;i++) {
                try {
                    bq.enqueue(i);
                    System.out.println("Enqueued -> " + i+ " at " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Runnable c_runnable = () -> {
            for (int i=0;i<10;i++) {
                try {
                    System.out.println("Dequeued -> " + bq.dequeueLock() + " at " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Thread producer = new Thread(p_runnable);
        Thread consumer1 = new Thread(c_runnable);
        Thread consumer2 = new Thread(c_runnable);

        producer.start();
        consumer1.start();
        consumer2.start();

        producer.join();
        consumer2.join();
        consumer1.join();
    }
}
