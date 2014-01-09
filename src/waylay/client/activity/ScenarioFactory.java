package waylay.client.activity;

import java.util.ArrayList;
import java.util.Collections;

import waylay.client.scenario.Scenario;

public class ScenarioFactory {

	private static final ArrayList<Scenario> scenarioList = new ArrayList<Scenario>();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Scenario> getsScenarios() {
		Collections.sort(scenarioList);
		return scenarioList;
	}
	
	public static void clear(){
		scenarioList.clear();
	}
	
	public static void addScenario(Scenario scenario){
		if(scenarioList.contains(scenario)){
			removeScenario(scenario);
			scenarioList.add(scenario);
			Collections.sort(scenarioList);
		}else
			scenarioList.add(scenario);
	}
	
	public static void removeScenario(Scenario scenario){
		scenarioList.remove(scenario);
	}

}