package waylay.client.sensor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeaconSensor extends LocalSensor{

    // TODO we probably want to store the proximity as well
    private Set<String> beacons = new HashSet<String>();

    @Override
    public String getStatus() {
        if(beacons == null)
            return "No data";
        return "OK";
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public Map<String, String> getRuntimeData() {
        return null;
    }

    public void updateData(Set<String> beacons) {
        this.beacons = beacons;
    }

    @Override
    public String toString() {
        return beacons.toString();
    }
}
