## Hierarchical Clustering Impl. on Java

This is a very simple implementation of hierarchical clustering, which you might heard of during your data mining algorithm classes.
If you are not familiar with it, go to https://en.wikipedia.org/wiki/Hierarchical_clustering for more detials. :)

### Implementation

Data points are stored in `mPoints`, and our goal is to compute `mClusters` from it. To do that, we keep the distance matrix `mMatirx` for all pairs of data points, and the indices of the minimum-distanced point for each points `mMinIndex`.

```java
// mN: number of points, mD: dimension of points.
private static int mN = 0;
private static int mD = 0;

// mPoints: raw data resides in here. Constant after it's been initialized.
private static ArrayList<double[]> mPoints;
// mClusters: contains centroids of clusters. Requires O(n) space.
private static ArrayList<double[]> mClusters;
// mNumPointsInClusters: contains the number of points in each clusters.
private static ArrayList<Integer> mNumPointsInClusters;
// mMatrix: contains distances between each pair of clusters. Requires O(n^2) space.
private static ArrayList<ArrayList<Double>> mMatrix;
// mMinDist: contains "index" of the minimum-distanced point for each points. Requires O(n) space.
private static ArrayList<Integer> mMinIndex;
```

Any possible improvement on the algorithms are not included, but you can put some tweaks like using Manhattan distance, etc. by yourself.

```java
/**
 * Returns Euclidean distance between two points. For arbitrary-dimensional data.
 * @param from
 * @param to
 * @return distance between two points
 */
private static double distanceBetween(double[] from, double[] to) {
    ...
}
```

### Usage

```
$ javac Cluster.java
$ java Cluster
usage: java Cluster <input_file_path> <number_of_clusters>
$ java Cluster input.txt my_output.txt
```

### Examples

You can use the input.txt to test this code.

