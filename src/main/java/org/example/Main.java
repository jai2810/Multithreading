package org.example;

import java.util.Random;

class Demonstration {
    public static void main( String args[] ) throws InterruptedException {
        RaceCondition.runTest();
    }
}

class RaceCondition {
    int randInt;
    Random random = new Random(System.currentTimeMillis());

    public void printer() {
        int i = 1000000;
        while (i != 0) {
            synchronized (this) {
                if (randInt % 5 == 0) {
                    if (randInt % 5 != 0) {
                        System.out.println(randInt);
                    }
                }
                i--;
            }
        }
        System.out.println("Printer Ended.");
    }

    public void modifier() {
        int i = 1000000;
        while (i != 0) {
            synchronized (this) {
                randInt = random.nextInt(1000);
                i--;
            }
        }
        System.out.println("Modifier Ended.");
    }

    public static void runTest() throws InterruptedException {
        final RaceCondition raceCondition = new RaceCondition();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                raceCondition.printer();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                raceCondition.modifier();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

    }
}


