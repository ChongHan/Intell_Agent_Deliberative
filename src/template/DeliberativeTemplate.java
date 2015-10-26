package template;

/* import table */
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

    @Override
    public Plan plan(Vehicle vehicle, TaskSet tasks) {
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

	private Plan DeliberativePlan(Vehicle vehicle, TaskSet tasks) {
        //Initialisation of variables
        State initialState = createInitialState(vehicle,tasks);
        List<State> finalStates = createFinalStateList(tasks);
        Node n;
        // node list Q of the algorithm
        List<Node> nodesToVisit = new LinkedList<>();
        List<Node> nodesVisited = new LinkedList<>();
        // node list C of the algorithm

        //add initial node to the node list Q
        nodesToVisit.add(new Node (initialState, null, EMPTY_LOAD, vehicle.capacity()));

        while(true){
            if (nodesToVisit.isEmpty()) {
                throw new AssertionError("List of nodes to be visited is empty!");
            }

            //get first element of the list and remove it
            n = nodesToVisit.get(0);
            nodesToVisit.remove(0);

            //check if it is a final state
            if (finalStates.contains(n.getCurrentState())){
                return n.getPlan();
            }

            switch (algorithm) {
                case ASTAR:
                    if (!stateHasBeenVisited(nodesVisited, n) || listContainsNodeWithBiggerCost(nodesVisited, n)) {
                        //Delete entry in the list
                        for ( Node node : nodesVisited) {
                            if (node.getCurrentState().equals(n.getCurrentState())) {
                                nodesVisited.remove(node);
                                break;
                            }
                        }
                        nodesVisited.add(n);
                        List<Node> successors = findSuccessors(n);
                        nodesToVisit.add((Node) successors);
                        Collections.sort(nodesToVisit);
                    }
                case BFS:
                    if (!stateHasBeenVisited(nodesVisited, n)) {
                        nodesVisited.add(n);
                        List<Node> successors = findSuccessors(n);
                        nodesToVisit.addAll(successors);
                    }
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
    private LinkedList<State> createFinalStateList(TaskSet tasks){
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

        return new State (vehicle.getCurrentCity(), tasksPosition);
    }

    /**
     * Looks for all reachable nodes from the node given as argument. The function will create all the
     * nodes with the relevant information and aggregate them in a list that will be returned.
     * @param n : the node we are at in the graph
     * @return a list of nodes reachable from node n
     */
    private LinkedList<Node> findSuccessors (Node n){
        // Get relevant information from the node
        TaskSet tasks = (TaskSet) n.getCurrentState().getTasksPosition().keySet();
        City agentCurrentCity = n.getCurrentState().getAgentPosition();
        List<City> neighbors = agentCurrentCity.neighbors();

        // Check for tasks that can be picked up at the current location.
        // Each task can be picked up if its location in the current state is equal to its pickup city
        // and if the agent is not full yet.
        int counter = 0;
        List<Task> breakpointTasks = new ArrayList<>();
        for (Task task : tasks){
            if (task.pickupCity == agentCurrentCity && task.weight <= n.getFreeSpace()){
                counter++;
                breakpointTasks.add(task);
            }
        }

        // For each pickup task available, the number of states is multiplied by 2.
        // In the following section, we create all possible states by iterating over the neighbors and over the
        // possible amount of states for each move to a neighboring city.

        //For each possible neighboring city create the states and its corresponding hash table.
        for (City neighbor: neighbors){
            int possibleStates = (int) Math.pow(2,counter);
            int separator = possibleStates/((int)Math.pow(2,breakpointTasks.size()));
            for (int j=0; j<possibleStates; j++){
                // Create the corresponding hash table for this state.
                Hashtable<Task,City> newTaskPositions = n.getCurrentState().getTasksPosition();
                Plan newPlan = n.getPlan();
                double newLoad = n.getLoad();
                for (Task task : tasks){
                    City taskPosition = newTaskPositions.get(task);
                    if (taskPosition == agentCurrentCity){
                        if (task.deliveryCity.equals(agentCurrentCity)) {
                            // The package has reached its destination, deliver it.
                            newPlan.appendDelivery(task);
                            newLoad -= task.weight;
                        }
                        else if(taskPosition != task.pickupCity){
                            // The package is in transit, continue the transit.
                            newTaskPositions.replace(task, neighbor);
                        }
                        else if (taskPosition == task.pickupCity && task.weight <= n.getFreeSpace()){
                            // This if condition corresponds to the tree of all possible states.
                            // More explanations available in the report.
                            if(j%(2*separator)<separator){
                                //Take it!
                                newTaskPositions.replace(task, neighbor);
                                newPlan.appendPickup(task);
                                newLoad+=task.weight;
                            }
                            // else leave it there.
                        }
                        newPlan.appendMove(neighbor);
                    }
                }
                State nextState = new State(neighbor, newTaskPositions);
                Node nextNode = new Node(nextState, newPlan, newLoad);
            }
        }

        return null;
    }

    /**
     * Checks if the node n is in the list with a lower cost.
     * @param nodesVisited list to check into
     * @param n node to find
     * @return true if the node is in the list with a lower cost, false otherwise.
     */
    private boolean listContainsNodeWithBiggerCost(List<Node> nodesVisited, Node n){
        //TODO Remove the node with smaller cost!
        boolean result = false;
        for ( Node node : nodesVisited){
            if (node.getCurrentState().equals(n.getCurrentState())){
                if (node.getCost() > n.getCost()){
                    result = true;
                }
            }
        }
        return false;
    }

    private boolean stateHasBeenVisited(List<Node> nodesVisited, Node n){
        boolean result = false;
        for ( Node node : nodesVisited) {
            if (node.getCurrentState().equals(n.getCurrentState())) {
                result = true;
            }
        }
        return result;
    }

}
