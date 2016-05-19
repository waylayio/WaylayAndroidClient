package waylay.client.actuator;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import waylay.client.WaylayApplication;
import waylay.mqtt.MqttListener;

/**
 * Created by Jasper De Vrient on 19/05/2016.
 */
public class CommandListener implements MqttListener {
    private static final String TAG = "Mqtt/CommandListener";
    private static final JsonParser PARSER = new JsonParser();

    private final WaylayApplication wapp;

    public CommandListener(WaylayApplication wapp) {
        this.wapp = wapp;
    }

    @Override
    public void receiveMessage(String topic, String message) {
        try {
            JsonObject obj = PARSER.parse(message).getAsJsonObject();
            AndroidCommand command = CommandFactory.makeCommand(obj, wapp);
            command.runCommand(obj);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }
}
