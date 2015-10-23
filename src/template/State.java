package template;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

import java.util.Hashtable;
import java.util.List;

/**
 * This class represent the State of our simulation at a given time.
 * Created by Daniel on 23.10.15.
 */
public class State {


    private City agentPosition;
    private Hashtable<Integer,City> tasksPosition;

    State (City position, Hashtable<Integer,City> tasksTable){
        agentPosition = position;
        tasksPosition = tasksTable;
    }

    private int computeCostFunction(){
        //TODO
        return 0;
    }

    public City getAgentPosition() {
        return agentPosition;
    }

    public Hashtable getTasksPosition() {
        return tasksPosition;
    }
}
