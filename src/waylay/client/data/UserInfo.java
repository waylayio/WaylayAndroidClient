package waylay.client.data;

import waylay.client.activity.ConnectionType;


public class UserInfo {
	private String name;
	private String password;
	private MachineInfo machine;
	private ConnectionType connectionType = ConnectionType.RDP;
	
	public UserInfo(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	
	public UserInfo(String name, String password, MachineInfo machine) {
		super();
		this.name = name;
		this.password = password;
		this.machine = machine;
	}


	public void addMachine(MachineInfo machineInfo){
		machine = machineInfo;
	}
	
	public MachineInfo getMachine(){
		return machine;
		
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}


	@Override
	public String toString() {
		if(machine !=null){
			return name + " ["+machine.getName() + "]";
		} else
			return name;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((connectionType == null) ? 0 : connectionType.hashCode());
		result = prime * result + ((machine == null) ? 0 : machine.hashCode());
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
		UserInfo other = (UserInfo) obj;
		if (connectionType != other.connectionType)
			return false;
		if (machine == null) {
			if (other.machine != null)
				return false;
		} else if (!machine.equals(other.machine))
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


	public void setConnectionType(ConnectionType type) {
		this.connectionType = type;
		
	}


	public ConnectionType getConnectionType() {
		return connectionType;
	}
	
	
}
