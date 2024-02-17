package ThreadingRevision;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class DiningPhilosophersDemonstration {
    public static void main(String[] args) throws InterruptedException {
        DiningPhilosophers.runTest();
    }
}

class DiningPhilosophers {

    private static Random random = new Random(System.currentTimeMillis());
    private Semaphore forks[] = new Semaphore[5];

    private Semaphore maxDiners = new Semaphore(5);

    public DiningPhilosophers() {
        forks[0] = new Semaphore(1);
        forks[1] = new Semaphore(1);
        forks[2] = new Semaphore(1);
        forks[3] = new Semaphore(1);
        forks[4] = new Semaphore(1);
    }

    private void lifeCycleOfPhilosophers(int id) throws InterruptedException {
        while(true) {
            contemplate();
            eat(id);
        }
    }

    void contemplate() throws InterruptedException {
        Thread.sleep(random.nextInt(500));
    }

    void eat(int id) throws InterruptedException {

        maxDiners.acquire();

        forks[id].acquire();
        forks[(id+4)%5].acquire();
        System.out.println(String.format("Philosopher %x is eating", id));
        System.out.flush();
        //Thread.sleep(2000);
        forks[id].release();
        forks[(id+4)%5].release();

        maxDiners.release();
    }

    public static void runTest() throws InterruptedException {
        DiningPhilosophers dp = new DiningPhilosophers();
        Thread d1 = new Thread(() -> {
            try {
                dp.lifeCycleOfPhilosophers(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread d2 = new Thread(() -> {
            try {
                dp.lifeCycleOfPhilosophers(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread d3 = new Thread(() -> {
            try {
                dp.lifeCycleOfPhilosophers(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread d4 = new Thread(() -> {
            try {
                dp.lifeCycleOfPhilosophers(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread d5 = new Thread(() -> {
            try {
                dp.lifeCycleOfPhilosophers(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        d1.start();
        d2.start();
        d3.start();
        d4.start();
        d5.start();

        d1.join();
        d2.join();
        d3.join();
        d4.join();
        d5.join();
    }

}
