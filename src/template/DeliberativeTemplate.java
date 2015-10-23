package template;

/* import table */
import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
	
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
        //Initialisation of variables
        State initialState = createInitialState(vehicle,tasks);
        List<State> finalStates = createFinalStateList(tasks);
        Node n;
        // node list Q of the algorithm
        List<Node> nodesToVisit = new LinkedList<>();
        List<Node> nodesVisited = new LinkedList<>();
        // node list C of the algorithm

        //add initial node to the node list Q
        nodesToVisit.add(new Node (null, initialState));

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
                    if (!nodesVisited.contains(n) || listContainsNodeWithSmallerCost(nodesVisited, n)) {
                        nodesVisited.add(n);
                        List<Node> successors = findSuccessors(n);
                        sortNodeList(successors);
                        addAndSortList(nodesToVisit, successors);
                    }
                case BFS:
                    if (!nodesVisited.contains(n)) {
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
        Hashtable<Integer, City> tasksPosition = new Hashtable<>();
        //Create the hash table of the final destinations of all tasks
        for (Task task : tasks) {
            tasksPosition.put(task.id,task.deliveryCity);
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
        Hashtable<Integer, City> tasksPosition = new Hashtable<>();
        //create the hash table of the initial positions of all tasks
        for (Task task : tasks) {
            tasksPosition.put(task.id,task.pickupCity);
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
        //TODO
        return null;
    }

    /**
     * Checks if the node n is in the list with a lower cost.
     * @param nodesVisited list to check into
     * @param n node to find
     * @return true if the node is in the list with a lower cost, false otherwise.
     */
    private boolean listContainsNodeWithSmallerCost(List<Node> nodesVisited, Node n){
        //TODO Remove the node with smaller cost!
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
