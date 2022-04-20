import java.util.HashMap;
import java.util.Scanner;

/**
 * this class takes in a data set of solar activity
 * assuming there is a single max and min this class can find
 * the minimum value in the data set with respect to the constraint.
 * of costly queries
 *
 * @author charly_bueno
 */

public class PS2 {

    private static int arrSize = 0;
    private static int ans = 0;
    private static int val = 0;
    private static HashMap<Integer,Integer> queries = new HashMap<>();

    public static void main(String[] args){

        // must begin reading the data
        Scanner input = new Scanner(System.in);

        // this takes in the size of the array
        arrSize = input.nextInt();

        //start the recursive search for the minimum
        int min = callSolarMin();

        // DEBUG PURPOSES
        //System.out.println("minimum index " + min + " val: " + val);

        System.out.println("minimum " + min);

    }

    /**
     * this is the driver method that starts the recursion call to
     * finding the solar minimum
     * @return the minimum in the dataset
     */
    public static int callSolarMin(){

        //driver for the recursive call for the solar array. 0-based indexing requires arrSize-1
        return solarMin(0, arrSize-1);
    }

    /**
     * this method is a recursive call that
     * @param left
     * @param right
     * @return the minimum in the data set
     */
    public static int solarMin(int left, int right){

        //base case when array is size 2
        if(right == (left + 1) % (arrSize - 1)){
            int l = queries.get(left);
            int r = queries.get(right);

            if(l < r){
                ans = left;
                val = l;
            }
            else{
                ans = right;
                val = r;
            }
        }
        else{
            int mid = (left + right) / 2;
            //call a query once by placing in a hashmap.
            int lQuery = 0;
            int mQuery = 0;
            int m1Query = 0;
            int rQuery = 0;

            // these lines check the hashmap for the queries before requesting a new query
            if(queries.containsKey(left)) {
                lQuery = queries.get(left);
            } else{
                lQuery = query(left);
                queries.put(left, lQuery);
            }

            if(queries.containsKey(mid)) {

                mQuery = queries.get(mid);
            } else{
                mQuery = query(mid);
                queries.put(mid, mQuery);
            }

            if(queries.containsKey(mid + 1)) {

                m1Query = queries.get(mid + 1);
            } else{
                m1Query = query(mid + 1);
                queries.put(mid + 1, m1Query);
            }

            if(queries.containsKey(right)){
                rQuery = queries.get(right);
            } else{
                rQuery = query(right);
                queries.put(right, rQuery);
            }

            //case where left is the minimum of both right and mid, making it the half of the array
            // containing the minimum value
            if(lQuery < rQuery && lQuery <= mQuery){
                solarMin(left, mid);
                //case where right is the minimum of both left and mid, making it the half of the array
                // containing the minimum value
            } else if(rQuery < lQuery && rQuery <= mQuery){
                solarMin(mid, right);
                //case where first two cases were inconlusive, the code samples neighboring points to
                //determine a trend letting us know where the minimum is.
            }else if(mQuery < m1Query){
                solarMin(left, mid);
            }else if (m1Query + 1 < mQuery){
                solarMin(mid + 1, right);
            }

        }

        return ans;
    }

    /**
     * this is a helper method that takes in an index that needs to be queried
     * and automates the command.
     * @param index
     * @return the value at the queried index
     */
    public static int query(int index){
        Scanner in = new Scanner(System.in);
        System.out.println("query " + index);

        return in.nextInt();
    }
}
