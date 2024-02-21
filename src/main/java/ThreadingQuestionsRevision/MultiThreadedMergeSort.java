package ThreadingQuestionsRevision;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MultiThreadedMergeSort {
    public static void main(String[] args) {
        int[] input1 = {8,10,1,2,5,7,3,9,4,6};
        int[] input2 = {8,10,1,2,5,7,3,9,4,6};
        MergeSortThreading mergeSortThreading = new MergeSortThreading(input1, false);
        mergeSortThreading.mergeSort(0,9);
        System.out.println("Without threading: " + (System.currentTimeMillis() - mergeSortThreading.timeTaken)/1000);
        MergeSortThreading mergeSortThreading1 = new MergeSortThreading(input2, true);
        mergeSortThreading1.mergeSort(0,9);
        System.out.println("With threading: " + (System.currentTimeMillis() - mergeSortThreading1.timeTaken));

        for (int i: input1)System.out.println(i + " ");
        System.out.println("\n\n");
        for (int i: input2)System.out.println(i + " ");

        Executor executor = Executors.newFixedThreadPool(5);
        executor.execute(() -> {
            // Do something.
        });

    }
}

class MergeSortThreading {
    int[] input;

    boolean isThreaded;

    long timeTaken;

    public MergeSortThreading(int [] input, boolean isThreaded) {
        this.input = input;
        this.isThreaded = isThreaded;
        this.timeTaken = System.currentTimeMillis();
    }

    private void merge(int start, int mid, int end) {
        int[] left = new int[mid-start+1];
        int[] right = new int[end-mid];

        for (int i = 0; i < mid-start+1; i++) {
            left[i] = input[i+start];
        }

        for (int i = 0; i < end-mid; i++) {
            right[i] = input[i+mid+1];
        }

        int i=0, j=0, k=start;

        while(i<mid-start+1 && j<end-mid) {
            if (left[i] <= right[j]) {
                input[k] = left[i];
                i++;
            } else {
                input[k] = right[j];
                j++;
            }
            k++;
        }

        while(i<mid-start+1) {
            input[k]=left[i];
            i++;
            k++;
        }

        while(j<end-mid) {
            input[k] = right[j];
            j++;
            k++;
        }
    }

    public void mergeSort(int start, int end) {
        if (start>=end) {
            return;
        }
        int mid = (start+end)/2;

        if (isThreaded) {
            Thread t1 = new Thread(() -> mergeSort(start, mid));

            Thread t2 = new Thread(() -> mergeSort(mid + 1, end));

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                // Swallow exception.
            }
        } else {
            mergeSort(start, mid);
            mergeSort(mid + 1, end);
        }

        merge(start, mid, end);
    }

}
