package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.scenario.Node;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import com.waylay.client.R;

public class ScenarioActivity extends Activity {

	
	public static final String TAG = "ScenarioActivity";
    private ListView mNodeList;
    private NodeAdapter nodeAdapter;
    private ArrayList<Node> nodes = new ArrayList<Node>();
    
    private void init(){
        mNodeList = (ListView) findViewById(R.id.listNodes);
        nodes = ScenariosFragment.selectedTask.getNodes();
        nodeAdapter = new NodeAdapter(this, nodes);
        mNodeList.setAdapter(nodeAdapter);
    }

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.scenario);
        init();      
    }

}
