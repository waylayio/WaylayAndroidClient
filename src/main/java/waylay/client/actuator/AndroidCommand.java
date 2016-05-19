package waylay.client.actuator;

import com.google.gson.JsonObject;

/**
 * Created by Jasper De Vrient on 19/05/2016.
 */
public interface AndroidCommand {
    public void runCommand(JsonObject parameters);
}
