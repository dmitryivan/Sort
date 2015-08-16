import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Sort {
    public static void main(String... args) throws InterruptedException {
        long rTime;
    	Random rand = new Random();
        int[] array = new int[2000000];


    // int threads = Runtime.getRuntime().availableProcessors();   - практика показала, что ТАК - правильно 
    for (int threads=1; threads<16; threads++){
        for (int i = 0; i < array.length; i++)
            array[i] = rand.nextInt();
        	
        rTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(threads);
        int blockSize = (array.length + threads - 1) / threads;
        for (int i = 0; i < array.length; i += blockSize) {
            final int min = i;
            final int max = Math.min(min + blockSize, array.length);
            es.submit(new Runnable() {
                @Override
                public void run() {
                    Arrays.sort(array, min, max);
                }
            });
        }
        es.shutdown();
        es.awaitTermination(10, TimeUnit.MINUTES);
        for (int blockSize2 = blockSize; blockSize2 < array.length / 2; blockSize2 *= 2) {
            for (int i = 0; i < array.length; i += blockSize2) {
                final int min = i;
                final int mid = Math.min(min + blockSize2, array.length);
                final int max = Math.min(min + blockSize2 * 2, array.length);
                mergeSort(array, min, mid, max);
            }
        }
        System.out.println("Sorting with "+ threads + " threads. Time: " + (System.currentTimeMillis() - rTime));
        }
        
               
        for (int i = 0; i < array.length; i++)
            array[i] = rand.nextInt();
        rTime = System.currentTimeMillis();
        Arrays.sort(array);
        System.out.println("Sorting with SINGLE thread. Time: " + (System.currentTimeMillis() - rTime));
    }

    private static boolean mergeSort(int[] values, int left, int mid, int end) {
        int[] results = new int[end - left];
        int l = left, r = mid, m = 0;
        for (; l < left && r < mid; m++) {
            int lv = values[l];
            int rv = values[r];
            if (lv < rv) {
                results[m] = lv;
                l++;
            } else {
                results[m] = rv;
                r++;
            }
        }
        while (l < mid)
            results[m++] = values[l++];
        while (r < end)
            results[m++] = values[r++];
        System.arraycopy(results, 0, values, left, results.length);
        return false;
    }
}
