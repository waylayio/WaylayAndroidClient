package waylay.client.scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
	private Map<String, Double> node = new ConcurrentHashMap<String, Double>();
	private String name;
	
	public Node(String name) {
		this.name = name;
	}

	public void addState(String state, Double probability){
		node.put(state, probability);
	}
	
	public Map<String, Double> getStates(){
		return node;
	}
	
	public String getName(){
		return name;
	}

	public boolean isMostLikelyStateNOK() {
		Double max = 0.5;
		for(String key : node.keySet()){
			if(key.equalsIgnoreCase("NOK") || key.equalsIgnoreCase("FALSE") || key.equalsIgnoreCase("trigger")){
				if(node.get(key) > max)
					return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String str = getStatesAsString();
		return "Node " + name + " " +str;
	}

	public String getStatesAsString() {
		String str = "";
		for(String key : node.keySet()){
			str += key + " " + node.get(key) + ", "; 
		}
		str = str.substring(0, str.length() -2 );
		return str;
	}

	public String getMostLikelyState() {
		Double max = -1.;
		String state = "";
		for(String key : node.keySet()){
			if(node.get(key) > max){
				state = key + ":" + node.get(key);
				max = node.get(key);
			}		
		}
		return state;
	}
	
}
