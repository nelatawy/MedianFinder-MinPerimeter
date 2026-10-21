# Assignment Report

---

## Problem 1: Comparing Median-Finding Methods

### Code

### Randomized Divide-and-Conquer Selection

**Part 1: Randomized partition.** Selects a pivot uniformly at random from the current range, moves it to the last position (as `partition` expects), and partitions around it. A range of size one returns immediately.

```java
private int randomizedPartition(int[] arr, int low, int high){
    if(low == high) return low; //return idx of pivot which is low

    int idx = new Random().nextInt(high - low + 1) + low;

    swap(arr, idx, high);

    return partition(arr, low, high);
}
```

**Part 2: Selection step.** Partitions the range and compares the pivot's rank with the requested rank. If they match, the pivot is returned; otherwise, the search recurses into only the side that contains the requested rank.

```java
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
```

### Deterministic Median-of-Medians Selection

**Part 1: Group medians (full groups).** Splits the range into groups of `GROUP_SIZE` elements, sorts each full group, and stores its median.

```java
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
```

**Part 2: Group medians (remaining group).** Handles the last, possibly smaller group and returns the array of medians.

```java
    if (!group.isEmpty()) {
        Collections.sort(group);
        medians[insertItr] = (group.get(group.size()/2));
    }
    return medians;
}
```

**Part 3: Pivot selection.** Handles the base case, then recursively computes the median of the group medians so that the pivot is deterministically good. The pivot is then located in the array and moved to the last position, because `partition` expects the pivot there.

```java
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
```

**Part 4: Partition and recursion.** Partitions around the chosen pivot and recurses into the side that contains the requested rank, exactly as in the randomized version.

```java
    int offset = partition(arr, low, high);
    int pivotIdx = offset + low;

    if (offset + 1 == rank) // we compare with pivotIdx + 1 because pivotIdx + 1 is equal to the rank of this pivot element
        return arr[pivotIdx];
    if (offset + 1 > rank)
        return MedianOfMediansSelect(arr, rank, low, pivotIdx - 1);
    else
        return MedianOfMediansSelect(arr, rank - (offset + 1), pivotIdx + 1, high);
}
```

### Naive Sorting Method

Sorts the array using the native sort and returns the middle element.

```java
public int findMedian(int[] arr) {
    Arrays.sort(arr);
    return arr[arr.length/2];
}
```

#### Experiment Driver

**Part 1: Correctness test.** Generates random arrays and compares the results of both implemented methods against the naive sorting method.

```java
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
```

**Part 2: Warm-up.** Runs all three methods on small arrays before measurement so that JVM warm-up effects (e.g., JIT compilation) do not distort the timings.

```java
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
```

**Part 3: Timing setup.** Initializes the three finders, prints the CSV header, and generates one random array for each input size.

```java
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
```

**Part 4: Timing loop.** Measures each finder over `itrCnt` iterations using `System.nanoTime()` and stores the average running time per call.

```java
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
```

**Part 5: Output.** Prints one CSV row per input size, with the averages converted from nanoseconds to milliseconds.

```java
        System.out.print(inputSize);
        for (double avgRunningTime : avgRunningTimes) {
            System.out.print("," + avgRunningTime/1e6);
        }
        System.out.print("\n");
    }
}
```

### Experimental Setup

- **Input sizes:** 100, 1,000, 10,000, 100,000, 1,000,000, 10,000,000
- **Runs per data point:** 10, with the results averaged
- **Correctness check:** Verified against the naive sort-then-select approach
- **Machine / Java version:** Intel Core i9-13900H, 32 GB DDR5, Java 25.0.3

### Results

| n | Randomized (ms) | Median of Medians (ms) | Sorting (ms) |
|---|-----------------|------------------------|--------------|
| 10^2 | 0.0040 | 0.0116 | 0.0134 |
| 10^3 | 0.0138 | 0.1038 | 0.0322 |
| 10^4 | 0.0661 | 1.0338 | 0.2548 |
| 10^5 | 0.7655 | 15.0255 | 2.1014 |
| 10^6 | 4.2464 | 67.4571 | 13.8770 |
| 10^7 | 27.9399 | 414.8774 | 31.5323 |

*Each value is the average of the two runs recorded per size (10^8 has a single run). Times are in milliseconds.*

### Analysis

The analysis over different input sizes shows that the randomized version, with an expected running time of O(n), outperforms both of the other algorithms on all input sizes. Although its theoretical worst-case running time is O(n^2), this case is highly unlikely (practically impossible). The Median of Medians version is actually slower than the naive version because of a very large hidden constant factor, even though its theoretical worst-case bound is O(n), compared with O(n^2) for the randomized version and O(n lg n) for the control (naive) version.

---

## Problem 2: Minimum Perimeter Triangle
### Approach

The idea of the solution is heavily based on the closest pair of points divide-and-conquer problem, but extended to account for the third point.

#### Logic

1. Split the set of points into two groups of roughly the same number of points.
2. Find the minimum perimeter of a triangle in the left half, and the same for the right half.
3. Handle the base cases. A set of exactly 3 points yields the perimeter of its triangle, and a set of fewer than 3 points returns a heavily penalized (invalid) value.
4. Account for the triangles composed of 2 points on one side of the split line and the third point on the other side.

#### Width of the Strip and Number of Comparisons

Let MP be the minimum perimeter found in the two halves. To handle the triangles that cross the split line, we only consider the points whose distance from the split line is at most MP/2. These strip points are sorted by their y-coordinates, and each point only needs to be compared with the 15 points that follow it. The justification for both values is given in the correctness notes below.

### Correctness Notes

#### Why Only the Strip Is Checked

1. If a triangle has a side of length L, then its perimeter is at least 2L (the degenerate case).
2. Therefore, any two points separated by a distance of MP/2 or more cannot belong to a triangle with a perimeter smaller than MP, so such pairs need not be considered.
3. Consequently, we only need to check points whose distance from the split line is at most MP/2.

#### Why 15 Following Points Are Sufficient

1. Consider two adjacent blocks, one to the left and one to the right of the split line, where each block has a side length of MP/2.
2. Divide each block into 4 sub-blocks of side length MP/4. Any 3 points within a single sub-block would form a triangle with a perimeter smaller than MP, which contradicts the minimality of MP. Hence, each sub-block contains at most 2 points.
3. The two blocks contain 8 sub-blocks in total, so they accommodate at most 2 * 8 = 16 points. Consequently, when the strip points are sorted by their y-coordinates, each point only needs to be compared with the 15 points that follow it.

### Time Complexity

#### Preprocessing

The points are sorted once by x-coordinate (`pointsX`) and once by y-coordinate (`pointsY`), which costs O(n lg n). This is done a single time, before the recursion begins.

#### Recurrence Relation

T(n) = 2T(n/2) + O(n)

#### Cost of the Non-Recursive Work per Call

1. **Splitting the points into two halves:** O(n). `pointsX` is already sorted by x, so the split is a direct division at the middle index. `pointsY` is divided into the same two halves with a single linear sweep, which keeps both halves sorted by y.
2. **Collecting the strip points sorted by y:** O(n). Because `pointsY` is already sorted by y, a single linear sweep that keeps the points within MP/2 of the split line is enough, and no re-sorting is needed.
3. **Comparing each strip point with its following points:** O(n). Each strip point is compared with at most 15 following points, which means at most C(15, 2) = 105 candidate triangles per point. This is a constant, so the total cost is 105 * n = O(n).
4. **Total per call:** O(n).

#### Runtime Complexity
O(n.lgn) (Master Method)
