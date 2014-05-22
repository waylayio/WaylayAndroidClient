package waylay.client.sensor;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationSensor extends AbstractLocalSensor implements LocationListener{

    private static final String TAG = "LocationSensor";

    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private String provider = LocationManager.NETWORK_PROVIDER;

	private Location location;

    private Runnable listener;
	
	public LocationSensor(){
		
	}

    public void start(final LocationManager locationManager, final Runnable listener) {
        Log.i(TAG, "Starting " + this + " with " + locationManager);
        this.listener = listener;

        // TODO what should be done with this
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;


        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network");

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                provider = LocationManager.NETWORK_PROVIDER;
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    provider = LocationManager.GPS_PROVIDER;
                }
            }
        }

        // Initialize the location fields
        if (location != null) {
            Log.d(TAG, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.w(TAG, "Location not available");
        }
    }

    public void stop(LocationManager locationManager){
        Log.i(TAG, "Stopping " + this + " with " + locationManager);
        locationManager.removeUpdates(this);
        location = null;
    }

	public String getStatus(){
		if(location == null)
			return "Searching";
		return "OK";
	}

	@Override
	public String toString() {
        if(location == null){
            return "[unknown]";
        }
		return "[latitude=" + location.getLatitude() + ", longitude=" + location.getLongitude() + "]";
	}

	@Override
	public int getId() {
		return 1;
	}
	
	public String getName(){
		return "Location";
	}
	@Override
	public Map<String, String> getRuntimeData() {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
        if(location != null) {
            map.put("runtime_latitude", Double.toString(location.getLatitude()));
            map.put("runtime_longitude", Double.toString(location.getLongitude()));
        }
		return map;
	}


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        listener.run();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // do we want to handle this?
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Enabled new provider " + provider);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Disabled provider " + provider);
    }

}
