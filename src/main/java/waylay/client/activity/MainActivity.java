package waylay.client.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import waylay.client.sensor.AbstractLocalSensor;
import waylay.client.sensor.AccelerometerSensor;
import waylay.client.sensor.ActivitySensor;
import waylay.client.sensor.BeaconSensor;
import waylay.client.sensor.LocationSensor;
import waylay.client.sensor.ForceSensor;
import waylay.client.sensor.RawSensor;
import waylay.client.sensor.SensorListener;
import waylay.client.service.ActivityManager;

import com.estimote.sdk.BeaconManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import waylay.client.R;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Window;

public class MainActivity extends BaseActivity implements SensorEventListener, SensorListener,
        SensorsFragement.OnFragmentInteractionListener,
        SetupFragment.OnFragmentInteractionListener{

	public static final String TAG = "MainActivity";

    private static final String FRAGMENT_TAG_SCENARIOS = "tasks";
    private static final String FRAGMENT_TAG_SENSORS = "sensors";
    private static final String FRAGMENT_TAG_SETUP = "setup";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static List<AbstractLocalSensor> listLocalSensors = new ArrayList<AbstractLocalSensor>();

    //sensors
    private LocationSensor locationSensor = new LocationSensor();

    private AccelerometerSensor accelometerSensor = new AccelerometerSensor();
    private ForceSensor velocitySensor = new ForceSensor();
    private BeaconSensor beaconSensor = new BeaconSensor();
    private ActivitySensor activitySensor = new ActivitySensor();

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private BeaconManager beaconManager;
    private ActivityManager activityManager;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        beaconManager = new BeaconManager(getApplicationContext());
        activityManager = new ActivityManager(getApplicationContext());

        initLocalSensors();

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        // does not work any more
        actionBar.setIcon(R.drawable.ic_waylay_full);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBar.Tab tab = actionBar.newTab()
                .setText("Tasks")
                .setTabListener(new DefaultTabListener<TasksFragment>(
                        this, FRAGMENT_TAG_SCENARIOS, TasksFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Sensors")
                .setTabListener(new DefaultTabListener<SensorsFragement>(
                        this, FRAGMENT_TAG_SENSORS, SensorsFragement.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Setup")
                .setTabListener(new DefaultTabListener<SetupFragment>(
                        this, FRAGMENT_TAG_SETUP, SetupFragment.class));
        actionBar.addTab(tab);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            int MY_PERMISSIONS_ACCESS_LOCATION = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_LOCATION);
            // MY_PERMISSIONS_ACCESS_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
	}


    @Override
    public void onSensorUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SensorsFragement fragment = fragmentByTag(FRAGMENT_TAG_SENSORS);
                if (fragment != null) {
                    fragment.update();
                }
            }
        });

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        startSensors();
        onServerChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSensors();
    }

    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                updateSensorEvent(event);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                updateSensorEvent(event);
                break;
            default:
                Log.i(TAG, "ignoring sensor type: " + event.sensor.getType());
        }
	}

    @Override
	public void stopPush(){
        getWaylayApplication().stopPushing();
	}

    @Override
    public void pushAll() {
        for(AbstractLocalSensor sensor:listLocalSensors){
            getWaylayApplication().startPushing(sensor);
        }
    }

    @Override
    public void onServerChange() {
        TasksFragment fragment = fragmentByTag(FRAGMENT_TAG_SCENARIOS);
        if(fragment != null) {
            fragment.refreshAllScenarios();
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }

        }

    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Activity Recognition", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(
                        getSupportFragmentManager(),
                        "Activity Recognition");
            }
            return false;
        }
    }

    private void initLocalSensors() {
        listLocalSensors.clear();
        listLocalSensors.add(locationSensor);
        listLocalSensors.add(accelometerSensor);
        listLocalSensors.add(velocitySensor);
        listLocalSensors.add(beaconSensor);
        listLocalSensors.add(activitySensor);

        for(Sensor sensor:sensorManager.getSensorList(Sensor.TYPE_ALL)){
            // TODO find out how to know a sensor is a trigger sensor: Trigger Sensors should use the requestTriggerSensor
            Log.i(TAG, "Adding raw sensor " + sensor.getType() + " " + sensor.getName());
            listLocalSensors.add(new RawSensor(sensor));
        }
    }


    private void updateSensorEvent(SensorEvent event) {
        float[] values = event.values;
        long actualTime = event.timestamp;
        if(accelometerSensor.isTilt(event.values))
            return;
        if ((actualTime - velocitySensor.getLastUpdate()) * AbstractLocalSensor.NS2S > .1) {
            velocitySensor.updateData(actualTime, values);
            accelometerSensor.updateData(actualTime, values);
            onSensorUpdate();
        }
    }

    private void startSensors() {

        SensorListener buffered = new BufferingSensorListener(this, 1, TimeUnit.SECONDS);

        locationSensor.start(getApplication(), locationManager, buffered);
        beaconSensor.start(beaconManager, buffered);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        for(AbstractLocalSensor sensor:listLocalSensors){
            if(sensor instanceof RawSensor){
                RawSensor rawSensor = (RawSensor)sensor;
                rawSensor.start(sensorManager, buffered);
            }
        }


        if(servicesConnected()){
            activitySensor.start(activityManager);
        }
    }

    private void stopSensors() {
        locationSensor.stop(locationManager);
        beaconSensor.stop(beaconManager);
        sensorManager.unregisterListener(this);
        for(AbstractLocalSensor sensor:listLocalSensors){
            if(sensor instanceof RawSensor){
                RawSensor rawSensor = (RawSensor)sensor;
                rawSensor.stop(sensorManager);
            }
        }
        if(servicesConnected()){
            activitySensor.stop(activityManager);
        }
    }

    private static class BufferingSensorListener implements SensorListener{

        // for avoiding too fast updates, create wrapper
        private volatile long last = System.currentTimeMillis();

        private final long delay;
        private final SensorListener delegate;

        private BufferingSensorListener(final SensorListener delegate, long delay, TimeUnit timeUnit) {
            this.delegate = delegate;
            this.delay = timeUnit.toMillis(delay);
        }

        @Override
        public void onSensorUpdate() {
            long now = System.currentTimeMillis();
            if(now - last > delay) {
                last = now;
                delegate.onSensorUpdate();
            }
        }
    }

}