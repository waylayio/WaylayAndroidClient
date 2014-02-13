package waylay.client.data;

public class BayesServer {
	private String URL;
	private String name;
	private String password;
	
	public BayesServer(String uRL, String name, String password) {
		super();
		URL = uRL;
		this.name = name;
		this.password = password;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
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
		BayesServer other = (BayesServer) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}



	public String getName() {
		return name;
	}

	public String getURL() {
		return URL;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return getURL();
	}
	
	public String constructURLtoListAllMachines(){
		 return "http://" + getName()+ ":" + getPassword() + "@" + getURL() + "/appserver/rest/cloud_api_machine/list";
		    
	}
	
	public String constructURLtoListAllScenarios(){
		 return "http://" + getURL() +"/scenarios";    
	}
	
	public String constructURLtoForScenario(Long id){
		 return "http://" + getURL() +"/scenarios/" +id;    
	}
	public String constructURLtoForScenarioAndNode(Long id, String node){
		 return constructURLtoForScenario(id) + "/"+node;    
	}
	
	public String constructURLtoGetIpAddress(){
		 return "http://" + getName()+ ":" + getPassword() + "@" + getURL() + "/appserver/rest/cloud_api_machine/getPublicIpaddress?machineguid=";
		    
	}
	
	public String constructURLtoGetDashboardData(){
		 return "http://" + getName()+ ":" + getPassword() + "@" + getURL() + "/appserver/rest/cloud_api_cmc/getDashboard";
		    
	}
	public String constructURLForWebAP(){
		 return "http://" + getURL().substring(0, getURL().indexOf("/api"));
		    
	}
	
}
