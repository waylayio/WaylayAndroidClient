package waylay.client.service.push;

import java.util.Map;

import waylay.mqtt.MqttServer;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public class MqttPushStrategy implements PushStrategy {
    private final MqttServer server;
    private final String domain;

    public MqttPushStrategy(final String domain, final MqttServer server) {
        this.domain = domain;
        this.server = server;
    }

    @Override
    public void push(String resource, Map<String, Object> data) {
        if (server.connect()) {
            data.put("timestamp", System.currentTimeMillis());
            server.publish(domain + "/" + resource, data);
        }
    }
}
