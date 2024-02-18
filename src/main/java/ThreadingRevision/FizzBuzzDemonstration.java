package ThreadingRevision;

public class FizzBuzzDemonstration {
    public static void main(String[] args) throws InterruptedException {
        FizzBuzz.runTest();
    }
}

class FizzBuzz {
    int n;

    int num = 1;

    public FizzBuzz(int n) {
        this.n = n;
    }

    private synchronized void fizz() throws InterruptedException {
        while (num <= n) {
            if (num % 3 == 0 && num % 5 != 0) {
                System.out.println("fizz");
                num++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    private synchronized void buzz() throws InterruptedException {
        while(num<=n) {
            if (num % 3 != 0 && num % 5 == 0) {
                System.out.println("buzz");
                num++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    private synchronized void fizzbuzz() throws InterruptedException {
        while(num<=n) {
            if (num % 3 == 0 && num % 5 == 0) {
                System.out.println("fizzbuzz");
                num++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    private synchronized void number() throws InterruptedException {
        while(num<=n) {
            if (num % 3 != 0 && num % 5 != 0) {
                System.out.println(num);
                num++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    public static void runTest() throws InterruptedException {
        FizzBuzz fb = new FizzBuzz(15);
        Thread t1 = new Thread(()-> {
            try {
                fb.fizz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(()-> {
            try {
                fb.buzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(()-> {
            try {
                fb.fizzbuzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t4 = new Thread(()-> {
            try {
                fb.number();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }
}
