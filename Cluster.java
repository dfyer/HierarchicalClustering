import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Cluster {
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
    
    /**
     * Returns Euclidean distance between two points. For arbitrary-dimensional data.
     * @param from
     * @param to
     * @return distance between two points
     */
    private static double distanceBetween(double[] from, double[] to) {
    	if(from.length != to.length)
    		return (double) -1;
    	
    	// Euclidean distance.
    	double dist = 0;
    	for(int i = 0; i < from.length; i++)
    		dist += (from[i] - to[i]) * (from[i] - to[i]);
    	return Math.sqrt(dist);
    }
    
    /**
     * Returns arithmetic mean of two points. For arbitrary-dimensional data.
     * @param from
     * @param to
     * @return average between two points
     */
    private static double[] averageBetween(double[] from, double[] to, int wFrom, int wTo) {
    	if(from.length != to.length)
    		return null;
    	
    	// Average = arithmetic mean
    	double[] average = new double[from.length];
    	for(int i = 0; i < from.length; i++)
    		average[i] = (from[i] * wFrom + to[i] * wTo) / (wFrom + wTo);
    	return average;
    }
    
    /**
     * Returns the index that contains minimum value in the list, except the case that index == indexNotInterested. 
     * @param list
     * @param indexNotInterested
     * @return index of minimum
     */
    private static int getMinDistIndex(ArrayList<Double> list, int indexNotInterested) {
    	double newDistMin = Double.MAX_VALUE;
		int newMinDistIndex = -1;
		
		for(int i = 0; i < list.size(); i++) {
			if(i != indexNotInterested && newDistMin > list.get(i)) {
				newDistMin = list.get(i);
				newMinDistIndex = i;
			}
		}
		
		return newMinDistIndex;
    }
    
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("usage: java Cluster <input_file_path> <number_of_clusters>");
            return;
        }

        String inputPath = args[0];
        int k = Integer.parseInt(args[1]);

        System.out.println("inputPath: " + args[0]);
        System.out.println("k: " + args[1]);
        
        // Load input file.
        try{
            File iFile = new File(inputPath);
            FileReader fReader = new FileReader(iFile);
            BufferedReader reader = new BufferedReader(fReader);
            String line = null;
            
            // Read meta-data line.
            if((line = reader.readLine()) != null) {
                String[] firstLine = line.split(" ");
                mN = Integer.parseInt(firstLine[0]);
                mD = Integer.parseInt(firstLine[1]);
            }

            mPoints = new ArrayList<double[]>();
            while((line = reader.readLine()) != null) {
                // Read a new mD-dimensional point.
            	double[] newPoint = new double[mD];
            	String[] splitLine = line.split(" ");
            	for(int i = 0; i < mD; i++)
            		newPoint[i] = Double.parseDouble(splitLine[i]);
            	// Add the new point to mPoints.
            	mPoints.add(newPoint);
            }
            
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        // Build and initialize mClusters, mMatrix, and mMinDist.
        mClusters = new ArrayList<double[]>(mPoints);
        mNumPointsInClusters = new ArrayList<Integer>();
        for(int i = 0; i < mClusters.size(); i++)
        	mNumPointsInClusters.add(1);
        mMatrix = new ArrayList<ArrayList<Double>>();
        mMinIndex = new ArrayList<Integer>();
        for(int i = 0; i < mN; i++) {
        	mMatrix.add(new ArrayList<Double>());
        	for(int j = 0; j < mN; j++)
        		mMatrix.get(i).add(distanceBetween(mClusters.get(i), mClusters.get(j)));
        	
        	int newMinDistIndex = getMinDistIndex(mMatrix.get(i), i);
        	
        	if(newMinDistIndex < 0) { System.out.println("ERROR: no newMinDistIndex"); System.exit(2); }
        	mMinIndex.add(newMinDistIndex);
        }
        
        // Find the minimum-distanced pair of points, and merge them. Repeat this until we reach to "k" clusters
        while(mClusters.size() != k) {
        	// Get the pair
	        int ith = -1;
	        int jth = -1;
	        double minDist = Double.MAX_VALUE;
	        for(int i = 0; i < mMinIndex.size(); i++) {
	        	int minForIthPoint = mMinIndex.get(i);
	        	double distIth = mMatrix.get(i).get(minForIthPoint);
	        	if(minDist > distIth) {
	        		ith = i;
	        		jth = minForIthPoint;
	        		minDist = distIth;
	        	}
	        }
	        
	        if(ith < 0 || jth < 0) { System.out.println("ERROR: negative ith or jth"); System.exit(1); }
	        
	        // Swap if necessary. Keep jth > ith to avoid index update problems.
	        if(ith > jth) {
	        	int temp = ith;
	        	ith = jth;
	        	jth = temp;
	        }
	        
	        // Update mClusters.
	        double[] average = averageBetween(mClusters.get(ith), mClusters.get(jth), mNumPointsInClusters.get(ith), mNumPointsInClusters.get(jth));
	        mClusters.set(ith, average);
			mNumPointsInClusters.set(ith, (mNumPointsInClusters.get(ith) + mNumPointsInClusters.get(jth)));
	        mClusters.remove(jth);
	        
	        // Update mMatrix first, and then mMinIndex.
	        mMatrix.remove(jth);
	        mMinIndex.remove(jth);
	        for(int ii = 0; ii < mMatrix.size(); ii++) {
	        	// mMatrix update
	        	mMatrix.get(ii).remove(jth);
	        	double distNow = distanceBetween(mClusters.get(ii), mClusters.get(ith));
        		mMatrix.get(ii).set(ith, distNow);
        		
        		// mMinIndex update. worst case is when mMinIndex[ii] == ith or jth, and distNow > distIth or distJth
        		int newMinDistIndex = getMinDistIndex(mMatrix.get(ii), ii);
        		mMinIndex.set(ii, newMinDistIndex);
	        }
        }
        
        // Print output
        for(int i = 0; i < mClusters.size(); i++) {
    		System.out.print(i + 1);
    		for(int j = 0; j < mD; j++)
    			System.out.print(" " + mClusters.get(i)[j]);
    		System.out.print("\n");
        }
    }
}
