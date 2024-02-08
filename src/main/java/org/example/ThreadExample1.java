package org.example;

public class ThreadExample1 {

    public static class MyThread extends Thread {
        public void run() {
            System.out.println("My thread running.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("My thread finished.");
        }
    }

    public static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("My runnable running.");
            System.out.println("My runnable finished.");
        }

    }


    public static void main(String[] args) throws InterruptedException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "Runnable running.");
            }
        };

        Thread t1 = new Thread(runnable, "Thread 1 ");
        Thread t2 = new Thread(runnable, "Thread 2 ");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }


}
