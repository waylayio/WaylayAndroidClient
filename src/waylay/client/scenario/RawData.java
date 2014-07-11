package waylay.client.scenario;

import java.util.List;
import java.util.Map;

public class RawData {

    private String resource;
    private Object data;

    public RawData(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Object getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
