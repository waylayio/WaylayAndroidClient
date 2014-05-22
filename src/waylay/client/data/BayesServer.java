package waylay.client.data;

public class BayesServer {
	private String host;
	private String name;
	private String password;
	
	public BayesServer(String host, String name, String password) {
		super();
		this.host = host;
		this.name = name;
		this.password = password;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
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
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
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

	public String getHost() {
		return host;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return getHost();
	}

    public String constructURLForWebAP(){
        return "http://" + host;
    }
}
