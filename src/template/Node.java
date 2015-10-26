package template;

import logist.plan.Plan;

/**
 * This Class represent a node in our search tree with all the related information including the update plan from the
 * root of the tree. Created by Daniel on 23.10.15.
 */
public class Node implements Comparable<Node>
{

    private static double capacity;
    private State currentState = null;
    private Plan plan = null;
    private double load = 0;
    private double cost = 0; //f function f = g + h

    public Node(State newState, Plan parentPlan, double newLoad, double newCapacity)
    {
        plan = parentPlan;
        currentState = newState;
        capacity = newCapacity;
        load = newLoad;
    }

    public Node(State newState, Plan parentPlan, double newLoad)
    {
        plan = parentPlan;
        currentState = newState;
        load = newLoad;
    }

    public Node(State newState, Plan parentPlan)
    {

        plan = parentPlan;
        currentState = newState;
        load = 0;
    }

    public static double getCapacity()
    {
        return capacity;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return currentState.equals(node.currentState);
    }

    @Override
    public int hashCode()
    {
        return currentState.hashCode();
    }

    private int costFunction()
    {
        //TODO
        
        return 0;
    }

    public State getCurrentState()
    {
        return currentState;
    }

    public Plan getPlan()
    {
        return plan;
    }

    public double getCost()
    {
        return cost;
    }

    public double getLoad()
    {
        return load;
    }

    public double getFreeSpace()
    {
        return (capacity - load);
    }

    @Override
    public int compareTo(Node o)
    {
        return Double.compare(this.getCost(), o.getCost());
    }
}
