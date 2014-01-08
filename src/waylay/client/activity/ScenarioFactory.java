package waylay.client.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import waylay.client.scenario.Scenario;

public class ScenarioFactory {

	private static final Set<Scenario> scenarioList = new HashSet<Scenario>();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Scenario> getsScenarios() {
		ArrayList<Scenario> l = new ArrayList<Scenario>(scenarioList);
		Collections.sort(l);
		return l;
	}
	
	public static void addScenario(Scenario scenario){
		scenarioList.add(scenario);
	}
	
	public static void removeMachine(Scenario scenario){
		scenarioList.remove(scenario);
	}

}