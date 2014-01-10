package waylay.client.activity;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import waylay.client.data.BackupInfo;
import waylay.client.data.DiskStats;
import waylay.client.data.MachineInfo;
import waylay.client.data.ReplicationInfo;
import waylay.client.data.ResourceUsage;
import waylay.client.data.BayesServer;
import waylay.client.data.StorageUsage;
import waylay.client.data.UserInfo;
import waylay.client.scenario.Scenario;
import waylay.client.sensor.AccelerometerSensor;
import waylay.client.sensor.LocalSensor;
import waylay.client.sensor.LocationSensor;
import waylay.client.sensor.ForceSensor;
import waylay.rest.GetResponseCallback;
import waylay.rest.Dashboard;
import waylay.rest.Machine;
import waylay.rest.PostResponseCallback;
import waylay.rest.RestAPI;
import com.waylay.client.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements LocationListener, SensorEventListener  {
	public static final String TAG = "Main Manager";


	UserFactory userFactory;

	//private Button mAddUserButton;
	private Button mSyncButton;
	public static ArrayList<UserInfo> listUsers = new ArrayList<UserInfo>();
	public static ArrayList<MachineInfo> listMachines = new ArrayList<MachineInfo>();
	//public static ArrayList<Scenario> listScenarios = new ArrayList<Scenario>();
	public static ArrayList<BayesServer> listServers = new ArrayList<BayesServer>();
	public static ArrayList<LocalSensor> listLocalSensors = new ArrayList<LocalSensor>();
	public static UserAdapter adapterUsers;
	public static MachineAdapter adapterMachines;
	public static SetupAdapter adapterSetup;
	public static ScenarioAdapter adapterScenarios;
	public static SensorAdapter adapterLocalSensors;
	ListView mLocalSensorList;
	ListView mUserList;
	ListView mMachineList;
	ListView mScenarioList;
	ListView serverList;


	protected Object mMachineActionMode;
	protected Object mScenarioActionMode;
	protected Object mUserActionMode;
	protected Object mSetupActionMode;


	protected static String alertMessage = "default alert message";

	protected static UserInfo selectedUser = null;
	protected static MachineInfo selectedMachine = null;
	protected static Scenario selectedScenario = null;
	protected static BackupInfo backupInfo;
	protected static ReplicationInfo replicationInfo;
	protected static ResourceUsage resourceUsage;
	protected static ArrayList<StorageUsage> storageUsage;
	protected static DiskStats diskStats;
	protected static BayesServer bayesServer = null;

	//sensors
	protected LocationSensor locationSensor = new LocationSensor();
	protected AccelerometerSensor accelometerSensor = new AccelerometerSensor();
	protected ForceSensor velocitySensor = new ForceSensor();
	private LocationManager locationManager;
	private SensorManager sensorManager;
	private String provider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//initUsers();
		//initMachines();
		initScenarios();
		initLocalSensors();
		initServer();

		TabHost tabHost = getTabHost(); 
		TabHost.TabSpec spec;

		mSyncButton = (Button) findViewById(R.id.buttonSyncWithServer);
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

		mSyncButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(bayesServer != null){
					//getAllSSOMachines(bayesServer.constructURLtoListAllMachines(), bayesServer.getName(), bayesServer.getPassword());
					getAllScenarios(bayesServer.constructURLtoListAllScenarios(), bayesServer.getName(), bayesServer.getPassword());
				}
			}
		});


		spec = tabHost.newTabSpec("scenarios").setIndicator("Scenarios").setContent(R.id.Machines);        
		//spec.setIndicator("Machines", getResources().getDrawable(R.drawable.icon_machine));
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("sensors").setIndicator("Sensors").setContent(R.id.Users);        
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("setup").setIndicator("Setup").setContent(R.id.Setup);                   
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	protected void alert(String error) {
		alertMessage = error;
		Intent i = new Intent(this, AlertDialogActivity.class);
		startActivity(i);   

	}
	
	private void initLocalSensors() {
		listLocalSensors.remove(locationSensor);
		listLocalSensors.add(locationSensor);
		listLocalSensors.remove(accelometerSensor);
		listLocalSensors.add(accelometerSensor);
		listLocalSensors.remove(velocitySensor);
		listLocalSensors.add(velocitySensor);
		mLocalSensorList = (ListView) findViewById(R.id.listUsers);
		adapterLocalSensors = new SensorAdapter(this, listLocalSensors);
		mLocalSensorList.setAdapter(adapterLocalSensors);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

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

	private void initUsers() {
		mUserList = (ListView) findViewById(R.id.listUsers);
		adapterUsers = new UserAdapter(this, listUsers);
		mUserList.setAdapter(adapterUsers);

		mUserList.setClickable(true);   
		mUserList.setOnItemClickListener(new UserViewListener());  

		mUserList.setOnItemLongClickListener(new OnItemLongClickListener() {
			// Called when the user long-clicks on someView
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mUserActionMode != null) {
					return false;
				}
				selectedUser = (UserInfo) mUserList.getItemAtPosition(position);
				mUserActionMode = startActionMode(mUserActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
	}

	private void initMachines() {
		mMachineList = (ListView) findViewById(R.id.listMachines);
		adapterMachines = new MachineAdapter(this, listMachines);
		mMachineList.setAdapter(adapterMachines);

		mMachineList.setClickable(true);   
		mMachineList.setOnItemClickListener(new MyMachineViewUserListener(this, UsersActivity.class));  

		mMachineList.setOnItemLongClickListener(new OnItemLongClickListener() {
			// Called when the user long-clicks on someView
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				if (mMachineActionMode!= null) {
					return false;
				}
				selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
				mMachineActionMode = startActionMode(mMachineActionModeCallback);
				view.setSelected(true);
				return true;

			}
		});
	}
	
	private void initScenarios() {
		mScenarioList = (ListView) findViewById(R.id.listMachines);
		adapterScenarios = new ScenarioAdapter(this, ScenarioFactory.getsScenarios());
		mScenarioList.setAdapter(adapterScenarios);

		mScenarioList.setClickable(true);   
		mScenarioList.setOnItemClickListener(new MyScenarioViewUserListener(this, ScenarioActivity.class));  

		mScenarioList.setOnItemLongClickListener(new OnItemLongClickListener() {
			// Called when the user long-clicks on someView
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				/*if (mScenarioActionMode!= null) {
					return false;
				}*/
				selectedScenario = (Scenario) mScenarioList.getItemAtPosition(position);
				startActionMode(MyScenarioActionModeCallback);
				view.setSelected(true);
				return true;

			}
		});
	}


	private void initServer() {
		bayesServer = new BayesServer("54.235.253.99/api", "admin", "admin");
		if(listServers.size() == 0){
			listServers.add(bayesServer);
		}
		serverList = (ListView) findViewById(R.id.listSSO);
		adapterSetup = new SetupAdapter(this, listServers);
		serverList.setAdapter(adapterSetup);

		serverList.setClickable(true);   
		serverList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startBrowser();
			}
		});

		serverList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mSetupActionMode != null) {
					return false;
				}
				mSetupActionMode = startActionMode(mSSOActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
	}

	protected void launchUserAdder() {
		Intent i = new Intent(this, UsersActivity.class);
		startActivity(i);
	}

	protected void deleteUserItem() {
		if(selectedUser != null){
			listUsers.remove(selectedUser);
			UserFactory.removeUser(selectedUser);
		}
		adapterUsers.notifyDataSetChanged(); 
	}

	protected void connectUserItem() {
		if(selectedUser != null && selectedUser.getMachine() != null && selectedUser.getMachine().getIpAddress() != null){

			if(ConnectionType.RDP.equals(selectedUser.getConnectionType())){
				/*RDPWrapperActivity rdpWrapperActivity = new RDPWrapperActivity(this, selectedUser.getMachine().getIpAddress(), 
						selectedUser.getMachine().getPort(), selectedUser.getName(), selectedUser.getPassword());
				String ret = rdpWrapperActivity.startRDPSession();
				if(!"".equals(ret)){
					alert(ret);  	
				}*/
			} else {
				try{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://"+selectedUser.getName()+"@"+selectedUser.getMachine().getIpAddress()+
							":22/#sso")));
				} catch (ActivityNotFoundException e){
					alert(e.getMessage());
				}
			}
		} else{
			Log.e(TAG, "machine for user "+selectedUser.getName() + " not defined");
		}
	}


	protected void launchMachineAdder() {
		Intent i = new Intent(this, MachinesActivity.class);
		startActivity(i);
	}

	protected void deleteMachineItem() {
		if(selectedMachine != null){
			listMachines.remove(selectedMachine);
			MachineFactory.removeMachine(selectedMachine);
		}
		adapterMachines.notifyDataSetChanged(); 
	}

	public void getAllSSOMachines(String URL, String name, String password){ 
		Log.d(TAG, "getAllSSOMachines");

		RestAPI ssoAPI = RestAPI.getInstance();
		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);

		String filters [] = { "?machinetype=VIRTUALSERVER", "?machinetype=VIRTUALDESKTOP" };
		for(int i = 0 ; i < filters.length; i ++) {

			ssoAPI.getMachines(URL, name, filters[i] , password, new GetResponseCallback() {
				@Override
				public
				void onDataReceived(ArrayList machines, boolean error, String message) {
					Log.i(TAG, "Received response for machines: "+ machines.size());

					if(!error){
						for(Object o : machines){
							Machine m = (Machine) o;
							if(!"IMAGEONLY".equals(m.getStatus())) {
								MachineInfo machine = new MachineInfo(m.getName(), m.getHostname(), 3389l, m.getGuid(), m.getStatus());
								MachineFactory.addMachine(machine);
							}
						}
						progress.dismiss();
						updateMachines();
						for(MachineInfo m: listMachines){
							Log.i(TAG, "Update Machine "+ m.getName());
							updateAllMachinesWithIp(bayesServer.constructURLtoGetIpAddress(), m, bayesServer.getName(), bayesServer.getPassword());
						}	
					}
					else{
						progress.dismiss();
						alert(message);
					}

				}

				@Override
				public void onUpdate(boolean error, String message) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDashboardReceived(Dashboard dashboard,
						boolean error, String message) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	public void updateAllMachinesWithIp(String URL, MachineInfo m, String name, String password){  
		RestAPI ssoAPI = RestAPI.getInstance();
		ssoAPI.getIPAddressesForMachine(m, URL, name, password, new GetResponseCallback() {

			@Override
			public void onUpdate(boolean error, String message) {
				if(!error){
					updateMachines();			
				} else{
					alert(message);
				}

			}

			@Override
			public void onDataReceived(ArrayList machines,
					boolean error, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDashboardReceived(Dashboard dashboard,
					boolean error, String message) {
			}

		});

	}


	protected void updateMachines() {
		//TODO still need to make list a set, I hate this
		for(MachineInfo m1: listMachines){
			MachineFactory.addMachine(m1);
		}
		listMachines.clear();
		listMachines.addAll(MachineFactory.getMachines());
		adapterMachines.notifyDataSetChanged(); 
	}

	protected void launchSSOSetup() {
		Intent i = new Intent(this, SetupActivity.class);
		startActivity(i);
	}
	
	protected void updateScenarios() {
		adapterScenarios.notifyDataSetChanged(); 
	}
	
	public void getAllScenarios(String URL, String name, String password){ 
		Log.d(TAG, "getAllScenarios");
		ScenarioFactory.clear();

		RestAPI restAPI = RestAPI.getInstance();
		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);

		restAPI.getScenarios(URL, name, "" , password, new GetResponseCallback() {
			@Override
			public
			void onDataReceived(ArrayList scenarios, boolean error, String message) {
				Log.i(TAG, "Received response for scenarios: "+ scenarios.size());

				if(!error){
					for(Object o : scenarios){
						ScenarioFactory.addScenario((Scenario) o);
					}
					progress.dismiss();
					updateScenarios();	
				}
				else{
					progress.dismiss();
					alert(message);
				}
			}

			@Override
			public void onUpdate(boolean error, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDashboardReceived(Dashboard dashboard,
					boolean error, String message) {
				// TODO Auto-generated method stub

			}

		});

	}
	
	public void getScenario(String URL, String name, String password){ 
		Log.d(TAG, "getAllScenarios");
		
		RestAPI restAPI = RestAPI.getInstance();
		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);

		restAPI.getScenario(URL, name, "" , password, new GetResponseCallback() {
			@Override
			public
			void onDataReceived(ArrayList scenarios, boolean error, String message) {
				Log.i(TAG, "Received response for scenarios: "+ scenarios.size());

				if(!error){
					for(Object o : scenarios){
						ScenarioFactory.addScenario((Scenario) o);
					}
					progress.dismiss();
					updateScenarios();	
				}
				else{
					progress.dismiss();
					alert(message);
				}
			}

			@Override
			public void onUpdate(boolean error, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDashboardReceived(Dashboard dashboard,
					boolean error, String message) {
				// TODO Auto-generated method stub

			}

		});

	}
	
	private class MyScenarioViewUserListener implements OnItemClickListener{

		private Activity activity;
		private Class m_class;

		public MyScenarioViewUserListener(Activity activity, Class c){
			this.activity = activity;
			m_class = c;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			selectedScenario = (Scenario) mScenarioList.getItemAtPosition(position);
			Intent i = new Intent(activity, m_class);
			startActivity(i);

		}	
	}
	
	private ActionMode.Callback MyScenarioActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.layout.menu_scenario, menu);

			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.itemDeleteScenario:
				mode.finish(); 
				actioScenarioItem("delete");
				return true;
			case R.id.itemStartScenario:
				mode.finish(); 
				actioScenarioItem("start");
				return true;
			case R.id.itemStopScenario:
				mode.finish(); 
				actioScenarioItem("stop");
				return true;
			default:
				return false;
			}
		}

		private void actioScenarioItem(String action) {
			Long id = MainActivity.selectedScenario.getId();
			final RestAPI rest = RestAPI.getInstance();
			final String url = bayesServer.constructURLtoForScenario(id);
			if("stop".equals(action) || "start".equals(action)){
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("action", action));
				rest.postScenarioAction(url, nameValuePairs, new PostResponseCallback(){
					@Override
					public void onPostSuccess() {
						Log.i(TAG, "action was success");
						getScenario(url, "", "");
					}});
			} else if("delete".equals(action)){
				rest.deleteScenarioAction(url,  new PostResponseCallback(){
					@Override
					public void onPostSuccess() {
						Log.i(TAG, "action was success");
						ScenarioFactory.removeScenario(MainActivity.selectedScenario);
						MainActivity.selectedScenario = null;
						updateScenarios();
					}});
			}
			
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMachineActionMode = null;
		}
	};


	private class UserViewListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			selectedUser = (UserInfo) mUserList.getItemAtPosition(position);
			connectUserItem();

		}

	}

	private class MyGenericViewUserListener implements OnItemClickListener{

		private Activity activity;
		private Class m_class;

		public MyGenericViewUserListener(Activity activity, Class c){
			this.activity = activity;
			m_class = c;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			if(m_class.getName().contains("UsersActivity")){
				selectedUser = (UserInfo) mUserList.getItemAtPosition(position);	
			} else if (m_class.getName().contains("Machine")){
				selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
			} else {
				bayesServer = (BayesServer) serverList.getItemAtPosition(position);
			}
			Intent i = new Intent(activity, m_class);
			startActivity(i);

		}	
	}

	private class MyMachineViewUserListener implements OnItemClickListener{

		private Activity activity;
		private Class m_class;

		public MyMachineViewUserListener(Activity activity, Class c){
			this.activity = activity;
			m_class = c;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			selectedUser = null;	
			selectedMachine = (MachineInfo) mMachineList.getItemAtPosition(position);
			Intent i = new Intent(activity, m_class);
			startActivity(i);

		}	
	}

	private ActionMode.Callback mMachineActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.layout.menu_item, menu);

			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.itemDeleteMachine:
				deleteMachineItem();
				mode.finish(); // Action picked, so close the CAB
				return true;
			case R.id.itemEditMachine:
				launchMachineAdder();
				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMachineActionMode = null;
		}
	};

	private ActionMode.Callback mUserActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.layout.menu_users, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.itemDeleteUser:
				deleteUserItem();
				mode.finish(); // Action picked, so close the CAB
				return true;
			case R.id.itemEditUser:
				launchUserAdder();
				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mUserActionMode = null;
		}
	};



	private ActionMode.Callback mSSOActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.layout.menu_sso, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.itemInfo:
				launchInfo();
				mode.finish(); 
				return true;
			case R.id.itemEditSSO:
				launchSSOSetup();
				mode.finish(); 
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mSetupActionMode = null;
		}
	};


	protected void startBrowser() {
		Log.d(TAG, "startBrowser");
		Uri marketUri = Uri.parse("http://"+bayesServer.getURL());
		Intent marketIntent = new
				Intent(Intent.ACTION_VIEW).setData(marketUri);
		startActivity(marketIntent);

	}

	protected void launchInfo() {
		RestAPI ssoAPI = RestAPI.getInstance();
		Dashboard ssoDashboard = new Dashboard();
		final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
		ssoAPI.getDashboardData(bayesServer.constructURLtoGetDashboardData(), bayesServer.getName(), 
				bayesServer.getPassword(), ssoDashboard , new GetResponseCallback() {

			@Override
			public void onDashboardReceived(Dashboard dashboard, boolean error, String message) {
				Log.i(TAG, "Received response for dashboard");
				backupInfo = dashboard.getBackupInfo();
				replicationInfo = dashboard.getReplicationInfo();
				storageUsage = dashboard.getStorageUsage();
				resourceUsage = dashboard.getResourceUsage();
				if(!error && dashboard.isValid()){
					Intent i = new Intent(MainActivity.this, PlotActivity.class);
					startActivity(i);	
				} else {
					alert("SSO connection not successful" + message);
				}
				progress.dismiss();
			}

			@Override
			public void onDataReceived(ArrayList machines,
					boolean error, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(boolean error, String message) {
				// TODO Auto-generated method stub

			}

		});

	}

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
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    sensorManager.registerListener(this,
	            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(this,
	            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
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
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
		      updateSensorEvent(event);
		      return; //don't try the other one ...
		}
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			updateSensorEvent(event);
		}
	}

	private void updateSensorEvent(SensorEvent event) {
		float[] values = event.values;
		long actualTime = event.timestamp;
		if(accelometerSensor.isTilt(event.values))
			return;
	    //if ((actualTime - velocitySensor.getLastUpdate()) * LocalSensor.NS2S > .02) {
	    	velocitySensor.updateData(actualTime, values);
	    	adapterLocalSensors.notifyDataSetChanged();
	    	accelometerSensor.updateData(actualTime, values);
	    	adapterLocalSensors.notifyDataSetChanged(); 
	   // }
	}
	

}