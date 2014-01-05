package waylay.client.activity;


import waylay.client.data.MachineInfo;
import waylay.client.data.UserInfo;

import com.waylay.client.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UsersActivity extends Activity {

	public static final String TAG = "User Manager";

	private Button mConnectButton;
	private EditText mUserName;
	private EditText mUserPassword;
	private Spinner machineSpinner;
	private Spinner connetionTypeSpinner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.users);

		mConnectButton = (Button) findViewById(R.id.buttonConnection);
		mUserName = (EditText) findViewById(R.id.userNameEditText);
		mUserPassword = (EditText) findViewById(R.id.passwordEditText);
		machineSpinner = (Spinner) findViewById(R.id.machineSpinner);
		connetionTypeSpinner = (Spinner) findViewById(R.id.ConnectionTypeSpinner);

		ArrayAdapter<MachineInfo> adapter;
		adapter = new ArrayAdapter<MachineInfo>(this, android.R.layout.simple_spinner_item, MachineFactory.getMachines());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		machineSpinner.setAdapter(adapter);

		ArrayAdapter<ConnectionType> adapterType;
		adapterType = new ArrayAdapter<ConnectionType>(this, android.R.layout.simple_spinner_item, 
				new ConnectionType[]{ConnectionType.RDP, ConnectionType.SSH});
		adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		connetionTypeSpinner.setAdapter(adapterType);

		if(MainActivity.selectedUser != null){

			Log.d(TAG, "selected existing user " + MainActivity.selectedUser);
			mUserName.setText((CharSequence) MainActivity.selectedUser.getName());
			mUserPassword.setText((CharSequence) MainActivity.selectedUser.getPassword());

			int spinnerPosition = adapter.getPosition(MainActivity.selectedUser.getMachine());
			machineSpinner.setSelection(spinnerPosition);

			spinnerPosition = adapterType.getPosition(MainActivity.selectedUser.getConnectionType());
			connetionTypeSpinner.setSelection(spinnerPosition);

		} else{
			if(MainActivity.selectedMachine != null){
				//we go from the window where there is no user selected, but we have machine
				mUserName.setText("");
				mUserPassword.setText("");
				int spinnerPosition = adapter.getPosition(MainActivity.selectedMachine);
				machineSpinner.setSelection(spinnerPosition);
				//try to find a user if exist already, regardless of the connection type
				for (UserInfo user: MainActivity.listUsers) {
					if(user.getMachine().equals(MainActivity.selectedMachine)){
						mUserName.setText(user.getName());
						mUserPassword.setText(user.getPassword());
						spinnerPosition = adapterType.getPosition(user.getConnectionType());
						connetionTypeSpinner.setSelection(spinnerPosition);
						break;
					}    	
				}
			}
			Log.d(TAG, "user not selected, and no machine, new user will be created");
		}


		// Register handler for UI elements
		mConnectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UserInfo userInfo = getCurrentUser();
				Log.d(TAG, "save user " + userInfo);

				UserFactory.removeUser(MainActivity.selectedUser);
				UserFactory.addUser(userInfo);
				// TODO I can't make listUsers a Set, since the adapter expects the list, to fix it later
				// right now recreating a list
				MainActivity.listUsers.clear();
				MainActivity.listUsers.addAll(UserFactory.getUsers());
				MainActivity.adapterUsers.notifyDataSetChanged();

				if(userInfo.getConnectionType().equals(ConnectionType.RDP)){
					Log.d(TAG, "start RDP session");
					if(userInfo.getMachine() != null && userInfo.getMachine().getIpAddress() != null){
						/*RDPWrapperActivity rdpWrapperActivity = new RDPWrapperActivity(UsersActivity.this, userInfo.getMachine().getIpAddress(), 
								userInfo.getMachine().getPort(), userInfo.getName(), userInfo.getPassword());
						String ret = rdpWrapperActivity.startRDPSession();
						if(!"".equals(ret)){
							alert(ret);  	
						}*/

					} else{
						Log.e(TAG, "machine for user "+userInfo.getName() + " not defined");
					}					
				} else{
					if(userInfo.getMachine() != null && userInfo.getMachine().getIpAddress() != null){
						try{
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://"+userInfo.getName()+"@"+userInfo.getMachine().getIpAddress()+
									":22/#sso")));
						} catch (ActivityNotFoundException e){
							alert(e.getMessage());
						}
					} else{
						Log.e(TAG, "machine for user "+userInfo.getName() + " not defined");
					}
				}

			}
		});

	}
	
	protected void alert(String message) {
		MainActivity.alertMessage = message;
		Intent i = new Intent(this, AlertDialogActivity.class);
		startActivity(i);   
	}

	private UserInfo getCurrentUser(){
		String name = mUserName.getText().toString();
		String password = mUserPassword.getText().toString();
		UserInfo userInfo = new UserInfo(name, password);
		Log.d(TAG, "save user " + userInfo);

		MachineInfo machineInfo = (MachineInfo) machineSpinner.getSelectedItem();
		ConnectionType connectionType = (ConnectionType) connetionTypeSpinner.getSelectedItem();
		userInfo.setConnectionType(connectionType);

		if(machineInfo!= null){
			Log.d(TAG, "add machine for the user " + machineInfo);
			userInfo.addMachine(machineInfo);

		}
		return userInfo;
	}
}
