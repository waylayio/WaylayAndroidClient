package waylay.client.activity;


import waylay.client.data.MachineInfo;

import com.waylay.client.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MachinesActivity extends Activity {

	
	public static final String TAG = "Machine Manager";
    
	private Button mSaveMachineButton;
    private EditText mMachineName;
    private EditText mMachineAddress;
    private EditText mMachinePort;

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.machines);
        
        mSaveMachineButton = (Button) findViewById(R.id.buttonSaveMachine);
        mMachineName = (EditText) findViewById(R.id.machineNameEditText);
        mMachineAddress = (EditText) findViewById(R.id.machineAddressEditText);
        mMachinePort = (EditText) findViewById(R.id.machinePortEditText);
        
        if(MainActivity.selectedMachine != null){
        	Log.d(TAG, "selected existing user " + MainActivity.selectedMachine);
        	mMachineName.setText((CharSequence) MainActivity.selectedMachine.getName());
        	mMachineAddress.setText((CharSequence) MainActivity.selectedMachine.getIpAddress());
        	mMachinePort.setText((CharSequence) Long.toString(MainActivity.selectedMachine.getPort()));
        	
        } else{
        	Log.d(TAG, "user not selected, new user will be created");
        }
            
        mSaveMachineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mSaveMachineButton clicked");
                saveMachine();
            }

			private void saveMachine() {
				Long port;
				String name = mMachineName.getText().toString();
				String address = mMachineAddress.getText().toString();
				try {
					port = Long.parseLong(mMachinePort.getText().toString());
				} catch (Exception e){
					Log.e(TAG, e.getMessage());
					return;
				}
				MachineInfo machineInfo = new MachineInfo(name, address, port);
				Log.d(TAG, "save machine " + machineInfo);
				
				MachineFactory.removeMachine(MainActivity.selectedMachine);
				MachineFactory.addMachine(machineInfo);
				// TODO I can't make listUsers a Set, since the adapter expects the list, to fix it later
				// right now recreating a list
				MainActivity.listMachines.clear();
				MainActivity.listMachines.addAll(MachineFactory.getMachines());
				MainActivity.adapterMachines.notifyDataSetChanged();
				finish();
			}
        });
           
    }

}
