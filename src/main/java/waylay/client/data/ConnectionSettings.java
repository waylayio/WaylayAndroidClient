package waylay.client.data;

/**
 * Created by Jasper De Vrient on 12/05/2016.
 */
public class ConnectionSettings {
    public final static String MQTT = "MQTT";
    public final static String HTTP = "HTTP";
    public final static String AUTH = "AUTH";
    private String connectionType;
    private String key, value;
    private boolean useAuth = false;

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
       if (connectionType.equals(MQTT))
           this.connectionType = MQTT;
        else if (connectionType.equals(HTTP))
           this.connectionType = HTTP;
        else
           throw new IllegalArgumentException("Only MQTT and HTTP allowed");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUseAuth() {
        return useAuth;
    }

    public void setUseAuth(boolean useAuth) {
        this.useAuth = useAuth;
    }

    @Override
    public String toString() {
        return "ConnectionSettings{" +
                "connectionType='" + connectionType + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
