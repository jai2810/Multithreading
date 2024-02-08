package org.example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MutexSolution {
    public static void main(String[] args) throws Exception{
        BlockingQueueMutex<Integer> q = new BlockingQueueMutex<>(3);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    q.enqueue(i);
                    System.out.println("Enqueued --> " + i);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Dequeued item from thread 2 --> " + q.dequeue());
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Dequeued item from thread 3 --> " + q.dequeue());
                }
            }
        });

        t1.start();
        Thread.sleep(5000);

        t2.start();

        t2.join();

        t3.start();
        t3.join();

        t1.join();

    }
}

class BlockingQueueMutex<T> {
    T[] array;

    int capacity;

    int size = 0;

    int head = 0;

    int tail = 0;

    Lock lock = new ReentrantLock();

    public BlockingQueueMutex(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) {
        // Acquire the mutex.
        lock.lock();
        while (size == capacity) {
            // Release the mutex.
            lock.unlock();

            // Acquire the mutex.
            lock.lock();
        }

        if (tail == capacity) {
            tail = 0;
        }

        array[tail] = item;
        tail++;
        size++;

        lock.unlock();
    }

    public T dequeue() {
        T item = null;
        // Acquire the mutex.
        lock.lock();
        while (size == 0) {
            // Release the mutex.
            lock.unlock();

            // Acquire the mutex.
            lock.lock();
        }

        if (head == capacity) {
            head = 0;
        }

        item = array[head];
        array[head] = null;
        head++;
        size--;

        lock.unlock();
        return item;
    }
}
