package waylay.client.scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
	private Map<String, Double> nodes = new ConcurrentHashMap<String, Double>();
	
	public void addState(String state, Double probability){
		nodes.put(state, probability);
	}
	
	public Map<String, Double> getNodes(){
		return nodes;
	}

}
