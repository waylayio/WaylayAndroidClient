package waylay.mqtt;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import waylay.client.WaylayApplication;
import waylay.client.actuator.CommandListener;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public class PahoMqttManager implements MqttManager {
    public static final String TAG = "PahoMQTT";
    private static Map<String, MqttServer> servers = new HashMap<>();
    private final WaylayApplication wapp;
    private final MqttListener commandListener;

    public PahoMqttManager(WaylayApplication wapp) {
        this.wapp = wapp;
        this.commandListener = new CommandListener(wapp);
    }

    @Override
    public MqttServer getMqttServer(String domain) {
        if (!servers.containsKey(domain)) {
            MqttServer server = new PahoMqttServer(wapp, domain);
            servers.put(domain, server);
            server.addSubscriber(commandListener);
        }
        return servers.get(domain);
    }

    @Override
    public void disconnectAllMqttServers() {
        for (MqttServer server: servers.values()) {
            Log.i(TAG, "Disconnecting " + server.toString());
            server.removeSubscriber(commandListener);
            server.disconnect();
        }
    }
}
