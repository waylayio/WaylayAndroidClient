package waylay.client.activity;


import java.util.ArrayList;

import waylay.client.WaylayApplication;
import waylay.client.data.BackupInfo;
import waylay.client.data.DiskStats;
import waylay.client.data.MachineInfo;
import waylay.client.data.ReplicationInfo;
import waylay.client.data.ResourceUsage;
import waylay.client.data.StorageUsage;
import waylay.client.data.UserInfo;
import waylay.client.sensor.AccelerometerSensor;
import waylay.client.sensor.BeaconSensor;
import waylay.client.sensor.LocalSensor;
import waylay.client.sensor.LocationSensor;
import waylay.client.sensor.ForceSensor;

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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends BaseActivity implements LocationListener, SensorEventListener,
        SensorsFragement.OnFragmentInteractionListener,
        SetupFragment.OnFragmentInteractionListener,
        ScenariosFragment.OnFragmentInteractionListener {
	public static final String TAG = "Main Manager";

    private static final String FRAGMENT_TAG_SCENARIOS = "scenarios";
    private static final String FRAGMENT_TAG_SENSORS = "sensors";
    private static final String FRAGMENT_TAG_SETUP = "setup";

    //UserFactory userFactory;

	//private Button mAddUserButton;

    public static ArrayList<LocalSensor> listLocalSensors = new ArrayList<LocalSensor>();


    public static ArrayList<UserInfo> listUsers = new ArrayList<UserInfo>();
    public static ArrayList<MachineInfo> listMachines = new ArrayList<MachineInfo>();
	public static UserAdapter adapterUsers;
	public static MachineAdapter adapterMachines;



    public static SensorAdapter adapterLocalSensors;


//	ListView mUserList;
//	ListView mMachineList;




	//protected Object mScenarioActionMode;
//	protected Object mUserActionMode;

    //sensors
    protected LocationSensor locationSensor = new LocationSensor();
    protected Criteria criteria = new Criteria();
    // The minimum distance to change Updates in meters
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected AccelerometerSensor accelometerSensor = new AccelerometerSensor();
    protected ForceSensor velocitySensor = new ForceSensor();
    protected BeaconSensor beaconSensor = new BeaconSensor();

    private String provider = LocationManager.NETWORK_PROVIDER;

	protected static UserInfo selectedUser = null;
	protected static MachineInfo selectedMachine = null;

	protected static BackupInfo backupInfo;
	protected static ReplicationInfo replicationInfo;
	protected static ResourceUsage resourceUsage;
	protected static ArrayList<StorageUsage> storageUsage;
	protected static DiskStats diskStats;



    public static LocalSensor selectedLocalSensor;



    private SensorManager sensorManager;

    private LocationManager locationManager;



    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		//initUsers();
		//initMachines();

        //
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        adapterLocalSensors = new SensorAdapter(this, MainActivity.listLocalSensors);




        initLocalSensors();

//		TabHost tabHost = getTabHost();
//		TabHost.TabSpec spec;


	
		
		/*mAddUserButton = (Button) findViewById(R.id.buttonAddUsers);
		//mAddMachineButton = (Button) findViewById(R.id.buttonSyncWithServer);
		

		mAddUserButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "mAddUserButton clicked");
				selectedUser = null;
				selectedMachine = null;
				launchUserAdder();
			}

		});*/

/*		mAddMachineButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "mAddMachineButton clicked");
				selectedUser = null;
				selectedMachine = null;
				launchMachineAdder();
			}

		});*/



//
//		spec = tabHost.newTabSpec("scenarios").setIndicator("Scenarios").setContent(R.id.Machines);
//		//spec.setIndicator("Machines", getResources().getDrawable(R.drawable.icon_machine));
//		tabHost.addTab(spec);
//
//		spec = tabHost.newTabSpec("sensors").setIndicator("Sensors").setContent(R.id.Users);
//		tabHost.addTab(spec);
//
//		spec = tabHost.newTabSpec("setup").setIndicator("Setup").setContent(R.id.Setup);
//		tabHost.addTab(spec);
//
//		tabHost.setCurrentTab(0);



        ActionBar actionBar = getActionBar();
        actionBar.show();
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

//	private void initUsers() {
//		mUserList = (ListView) findViewById(R.id.listUsers);
//		adapterUsers = new UserAdapter(this, listUsers);
//		mUserList.setAdapter(adapterUsers);
//
//		mUserList.setClickable(true);
//		mUserList.setOnItemClickListener(new UserViewListener());
//
//		mUserList.setOnItemLongClickListener(new OnItemLongClickListener() {
//			// Called when the user long-clicks on someView
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//				if (mUserActionMode != null) {
//					return false;
//				}
//				selectedUser = (UserInfo) mUserList.getItemAtPosition(position);
//				mUserActionMode = startActionMode(mUserActionModeCallback);
//				view.setSelected(true);
//				return true;
//			}
//		});
//	}
//
//	private void initMachines() {
//		mMachineList = (ListView) findViewById(R.id.listMachines);
//		adapterMachines = new MachineAdapter(this, listMachines);
//		mMachineList.setAdapter(adapterMachines);
//
//		mMachineList.setClickable(true);
//		mMachineList.setOnItemClickListener(new MyMachineViewUserListener(this, UsersActivity.class));
//
//		mMachineList.setOnItemLongClickListener(new OnItemLongClickListener() {
//			// Called when the user long-clicks on someView
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//				if (mMachineActionMode!= null) {
//					return false;
//				}
//				selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
//				mMachineActionMode = startActionMode(mMachineActionModeCallback);
//				view.setSelected(true);
//				return true;
//
//			}
//		});
//	}
	




//
//	protected void launchUserAdder() {
//		Intent i = new Intent(this, UsersActivity.class);
//		startActivity(i);
//	}

//	protected void deleteUserItem() {
//		if(selectedUser != null){
//			listUsers.remove(selectedUser);
//			UserFactory.removeUser(selectedUser);
//		}
//		adapterUsers.notifyDataSetChanged();
//	}

//	protected void connectUserItem() {
//		if(selectedUser != null && selectedUser.getMachine() != null && selectedUser.getMachine().getIpAddress() != null){
//
//			if(ConnectionType.RDP.equals(selectedUser.getConnectionType())){
//				/*RDPWrapperActivity rdpWrapperActivity = new RDPWrapperActivity(this, selectedUser.getMachine().getIpAddress(),
//						selectedUser.getMachine().getPort(), selectedUser.getName(), selectedUser.getPassword());
//				String ret = rdpWrapperActivity.startRDPSession();
//				if(!"".equals(ret)){
//					alert(ret);
//				}*/
//			} else {
//				try{
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://"+selectedUser.getName()+"@"+selectedUser.getMachine().getIpAddress()+
//							":22/#sso")));
//				} catch (ActivityNotFoundException e){
//					alert(e.getMessage());
//				}
//			}
//		} else{
//			Log.e(TAG, "machine for user "+selectedUser.getName() + " not defined");
//		}
//	}


//	protected void launchMachineAdder() {
//		Intent i = new Intent(this, MachinesActivity.class);
//		startActivity(i);
//	}
//
//	protected void deleteMachineItem() {
//		if(selectedMachine != null){
//			listMachines.remove(selectedMachine);
//			MachineFactory.removeMachine(selectedMachine);
//		}
//		adapterMachines.notifyDataSetChanged();
//	}
//
//	public void getAllSSOMachines(String URL, String name, String password){
//		Log.d(TAG, "getAllSSOMachines");
//
//		RestAPI ssoAPI = RestAPI.getInstance();
//		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
//
//		String filters [] = { "?machinetype=VIRTUALSERVER", "?machinetype=VIRTUALDESKTOP" };
//		for(int i = 0 ; i < filters.length; i ++) {
//
//			ssoAPI.getMachines(URL, name, filters[i] , password, new GetResponseCallback() {
//				@Override
//				public
//				void onDataReceived(ArrayList machines, boolean error, String message) {
//					Log.i(TAG, "Received response for machines: "+ machines.size());
//
//					if(!error){
//						for(Object o : machines){
//							Machine m = (Machine) o;
//							if(!"IMAGEONLY".equals(m.getStatus())) {
//								MachineInfo machine = new MachineInfo(m.getName(), m.getHostname(), 3389l, m.getGuid(), m.getStatus());
//								MachineFactory.addMachine(machine);
//							}
//						}
//						progress.dismiss();
//						updateMachines();
//						for(MachineInfo m: listMachines){
//							Log.i(TAG, "Update Machine "+ m.getName());
//							updateAllMachinesWithIp(bayesServer.constructURLtoGetIpAddress(), m, bayesServer.getName(), bayesServer.getPassword());
//						}
//					}
//					else{
//						progress.dismiss();
//						alert(message);
//					}
//
//				}
//
//				@Override
//				public void onUpdate(boolean error, String message) {
//
//				}
//
//				@Override
//				public void onDashboardReceived(Dashboard dashboard,
//						boolean error, String message) {
//
//				}
//			});
//		}
//
//	}
//
//	public void updateAllMachinesWithIp(String URL, MachineInfo m, String name, String password){
//		RestAPI ssoAPI = RestAPI.getInstance();
//		ssoAPI.getIPAddressesForMachine(m, URL, name, password, new GetResponseCallback() {
//
//			@Override
//			public void onUpdate(boolean error, String message) {
//				if(!error){
//					updateMachines();
//				} else{
//					alert(message);
//				}
//
//			}
//
//			@Override
//			public void onDataReceived(ArrayList machines,
//					boolean error, String message) {
//
//			}
//
//			@Override
//			public void onDashboardReceived(Dashboard dashboard,
//					boolean error, String message) {
//			}
//
//		});
//
//	}
//
//
//	protected void updateMachines() {
//		//TODO still need to make list a set, I hate this
//		for(MachineInfo m1: listMachines){
//			MachineFactory.addMachine(m1);
//		}
//		listMachines.clear();
//		listMachines.addAll(MachineFactory.getMachines());
//		adapterMachines.notifyDataSetChanged();
//	}
//




//	private class UserViewListener implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//				long arg3) {
//			selectedUser = (UserInfo) mUserList.getItemAtPosition(position);
//			connectUserItem();
//
//		}
//
//	}
//
//	private class MyGenericViewUserListener implements OnItemClickListener{
//
//		private Activity activity;
//		private Class m_class;
//
//		public MyGenericViewUserListener(Activity activity, Class c){
//			this.activity = activity;
//			m_class = c;
//		}
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//			if(m_class.getName().contains("UsersActivity")){
//				selectedUser = (UserInfo) mUserList.getItemAtPosition(position);
//			} else if (m_class.getName().contains("Machine")){
//				selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
//			} else {
//				bayesServer = (BayesServer) serverList.getItemAtPosition(position);
//			}
//			Intent i = new Intent(activity, m_class);
//			startActivity(i);
//
//		}
//	}
//
//	private class MyMachineViewUserListener implements OnItemClickListener{
//
//		private Activity activity;
//		private Class m_class;
//
//		public MyMachineViewUserListener(Activity activity, Class c){
//			this.activity = activity;
//			m_class = c;
//		}
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//			selectedUser = null;
//			selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
//			Intent i = new Intent(activity, m_class);
//			startActivity(i);
//
//		}
//	}

//	private ActionMode.Callback mMachineActionModeCallback = new ActionMode.Callback() {
//
//		// Called when the action mode is created; startActionMode() was called
//		@Override
//		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//			// Inflate a menu resource providing context menu items
//			MenuInflater inflater = mode.getMenuInflater();
//			inflater.inflate(R.layout.menu_item, menu);
//
//			return true;
//		}
//
//		// Called each time the action mode is shown. Always called after onCreateActionMode, but
//		// may be called multiple times if the mode is invalidated.
//		@Override
//		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//			return false; // Return false if nothing is done
//		}
//
//		// Called when the user selects a contextual menu item
//		@Override
//		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//			switch (item.getItemId()) {
//			case R.id.itemDeleteMachine:
//				deleteMachineItem();
//				mode.finish(); // Action picked, so close the CAB
//				return true;
//			case R.id.itemEditMachine:
//				launchMachineAdder();
//				mode.finish(); // Action picked, so close the CAB
//				return true;
//			default:
//				return false;
//			}
//		}
//
//		// Called when the user exits the action mode
//		@Override
//		public void onDestroyActionMode(ActionMode mode) {
//			mMachineActionMode = null;
//		}
//	};

//	private ActionMode.Callback mUserActionModeCallback = new ActionMode.Callback() {
//
//		// Called when the action mode is created; startActionMode() was called
//		@Override
//		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//			// Inflate a menu resource providing context menu items
//			MenuInflater inflater = mode.getMenuInflater();
//			inflater.inflate(R.layout.menu_users, menu);
//			return true;
//		}
//
//		// Called each time the action mode is shown. Always called after onCreateActionMode, but
//		// may be called multiple times if the mode is invalidated.
//		@Override
//		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//			return false; // Return false if nothing is done
//		}
//
//		// Called when the user selects a contextual menu item
//		@Override
//		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//			switch (item.getItemId()) {
//			case R.id.itemDeleteUser:
//				deleteUserItem();
//				mode.finish(); // Action picked, so close the CAB
//				return true;
//			case R.id.itemEditUser:
//				launchUserAdder();
//				mode.finish(); // Action picked, so close the CAB
//				return true;
//			default:
//				return false;
//			}
//		}
//
//		// Called when the user exits the action mode
//		@Override
//		public void onDestroyActionMode(ActionMode mode) {
//			mUserActionMode = null;
//		}
//	};
//
//
//


//	protected void launchInfo() {
//		Dashboard ssoDashboard = new Dashboard();
//		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
//		WaylayApplication.getRestService().getDashboardData(ssoDashboard, new GetResponseCallback<Dashboard>() {
//
//            @Override
//            public void onDataReceived(Dashboard dashboard, boolean error, String message) {
//                Log.i(TAG, "Received response for dashboard");
//                backupInfo = dashboard.getBackupInfo();
//                replicationInfo = dashboard.getReplicationInfo();
//                storageUsage = dashboard.getStorageUsage();
//                resourceUsage = dashboard.getResourceUsage();
//                if (!error && dashboard.isValid()) {
//                    Intent i = new Intent(MainActivity.this, PlotActivity.class);
//                    startActivity(i);
//                } else {
//                    alert("SSO connection not successful" + message);
//                }
//                progress.dismiss();
//            }
//
//            @Override
//            public void onUpdate(boolean error, String message) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
//
//	}

	@Override
	public void onLocationChanged(Location location) {
		locationSensor.setLatitude(location.getLatitude());
		locationSensor.setLongitude(location.getLongitude());
		adapterLocalSensors.notifyDataSetChanged(); 
	}

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(
                provider,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);


        onServerChange();
    }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	    sensorManager.unregisterListener(this);
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
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
	    if ((actualTime - velocitySensor.getLastUpdate()) * LocalSensor.NS2S > .1) {
	    	velocitySensor.updateData(actualTime, values);
	    	adapterLocalSensors.notifyDataSetChanged();
	    	accelometerSensor.updateData(actualTime, values);
	    	adapterLocalSensors.notifyDataSetChanged(); 
	    }
	}

    @Override
	public void stopPush(){
        WaylayApplication.stopPushing();
	}

    @Override
    public void onServerChange() {
        ScenariosFragment fragment = fragmentByTag(FRAGMENT_TAG_SCENARIOS);
        fragment.refreshAllScenarios();
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
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    provider = LocationManager.NETWORK_PROVIDER;
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        provider = LocationManager.GPS_PROVIDER;
                    }
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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);

    }
}