import java.util.*;
import java.util.Scanner;

public class PS7 {

    private static int totalWeight;
    private static List<Edge> edgesMST = new ArrayList<>();
    private static int[] components;
    private static int vertexCount;

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);

        int seed = in.nextInt();
        vertexCount = in.nextInt();
        int minWeight = in.nextInt();
        int maxWeight = in.nextInt();
        int connectivity = in.nextInt();
        int startingVertex = 0;
        String algorithm = in.next();

        if(algorithm.equals("Jarnik")){
            startingVertex = in.nextInt();
        }

        int[][] forestGraph = generateWeights(seed, vertexCount, minWeight, maxWeight, connectivity);

        switch (algorithm){
            case "Jarnik":
                JarnikMST(forestGraph, startingVertex);
                break;

            case "Kruskal":
                KruskalMST(forestGraph);
                break;

            case "Boruvka":
                BoruvkaMST(forestGraph);
                break;
        }

        System.out.println(totalWeight);
        System.out.println(edgesMST.size());

        for(Edge edgeConnection : edgesMST){
            System.out.println(edgeConnection.toString());
        }

    }

    private static void JarnikMST(int[][] graph, int currentVertex){
        HashSet<Integer> tree = new HashSet<>();
        Queue<Edge> q = new PriorityQueue<>();

        tree.add(currentVertex);

        q.addAll(getEdges(graph, currentVertex));

        while(!q.isEmpty()){
            Edge current = q.poll();

            if(!tree.contains(current.v)){
                tree.add(current.v);
                edgesMST.add(current);
                totalWeight += current.weight;
                q.addAll(getEdges(graph, current.v));
            }
            else if(!tree.contains(current.u)){
                tree.add(current.u);
                edgesMST.add(current);
                totalWeight += current.weight;
                q.addAll(getEdges(graph, current.u));
            }
        }

    }

    private static void KruskalMST(int[][] graph){
        HashMap<Integer, Set<Integer>> graphComponents = new HashMap<>();
        PriorityQueue<Edge> q = new PriorityQueue<>();

        //add all edges to the priority queue
        for (int i = 0; i < graph.length; i ++){
            int[] current = graph[i];
            for(int j = 0; j < i; j++){
                if(current[j] == 0){
                    continue;
                }
                Edge validEdge = new Edge(i,j, current[j]);
                q.add(validEdge);

            }
        }

        while(!q.isEmpty()){
            Edge current = q.poll();
            if(isConnected(current, graphComponents)){
                continue;
            }
            unionSets(current, graphComponents);
        }

    }

    private static boolean isConnected(Edge e, HashMap<Integer, Set<Integer>> components){
        // first run through where the vertex isn't in the map yet
        if(!components.containsKey(e.u)){
            HashSet<Integer> cleanSet = new HashSet<>();
            cleanSet.add(e.u);
            components.put(e.u, cleanSet);
        }
        if(!components.containsKey(e.v)){
            HashSet<Integer> cleanSet = new HashSet<>();
            cleanSet.add(e.v);
            components.put(e.v, cleanSet);
            return false; // cut to save time if val doesn't exist in either set, return early.
        }

        Set<Integer> s1 = components.get(e.u);
        Set<Integer> s2 = components.get(e.v);

       // if either set contains each other, then the edge is a useless edge
        return s1.contains(e.v) || s2.contains(e.u);
    }

    private static void unionSets(Edge e, HashMap<Integer, Set<Integer>> components){

        Set<Integer> s1 = components.get(e.u);
        Set<Integer> s2 = components.get(e.v);

        //union sets
        s1.addAll(s2);

        for(Integer i : s1){
            //update all sets of the vertexes which appears in the set
            components.put(i, s1);
        }
        totalWeight += e.weight;
        edgesMST.add(e);

    }

    private static void BoruvkaMST(int[][] graph){
        HashMap<Integer, Set<Integer>> graphComponents = new HashMap<>();
        ArrayList<Edge> edges = new ArrayList<>();
        components = new int[vertexCount];


        // add all the edges from the graph to an arraylist bruhhh
        for (int i = 0; i < vertexCount; i ++){
            //create hashmap of graph connections
            HashSet<Integer> temp = new HashSet<>();
            temp.add(i);
            graphComponents.put(i, temp);

            int[] current = graph[i];

            for(int j = i; j < vertexCount; j++){
                if(current[j] == 0){
                    continue;
                }
                Edge validEdge = new Edge(i,j, current[j]);
                edges.add(validEdge);

            }
        }

        int count = countAndLabel(graph, graphComponents);

        while(count > 1){
            addAllSafeEdges(count, edges, graphComponents);
            count = countAndLabel(graph, graphComponents);
        }
    }

    private static void addAllSafeEdges(int count, List<Edge> edges, HashMap<Integer,Set<Integer>> gc){
        Edge[] safeEdge = new Edge[count];

        for(int i = 0 ; i < count; i++){
            safeEdge[i] = null;
        }

        boolean[][] added = new boolean[vertexCount][vertexCount];

        for(int i = 0 ; i < vertexCount; i++){
            for(int j = 0 ; j < vertexCount; j++){
                added[i][j] = false;
            }
        }

        for (Edge e : edges){
            if(components[e.v] != components[e.u]){
                // if there is no safe edge currently, or there is a better weight, update the safest edge
                if(safeEdge[components[e.v]] == null || e.weight < safeEdge[components[e.v]].weight){
                    safeEdge[components[e.v]] = e;

                }
                // if there is no safe edge currently, or there is a better weight, update the safest edge
                if(safeEdge[components[e.u]] == null || e.weight < safeEdge[components[e.u]].weight){
                    safeEdge[components[e.u]] = e;
                }
            }
        }

        for(int i = 0; i < count; i++){
            Edge e = safeEdge[i];

            Set<Integer> s1 = gc.get(e.v);
            Set<Integer> s2 = gc.get(e.u);

            s1.addAll(s2);

            gc.put(e.v, s1);
            gc.put(e.u, s1);

            if(!added[e.v][e.u] || !added[e.u][e.v]){
                added[e.v][e.u] = true;
                added[e.u][e.v] = true;
                edgesMST.add(e);
                totalWeight += e.weight;
            }
            edges.remove(e);
        }

    }

    private static int countAndLabel(int[][] graph, HashMap<Integer, Set<Integer>> GC){
        int count = 0;
        boolean[] marked = new boolean[vertexCount];

        for(int i = 0; i < marked.length; i++){
            marked[i] = false;
        }

        for(int i = 0; i < marked.length; i++){
            if(marked[i] == false){
                labelOne(i, count, marked, GC);
                count++;
            }
        }

        return count;
    }

    private static void labelOne(int v, int count, boolean[] marked, HashMap<Integer, Set<Integer>> GC){
        Set<Integer> vertecies = GC.get(v);
        for(int u : vertecies){
            if(marked[u] == false){
                marked[u] = true;
                components[u] = count;
                labelOne(u, count, marked, GC);
            }
        }
    }

    private static List<Edge> getEdges(int[][] graph, int v){
        // grabbing all vals at that col
        List<Edge> listOfEdges = new ArrayList<>();
        int[] edges = graph[v];
        Edge currentEdge;
        for(int i = 0; i < edges.length; i++){
            if(edges[i] == 0){
                continue;
            }
            currentEdge = new Edge(v, i, edges[i]);
            listOfEdges.add(currentEdge);
        }

        return listOfEdges;
    }

    protected static class Edge implements Comparable<Edge>{
        private int v;
        private int u;
        private int weight;

        public Edge(int v, int u, int weight){
            this.v = v;
            this.u = u;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge o) {

            if(this.weight < o.weight){
                return -1;
            }
            if(this.weight > o.weight){
                return 1;
            }
            if(Math.min(v,u) < Math.min(o.v, o.u)){
                return -1;
            }
            if(Math.min(v,u) > Math.min(o.v, o.u)){
                return 1;
            }
            if(Math.max(v,u) < Math.max(o.v, o.u)){
                return -1;
            }
            if(Math.max(v,u) > Math.max(o.v, o.u)){
                return 1;
            }
            return 0;

        }

        @Override
        public boolean equals(Object o){

            if(o instanceof Edge){

                if(this.v == ((Edge) o).v && this.u == ((Edge) o).u)
                    return true;

                if(this.v == ((Edge) o).u && this.u == ((Edge) o).v)
                    return true;

            }
            return false;
        }

        @Override
        public String toString() {
           // return "V:" + v + " U:" + u + " Weight:" + weight;
            return v + " " + u;
        }
    }


    /**
     * Generates a connected, undirected, weighted graph.  Note that
     * the result is a symmetric adjacency matrix where each value
     * represents an edge weight.  (An edge weight of 0 represents
     * a non-edge.)
     *
     * Note that the arrays are zero-based, so vertices are numbered
     * [0..vertexCount).
     *
     * The connectivity parameter specifies how many times a random
     * spanning tree should be added to the graph.  Note that a
     * value greater than 1 will probably result in a cycle, but it
     * is not guaranteed (especially for tiny graphs)
     *
     * For language independence, the random number generation is
     * done using a linear feedback shift register with a cycle
     * length of 2^31-1.  (Bits 27 and 30 are xor'd and fed back.)
     * (Note:  2^31-1 is prime which is useful when generating
     * pairs, triples, or other multi-valued sequences.  The
     * pattern won't repeat until after 2^31-1 pairs, triples, etc.
     * are generated.)
     *
     * Finally, the runtime of this generation is O(v) in connectivity,
     * or k*v*connectivity.
     *
     * @param seed  any positive int
     * @param vertexCount any int greater than 1
     * @param minWeight   any positive int
     * @param maxWeight   any int greater than minWeight
     * @param connectivity the overall connectedness of the graph, min 1
     * @return the weighted adjacency matrix for the graph
     */
    public static int[][] generateWeights (int seed, int vertexCount, int
            minWeight, int maxWeight, int connectivity)  // Non-zero seed, cap vertices at 100, weights at 10000
    {
        int[][] weights = new int[vertexCount][vertexCount];
        for (int pass = 0; pass < connectivity; pass++)
        {
            List<Integer> connected = new ArrayList<Integer>();
            List<Integer> unused    = new ArrayList<Integer>();
            connected.add(0);
            for (int vertex = 1; vertex < vertexCount; vertex++)
                unused.add(vertex);
            while (unused.size() > 0)
            {
                seed = (((seed ^ (seed >> 3)) >> 12) & 0xffff) | ((seed &
                        0x7fff) << 16);
                int weight = seed % (maxWeight-minWeight+1) + minWeight;
                seed = (((seed ^ (seed >> 3)) >> 12) & 0xffff) | ((seed &
                        0x7fff) << 16);
                Integer fromVertex = connected.get(seed %
                        connected.size());
                seed = (((seed ^ (seed >> 3)) >> 12) & 0xffff) | ((seed &
                        0x7fff) << 16);
                Integer toVertex   = unused.get(seed % unused.size());
                weights[fromVertex][toVertex] = weight;
                weights[toVertex][fromVertex] = weight;  // Undirected
                connected.add(toVertex);
                unused.remove(toVertex);  // Note -- overloaded, remove element Integer, not position int
            }
        }
        return weights;
    }
}
