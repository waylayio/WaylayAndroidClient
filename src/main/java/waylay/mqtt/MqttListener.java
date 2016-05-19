package waylay.mqtt;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public interface MqttListener {
    public void receiveMessage(String topic, String message);
}
