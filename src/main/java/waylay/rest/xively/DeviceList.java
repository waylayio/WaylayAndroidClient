package waylay.rest.xively;


import java.util.ArrayList;
import java.util.List;

public class DeviceList {

    public long totalResults;
    public long itemsPerPage;
    public long startIndex;
    public List<Device> devices = new ArrayList<Device>();

    @Override
    public String toString() {
        return "DeviceList{" +
                "totalResults=" + totalResults +
                ", itemsPerPage=" + itemsPerPage +
                ", startIndex=" + startIndex +
                ", devices=" + devices +
                '}';
    }
}
