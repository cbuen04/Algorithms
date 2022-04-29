import java.util.*;

public class PS9 {

    private static int numOfVerts = 0;
    private static int maxFlow = 0;
    private static int flowStoT = 0; // should be max flow
    private static int capTtoS = 0; // should be max flow
    private static int[][] capacities;
    private static int[][] flow;
    private static Vertex[][] residualGraph;
    private static ArrayList<Integer> flowGained = new ArrayList<>();
    private static ArrayList<Integer> setS = new ArrayList<>();
    private static ArrayList<Integer> setT = new ArrayList<>();


    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        numOfVerts = in.nextInt();
        int source = in.nextInt();
        int target = in.nextInt();
        capacities = new int[numOfVerts][numOfVerts];
        flow = new int[numOfVerts][numOfVerts];

        // REMEMBER: residual[j][i] = capacity - flow
        //           residual[i][j] = flow (backwards hehe)
        residualGraph = new Vertex[numOfVerts][numOfVerts];



        // creates initial graphs
        for(int i = 0; i < numOfVerts; i++) {
            for (int j = 0; j < numOfVerts; j++) {
                int cap = in.nextInt(); // capacity of the graph
                capacities[i][j] = cap; // keeping track of og graph
                flow[j][i] = capacities[i][j]; // flow is the reverse of the capacity
            }
        }

        // creates residual graph
        for(int i = 0; i < numOfVerts; i++){
            for(int j = 0; j < numOfVerts; j++){
                int cap = Math.max(capacities[i][j], flow[i][j]);
                Vertex curr = new Vertex(i, null, -1 cap);
                curr.setFlow(flow[i][j]);
                residualGraph[i][j] = curr;
            }
        }

        while(pathFromStoT(source, target)){
            System.out.println("yes");
            if(maxFlow > 20){
                System.out.println(maxFlow);
            }
        }


//        Collections.sort(setS);
//        Collections.sort(setT);

        System.out.println(flowGained);
        System.out.println(maxFlow);
//        //A single integer indicating the number of saturated edges in the s-t maximum flow.
//        System.out.println(setS.size());
//        //A sorted (increasing) sequence of integers indicating the indices of the vertices in S in the s-t cut.
//        System.out.println(setS);
//        //A single integer indicating the total flow across the edges from S to T.  (Note that S and T are sets of vertices in the min-cut.)
//        System.out.println(flowStoT);
//        //A single integer indicating the total capacity across the edges from T to S.
//        System.out.println(capTtoS);
    }

    /**
     * checks residual graph if there is a path from s to t
     * @return
     */
    private static boolean pathFromStoT(int source, int sink){

        int cap = Integer.MAX_VALUE;
        boolean[] visited = new boolean[numOfVerts];
        int[] currentLength = new int[numOfVerts];
        int[] prevPath = new int[numOfVerts];

        for(int i = 0; i < numOfVerts; i++){
            currentLength[i] = Integer.MAX_VALUE;
            visited[i] = false;
        }
        //do a bfs using a priority queue where lighter weights are favored
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        for(int i = 0; i < numOfVerts; i++){
            Vertex start = residualGraph[source][i];
            pq.add(start);
        }
        visited[source] = true;
        currentLength[source] = 0;
        prevPath[source] = -1;


        while(!pq.isEmpty()){
            Vertex current = pq.poll();
            int newWeight = currentLength[current.start] + 1;
            if(currentLength[current.dest] > newWeight && current.feasibleFlow > 0){
                if(cap > current.feasibleFlow){
                    cap = current.feasibleFlow; // sets the maximum possible flow that can travel on this path

                }
                currentLength[current.dest] = newWeight;
                prevPath[current.dest] = current.start;
                //TODO: maybe add a check to see if dest == sink and return early

                if(!visited[current.dest]){
                    for(int i = 0; i < numOfVerts; i++){
                        Vertex next = residualGraph[current.dest][i];
                        pq.add(next);
                    }
                    visited[current.dest] = true;
                }
            }
        }

        if(!visited[sink]){ // if dijkstras couldn't reach the sink then break out there is no more work that we can do
            return false;
        }

        // keep track of path

        //update residual graph
        //pull the path
        ArrayList<Integer> finalShortestPath = new ArrayList<>();
        generatePath(prevPath, sink, finalShortestPath);

        Collections.reverse(finalShortestPath);
        for(int i = 0; i < finalShortestPath.size()-1; i++){
            for(int j = 1; j < finalShortestPath.size(); j++){
                //add the capped flow to the graph switching both i,j and j,i in the resid graph
                int x = finalShortestPath.get(i);
                int y = finalShortestPath.get(j);
                residualGraph[x][y].setFlow(cap + residualGraph[x][y].flow); //TODO: logic sound but could be wrong debug
                residualGraph[y][x].setFlow(residualGraph[y][x].flow - cap);
            }
        }

        flowGained.add(cap); // for line 1 of results
        maxFlow += cap; // for line 2


        return true;
    }

    public static void generatePath(int[] paths, int index, ArrayList<Integer> path){
        path.add(0,index);
        if(paths[index] == -1){
            return;
        }
        generatePath(paths, paths[index], path);
    }

    public static void StoTCut(){
        //search all verts for saturated edges

        //all saturated edges are part of S - T cut

        // S verts are saturated flow = cap i -> j

        //T edges are avoided flow = 0  j -> i

        //add verts in ascending order in two lists S and T

        // count of S and T should be equal, this number will be number of saturated edges
    }

    public static class Vertex implements Comparable<Vertex> {


        private int start;
        private int dest;

        private int vertexName;
        public int pathLength;
        public Vertex prevParent;
        private int capacity;
        private int flow;
        private int feasibleFlow;

        public Vertex(int vertex, int c) {
            start = 0;
            dest = 0;
            vertexName = vertex;
            capacity = feasibleFlow = c;
            pathLength = -1;
            flow = 0;

        }

        public void setFlow(int f) {
            flow += f;
            feasibleFlow = capacity - flow;
        }

//        private int smallestParent(Vertex o){
//            //TODO: remove this
//            System.out.println("confirmed this was used");
//            Vertex temp1 = prevParent;
//            Vertex temp2 = o.prevParent;
//            while(temp1 != null && temp2 != null){
//                if(temp1.start < temp2.start){
//                    return -1;
//                }
//                if(temp1.start > temp2.start){
//                    return 1;
//                }
//                temp1 = temp1.prevParent;
//                temp2 = temp2.prevParent;
//            }
//            return 0;
//        }

        @Override
        public int compareTo(Vertex o) {

            //first rule, smallest path
//            if (pathLength < o.pathLength) {
//                return -1;
//            }
//            if (pathLength > o.pathLength) {
//                return 1;
//            }

            // second rule, largest feasible flow
            if (feasibleFlow > o.feasibleFlow) {
                return -1;
            }
            if (feasibleFlow < o.feasibleFlow) {
                return 1;
            }

            //third rule smallest vertex number
            if (start < o.start) {
                return -1;
            }
            if (start > o.start) {
                return 1;
            }

            //TODO: remove debug
           // System.out.println("smallest Parent used");
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Vertex) {
                if (start == ((Vertex) o).start && dest == ((Vertex) o).dest){
                    return true;
                }
            }
            return false;
        }
    }
}
