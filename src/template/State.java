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
    private Hashtable<Task,City> tasksPosition;

    State (City position, Hashtable<Task,City> tasksTable){
        agentPosition = position;
        tasksPosition = new Hashtable<>(tasksTable);
    }

    @Override
    public String toString(){
        String string = new String("Agent is in " + agentPosition + "\n");
        for ( Task task : tasksPosition.keySet()){
            string += ("Task " + task.id + " is in " + tasksPosition.get(task) + "\n");
        }
        return string;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        State state = (State) o;
        if (!agentPosition.equals(state.agentPosition)) {return false;}
        return tasksPosition.equals(state.tasksPosition);
    }

    @Override
    public int hashCode()
    {
        int result = agentPosition.hashCode();
        result = 31 * result + tasksPosition.hashCode();
        return result;
     }

    public City getAgentPosition() {
        return agentPosition;
    }

    public Hashtable getTasksPosition() {
        return tasksPosition;
    }
}
