package template;

import logist.plan.Plan;

import java.util.List;

/**
 * This Class represent a node in our search tree with all the related information including the update plan from
 * the root of the tree.
 * Created by Daniel on 23.10.15.
 */
public class Node {

    private State currentState = null;
    private Plan plan = null;
    private static double capacity;
    private double load = 0;
    private double cost = 0; //f function f = g + h

    public Node (State newState, Plan parentPlan, double newLoad, double newCapacity){
        plan = parentPlan;
        currentState = newState;
        capacity = newCapacity;
        load = newLoad;
    }

    public Node (State newState, Plan parentPlan, double newLoad){
        plan = parentPlan;
        currentState = newState;
        load = newLoad;
    }

    public Node (State newState, Plan parentPlan){
        plan = parentPlan;
        currentState = newState;
        load = 0;
    }

    private int costFunction(){
        //TODO
        return 0;
    }

    public State getCurrentState() {return currentState;}

    public Plan getPlan() {return plan;}

    public double getCost() {return cost;}

    public double getLoad() {return load;}

    public static double getCapacity() {return capacity;}

    public double getFreeSpace() {return (capacity-load);}

}
