package waylay.client.activity;


import java.util.ArrayList;
import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.sensor.AbstractLocalSensor;
import waylay.client.sensor.AccelerometerSensor;
import waylay.client.sensor.BeaconSensor;
import waylay.client.sensor.LocationSensor;
import waylay.client.sensor.ForceSensor;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.waylay.client.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements SensorEventListener, Runnable,
        SensorsFragement.OnFragmentInteractionListener,
        SetupFragment.OnFragmentInteractionListener{

	public static final String TAG = "Main Manager";

    private static final String FRAGMENT_TAG_SCENARIOS = "scenarios";
    private static final String FRAGMENT_TAG_SENSORS = "sensors";
    private static final String FRAGMENT_TAG_SETUP = "setup";

    public static List<AbstractLocalSensor> listLocalSensors = new ArrayList<AbstractLocalSensor>();

    //sensors
    private LocationSensor locationSensor = new LocationSensor();

    private AccelerometerSensor accelometerSensor = new AccelerometerSensor();
    private ForceSensor velocitySensor = new ForceSensor();
    private BeaconSensor beaconSensor = new BeaconSensor();

    private SensorManager sensorManager;

    private LocationManager locationManager;

    private BeaconManager beaconManager;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        beaconManager = new BeaconManager(getApplicationContext());

        initLocalSensors();

        ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setIcon(R.drawable.ic_waylay_full);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab()
                .setText("Scenarios")
                .setTabListener(new TabListener<ScenariosFragment>(
                        this, FRAGMENT_TAG_SCENARIOS, ScenariosFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Sensors")
                .setTabListener(new TabListener<SensorsFragement>(
                        this, FRAGMENT_TAG_SENSORS, SensorsFragement.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Setup")
                .setTabListener(new TabListener<SetupFragment>(
                        this, FRAGMENT_TAG_SETUP, SetupFragment.class));
        actionBar.addTab(tab);
	}


    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

    /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }

    @Override
    public void run() {
        onSensorUpdate();
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationSensor.start(locationManager, this);
        beaconSensor.start(beaconManager, this);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);


        onServerChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationSensor.stop(locationManager);
        beaconSensor.stop(beaconManager);
        sensorManager.unregisterListener(this);
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

    @Override
	public void stopPush(){
        WaylayApplication.stopPushing();
	}

    @Override
    public void onServerChange() {
        ScenariosFragment fragment = fragmentByTag(FRAGMENT_TAG_SCENARIOS);
        if(fragment != null) {
            fragment.refreshAllScenarios();
        }
    }

    private void onSensorUpdate(){
        SensorsFragement fragment = fragmentByTag(FRAGMENT_TAG_SENSORS);
        if(fragment != null) {
            fragment.update();
        }
    }

    private void initLocalSensors() {
        listLocalSensors.remove(locationSensor);
        listLocalSensors.add(locationSensor);
        listLocalSensors.remove(accelometerSensor);
        listLocalSensors.add(accelometerSensor);
        listLocalSensors.remove(velocitySensor);
        listLocalSensors.add(velocitySensor);
        listLocalSensors.remove(beaconSensor);
        listLocalSensors.add(beaconSensor);

        locationSensor.start(locationManager, this);
        beaconSensor.start(beaconManager, this);


        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);


    }


}