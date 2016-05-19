package waylay.client.service.push;

import java.util.Map;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public interface PushStrategy {

    public void push(String resource, Map<String, Object> data);
}
