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
	
	public String constructURLtoGetIpAddress(){
		 return "http://" + getName()+ ":" + getPassword() + "@" + getURL() + "/appserver/rest/cloud_api_machine/getPublicIpaddress?machineguid=";
		    
	}
	
	public String constructURLtoGetDashboardData(){
		 return "http://" + getName()+ ":" + getPassword() + "@" + getURL() + "/appserver/rest/cloud_api_cmc/getDashboard";
		    
	}
	
}
