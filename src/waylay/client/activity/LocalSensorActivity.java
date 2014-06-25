package waylay.client.activity;



import java.util.ArrayList;
import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.scenario.Node;
import waylay.client.scenario.Task;
import waylay.client.sensor.AbstractLocalSensor;

import com.waylay.client.R;

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
	private Task task;
	private List<Node> listNodes = new ArrayList<Node>();
	private List<Task> tasks = new ArrayList<Task>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.local_sensor);

		tasks = ScenarioFactory.getsScenarios();//filterScenarios(ScenarioFactory.getsScenarios());

		mConnectButton = viewById(R.id.buttonPushLocalData);
		scenarioSpinner = viewById(R.id.scenarioSpinner);
		nodeSpinner = viewById(R.id.nodeSpinner);
		mSelectedSensor = viewById(R.id.selectedLocalSensorLabel);
		localSensor = SensorsFragement.selectedLocalSensor;

		ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, tasks);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		scenarioSpinner.setAdapter(adapter);
		
		if(tasks.size() > 0) {
            listNodes = filterNodes(tasks.get(0).getNodes());
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
				mSelectedSensor.setText("Scenario[" + task.getId()+ "], " + "Node[" + selectedNode+ "] " + SensorsFragement.selectedLocalSensor.getName());
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
				task = tasks.get(i);
				listNodes = filterNodes(task.getNodes());
				if(listNodes.size() > 0){
					//what the hell...with final it is not working
					ArrayAdapter<Node> adapterNode = new ArrayAdapter<Node>(LocalSensorActivity.this, android.R.layout.simple_spinner_item, listNodes);
					adapterNode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
					nodeSpinner.setAdapter(adapterNode);
					adapterNode.notifyDataSetChanged();
					mSelectedSensor.setText("Scenario[" + task.getId()+ "] " + SensorsFragement.selectedLocalSensor.getName());
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
                if(localSensor != null && task != null){
                	WaylayApplication.startPushing(localSensor, task.getId(), selectedNode);
                    finish();
                }else{
                    Log.e(TAG,  "Sensor or Scenario is null");
                    Toast.makeText(LocalSensorActivity.this, "Sensor or Scenario is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
		
	}

	private ArrayList<Task> filterScenarios(ArrayList<Task> tasks) {
		ArrayList<Task> filtered = new ArrayList<Task>();
		for(Task task : tasks){
			for(Node node : task.getNodes()){
				if(node.getSensorName() != null && 
						node.getSensorName().startsWith(SensorsFragement.selectedLocalSensor.getName())){
					filtered.add(task);
					break;
				}	//TODO hack, later ask for runtime properties from the REST call 	
				else if(node.getSensorName() != null && 
						( (node.getSensorName().startsWith("Parking") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ||
						(node.getSensorName().startsWith("Pharmacy") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location"))  ||
						(node.getSensorName().startsWith("Tree") &&
                                SensorsFragement.selectedLocalSensor.getName().startsWith("Location")) ) )    {
					filtered.add(task);
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
