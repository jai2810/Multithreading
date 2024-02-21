package ThreadingQuestionsRevision;

public class BarrierDemonstration {
    public static void main(String[] args) {

    }
}

class Barrier {
    int count = 0;

    int n;

    int released = 0;

    public Barrier(int n) {
        this.n = n;
    }

    private synchronized void await() throws InterruptedException {

        while(count == n) wait();

        count++;
        if (count == n) {
            notifyAll();
            released = n;
        } else {
            while(count < n) {
                wait();
            }
        }

        released--;
        if (released == 0) {
            count = 0;
            notifyAll();
        }
    }

    public static void runTest() {

    }
}
