package waylay.client.activity;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import waylay.client.sensor.LocalSensor;
import waylay.rest.PostResponseCallback;
import waylay.rest.RestAPI;

import com.waylay.client.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalSensorActivity extends Activity {

	public static final String TAG = "LocalSensorActivity";

	private Button mConnectButton;
	//private Spinner scenarioSpinner;
	private EditText mRemoteNodeName;
	private EditText mRemoteScenarioID;
	private TextView mSelectedSensor;
	private LocalSensor localSensor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.local_sensor);

		mConnectButton = (Button) findViewById(R.id.buttonPushLocalData);
		//scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
		mRemoteNodeName = (EditText) findViewById(R.id.remoteScenarioNode);
		mRemoteScenarioID = (EditText) findViewById(R.id.remoteScenarioID);
		mSelectedSensor = (TextView) findViewById(R.id.selectedLocalSensorLabel);
		ImageView imageView = (ImageView) findViewById(R.id.selectedLocalSensorIcon);
		imageView.setImageResource(R.drawable.user);
		localSensor = MainActivity.selectedLocalSensor;

		/*ArrayAdapter<Scenario> adapter;
		adapter = new ArrayAdapter<Scenario>(this, android.R.layout.simple_spinner_item, ScenarioFactory.getsScenarios());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		scenarioSpinner.setAdapter(adapter);*/
		
        if(MainActivity.selectedLocalSensor != null){
        	Log.d(TAG, "selected sensor " + MainActivity.selectedLocalSensor);
        	mSelectedSensor.setText((CharSequence) MainActivity.selectedLocalSensor.getName());
        } else{
        	Log.e(TAG, "sensor not selected");
        	return;
        }
            
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mConnectButton clicked");
                final Long id = Long.parseLong(mRemoteScenarioID.getText().toString());
                final String nodeName = mRemoteNodeName.getText().toString();
                if(localSensor != null && nodeName != null){
                	MainActivity.pushData(localSensor, id, nodeName);
                }  
                finish();
            }
        });
		
	}
}
