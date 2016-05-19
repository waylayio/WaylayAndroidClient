package waylay.mqtt;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import waylay.client.WaylayApplication;

/**
 * Created by Jasper De Vrient on 13/05/2016.
 */
public class PahoMqttServer implements MqttCallback, MqttServer {
    private final String TAG = "MQTT";
    private static final Gson gson = new GsonBuilder().create();

    private final MqttDefaultFilePersistence persistence;
    private MqttClient client;
    private final MqttConnectOptions options;
    private boolean isConnecting = false;
    private final Set<MqttListener> subscribers = new HashSet<>();
    private final String resource;

    public PahoMqttServer(WaylayApplication waylayApplication, String domain) {
        resource = waylayApplication.getSelectedServer().getHost() + "/" + waylayApplication.getResourceId() + "/actions";
        persistence = new MqttDefaultFilePersistence(waylayApplication.getCacheDir().getAbsolutePath());
        options = new MqttConnectOptions();
        try {
            client = new MqttClient("tcp://" + domain + ":1883", waylayApplication.getResourceId().substring(0, 22), persistence);
            client.setCallback(this);
        } catch (MqttException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    @Override
    public boolean connect() {
        try {
            if (client != null && !client.isConnected() && !isConnecting) {
                isConnecting = true;
                client.connect(options);
                subscribe(resource);
            }
            return true;
        } catch (MqttException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public void setUserName(String userName) {
        options.setUserName(userName);
    }

    @Override
    public void setPassword(char[] password) {
        options.setPassword(password);
    }

    @Override
    public boolean subscribe(String topic) {
        try {
            client.subscribe(topic);
            return true;
        } catch (MqttException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public void addSubscriber(MqttListener listener) {
        if (!subscribers.contains(listener))
            subscribers.add(listener);
    }

    @Override
    public void removeSubscriber(MqttListener listener) {
        if (subscribers.contains(listener))
            subscribers.remove(listener);
    }

    @Override
    public boolean disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                isConnecting = false;
            }
            return true;
        } catch (MqttException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean publish(String topic, Map<String, Object> data) {
        final String MESSAGE = gson.toJson(data);
        try {
            client.publish(topic, new MqttMessage(MESSAGE.getBytes()));
            return true;
        } catch (MqttException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.w(TAG, "connection lost.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.i(TAG, "Message arrived at client.");
        for (MqttListener subscriber: subscribers)
            subscriber.receiveMessage(topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "Message delivered to server.");
    }
}
