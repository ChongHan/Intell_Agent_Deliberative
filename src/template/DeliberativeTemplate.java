package template;

/* import table */
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import logist.plan.Action;
import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.lang.reflect.Array;
import java.util.*;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
    private static int EMPTY_LOAD = 0;
	
	/* Environment */
	Topology topology;
	TaskDistribution td;
	
	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;

    /*Print flag*/
    boolean print = false;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;
		
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		
		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
		
		// ...
	}

    //@Override
    public Plan FirstPlan(Vehicle vehicle, TaskSet tasks) {
        Plan plan;
        System.out.println("calculating new Plan! " + vehicle.toString());
        System.out.println(tasks.toString());
        // Compute the plan with the selected algorithm.
        switch (algorithm) {
            case ASTAR:
                // ...
                plan = naivePlan(vehicle, tasks);
                break;
            case BFS:
                // ...
                plan = naivePlan(vehicle, tasks);
                break;
            default:
                throw new AssertionError("Should not happen.");
        }
        System.out.println(plan.toString());
        return plan;
    }

	public Plan plan(Vehicle vehicle, TaskSet tasks) {
        System.out.println("Calculating new Plan for " + vehicle.name());
        //Initialisation of variables
        List<Task> completeTasks = new LinkedList<>(tasks);
        if (!vehicle.getCurrentTasks().isEmpty())
            {completeTasks.addAll(vehicle.getCurrentTasks());}
        State initialState = createInitialState(vehicle,tasks);
        List<State> finalStates = createFinalStateList(vehicle,completeTasks);
        Node n;
        City origin = vehicle.getCurrentCity();

        // node list Q of the algorithm
        List<Node> nodesToVisit = new LinkedList<>();
        // node list C of the algorithm
        List<Node> nodesVisited = new LinkedList<>();

        //add initial node to the node list Q
        Node initialNode = new Node (initialState, null, vehicle.getCurrentTasks(),
                                    vehicleLoad(vehicle), vehicle.capacity(), vehicle.costPerKm());
        nodesToVisit.add(initialNode);
        int i = 0; //loop counter
        long startTime = System.currentTimeMillis();

        while(true) {
            i++;
            if (print) {
                System.out.println("Iteration " + i +
                        " -----------------------------------------------------------------------------------------------");
            }
            if (nodesToVisit.isEmpty()) {
                throw new AssertionError("List of nodes to be visited is empty!");
            }

            //get first element of the list and remove it
            n = nodesToVisit.get(0);
            nodesToVisit.remove(0);
            if (print) {
                System.out.println(n.toString());
            }
            //check if it is a final state
            if (reachedFinalState(n)) {
                long finishTime = System.currentTimeMillis();
                long duration = finishTime - startTime;
                System.out.println("After " + i + " iterations, and " + duration + "ms, we got the following plan:");
                Plan plan =  new Plan(origin, n.getPlan());
                System.out.println(plan.toString());
                print = false;
                return plan;
            }

            switch (algorithm) {
                case ASTAR:
                    if (!stateHasBeenVisited(nodesVisited, n) || listContainsNodeWithBiggerCost(nodesVisited, n)) {
                        //Delete entry in the list
                        for (Node node : nodesVisited) {
                            if (node.getCurrentState().equals(n.getCurrentState())) {
                                nodesVisited.remove(node);
                                break;
                            }
                        }
                        nodesVisited.add(n);
                        List<Node> successors = findSuccessors(n, completeTasks);
                        for (Node node : successors)
                        {
                            node.updateG(n);
                        }
                        nodesToVisit.addAll(successors);
                        Collections.sort(nodesToVisit);
                    }
                    break;
                case BFS:
                    if (!stateHasBeenVisited(nodesVisited, n)) {
                        nodesVisited.add(n);
                        List<Node> successors = findSuccessors(n, completeTasks);
                        nodesToVisit.addAll(successors);
                    } else {
                        if (print) {
                            System.out.println("State visited previously!");
                        }
                    }
                    break;
                default:
                    throw new AssertionError("Should not happen.");
            }
        }
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	@Override
	public void planCancelled(TaskSet carriedTasks) {
		
		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
		}
	}

    /**
     * Creates a list of final states for comparison.
     * @param tasks is the set of tasks on the environment
     * @return a linked list of State object corresponding to all the possible final states.
     */
    private LinkedList<State> createFinalStateList(Vehicle vehicle, List<Task> tasks){
        LinkedList<State> finalStates = new LinkedList<>();
        Hashtable<Task, City> tasksPosition = new Hashtable<>();
        //Create the hash table of the final destinations of all tasks
        for (Task task : tasks) {
            tasksPosition.put(task,task.deliveryCity);
        }
        //create a list of all possible final states. Variable: agent location.
        for (City city: topology)
            finalStates.add(new State (city, tasksPosition));

        return finalStates;
    }

    /**
     * Creates the initial state of the simulation
     * @param vehicle is the vehicle for which the inital state is computed
     * @param tasks is the set of tasks on the environment
     * @return a State object corresponding to the initial state of the simulation.
     */
    private State createInitialState(Vehicle vehicle, TaskSet tasks){
        Hashtable<Task, City> tasksPosition = new Hashtable<>();
        //create the hash table of the initial positions of all tasks
        for (Task task : tasks) {
            tasksPosition.put(task,task.pickupCity);
        }
        if (!vehicle.getCurrentTasks().isEmpty()){
            for (Task task : vehicle.getCurrentTasks()){
                tasksPosition.put(task,vehicle.getCurrentCity());
            }
        }

        return new State (vehicle.getCurrentCity(), tasksPosition);
    }

    /**
     * Coalculate the load of the vehicle according to its list of tasks that the vehicle is carrying.
     * @param vehicle vehicle object containing the list of current tasks.
     * @return the load of the vehicle.
     */
    private double vehicleLoad (Vehicle vehicle){
        double load =0;
        if (!vehicle.getCurrentTasks().isEmpty()){
            for (Task task : vehicle.getCurrentTasks()){
                load += task.weight;
            }
        }
        return load;
    }

    /**
     * Looks for all reachable nodes from the node given as argument. The function will create all the
     * nodes with the relevant information and aggregate them in a list that will be returned.
     * @param n : the node we are at in the graph
     * @return a list of nodes reachable from node n
     */
    private LinkedList<Node> findSuccessors (Node n, List<Task> tasks){
        if (print) {
            System.out.println("Looking for successors");
        }
        //Create List to return
        LinkedList<Node> successors = new LinkedList();
        // Get relevant information from the node
        City agentCurrentCity = n.getCurrentState().getAgentPosition();
        List<City> neighbors = agentCurrentCity.neighbors();

        // Check for tasks that can be picked up at the current location.
        // Each task can be picked up if its location in the current state is equal to its pickup city
        // and if the agent is not full yet.
        int counter = 0;
        List<Task> taskToDeliver = new LinkedList<>();
        for (Task task : tasks){
            if (task.pickupCity.equals(agentCurrentCity) && !n.getCarriedTasks().contains(task)){
                counter++;
                taskToDeliver.add(task);
            }
        }
        /*
        System.out.println("Breakpoints: " + counter);
         */

        // For each pickup task available, the number of states is multiplied by 2.
        // In the following section, we create all possible states by iterating over the neighbors and over the
        // possible amount of states for each move to a neighboring city.

        //For each possible neighboring city create the states and its corresponding hash table.
        for (City neighbor: neighbors){
            int possibleStates = (int) Math.pow(2,counter);
            for (int j=0; j<possibleStates; j++){
                // Create the corresponding hash table for this state.
                Hashtable<Task,City> newTaskPositions = new Hashtable<>(n.getCurrentState().getTasksPosition());
                ArrayList<Action> newPlan = new ArrayList<>(n.getPlan());
                ArrayList<Task> carriedTasks = new ArrayList<>(n.getCarriedTasks());
                double newLoad = n.getLoad();

                for (Task task : tasks){
                    City taskPosition = newTaskPositions.get(task);
                    if (taskPosition.equals(agentCurrentCity)){
                        if(carriedTasks.contains(task)){
                            // The package is in transit, continue the transit.
                            newTaskPositions.replace(task, neighbor);
                        }
                        else if (taskPosition.equals(task.pickupCity)){
                            // This if condition corresponds to the tree of all possible states.
                            // More explanations available in the report.
                            int separator = (int) Math.pow(2,taskToDeliver.indexOf(task));
                            if (j%(2*separator)<separator){
                                //Take it!
                                newTaskPositions.replace(task, neighbor);
                                newPlan.add(new Action.Pickup(task));
                                newLoad+=task.weight;
                                carriedTasks.add(task);
                            }
                            // else leave it there.
                        }
                    }
                }
                /*
                String string = new String();
                for ( Task task : newTaskPositions.keySet()){
                    string += ("Task " + task.id + " is in " + newTaskPositions.get(task) + "\n");
                }
                System.out.println(string);
                */
                newPlan.add(new Action.Move(neighbor));
                State nextState = new State(neighbor, newTaskPositions);
                if (newLoad >= 0  &&  newLoad < n.getCapacity()){
                    //Deliver packages of next move
                    for (Task task : tasks){
                        City taskPosition =(City)nextState.getTasksPosition().get(task);
                        City current = nextState.getAgentPosition();
                        if (current.equals(taskPosition) && carriedTasks.contains(task) &&
                            taskPosition.equals(task.deliveryCity)){
                            // The package has reached its destination, deliver it.
                            newPlan.add(new Action.Delivery(task));
                            newLoad -= task.weight;
                            carriedTasks.remove(task);
                        }
                    }
                    //Create node and add it to successor list.
                    //System.out.println("new load is " + newLoad);
                    Node nextNode = new Node(nextState, newPlan, carriedTasks, newLoad);
                    successors.add(nextNode);
                }
            }
        }
        if (print) {
            System.out.println(successors.size() + " successors found!");
        }
        /*
        for (Node node : successors){
            System.out.println(node.getCurrentState().toString());
        }
        */
        return successors;
    }

    /**
     * Checks if the node that we are analyzing in the algorithm is a final state and thus, if we can finalize
     * the plan.
     * @param n node to be checked
     * @return true is the node n is a final node, false otherwise.
     */
    private boolean reachedFinalState(Node n){
        Hashtable <Task, City> taskTable = n.getCurrentState().getTasksPosition();
        for ( Task task : taskTable.keySet()){
            if (task.deliveryCity != taskTable.get(task)) {return false;}
        }
        return true;
    }

    /**
     * Checks if the node n is in the list with a lower cost.
     * @param nodesVisited list to check into
     * @param n node to find
     * @return true if the node is in the list with a lower cost, false otherwise.
     */
    private boolean listContainsNodeWithBiggerCost(List<Node> nodesVisited, Node n){
        for ( Node node : nodesVisited){
            if (node.getCurrentState().equals(n.getCurrentState())){
                if (node.getCost() > n.getCost()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the node contains a state that the algorithm have been through before.
     * @param nodesVisited list of visited nodes with their corresponding states.
     * @param n node ot check against.
     * @return true if the state of node n is equal to the state of a node in nodesVisited list, false otherwise.
     */
    private boolean stateHasBeenVisited(List<Node> nodesVisited, Node n){
        for ( Node node : nodesVisited) {
            if (node.getCurrentState().equals(n.getCurrentState())) {
                //System.out.println("State previously visited:\n" + node.toString());
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts the list given in argument according to the cost.
     * @param successors is the list to sort
     * @return a sorted list
     */
    private LinkedList<Node> sortNodeList(List<Node> successors){
        //TODO
        return null;
    }

    /**
     * Uses a merge-sort algorithm to merge two sorted lists
     * @param nodesToVisit is one sorted list to be merge
     * @param successors is the second sorted list to be merged
     * @return a sorted list merging the two lists given in argument.
     */
    private LinkedList<Node> addAndSortList(List<Node> nodesToVisit, List<Node> successors){
        //TODO
        return null;
    }
}
