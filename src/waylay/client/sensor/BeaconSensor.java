package waylay.client.sensor;

import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeaconSensor extends AbstractLocalSensor {

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", null/*ESTIMOTE_PROXIMITY_UUID*/, null, null);

    private static final String TAG = "BeaconSensor";

    // TODO we probably want to store the proximity as well
    private List<Beacon> beacons = Collections.emptyList();

    private boolean active = false;

    private SensorListener listener;

    public BeaconSensor(){
        Log.i(TAG, "new BeaconSensor");
    }

    public void start(final BeaconManager beaconManager, final SensorListener listener){
        Log.i(TAG, "Starting " + this + " with " + beaconManager);
        this.listener = listener;

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                updateData(beacons);
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    active = true;
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                    active = false;
                }
            }
        });
    }

    public void stop(final BeaconManager beaconManager){
        Log.i(TAG, "Stopping " + this + " with " + beaconManager);
        beaconManager.disconnect();
        active = false;
        this.listener = null;
    }

    @Override
    public String getStatus() {
        if(active)
            return "OK";
        return "Not active";
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
        JsonObject data = new JsonObject();
        for (Beacon beacon : beacons) {
            data.addProperty("proximityUUID", beacon.getProximityUUID());
            data.addProperty("name", beacon.getName());
            data.addProperty("macAddress", beacon.getMacAddress());
            data.addProperty("major", beacon.getMajor());
            data.addProperty("minor", beacon.getMinor());
            data.addProperty("power", beacon.getMeasuredPower());
            data.addProperty("rssi", beacon.getRssi());
        }

        Map<String, String> map = new ConcurrentHashMap<String, String>();
        Gson gson = new Gson();
        map.put("runtime_beacons", gson.toJson(data));
        return map;
    }

    private void updateData(final List<Beacon> beacons) {
        this.beacons = beacons;
        if(listener != null){
            listener.onSensorUpdate();
        }else{
            // can take a while for unregistration
        }
    }

    @Override
    public String toString() {
        return beacons.toString();
    }
}
