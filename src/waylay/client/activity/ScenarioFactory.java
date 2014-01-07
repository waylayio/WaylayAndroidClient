package waylay.client.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import waylay.client.scenario.Scenario;

public class ScenarioFactory {

	private static final Set<Scenario> scenarioList = new HashSet<Scenario>();
	
	public static ArrayList<Scenario> getsScenarios() {
		return new ArrayList<Scenario>(scenarioList);
	}
	
	public static void addScenario(Scenario scenario){
		scenarioList.add(scenario);
	}
	
	public static void removeMachine(Scenario scenario){
		scenarioList.remove(scenario);
	}

}