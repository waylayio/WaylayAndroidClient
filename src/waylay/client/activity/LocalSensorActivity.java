package waylay.client.activity;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import waylay.client.scenario.Node;
import waylay.client.scenario.Scenario;
import waylay.client.sensor.LocalSensor;
import waylay.rest.PostResponseCallback;
import waylay.rest.RestAPI;

import com.waylay.client.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;

public class LocalSensorActivity extends Activity{

	public static final String TAG = "LocalSensorActivity";

	private Button mConnectButton;
	private Spinner scenarioSpinner;
	private Spinner nodeSpinner;
	private TextView mSelectedSensor;
	private LocalSensor localSensor;
	private String selectedNode;
	private Scenario scenario;
	private ArrayList<Node> listNodes = new ArrayList<Node>();
	private ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.local_sensor);
		scenarios = filterScenarios(ScenarioFactory.getsScenarios());

		mConnectButton = (Button) findViewById(R.id.buttonPushLocalData);
		scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
		nodeSpinner = (Spinner) findViewById(R.id.nodeSpinner);
		mSelectedSensor = (TextView) findViewById(R.id.selectedLocalSensorLabel);
		localSensor = MainActivity.selectedLocalSensor;

		ArrayAdapter<Scenario> adapter;
		adapter = new ArrayAdapter<Scenario>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, scenarios);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		scenarioSpinner.setAdapter(adapter);
		
		if(scenarios.size() > 0)
			listNodes = filerNodes(scenarios.get(0).getNodes());
		final ArrayAdapter<Node> adapterNode = new ArrayAdapter<Node>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, listNodes);
		adapterNode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		nodeSpinner.setAdapter(adapterNode);
		
		nodeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int i = nodeSpinner.getSelectedItemPosition();
				Node node = listNodes.get(i);
				selectedNode =  String.valueOf(node.getName());
				mSelectedSensor.setText("Scenario[" + scenario.getId()+ "], " + "Node[" + selectedNode+ "] " +
				(CharSequence) MainActivity.selectedLocalSensor.getName());				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		scenarioSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int i = scenarioSpinner.getSelectedItemPosition();
				scenario = scenarios.get(i);
				listNodes = filerNodes(scenario.getNodes());
				if(listNodes.size() > 0){
					//what the hell...with final it is not working
					ArrayAdapter<Node> adapterNode = new ArrayAdapter<Node>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, listNodes);
					adapterNode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
					nodeSpinner.setAdapter(adapterNode);
					adapterNode.notifyDataSetChanged();
					mSelectedSensor.setText("Scenario[" + scenario.getId()+ "] " + (CharSequence) MainActivity.selectedLocalSensor.getName());				
				}	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

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
                if(localSensor != null && scenario != null && selectedNode != null){
                	MainActivity.pushData(localSensor, scenario.getId(), selectedNode);
                }  
                finish();
            }
        });
		
	}

	private ArrayList<Scenario> filterScenarios(ArrayList<Scenario> _scenarios) {
		ArrayList<Scenario> list = new ArrayList<Scenario>();
		for(Scenario scenario : _scenarios ){
			for(Node node : scenario.getNodes()){
				if(node.getSensorName() != null && 
						node.getSensorName().startsWith(MainActivity.selectedLocalSensor.getName())){
					list.add(scenario);
					break;
				}	//hack	
				else if(node.getSensorName() != null && 
						( (node.getSensorName().startsWith("Parking") && 
								MainActivity.selectedLocalSensor.getName().startsWith("Location")) ||
						(node.getSensorName().startsWith("Pharmacy") && 
						MainActivity.selectedLocalSensor.getName().startsWith("Location")) ) )    {
					list.add(scenario);
					break;
				}	//hack	
			}
		}
		return list;
	}

	protected ArrayList<Node> filerNodes(ArrayList<Node> nodes) {
		ArrayList<Node> list = new ArrayList<Node>();
		for(Node node : nodes){
			if(node.getSensorName() != null && 
					node.getSensorName().startsWith(MainActivity.selectedLocalSensor.getName())){
				list.add(node);
			}	//hack	
			else if(node.getSensorName() != null && 
					( (node.getSensorName().startsWith("Parking") && 
							MainActivity.selectedLocalSensor.getName().startsWith("Location")) ||
					(node.getSensorName().startsWith("Pharmacy") && 
					MainActivity.selectedLocalSensor.getName().startsWith("Location")) ) )    {
				list.add(node);
			}	//hack		
		}
		return list;
	}
}
