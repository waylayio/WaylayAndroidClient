package waylay.client.scenario;

import java.util.ArrayList;

public class RemoteScenarioStatus {
    private String name;
    private String targetNode;
    private Long id;
    private ScenarioStatus scenarioStatus;
    private Condition condition;
    private int refreshRate = 10;
    private ArrayList<Pair<String, String>> nodesInGivenState;

    public RemoteScenarioStatus(String name, String targetNode, Long id, ScenarioStatus scenarioStatus, Condition condition, int refreshRate) {
        this(name, targetNode, id, scenarioStatus, condition);
        this.refreshRate = refreshRate;
    }

    public RemoteScenarioStatus(String name, String targetNode, Long id, ScenarioStatus scenarioStatus, Condition condition) {
        this.name = name;
        this.targetNode = targetNode;
        this.id = id;
        this.scenarioStatus = scenarioStatus;
        this.condition = condition;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public String getName() {
        return name;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public Long getId() {
        return id;
    }

    public ScenarioStatus getScenarioStatus() {
        return scenarioStatus;
    }

    public Condition getCondition() {
        return condition;
    }
    
    public void addNodeState(String node, String state){
        if(nodesInGivenState == null) {
            nodesInGivenState = new ArrayList<Pair<String, String>>();
        }
        nodesInGivenState.add(new Pair<String, String>(node, state));
    }
    
    public ArrayList<Pair<String, String>> getNodesInGivenState(){
        return nodesInGivenState;
    }

    @Override
    public String toString() {
        return "RemoteScenarioStatus{" +
                "name='" + name + '\'' +
                ", targetNode='" + targetNode + '\'' +
                ", id=" + id +
                ", scenarioStatus=" + scenarioStatus +
                ", condition=" + condition +
                '}';
    }
}
