package waylay.client.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import waylay.client.R;

import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.scenario.Task;
import waylay.rest.DeleteResponseCallback;
import waylay.rest.GetResponseCallback;
import waylay.rest.PostResponseCallback;

public class TasksFragment extends WaylayFragment {

    private static final String TAG = "ScenariosFragment";

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";

    private Task selectedTask;

    private ListView mScenarioList;
    private Button mSyncButton;
    protected Object mMachineActionMode;


    public static ScenarioAdapter adapterScenarios;

    private LoadingListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScenariosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }
    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scenarios, container, false);

        adapterScenarios = new ScenarioAdapter(getActivity(), Tasks.getTasks());

        mScenarioList = (ListView) view.findViewById(R.id.listMachines);
        mScenarioList.setAdapter(adapterScenarios);

        mScenarioList.setClickable(true);
        mScenarioList.setOnItemClickListener(new MyScenarioViewUserListener(getActivity()));

        mScenarioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				/*if (mScenarioActionMode!= null) {
					return false;
				}*/
                selectedTask = (Task) mScenarioList.getItemAtPosition(position);
                getActivity().startActionMode(myScenarioActionModeCallback);
                view.setSelected(true);
                return true;

            }
        });

        mSyncButton = (Button) view.findViewById(R.id.buttonSyncWithServer);
        mSyncButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshAllScenarios();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoadingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoadingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class MyScenarioViewUserListener implements AdapterView.OnItemClickListener {

        private Context packageContext;
        public MyScenarioViewUserListener(Context packageContext){
            this.packageContext = packageContext;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            TaskActivity.start(packageContext, ((Task)mScenarioList.getItemAtPosition(position)).getId());
        }
    }

    private ActionMode.Callback myScenarioActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_scenario, menu);
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
            if(selectedTask != null) {
                final Long taskId = selectedTask.getId();
                switch (item.getItemId()) {
                    case R.id.itemDeleteScenario:
                        mode.finish();
                        executeDelete(taskId);
                        return true;
                    case R.id.itemStartScenario:
                        mode.finish();
                        executeAction(ACTION_START, taskId);
                        return true;
                    case R.id.itemStopScenario:
                        mode.finish();
                        executeAction(ACTION_STOP, taskId);
                        return true;
                    default:
                        return false;
                }
            }else{
                Toast.makeText(getActivity(), "Something went wrong, no task selected", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMachineActionMode = null;
        }
    };

    private void executeAction(final String action, final long taskId) {
        WaylayApplication.getRestService().postScenarioAction(taskId, action, new PostResponseCallback<Void>() {
            @Override
            public void onPostSuccess(Void t) {
                Log.i(TAG, "action was success");
                etTask(taskId);
            }

            @Override
            public void onPostFailure(Throwable t) {
                Log.e(TAG, "action failed", t);
                Activity activity = getActivity();
                if (activity != null) {
                    Toast.makeText(activity, "Failed to perform action: " + action, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void executeDelete(Long taskId) {
        WaylayApplication.getRestService().deleteScenarioAction(taskId, new DeleteResponseCallback() {
            @Override
            public void onDeleteSuccess() {
                Log.i(TAG, "action was success");
                Tasks.removeScenario(selectedTask);
                notifyTasksChanged();
                selectedTask = null;
            }

            @Override
            public void onDeleteFailure(Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Activity activity = getActivity();
                if (activity != null) {
                    Toast.makeText(activity, "Failed to delete scenario", Toast.LENGTH_SHORT).show();
                }
                notifyTasksChanged();
            }
        });
    }


    private void etTask(long scenarioId){
        Log.d(TAG, "getTask");
        mListener.startLoading();
        WaylayApplication.getRestService().getScenario(scenarioId, new GetResponseCallback<Task>() {
            @Override
            public void onDataReceived(Task task) {
                Log.i(TAG, "Received response for scenario " + task);
                Tasks.addScenario(task);
                notifyTasksChanged();
                endLoading();
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                endLoading();
                alert(t.getMessage());
            }
        });
    }

    private void endLoading() {
        // this is called in callbacks and the UI might be detached/closed by this time
        if(mListener != null) {
            mListener.endLoading();
        }
    }

    protected void notifyTasksChanged() {
        adapterScenarios.notifyDataSetChanged();
    }

    public void refreshAllScenarios(){
        if(getWaylayApplication().getSelectedServer() != null) {
            Log.d(TAG, "refreshAllScenarios");
            Tasks.clear();
            notifyTasksChanged();

            mListener.startLoading();
            WaylayApplication.getRestService().getScenarios( new GetResponseCallback<List<Task>>() {
                @Override
                public void onDataReceived(List<Task> tasks) {
                    Log.i(TAG, "Received response with " + tasks.size() + " scenarios");
                    Tasks.addAll(tasks);
                    notifyTasksChanged();
                    endLoading();
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, t.getMessage(), t);
                    endLoading();
                    alert(t.getMessage());
                }
            });
        }else{
            final Activity activity = getActivity();
            if(activity != null) {
                Toast.makeText(activity, "No server selected", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
