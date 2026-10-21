package selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MedianOfMediansRankFinder implements MedianFinder {
    final static int GROUP_SIZE = 5;

    int[] getMedians(int[] arr, int low, int high) {
        int[] medians = new int[(high - low + GROUP_SIZE)/GROUP_SIZE];
        int insertItr = 0;

        List<Integer> group = new  ArrayList<>(); // current group elements
        for (int i = low; i <= high; i++) {
            group.add(arr[i]);
            if (group.size() == GROUP_SIZE) {
                Collections.sort(group);
                medians[insertItr++] = (group.get(group.size()/2));
                group.clear();
            }
        }
        if (!group.isEmpty()) {
            Collections.sort(group);
            medians[insertItr] = (group.get(group.size()/2));
        }
        return medians;
    }

    private int MedianOfMediansSelect(int[] arr, int rank, int low, int high) {
        if (low == high)
            return arr[low];
        // first we get the median of medians to have a deterministically good pivot
        int[] medians = getMedians(arr, low, high);
        int medianOfMedians = MedianOfMediansSelect(medians, (medians.length + 1)/2, 0, medians.length - 1);

        int medianIdx = -1;
        for (int i = low; i <= high; i++) {
            if (arr[i] == medianOfMedians) {
                medianIdx = i;
                break;
            }
        }
        // we swap indices because partition expects the pivot to be at index high
        swap(arr, medianIdx, high);

        int offset = partition(arr, low, high);
        int pivotIdx = offset + low;

        if (offset + 1 == rank) // we compare with pivotIdx + 1 because pivotIdx + 1 is equal to the rank of this pivot element
            return arr[pivotIdx];
        if (offset + 1 > rank)
            return MedianOfMediansSelect(arr, rank, low, pivotIdx - 1);
        else
            return MedianOfMediansSelect(arr, rank - (offset + 1), pivotIdx + 1, high);
    }

    @Override
    public int findMedian(int[] arr) {
        return MedianOfMediansSelect(arr, (arr.length + 1)/2, 0, arr.length - 1);
    }
}
