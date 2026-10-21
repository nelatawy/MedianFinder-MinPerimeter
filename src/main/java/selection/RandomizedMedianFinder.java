package selection;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomizedMedianFinder implements MedianFinder{

    private int randomizedPartition(int[] arr, int low, int high){
        if(low == high) return low; //return idx of pivot which is low

        int idx = new Random().nextInt(high - low + 1) + low;

        swap(arr, idx, high);

        return partition(arr, low, high);
    }

    private int quickSelect(int[] arr, int rank, int low, int high) {

        int offset = randomizedPartition(arr, low, high); //offset from low
        int pivotIdx = offset + low;

        if (offset + 1 == rank) // we compare with pivotIdx + 1 because pivotIdx + 1 is equal to the rank of this pivot element
            return arr[pivotIdx];
        if (offset + 1 > rank)
            return quickSelect(arr, rank, low, pivotIdx - 1);
        else
            return quickSelect(arr, rank - (offset + 1), pivotIdx + 1, high);
    }

    @Override
    public int findMedian(int[] arr) {
        return quickSelect(arr, (arr.length + 1)/2, 0, arr.length - 1);
    }
}
