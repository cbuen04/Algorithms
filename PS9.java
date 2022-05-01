import java.util.*;

public class PS9 {

    private static int numOfVerts = 0;
    private static int maxFlow = 0;
    private static int flowStoT = 0; // should be max flow
    private static int capTtoS = 0; // should be max flow
    private static int saturatedEdges = 0;
    private static int[][] capacities;
    private static int[][] flow;
    private static Vertex[][] residualGraph;
    private static ArrayList<Integer> flowGained = new ArrayList<>();
    private static ArrayList<Integer> setS = new ArrayList<>();


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
                Vertex curr = new Vertex(i, j, cap);
                curr.setFlow(flow[i][j]);
                residualGraph[i][j] = curr;
            }
        }


        while(pathFromStoT(source, target)){
            for(int i = 0; i < numOfVerts; i ++){
                for(int j = 0; j < numOfVerts; j++){
                    residualGraph[i][j].prevParent = null;
                    residualGraph[i][j].pathLength = Integer.MAX_VALUE;
                }
            }
        }


        //Collections.sort(setS);
        for(int i = 0; i < flowGained.size()-1; i++){
            System.out.print(flowGained.get(i) + " ");
        }
        System.out.println(flowGained.get(flowGained.size()-1));
        System.out.println(maxFlow);
//        //A single integer indicating the number of saturated edges in the s-t maximum flow.
//        System.out.println(saturatedEdges);
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

        for(int i = 0; i < numOfVerts; i++){
            visited[i] = false;
        }
        //do a bfs using a priority queue where lighter weights are favored
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        for(int i = 0; i < numOfVerts; i++){
            Vertex start = residualGraph[source][i];
            start.prevParent = null; // set parent to null, this is our root
            pq.add(start);
        }
        visited[source] = true;

        Vertex pathVertex = null; // this vertex will hold the target path
        while(!pq.isEmpty()){
            Vertex current = pq.poll();

            int newWeight = 1; // initialize path length from source for root vertex with no parent
            if(current.prevParent != null){ // safety clause
                newWeight = current.prevParent.pathLength + 1;
            }

            if(current.pathLength > newWeight && current.feasibleFlow > 0 && !visited[current.dest]){

                current.pathLength = newWeight;

                if(current.dest == sink){ // if current is the sink we can break out of the loop
                    pathVertex = current;
                    visited[sink] = true;
                    break;
                }
                //TODO: maybe add a check to see if dest == sink and return early
                if(!visited[current.dest]){
                    for(int i = 0; i < numOfVerts; i++){
                        Vertex next = residualGraph[current.dest][i];
                        next.prevParent = current;
                        pq.add(next);
                    }

                    visited[current.dest] = true;
                }
            }
        }

        if(!visited[sink]){ // if dijkstras couldn't reach the sink then break out there is no more work that we can do
            for(int i = 0; i < numOfVerts; i++){
                if(visited[i]){
                    setS.add(i);
                    for(int j = 0; j < numOfVerts; j++){
                        if(residualGraph[i][j].feasibleFlow == 0 && residualGraph[i][j].capacity > 0){ // this is a saturated edge
                            saturatedEdges++;
                            flowStoT += residualGraph[i][j].flow;  // total flow across s to t
                            capTtoS += residualGraph[j][i].capacity;
                        }
                    }
                }
            }
            return false;
        }

        // keep track of path

        //update residual graph
        //pull the path
        ArrayList<Integer> finalShortestPath = new ArrayList<>();
        finalShortestPath.add(pathVertex.dest);

        while(pathVertex != null){
            if(cap > pathVertex.feasibleFlow){
                cap = pathVertex.feasibleFlow; // sets the maximum possible flow that can travel on this path
            }
            finalShortestPath.add(pathVertex.start);
            pathVertex = pathVertex.prevParent;
        }

        Collections.reverse(finalShortestPath);
        for(int i = 0; i < finalShortestPath.size()-1; i++){
                //add the capped flow to the graph switching both i,j and j,i in the resid graph
                int x = finalShortestPath.get(i);
                int y = finalShortestPath.get(i+1);
                residualGraph[x][y].setFlow(cap); //TODO: I think this works now
                residualGraph[y][x].setFlow(-cap);

        }

        flowGained.add(cap); // for line 1 of results
        maxFlow += cap; // for line 2


        return true;
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
        public int pathLength;
        public Vertex prevParent;
        private int capacity;
        private int flow;
        private int feasibleFlow;

        public Vertex(int s, int d, int c) {
            start = s;
            dest = d;
            capacity = feasibleFlow = c;
            pathLength = Integer.MAX_VALUE;
            flow = 0;

        }

        public void setFlow(int f) {
            flow += f;
            feasibleFlow = capacity - flow;
        }

        private int smallestParent(Vertex o){

            Vertex temp1 = prevParent;
            Vertex temp2 = o.prevParent;
            while(temp1 != null && temp2 != null){
                if(temp1.start < temp2.start){
                    return -1;
                }
                if(temp1.start > temp2.start){
                    return 1;
                }
                temp1 = temp1.prevParent;
                temp2 = temp2.prevParent;
            }
            return 0;
        }

        @Override
        public int compareTo(Vertex o) {

            //first rule, smallest path
            if (pathLength < o.pathLength) {
                return -1;
            }
            if (pathLength > o.pathLength) {
                return 1;
            }

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

            return smallestParent(o);
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

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(start + "->" + dest);
            Vertex parent = prevParent;
            while (parent != null){
                sb.insert(0, parent.start + "->");
                parent = parent.prevParent;
            }
            return sb.toString();
        }
    }
}
