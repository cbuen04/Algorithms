import java.util.*;


public class PS8 {

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);

        int segments = in.nextInt();
        int intersections = in.nextInt();
        int start = in.nextInt();
        int end = in.nextInt();

        HashMap<Integer, Set<Direction>> segmentIntersections = new HashMap<>();

        in.nextLine();
        for(int i = 0; i < intersections; i++){
            Direction currentdir = new Direction(in.nextLine());
            if(segmentIntersections.containsKey(currentdir.start)){
                Set<Direction> temp = segmentIntersections.get(currentdir.start);
                temp.add(currentdir);
            }
            else{
                Set<Direction> temp = new HashSet<>();
                temp.add(currentdir);
                segmentIntersections.put(currentdir.start, temp);
            }
        }

        List<Direction> results = shortestPath(segmentIntersections, segments, start, end);

        System.out.print(results.size());
        for(Direction dir : results){
            System.out.print(" " + dir);
        }
    }

    public static List<Direction> shortestPath(HashMap<Integer, Set<Direction>> intersectionList,
                                               int segments, int start, int end){

        Set<Integer> visited = new HashSet<>();
        HashMap<Integer, Direction> bestDirection = new HashMap<>(); // stores the best direction associated with path
        long[] shortestPath = new long[segments];
        int[] prevNode = new int[segments];

        //setting all spots to infinity
        for(int i = 0; i < segments; i++){
            shortestPath[i] = Long.MAX_VALUE;
            prevNode[i] = -1;
        }

        shortestPath[start] = 0; //initialize starting point path length
        PriorityQueue<Direction> pq = new PriorityQueue<>();
        pq.addAll(intersectionList.get(start)); //add the starting verts to the q
        visited.add(start); // mark it as visited after they are in the q

        while(!pq.isEmpty()){
            Direction current = pq.poll();
            long newWeight = shortestPath[current.start] + current.dirWeight;
            if (shortestPath[current.dest] > newWeight) {
                shortestPath[current.dest] = newWeight;
                prevNode[current.dest] = current.start;
                bestDirection.put(current.dest, current);
                if(!visited.contains(current.dest) && intersectionList.containsKey(current.dest)){
                    Set<Direction> temp = intersectionList.get(current.dest);
                    pq.addAll(temp);
                }
            }

        }

        ArrayList<Integer> pathGenerated = new ArrayList<>();
        generatePath(prevNode, end, pathGenerated);
        ArrayList<Direction> finalShortestPath = new ArrayList<>();

        for(int i = 1; i < pathGenerated.size(); i++){
            Direction current = bestDirection.get(pathGenerated.get(i));
            finalShortestPath.add(current);
        }

        return finalShortestPath;
    }

    public static void generatePath(int[] paths, int index, ArrayList<Integer> path){
        path.add(0,index);
        if(paths[index] == -1){
            return;
        }
        generatePath(paths, paths[index], path);
    }

    public static class Direction implements Comparable<Direction>{
        int start;
        int dest;
        int dirWeight;
        String direction;

        public Direction(String instruction){
            String[] instructions = instruction.split(" ");

            start = Integer.parseInt(instructions[0]);
            direction = instructions[1];
            dest = Integer.parseInt(instructions[2]);

            switch (direction){
                case "right":
                    dirWeight = 1;
                    break;
                case "straight":
                    dirWeight = Integer.MAX_VALUE/2;
                    break;
                case "left":
                    dirWeight = Integer.MAX_VALUE;
            }
        }


        @Override
        public int compareTo(Direction o) {

            if(this.dirWeight < o.dirWeight){
                return -1;
            }
            if(this.dirWeight > o.dirWeight){
                return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object o){

            if(o instanceof Direction){
                if(this.start == ((Direction) o).start && this.dest == ((Direction) o).dest
                        && this.dirWeight == ((Direction) o).dirWeight){
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString(){
            return direction;
        }
    }
}


