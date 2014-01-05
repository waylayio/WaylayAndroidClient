package waylay.client.data;

public class MachineInfo {
	private String ipAddress;
	private Long port;
	private String name;
	private String guid;
	//TODO use enums
	public final String CREATED = "CREATED";
	public final String RUNNING = "RUNNING";
	public final String PAUSED = "PAUSED";
	public final String STOPPED = "STOPPED";
	private String status = CREATED;

	
	public MachineInfo(String name, String ipAddress, Long port) {
		this.name = name;
		this.ipAddress = ipAddress;
		this.port = port;
	}


	public MachineInfo(String name, String ipAddress, Long port, String guid, String status) {
		this.name = name;
		this.ipAddress = ipAddress;
		this.port = port;
		this.guid = guid;
		this.status = status;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String address){
		this.ipAddress = address;
	}
	
	public Long getPort() {
		return port;
	}

	public String getName() {
		return name;
	}
	

	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	@Override
	public String toString() {
		return name;
	}
	

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MachineInfo other = (MachineInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	//user can add his non-SSO machine too
	public boolean isSSOMachine(){
		return guid != null;
		
	}
	
	public boolean isValid(){
		return (ipAddress != null && !"null".equals(ipAddress)) && 
				((isSSOMachine() && status.equals(RUNNING)) || !isSSOMachine());
		
	}


}