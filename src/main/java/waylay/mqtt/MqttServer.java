package waylay.mqtt;

import java.util.Map;

/**
 * Created by Jasper De Vrient on 13/05/2016.
 */
public interface MqttServer {

    public boolean connect();
    public boolean disconnect();
    public boolean publish(String topic, Map<String, Object> data);
    public void setUserName(String username);
    public void setPassword(char[] password);
    public boolean subscribe(String topic);
    public void addSubscriber(MqttListener listener);
    public void removeSubscriber(MqttListener listener);
}
