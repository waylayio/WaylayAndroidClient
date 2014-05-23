package waylay.client.activity;



import java.util.ArrayList;
import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.scenario.Node;
import waylay.client.scenario.Scenario;
import waylay.client.sensor.AbstractLocalSensor;

import com.waylay.client.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocalSensorActivity extends BaseActivity {

	public static final String TAG = "LocalSensorActivity";

	private Button mConnectButton;
	private Spinner scenarioSpinner;
	private Spinner nodeSpinner;
	private TextView mSelectedSensor;
	private AbstractLocalSensor localSensor;
	private String selectedNode;
	private Scenario scenario;
	private List<Node> listNodes = new ArrayList<Node>();
	private List<Scenario> scenarios = new ArrayList<Scenario>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.local_sensor);

		scenarios = ScenarioFactory.getsScenarios();//filterScenarios(ScenarioFactory.getsScenarios());

		mConnectButton = viewById(R.id.buttonPushLocalData);
		scenarioSpinner = viewById(R.id.scenarioSpinner);
		nodeSpinner = viewById(R.id.nodeSpinner);
		mSelectedSensor = viewById(R.id.selectedLocalSensorLabel);
		localSensor = SensorsFragement.selectedLocalSensor;

		ArrayAdapter<Scenario> adapter = new ArrayAdapter<Scenario>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, scenarios);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		scenarioSpinner.setAdapter(adapter);
		
		if(scenarios.size() > 0) {
            listNodes = filterNodes(scenarios.get(0).getNodes());
        }
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
				mSelectedSensor.setText("Scenario[" + scenario.getId()+ "], " + "Node[" + selectedNode+ "] " + SensorsFragement.selectedLocalSensor.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		scenarioSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int i = scenarioSpinner.getSelectedItemPosition();
				scenario = scenarios.get(i);
				listNodes = filterNodes(scenario.getNodes());
				if(listNodes.size() > 0){
					//what the hell...with final it is not working
					ArrayAdapter<Node> adapterNode = new ArrayAdapter<Node>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, listNodes);
					adapterNode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
					nodeSpinner.setAdapter(adapterNode);
					adapterNode.notifyDataSetChanged();
					mSelectedSensor.setText("Scenario[" + scenario.getId()+ "] " + SensorsFragement.selectedLocalSensor.getName());
				}	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

        if(SensorsFragement.selectedLocalSensor != null){
        	Log.d(TAG, "selected sensor " + SensorsFragement.selectedLocalSensor);
        	mSelectedSensor.setText(SensorsFragement.selectedLocalSensor.getName());
        } else{
        	Log.e(TAG, "sensor not selected");
        	return;
        }
            
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(localSensor != null && scenario != null){
                	WaylayApplication.startPushing(localSensor, scenario.getId(), selectedNode);
                    finish();
                }else{
                    Log.e(TAG,  "Sensor or Scenario is null");
                    Toast.makeText(LocalSensorActivity.this, "Sensor or Scenario is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
		
	}

	private ArrayList<Scenario> filterScenarios(ArrayList<Scenario> scenarios) {
		ArrayList<Scenario> filtered = new ArrayList<Scenario>();
		for(Scenario scenario : scenarios ){
			for(Node node : scenario.getNodes()){
				if(node.getSensorName() != null && 
						node.getSensorName().startsWith(SensorsFragement.selectedLocalSensor.getName())){
					filtered.add(scenario);
					break;
				}	//TODO hack, later ask for runtime properties from the REST call 	
				else if(node.getSensorName() != null && 
						( (node.getSensorName().startsWith("Parking") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ||
						(node.getSensorName().startsWith("Pharmacy") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location"))  ||
						(node.getSensorName().startsWith("Tree") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ) )    {
					filtered.add(scenario);
					break;
				}	//hack	
			}
		}
		return filtered;
	}

	private List<Node> filterNodes(List<Node> nodes) {
		List<Node> list = new ArrayList<Node>();
		for(Node node : nodes){
			if(node.getSensorName() != null && 
					node.getSensorName().startsWith(SensorsFragement.selectedLocalSensor.getName())){
				list.add(node);
			}	//TODO hack	
			else if(node.getSensorName() != null && 
					( (node.getSensorName().startsWith("Parking") &&
                            SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ||
					(node.getSensorName().startsWith("Pharmacy") &&
                            SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ||
					(node.getSensorName().startsWith("Tree") &&
                            SensorsFragement.selectedLocalSensor.getName().startsWith("Location"))) )    {
				list.add(node);
			}	//hack		
		}
		return list;
	}
}
