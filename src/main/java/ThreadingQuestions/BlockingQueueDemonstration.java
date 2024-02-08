package ThreadingQuestions;

public class BlockingQueueDemonstration {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue.runTest();
    }
}

class BlockingQueue<T>{
    int capacity;

    T[] array;

    int head = 0;

    int tail = 0;

    int count = 0;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        array = (T[]) new Object[capacity];
    }

    synchronized void enqueue(T item) throws InterruptedException {
        while(count == capacity) {
            wait();
        }
        count++;
        if (tail == capacity) {
            tail = 0;
        }
        array[tail] = item;
        tail++;
        notifyAll();
    }

    synchronized T dequeue() throws InterruptedException {
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

    public static void runTest() throws InterruptedException {
        BlockingQueue<Integer> bq = new BlockingQueue<>(3);

        Thread pThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    try {
                        bq.enqueue(i+1);
                        System.out.println("Enqueued: " + (i+1) + " at:" + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Thread cThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    try {
                        System.out.println("Thread 2 dequeued: " + bq.dequeue() + " at: " + System.currentTimeMillis());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Thread cThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    try {
                        System.out.println("Thread 3 dequeued: " + bq.dequeue() + " at: " + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        cThread.start();
        pThread.start();
        Thread.sleep(3000);


        cThread.join();
        cThread2.start();
        pThread.join();
        cThread2.join();
    }
}
