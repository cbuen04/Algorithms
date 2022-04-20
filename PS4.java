import java.util.ArrayList;
import java.util.Scanner;

public class PS4 {
    private static int[][] productionCosts;
    private static int[] switchingCost;

    public static void main(String[] args){
        // must begin reading the data
        Scanner input = new Scanner(System.in);

        //retrieving lines and steps
        int lines = input.nextInt();
        int steps = input.nextInt();

        //initializing input data
        productionCosts = new int[lines][steps];
        switchingCost = new int[steps - 1];

        //loading data into array
        for(int i = 0; i < lines; i++){
            for(int j = 0; j < steps; j++){
                productionCosts[i][j] = input.nextInt();
            }
        }

        //populating switch costs
        for(int i = 0; i < steps -1; i++){
            switchingCost[i] = input.nextInt();
        }

        // solution arrays
        int[][] solutionTimesTable = new int[lines][steps];
        int[][] solutionPathTable = new int[lines][steps];

        //load last values into the solution as base case.
        for(int i = 0; i < lines; i++){
            solutionTimesTable[i][steps-1] = productionCosts[i][steps - 1];
            solutionPathTable[i][steps - 1] = i + 1;
        }

        //starts at the end of the array
        for(int i = (steps - 1); i > 0; i--){
            //and beginning element to fill solution table
            for(int j = 0; j < lines; j++){
                //internal comparisons for each element
                int min = Integer.MAX_VALUE;
                int path = 0;
                for(int k = 0; k < lines; k++){
                    int currentMin;
                    if((k+1) == solutionPathTable[j][i]){
                        //cost at i+1 and j is = to current cost plus next step cost
                        currentMin = productionCosts[k][i-1] + solutionTimesTable[j][i];

                    } else{
                        // case where production line is different and we need to account for switch cost
                        currentMin = productionCosts[k][i-1] + solutionTimesTable[j][i] + switchingCost[i-1];
                    }
                    // updating the min val to find best min
                    if(currentMin < min){
                        min = currentMin;
                        path = k + 1;
                    }
                }
                //populating solution table with answer
                solutionTimesTable[j][i-1] = min;
                solutionPathTable[j][i-1] = path;

            }
        }
        // gathers the smallest time from solution and row where that time was found
        int solutionTime = Integer.MAX_VALUE;
        int solutionRow = 0;
        for(int i = 0; i < lines; i++){
            int minTime = solutionTimesTable[i][0];
            if(minTime < solutionTime){
                solutionTime = minTime;
                solutionRow = i;
            }
        }

        //generates path solution
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < steps; i++){
            sb.append(solutionPathTable[solutionRow][i] + " ");
        }

        sb.deleteCharAt(sb.length() - 1);

        System.out.println(solutionTime);
        System.out.println(sb.toString());


    }
}
