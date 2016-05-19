package waylay.rest;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jasper De Vrient on 17/05/2016.
 */
public class DeviceAdapter implements JsonDeserializer<Map<String, String>> {
    @Override
    public Map<String, String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Map<String, String> map = new HashMap<>();
        if (jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> el : obj.entrySet())
                map.put(el.getKey(), el.getValue().getAsString());

        }
        return map;
    }
}
