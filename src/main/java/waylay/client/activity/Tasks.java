package waylay.client.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import waylay.client.scenario.Task;

public class Tasks {

	private static final ArrayList<Task> TASK_LIST = new ArrayList<Task>();

	@SuppressWarnings("unchecked")
	public static ArrayList<Task> getTasks() {
		return TASK_LIST;
	}
	
	public static void clear(){
		TASK_LIST.clear();
	}
	
	public static void addScenario(Task task){
		if(TASK_LIST.contains(task)){
			removeScenario(task);
			TASK_LIST.add(task);
			Collections.sort(TASK_LIST);
		}else {
            TASK_LIST.add(task);
        }
        Collections.sort(TASK_LIST);
	}

    public static void addAll(Collection<Task> tasks){
        for(Task task : tasks){
            addScenario(task);
        }
    }
	
	public static void removeScenario(Task task){
		TASK_LIST.remove(task);
	}

    public static Task getTaskById(long taskId){
        for(Task task : TASK_LIST){
            if(task.getId() == taskId){
                return task;
            }
        }
        return null;
    }

}