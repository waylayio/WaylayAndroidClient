package waylay.rest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import waylay.client.scenario.Condition;
import waylay.client.scenario.Node;
import waylay.client.scenario.Scenario;
import waylay.client.scenario.ScenarioStatus;

import android.util.Log;

public class ScenarioJSONParser {
	public static final String TAG = "Scenario";
	
	public static List<Scenario> getAllScenariosViaJSON(String jsonString) throws Exception {
		Log.d(TAG, "getAllScenariosViaJSON");
		ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        JSONArray array = new JSONArray(jsonString);
        for(int i=0; i < array.length(); i++){
            JSONObject value = array.getJSONObject(i);
            Scenario scenario = parseScenario(value.toString());
            scenarios.add(scenario);
        }
        return scenarios;
    }

	public static Scenario parseScenario(String stringValue) throws JSONException {
		JSONObject value = new JSONObject(stringValue);
		JSONObject target = value.has("target")? value.getJSONObject("target") : null;
		String targetName = target == null? "target" : (String) target.get("name");
        String name = value.getString("name");
        String status = value.getString("status");
        Number refreshRate = value.has("frequency")? (Number) value.getLong("frequency") :10;

        JSONObject conditionObj = value.has("condition") ? value.getJSONObject("condition"): null;
        Condition condition;
        if(conditionObj != null){
        	condition = new Condition((Number)conditionObj.getLong("threshold"),
                    (Number) conditionObj.getLong("operator"), conditionObj.getString("stopState"));
        }else {
        	condition = new Condition(0.99, 1, "NOK");
        }
        
        Long id = value.getLong("ID");
        Log.d(TAG, "getAllScenariosViaJSON got scenario "+id);
        Scenario scenario = new Scenario(name, targetName, id, ScenarioStatus.getStatus(status),
                condition, refreshRate.intValue());
        JSONArray nodes = value.getJSONArray("nodes");
        for(int k=0; k < nodes.length(); k++){
        	JSONObject obj = nodes.getJSONObject(k);
        	String name1 = obj.getString("name");
        	String sensorName = null;
        	try{
            	JSONObject sensor = obj.getJSONObject("sensor");
            	sensorName = sensor.getString("name");	
        	}catch (Exception e){
        		
        	}
        	Log.d(TAG, "node "+name1 + " , sensor "+ sensorName);
        	JSONArray states = obj.getJSONArray("states");
        	Node node = new Node(name1, sensorName);
        	for(int j=0; j < states.length(); j++){
        		JSONObject stateObj = states.getJSONObject(j);
        		Iterator itr = stateObj.keys();
        		while(itr.hasNext()){
        			String stateName = (String) itr.next();
        			Double prob = stateObj.getDouble(stateName);
        			node.addState(stateName, prob);
        		}
        	}
        	scenario.addNode(node);
        }
        return scenario;
	}

}
