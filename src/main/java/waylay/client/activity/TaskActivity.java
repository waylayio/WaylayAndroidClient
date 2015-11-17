package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.scenario.Node;
import waylay.client.scenario.Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import waylay.client.R;

public class TaskActivity extends Activity {

	public static final String TAG = "ScenarioActivity";

    private static final String EXTRA_TASK_ID = "taskid";

    private ListView mNodeList;
    private NodeAdapter nodeAdapter;
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public static void start(Context packageContext, long taskId){
        Intent i = new Intent(packageContext, TaskActivity.class);
        i.putExtra(EXTRA_TASK_ID, taskId);
        packageContext.startActivity(i);
    }
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, 0);
        Task task = Tasks.getTaskById(taskId);
        if(task != null) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.scenario);
            mNodeList = (ListView) findViewById(R.id.listNodes);
            nodes = task.getNodes();
            nodeAdapter = new NodeAdapter(this, nodes, task.getTargetNode());
            mNodeList.setAdapter(nodeAdapter);
        }else{
            finish();
        }
    }
}
