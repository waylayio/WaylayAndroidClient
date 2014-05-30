package waylay.rest;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import waylay.client.scenario.Condition;
import waylay.client.scenario.Node;
import waylay.client.scenario.Scenario;
import waylay.client.scenario.ScenarioStatus;

public class ScenarioAdapter implements JsonDeserializer<Scenario> {

    private static final String TAG = "ScenarioAdapter";

    @Override
    public Scenario deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject value = json.getAsJsonObject();

        JsonObject target = value.getAsJsonObject("target");
        String targetName = target == null? "target" : target.get("name").getAsString();
        String name = value.get("name").getAsString();
        String status = value.get("status").getAsString();
        Number refreshRate = value.has("frequency")? (Number) value.get("frequency").getAsLong() :10;

        JsonObject conditionObj = value.has("condition") ? value.getAsJsonObject("condition"): null;
        Condition condition;
        if(conditionObj != null){
            condition = new Condition(
                    conditionObj.get("threshold").getAsLong(),
                    conditionObj.get("operator").getAsLong(),
                    conditionObj.get("stopState").getAsString());
        }else {
            condition = new Condition(0.99, 1, "NOK");
        }

        Long id = value.get("ID").getAsLong();
        Log.d(TAG, "getAllScenariosViaJSON got scenario " + id);
        Scenario scenario = new Scenario(name, targetName, id, ScenarioStatus.getStatus(status),
                condition, refreshRate.intValue());
        JsonArray nodes = value.getAsJsonArray("nodes");
        for(JsonElement nodeElement:nodes){
            JsonObject obj = nodeElement.getAsJsonObject();
            String name1 = obj.get("name").getAsString();
            String sensorName = null;
            try{
                JsonObject sensor = obj.getAsJsonObject("sensor");
                sensorName = sensor.get("name").getAsString();
            }catch (Exception e){
                // FIXME ugly
            }
            Log.d(TAG, "node " + name1 + " , sensor "+ sensorName);
            Node node = new Node(name1, sensorName);
            JsonArray states = obj.getAsJsonArray("states");
            for(JsonElement stateElement:states){
                JsonObject stateObj = stateElement.getAsJsonObject();
                for(Map.Entry<String,JsonElement> entry:stateObj.entrySet()){
                    String stateName = entry.getKey();
                    Double prob = entry.getValue().getAsDouble();
                    node.addState(stateName, prob);
                }
            }
            scenario.addNode(node);
        }
        return scenario;
    }

}
