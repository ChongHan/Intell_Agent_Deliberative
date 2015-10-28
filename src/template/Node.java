package template;

import logist.plan.Plan;
import logist.plan.Action;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * This Class represent a node in our search tree with all the related information including the update plan from
 * the root of the tree.
 * Created by Daniel on 23.10.15.
 */
public class Node implements Comparable<Node>{

    private State currentState = null;
    private List<Action> plan = new ArrayList<>();
    private static double capacity;
    private double load = 0;
    private double cost = 0; //f function f = g + h
    private static double costPerKm = 0;
    private ArrayList<Task> carriedTasks = new ArrayList<>();

    public Node (State newState, List<Action> parentPlan, TaskSet tasks, double newLoad, double newCapacity,
                 double newCostPerKm){
        if (parentPlan != null && !parentPlan.isEmpty()){
            plan = new ArrayList<>(parentPlan);
        }
        currentState = newState;
        if (tasks != null && !tasks.isEmpty()){
            for (Task task : tasks){
                carriedTasks.add(task);
            }
        }
        capacity = newCapacity;
        load = newLoad;
        costPerKm = newCostPerKm;
        computeCost();
    }

    public Node (State newState, List<Action> parentPlan, List<Task> tasks, double newLoad){
        if (parentPlan != null && !parentPlan.isEmpty()){
            plan = new ArrayList<>(parentPlan);
        }
        currentState = newState;
        if (tasks != null && !tasks.isEmpty()){
            carriedTasks = new ArrayList<>(tasks);
        }
        load = newLoad;
        computeCost();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Node node = (Node) o;
        return currentState.equals(node.currentState);
    }

    @Override
    public int hashCode()
    {
        return currentState.hashCode();
    }

    @Override
    public String toString(){
        return (currentState.toString() + "The vehicle has a capacity of " + capacity + " and has a load of "
                          + load + ".\nIt contains the following tasks: " + carriedTasks.toString() +
                          "\nThe plan is:\n" + plan.toString() + "\nThe cost of this plan is evaluated at " + cost);
    }

    @Override
    public int compareTo(Node o)
    {
        return Double.compare(this.getCost(), o.getCost());
    }

    private void computeCost()
    {
        Hashtable<Task, City> tasks =  currentState.getTasksPosition();
        for (Map.Entry<Task, City> entry : tasks.entrySet())
        {
            double distance;
            distance = entry.getKey().deliveryCity.distanceTo(entry.getValue());
            if ((distance * costPerKm) > cost)
            {
                cost = costPerKm;
            }
        }
    }

    public State getCurrentState() {return currentState;}

    public List<Task> getCarriedTasks() {return carriedTasks;}

    public List<Action> getPlan() {return plan;}

    public double getCost() {return cost;}

    public double getLoad() {return load;}

    public static double getCapacity() {return capacity;}

    public double getFreeSpace() {return (capacity-load);}

}
