package template;

import logist.task.Task;
import logist.topology.Topology.City;

import java.util.Hashtable;

/**
 * This class represent the State of our simulation at a given time. Created by Daniel on 23.10.15.
 */
public class State
{


    private City agentPosition;
    private Hashtable<Task, City> tasksPosition;

    State(City position, Hashtable<Task, City> tasksTable)
    {
        agentPosition = position;

        tasksPosition = tasksTable;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (!agentPosition.equals(state.agentPosition)) return false;
        return tasksPosition.equals(state.tasksPosition);

    }

    @Override
    public int hashCode()
    {
        int result = agentPosition.hashCode();
        result = 31 * result + tasksPosition.hashCode();
        return result;
    }

    public City getAgentPosition()
    {
        return agentPosition;
    }

    public Hashtable getTasksPosition()
    {
        return tasksPosition;
    }
}
