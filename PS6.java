import java.util.*;

/**
 * This class creates a topological sorting of tasks that are needed to be completed in a
 * coop game. The console prints out the best way to accomplish these tasks to ensure both players
 * end up in the coop task when needed
 *
 * @author charly_bueno
 */

public class PS6 {

    private static HashMap<String, Set<String>> dgPlayer1 = new HashMap<>();
    private static HashMap<String, Set<String>> dgPlayer2 = new HashMap<>();
    private static HashMap<String, Set<String>>  joinedGraph = new HashMap<>();

    private static HashSet<String> joinedTasks = new HashSet<>();
    private static HashSet<String> activeTasks = new HashSet<>();

    private static boolean unsolvable = false;


    public static void main(String[] args){

        // read from the console
        Scanner input = new Scanner(System.in);
        int numOfP1Tasks = 0;
        int numOfP2Tasks = 0;
        int numOfSharedTasks = 0;

        numOfP1Tasks = input.nextInt();
        input.nextLine();

        for(int i = 0; i < numOfP1Tasks; i ++){
            // load player one tasks on hashmap
            String temp = input.nextLine();
            String[] kvPair = temp.split(" ");
            if(dgPlayer1.containsKey(kvPair[0])){
                Set<String> dependencies = dgPlayer1.get(kvPair[0]);
                dependencies.add(kvPair[1]);
                dgPlayer1.put(kvPair[0], dependencies);
            }
            else{
                Set<String> dependencies = new HashSet<>();
                dependencies.add(kvPair[1]);
                dgPlayer1.put(kvPair[0], dependencies);
            }
        }

        numOfP2Tasks = input.nextInt();
        input.nextLine();

        for(int i = 0; i < numOfP2Tasks; i ++){
            // load player two tasks on hashmap
            String temp = input.nextLine();
            String[] kvPair = temp.split(" ");
            if(dgPlayer2.containsKey(kvPair[0])){
                Set<String> dependencies = dgPlayer2.get(kvPair[0]);
                dependencies.add(kvPair[1]);
                dgPlayer2.put(kvPair[0], dependencies);
            }
            else{
                Set<String> dependencies = new HashSet<>();
                dependencies.add(kvPair[1]);
                dgPlayer2.put(kvPair[0], dependencies);
            }
        }

        numOfSharedTasks = input.nextInt();
        input.nextLine();

        for(int i = 0; i < numOfSharedTasks; i ++){
            // load shared tasks to hashset
            joinedTasks.add(input.nextLine());
        }

        //add elements from p1
        for(String key : dgPlayer1.keySet()){
            String newKey = key;
            Set<String> values = dgPlayer1.get(key);
            Set<String> newVals = new HashSet<>();

            if(!joinedTasks.contains(key)){
                newKey = newKey + "-1";
            }
            for(String value : values){
                String newValue = value;
                if(!joinedTasks.contains(value)){
                    newValue = value + "-1";
                }
                newVals.add(newValue);
            }
            joinedGraph.put(newKey, newVals);
        }

        for(String key : dgPlayer2.keySet()){
            String newKey = key;
            Set<String> values = dgPlayer2.get(key);
            Set<String> newVals = new HashSet<>();

            if(joinedTasks.contains(key)){
                //current dependents that are in a shared task
                Set<String> dependents = joinedGraph.get(key);
                for(String value : values){
                    String newValue = value;
                    // only change the name if the task is not a joined task
                    if(!joinedTasks.contains(value)){
                        newValue = value + "-2";
                    }
                    // add the task to the graph, if duplicate the set will not add twice
                    dependents.add(newValue);
                }
                //place the dependents back
                joinedGraph.put(key, dependents);
            }
            else{
                // player 2 specific quest
                newKey = key + "-2";

                for (String value : values) {
                    // change the name if not a joined task
                    if(!joinedTasks.contains(value)) {
                        value = value + "-2";
                    }
                    // add task to the set
                    newVals.add(value);
                }
                //add to new hashset
                joinedGraph.put(newKey, newVals);
            }

        }

        // topo sort
        TopoSortDriver();
    }

    private static void TopoSortDriver(){
        Stack<String> sortedOrder = new Stack<>();
        HashSet<String> visited = new HashSet<>();

        // While bag is not empty
        for (Map.Entry kvPair : joinedGraph.entrySet()) {
            if(unsolvable){
                break;
            }
            // if element is not in visited set
            // do dfs toposort ( visited, sortedOrder)
            String key = (String)kvPair.getKey();
            if(!visited.contains(key)){
                TopologicalSort(visited, sortedOrder, key);
            }
        }


        // print the outcome.
        if(unsolvable){
            System.out.println("Unsolvable");
        }
        else {
            while (!sortedOrder.isEmpty()) {
                System.out.println(sortedOrder.pop());
            }
        }

    }

    private static void TopologicalSort(HashSet<String> visited, Stack<String> sortedOrder, String key){
        //add current node to visited bag
        visited.add(key);
        activeTasks.add(key);

        // current

        //get all neighbors
        Set<String> dependents = joinedGraph.get(key);

        // if the current node has no neighbors, the set will be null, skip this step
        if(dependents != null) {
            for (String neighbor : dependents) {
                if(activeTasks.contains(neighbor)){
                    unsolvable = true;
                    return;
                }
                if (!visited.contains(neighbor)) {
                    TopologicalSort(visited, sortedOrder, neighbor);
                }
            }
        }

        activeTasks.remove(key);
        sortedOrder.push(key);
    }
}
