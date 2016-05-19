package waylay.client.actuator;

import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by Jasper De Vrient on 19/05/2016.
 */
public class LogCommand implements AndroidCommand {
    public static final String TAG = "Mqtt/LogCommand";
    @Override
    public void runCommand(JsonObject parameters) {
        Log.i(TAG, parameters.toString());
    }
}
