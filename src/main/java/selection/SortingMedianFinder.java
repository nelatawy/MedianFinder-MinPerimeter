package selection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortingMedianFinder implements MedianFinder {

    @Override
    public int findMedian(int[] arr) {
        Arrays.sort(arr);
        return arr[arr.length/2];
    }
}
