package waylay.client.actuator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import waylay.client.WaylayApplication;

/**
 * Created by Jasper De Vrient on 19/05/2016.
 */
public class CommandFactory {
    private static final String ACTION = "action";

    public static AndroidCommand makeCommand(JsonObject obj, WaylayApplication wapp) {
        String action = "";
        if (obj.get(ACTION) != null)
            action = obj.get(ACTION).getAsString();

        switch (action) {
            case "notify":
                return new NotifyCommand(wapp);
            default:
                return new LogCommand();
        }
    }
}
