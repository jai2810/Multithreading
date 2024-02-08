package org.example;

public class BlockingQueueAgain {
    public static void main(String[] args) throws Exception{
        BlockingQueue1<Integer> blockingQueue1 = new BlockingQueue1<Integer>(5);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    try {
                        blockingQueue1.enqueue(i);
                    } catch (InterruptedException e) {
                        // Do nothing.
                    }
                    System.out.println("Enqueued --> " + i);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    try {
                        System.out.println("Dequeued item --> " + blockingQueue1.dequeue());
                    } catch (InterruptedException e) {
                        // Do nothing.
                    }
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    try {
                        System.out.println("Dequeued item --> " + blockingQueue1.dequeue());
                    } catch (InterruptedException e) {
                        // Do nothing.
                    }
                }
            }
        });

        t1.start();
        Thread.sleep(4000);
        t2.start();

        t2.join();

        t3.start();
        t1.join();
        t3.join();

    }
}

class BlockingQueue1<T> {

    int capacity;
    T[] array;

    int size = 0; // Current size

    int head = 0;

    int tail = 0;

    public BlockingQueue1(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public synchronized void enqueue(T item) throws InterruptedException {
        while (size == capacity) {
            wait();
        }
        if (tail == capacity) {
            tail = 0;
        }
        array[tail] = item;
        tail++;
        size++;
        notifyAll();

    }

    public synchronized T dequeue() throws InterruptedException {
        T item = null;
        while (size == 0) {
            wait();
        }

        if (head == capacity) {
            head = 0;
        }

        item = array[head];
        array[head] = null;
        head++;
        size--;
        notifyAll();
        return item;
    }

}
