package waylay.mqtt;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public interface MqttManager {
    public MqttServer getMqttServer(String domain);

    public void disconnectAllMqttServers();
}
