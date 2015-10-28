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
    public boolean equals(Object obj){
        State state = (State) obj;
        if(agentPosition != state.getAgentPosition()) { return false;}
        for (Task taskA : tasksPosition.keySet()){
            for (Object taskB : state.getTasksPosition().keySet()){
                if (taskA.equals(taskB) && tasksPosition.get(taskA) != state.getTasksPosition().get(taskB)){
                    return false;
                }
            }

        }
        return true;
    }

    public City getAgentPosition() {
        return agentPosition;
    }

    public Hashtable getTasksPosition() {
        return tasksPosition;
    }
}
