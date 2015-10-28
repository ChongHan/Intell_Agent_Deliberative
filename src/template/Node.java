package template;

import logist.plan.Plan;
import logist.plan.Action;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class represent a node in our search tree with all the related information including the update plan from
 * the root of the tree.
 * Created by Daniel on 23.10.15.
 */
public class Node {

    private State currentState = null;
    private List<Action> plan = new ArrayList<>();
    private static double capacity;
    private double load = 0;
    private double cost = 0; //f function f = g + h
    private ArrayList<Task> carriedTasks = new ArrayList<>();

    public Node (State newState, List<Action> parentPlan, TaskSet tasks, double newLoad, double newCapacity){
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
    }

    private int costFunction(){
        //TODO
        return 0;
    }

    @Override
    public String toString(){
        return (currentState.toString() + "The vehicle has a capacity of " + capacity + " and has a load of "
                          + load + ".\nIt contains the following tasks: " + carriedTasks.toString() +
                          "\nThe plan is:\n" + plan.toString() + "\nThe cost of this plan is evaluated at " + cost);
    }

    public State getCurrentState() {return currentState;}

    public List<Task> getCarriedTasks() {return carriedTasks;}

    public List<Action> getPlan() {return plan;}

    public double getCost() {return cost;}

    public double getLoad() {return load;}

    public static double getCapacity() {return capacity;}

    public double getFreeSpace() {return (capacity-load);}

}
