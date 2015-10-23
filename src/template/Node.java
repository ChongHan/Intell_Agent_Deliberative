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
    private double cost = 0; //f function f = g + h

    public Node (Plan parentPlan, State newState){
        plan = parentPlan;
        currentState = newState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public Plan getPlan() {
        return plan;
    }

    public double getCost() {
        return cost;
    }




}
