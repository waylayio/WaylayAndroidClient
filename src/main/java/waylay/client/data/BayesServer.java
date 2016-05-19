package waylay.client.data;

public class BayesServer {
	private final String host;
	private final String name;
	private final String password;
    private final String masterkey;
    private final String deviceGateway;
    private final String dataBroker;
    private final boolean secure;
	
	public BayesServer(String host, String name, String password, boolean secure) {
		this(host, name, password, "", "", "",  secure);
	}

    public BayesServer(String host, String name, String password, String masterkey, String deviceGateway, String dataBroker, boolean secure) {
        this.host = host;
        this.name = name;
        this.password = password;
        this.masterkey = masterkey;
        this.secure = secure;
        this.deviceGateway = deviceGateway;
        this.dataBroker = dataBroker;
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

    public boolean isSecure() {
        return secure;
    }

    public String getMasterkey() {return masterkey;}

    public String getDeviceGateway() {return deviceGateway;}

    @Override
	public String toString() {
		return getHost();
	}

    public String constructURLForWebAP(String host){
        return getScheme() + "://" + host;
    }

    public String constructURLForWebAP() {
        return constructURLForWebAP(host);
    }

    public String apiBase() {
        String url = constructURLForWebAP();
        // if we have specific path we see it as the api root
        if(!host.contains("/")){
            url += "/api";
        }
        return url;
    }

    public String devicesApiBase() {
        String url = constructURLForWebAP(deviceGateway);
        // if we have specific path we see it as the api root
        if(!url.contains("/client")){
            url += "/client";
        }
        return url;
    }

    public String getDataBroker() {
        return dataBroker;
    }

    public String brokerApiBase() {
        String url = constructURLForWebAP(dataBroker);

        return url;
    }

    private String getScheme(){
        if(secure){
            return "https";
        }else{
            return "http";
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((masterkey == null) ? 0 : masterkey.hashCode());
        result = prime * result + ((deviceGateway == null) ? 0 : deviceGateway.hashCode());
        result = prime * result + ((dataBroker == null) ? 0 : dataBroker.hashCode());
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
        if (masterkey == null) {
            if (other.password != null)
                return false;
        } else if (!masterkey.equals(other.masterkey))
            return false;
        if (deviceGateway == null) {
            if (other.deviceGateway != null)
                return false;
        } else if (!deviceGateway.equals(other.deviceGateway))
            return false;
        if (dataBroker == null) {
            if (other.dataBroker != null)
                return false;
        } else if (!dataBroker.equals(other.dataBroker))
            return false;

        return true;
    }

}
