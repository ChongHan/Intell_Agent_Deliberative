package template;

import logist.plan.Plan;
import logist.task.Task;
import logist.topology.Topology;

import java.util.Hashtable;
import java.util.Map;

import logist.topology.Topology.City;

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
    private double costPerKm = 0;

    public Node(State newState, Plan parentPlan, double newLoad, double newCapacity, double costPerKm)
    {
        plan = parentPlan;
        currentState = newState;
        capacity = newCapacity;
        load = newLoad;
        costPerKm = costPerKm;
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
