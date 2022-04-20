
import java.util.*;

public class PS3 {
    private static int dest = 0;
    private static int range = 0;
    private static long bestShopLocations = 0;
    private static long[] islands;
    private static int bestSolutionCount = 0;
    private static long completeSolution = 0;

    public static void main(String[] args){

        // must begin reading the data
        Scanner input = new Scanner(System.in);

        //load initial value first number is range, second is connections (aka dest)
        //restriction that first value has to be in range of 3 < x < 36
        range = input.nextInt();
        dest = input.nextInt();

        completeSolution = (1L << range) - 1;
        bestShopLocations = completeSolution;

        // used to place each island and their connections
        islands = new long[range];

        //set solution as the worst case, shop on every island
        bestSolutionCount = range;

        //create a for loop that will keep accepting inputs from console 0 - dest
        for(int i = 0; i < dest; i++) {
            //read the two numbers [n,m]
            int a = input.nextInt();
            int b = input.nextInt();

            islands[a - 1] = updateBitmask(b - 1, islands[a - 1]);
            islands[b - 1] = updateBitmask(a - 1, islands[b - 1]);

            if(i < range){
                islands[i] = updateBitmask(i, islands[i]);
            }
        }

        if(dest < range){
            for(int i = dest; i < range; i++){
                islands[i] = updateBitmask(i, islands[i]);
            }
        }


        long currSolution = 0;

        for(int i = 0; i < islands.length; i++){
            long temp = 0;
            temp = updateBitmask(i, temp);

            if((islands[i] | temp) == temp){
                currSolution = updateBitmask(i, currSolution);
            }
        }

        // call driver for backtracking recursive fn
        List<Integer> shops = generateListOfShops(callCalcNumOfShops(currSolution));
        // print the result
        System.out.println(shops.size());
        //figure out how to print correctly, might need to create helper method.
        System.out.println(printVals(shops));
    }

    public static long callCalcNumOfShops(long currSolution){
        //start on the first island, and pass a new list
        return calcNumOfShops(currSolution, currSolution, 0, 0);
    }

    public static long calcNumOfShops(long currSolution, long shopLocations, int currIslandIndex, int currNumOfStores){
        //base case starts first

        //if bitmask is all 1's && best ans
        if(((currSolution & completeSolution) == completeSolution) && currNumOfStores <= bestSolutionCount){
            bestShopLocations = shopLocations;
            bestSolutionCount = currNumOfStores;
            return bestShopLocations;
        }
//        //if curr island is greater than list of needed to be hit islands, on zero based indexing
//        if(currIslandIndex >= islands.length){
//            return bestShopLocations;
//        }
        //if best ans != currentAns
            //return null;
        if(currNumOfStores > bestSolutionCount || currIslandIndex >= islands.length){
            return bestShopLocations;
        }

        long island = islands[currIslandIndex];

        //check that island hasn't been accounted for yet. if the island has no connections
        //then it HAS to be accounted in the solution
        long tempSoln = (island | currSolution);
        int tempNumStores = currNumOfStores+1;
        long tempShopLocations = updateBitmask(currIslandIndex, shopLocations);

        //tempShopLocations = updateBitmask(currIslandIndex, shopLocations);
        //tempSoln = (island | tempSoln);
        //tempNumStores++;

        if(!((currSolution | island) == currSolution)) {
            //recurse 2 calls one that has the copy of the updated list and one that has the original untouched list
            calcNumOfShops(tempSoln, tempShopLocations, currIslandIndex + 1, tempNumStores);
        }
        calcNumOfShops(currSolution, shopLocations, currIslandIndex+1, currNumOfStores);

       return bestShopLocations;
    }

    public static long updateBitmask(int shamt, long bitMask){

       return bitMask = bitMask | (1L << shamt);
    }

    public static List<Integer> generateListOfShops(long bitMask){
        List<Integer> islands = new ArrayList<>();
        StringBuilder solutionString = new StringBuilder();
        for(int i = 0; i < range; i++){
            solutionString.append((bitMask >> i) & 1);
        }
        for(int i = 0; i < solutionString.length(); i++){
            if(solutionString.charAt(i) == '1'){
                islands.add(i+1);
            }
        }
        return islands;
    }

    public static String printVals(List<Integer> L){
        StringBuilder sb = new StringBuilder();
        Collections.sort(L);

        if(L.isEmpty()){
            return "empty";
        }

        for(int i = 0; i < (L.size()-1); i++){
            sb.append(L.get(i) + " ");
        }
        sb.append(L.get(L.size()-1));

        return sb.toString();
    }
}
