package selection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface MedianFinder {
    public int findMedian(int[] arr);

    default void swap(int[] arr, int i, int j) {
        if (i == j)
            return;
        arr[j] ^= arr[i];
        arr[i] ^= arr[j];
        arr[j] ^= arr[i];

    }
    default int partition(int[] arr, int low, int high) {

        // now pivot is at idx high
        int pivot = arr[high];
        int last_smaller_idx = low - 1; //idx of rightmost element smaller than pivot
        for (int i = low; i < high; i++) {
            if (arr[i] < pivot) {
                last_smaller_idx++;
                swap(arr, i, last_smaller_idx);
            }
        }
        //now we have the segment starting from low and ending at last_smaller_idx (< pivot)
        //and from last_smaller_idx + 1 to high (> pivot)

        // we place the pivot element at its correct position
        swap(arr, last_smaller_idx + 1, high);

        return (last_smaller_idx + 1) - low;
    }
}
