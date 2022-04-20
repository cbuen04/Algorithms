import java.util.*;

/**
 * this class is performs a modified version of WFS to determine an optimal spot to place a
 * monster on a grid such that a player collects the least amount of treasure
 *
 * @author charly_bueno
 * @version 03/04/22
 */
public class PS5 {
    private static int row = 0;
    private static int col = 0;
    private static char[][] grid;
    private static int[] playerLocation;
    private static int[] bestSolution;
    private static int score = Integer.MAX_VALUE;

    public static void main(String[] args) {

        // must begin reading the data
        Scanner input = new Scanner(System.in);

        // collect dimensions of board
        row = input.nextInt();
        col = input.nextInt();

        //moves to the board
        input.nextLine();

        // initialize DS needed with correct dimensions
        grid = new char[row][col];
        boolean[][] visited = new boolean[row][col];
        playerLocation = new int[2];
        bestSolution = new int[3];

        // loading values into arrays
        for (int i = 0; i < row; i++) {
            String s = input.nextLine();
            for (int j = 0; j < col; j++) {
                grid[i][j] = s.charAt(j);
                // filling in spaces from the grid that cannot be visited
                if (s.charAt(j) == '#' || s.charAt(j) == 'm') {
                    visited[i][j] = true;
                }
                if (s.charAt(j) == 'p') {
                    playerLocation[0] = i;
                    playerLocation[1] = j;
                }
            }
        }

        // cycling through the board to see where the best monster location is
        for (int i = 1; i < row - 1; i++) {
            for (int j = 1; j < col - 1; j++) {
                if (!validMonsterLocation(i, j)) {
                    continue;
                }
                // store the previous spot type
                char temp = grid[i][j];

                // place a monster
                grid[i][j] = 'm';

                //perform the search
                treasureSearch(visited, i, j);

                // restore the board
                grid[i][j] = temp;

                // reset visited paths
                visited = new boolean[row][col];

            }
        }

        System.out.println(bestSolution[1] + " " + bestSolution[2]);
        System.out.println(bestSolution[0]);

    }

    /**
     * helper method that checks to see if a spot on the board is a valid spot to place a monster
     *
     * @param row - row coordinate
     * @param col - col coordinate
     * @return - bool indicating a valid spot
     */
    private static boolean validMonsterLocation(int row, int col) {
        // checks for walls and valid spots that aren't neighboring the player

        int[][] neighbors = getNeighbors(playerLocation[0], playerLocation[1]);
        if (grid[row][col] != '.') {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            int[] location = neighbors[i];
            if (location[0] == row && location[1] == col) {
                return false;
            }
        }

        return true;
    }

    /**
     * helper method that checks if player can explore that spot of the map
     *
     * @param row - row coordinate
     * @param col - col coordinate
     * @return - bool if player can move there or not
     */
    private static boolean validPlayerMove(int row, int col) {
        if (grid[row][col] != '#' && grid[row][col] != 'm') {
            return true;
        }
        return false;
    }

    /**
     * helper method that determines if a spot contains treasure
     *
     * @param row - row coordinate
     * @param col - col coordinate
     * @return - if treasure is found or not
     */
    private static boolean isANumber(int row, int col) {
        if (grid[row][col] != '#' && grid[row][col] != '.' && grid[row][col] != 'm' && grid[row][col] != 'p') {
            return true;
        }

        return false;
    }

    /**
     * helper method that retrieves the neighbors at a specific location.
     *
     * @param row - row coordinate
     * @param col - col coordinate
     * @return - a 2D array of neighboring spots
     */
    public static int[][] getNeighbors(int row, int col) {

        int[] leftOfPlayer = {row, col - 1};
        int[] rightOfPlayer = {row, col + 1};
        int[] abovePlayer = {row - 1, col};
        int[] belowPlayer = {row + 1, col};

        int[][] neighbors = {leftOfPlayer, rightOfPlayer, abovePlayer, belowPlayer};

        return neighbors;
    }

    /**
     * this is a helper method that checks if a player can smell a monster
     *
     * @param row - row coordinate of the player
     * @param col - col coordinate of the player
     * @return - bool indicating if a player smells a monster or not
     */
    private static boolean neighboringMonster(int row, int col) {
        int[][] neighbors = getNeighbors(row, col);
        for (int i = 0; i < 4; i++) {
            int[] location = neighbors[i];
            if (grid[location[0]][location[1]] == 'm') {
                return true;
            }
        }
        return false;
    }

    /**
     * this is a method that performs WFS modified to work for the treasure problem
     *
     * @param visited - a boolean array to mark visited spots
     * @param monRow  - the row coordinate of the monster
     * @param monCol  - the col coordinate of the monster
     */
    private static void treasureSearch(boolean[][] visited, int monRow, int monCol) {

        Stack<Integer> bag = new Stack<>();
        int currentScore = 0;

        //put the players starting location in the bag
        bag.push(playerLocation[0]);
        bag.push(playerLocation[1]);

        // do the search while we have nodes to visit
        while (!bag.isEmpty()) {
            //case where current run is worse than a previous solution
            if (currentScore > score) {
                break;
            }
            // player's current location
            int colCoordinate = bag.pop();
            int rowCoodinate = bag.pop();

            //if location has not been visited, and we are allowed to explore, explore it.
            if (!(visited[rowCoodinate][colCoordinate]) && validPlayerMove(rowCoodinate, colCoordinate)) {
                visited[rowCoodinate][colCoordinate] = true;
                //if player is on treasure, collect it.
                if (isANumber(rowCoodinate, colCoordinate)) {
                    String number = String.valueOf(grid[rowCoodinate][colCoordinate]);
                    currentScore += Integer.parseInt(number);

                }
                // if player doesn't smell a monster, further attempt to explore the area
                if (!neighboringMonster(rowCoodinate, colCoordinate)) {
                    int[][] neighbors = getNeighbors(rowCoodinate, colCoordinate);
                    for (int i = 0; i < 4; i++) {
                        int[] move = neighbors[i];
                        if (validPlayerMove(move[0], move[1])) {
                            bag.push(move[0]);
                            bag.push(move[1]);
                        }
                    }
                }


            }
        }
        // if the score we found is better than our previous best, update the score.
        if (currentScore < score) {
            score = currentScore;
            bestSolution[0] = score;
            bestSolution[1] = monRow;
            bestSolution[2] = monCol;
        }

    }

}
