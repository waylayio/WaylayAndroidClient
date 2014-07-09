package waylay.client.scenario;

import java.util.Map;

public class RawData {

    private String resource;
    private Map<String, Object> data;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
