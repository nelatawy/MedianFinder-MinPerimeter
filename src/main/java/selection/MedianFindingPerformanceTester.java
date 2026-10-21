package selection;

import java.util.*;

public class MedianFindingPerformanceTester {

    public static void correctnessTest(int inputSize, int itrCnt) {
        Random rand = new Random();
        for (int t = 0; t < itrCnt; t++) {
            int[] arr = new int[inputSize];
            for (int i = 0; i < inputSize; i++) {
                arr[i] = (rand.nextInt(inputSize));
            }
            Integer normal = new SortingMedianFinder().findMedian(arr.clone());
            Integer randomizedRes = new RandomizedMedianFinder().findMedian(arr.clone());
            Integer medianOfMediansRes = new MedianOfMediansRankFinder().findMedian(arr.clone());
            if (!normal.equals(randomizedRes)) {
                return;
            }
            if (!normal.equals(medianOfMediansRes)) {
                return;
            }
        }
    }

    public static void warmup(int itrCnt){
        Random rand = new Random();
        for (int t = 0; t < itrCnt; t++) {
            int[] arr = new int[1000];
            for (int i = 0; i < 1000; i++) {
                arr[i] = (rand.nextInt(1000));
            }
            new SortingMedianFinder().findMedian(arr.clone());
            new RandomizedMedianFinder().findMedian(arr.clone());
            new MedianOfMediansRankFinder().findMedian(arr.clone());
        }
    }

    public static void runTest(List<Integer> inputSizes, int itrCnt){
        Random rand = new Random();
        List<MedianFinder> finders = new ArrayList(List.of(
                new SortingMedianFinder(),
                new MedianOfMediansRankFinder(),
                new RandomizedMedianFinder()
        ));
        System.out.println("Input Size," + "Sorting," + "MedianOfMedians," +"Randomized");


        for (int inputSize : inputSizes) {
            long startTime, endTime;

            int[] arr = new int[inputSize];
            for (int i = 0; i < inputSize; i++) {
                arr[i] = (rand.nextInt(inputSize));
            }
            double[] avgRunningTimes = new double[finders.size()];
            int insertItr = 0;

            for (MedianFinder finder : finders) {
                long totalTime = 0L;
                for (int i = 0; i < itrCnt; i++) {
                    startTime = System.nanoTime();
                    finder.findMedian(arr);
                    endTime = System.nanoTime();
                    totalTime += (endTime - startTime);
                }
                avgRunningTimes[insertItr++] = ((double)totalTime / (itrCnt));
            }
            System.out.print(inputSize);
            for (double avgRunningTime : avgRunningTimes) {
                System.out.print("," + avgRunningTime/1e6);
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
//        correctnessTest(10_000, 100);
        warmup(1000);
        runTest(List.of(100,1000,10_000, 100_000,(int)1e6, (int)1e7, (int)1e8), 10);
    }

}
