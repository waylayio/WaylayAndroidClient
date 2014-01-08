package waylay.client.scenario;

import java.util.ArrayList;


public class Scenario implements Comparable{
    private String name;
    private String targetNode;
    private Long id;
    private ScenarioStatus scenarioStatus;
    private Condition condition;
    private int refreshRate = 10;
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public Scenario(String name, String targetNode, Long id, ScenarioStatus scenarioStatus, Condition condition, int refreshRate) {
        this(name, targetNode, id, scenarioStatus, condition);
        this.refreshRate = refreshRate;
    }

    public Scenario(String name, String targetNode, Long id, ScenarioStatus scenarioStatus, Condition condition) {
        this.name = name;
        this.targetNode = targetNode;
        this.id = id;
        this.scenarioStatus = scenarioStatus;
        this.condition = condition;
    }
    
    public void addNode(Node node){
    	nodes.add(node);
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

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scenario other = (Scenario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	@Override
	public int compareTo(Object arg0) {
		Scenario o2 = (Scenario) arg0;
		return this.id.compareTo(o2.id);
	}
}
