package waylay.client.sensor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import waylay.client.WaylayApplication;
import static android.support.v4.content.ContextCompat.*;

public class LocationSensor extends AbstractLocalSensor implements LocationListener {

    private static final String TAG = "LocationSensor";

    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds

    private String provider = LocationManager.NETWORK_PROVIDER;

    private Location location;

    private SensorListener listener;

    private LocationManager locationManager;

    private Context context;

    public LocationSensor() {

    }

    public void start(Context context, LocationManager locationManager, final SensorListener listener) {
        Log.i(TAG, "Starting " + this + " with " + locationManager);
        this.listener = listener;
        this.locationManager = locationManager;
        this.context = context;
        init();
    }

    private void init() {
        // TODO what should be done with this
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled

        } else {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                Log.i(TAG, "Could not start location updates as we don' have the needed permisission");

                // TODO we need to keep repeating this until the permission is granted

                return;
            }
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
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
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
	public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        if(location != null) {
            data.put("latitude", Double.toString(location.getLatitude()));
            data.put("longitude", Double.toString(location.getLongitude()));
        } else {
            init();
        }
		return data;
	}


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        listener.onSensorUpdate();
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
