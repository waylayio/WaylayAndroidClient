package waylay.client.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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

import com.waylay.client.R;

import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;
import waylay.client.scenario.Scenario;
import waylay.rest.GetResponseCallback;
import waylay.rest.PostResponseCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScenariosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScenariosFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScenariosFragment extends BaseFragment {

    private static final String TAG = "ScenariosFragment";

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";

    protected static Scenario selectedScenario = null;
    private ListView mScenarioList;
    private Button mSyncButton;
    protected Object mMachineActionMode;


    public static ScenarioAdapter adapterScenarios;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScenariosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScenariosFragment newInstance(String param1, String param2) {
        ScenariosFragment fragment = new ScenariosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ScenariosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scenarios, container, false);

        adapterScenarios = new ScenarioAdapter(getActivity(), ScenarioFactory.getsScenarios());

        mScenarioList = (ListView) view.findViewById(R.id.listMachines);
        mScenarioList.setAdapter(adapterScenarios);

        mScenarioList.setClickable(true);
        mScenarioList.setOnItemClickListener(new MyScenarioViewUserListener(getActivity(), ScenarioActivity.class));

        mScenarioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				/*if (mScenarioActionMode!= null) {
					return false;
				}*/
                selectedScenario = (Scenario) mScenarioList.getItemAtPosition(position);
                getActivity().startActionMode(MyScenarioActionModeCallback);
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
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }


    private class MyScenarioViewUserListener implements AdapterView.OnItemClickListener {

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
                    actioScenarioItem(ACTION_DELETE);
                    return true;
                case R.id.itemStartScenario:
                    mode.finish();
                    actioScenarioItem(ACTION_START);
                    return true;
                case R.id.itemStopScenario:
                    mode.finish();
                    actioScenarioItem(ACTION_STOP);
                    return true;
                default:
                    return false;
            }
        }

        private void actioScenarioItem(String action) {
            final Long id = selectedScenario.getId();
            if(ACTION_STOP.equals(action) || ACTION_START.equals(action)){
                WaylayApplication.getRestService().postScenarioAction(id, action, new PostResponseCallback(){
                    @Override
                    public void onPostSuccess() {
                        Log.i(TAG, "action was success");
                        getScenario(id);
                    }
                });
            } else if(ACTION_DELETE.equals(action)){
                WaylayApplication.getRestService().deleteScenarioAction(id, new PostResponseCallback() {
                    @Override
                    public void onPostSuccess() {
                        Log.i(TAG, "action was success");
                        ScenarioFactory.removeScenario(selectedScenario);
                        selectedScenario = null;
                        updateScenarios();
                    }
                });
            }

        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMachineActionMode = null;
        }
    };

    public void getScenario(long scenarioId){
        Log.d(TAG, "refreshAllScenarios");

        final ProgressDialog progress = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        WaylayApplication.getRestService().getScenario(scenarioId, "", new GetResponseCallback<Scenario>() {
            @Override
            public void onDataReceived(Scenario scenario, boolean error, String message) {
                Log.i(TAG, "Received response for scenarios: " + scenario);

                if (!error) {
                    ScenarioFactory.addScenario(scenario);
                    progress.dismiss();
                    updateScenarios();
                } else {
                    progress.dismiss();
                    alert(message);
                }
            }
        });

    }
    protected void updateScenarios() {
        adapterScenarios.notifyDataSetChanged();
    }

    public void refreshAllScenarios(){
        if(WaylayApplication.getSelectedServer() != null) {
            Log.d(TAG, "refreshAllScenarios");
            ScenarioFactory.clear();

            final ProgressDialog progress = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

            WaylayApplication.getRestService().getScenarios("", new GetResponseCallback<List<Scenario>>() {
                @Override
                public void onDataReceived(List<Scenario> scenarios, boolean error, String message) {
                    Log.i(TAG, "Received response for scenarios: " + scenarios.size());

                    if (!error) {
                        ScenarioFactory.addAll(scenarios);
                        progress.dismiss();
                        updateScenarios();
                    } else {
                        progress.dismiss();
                        alert(message);
                    }
                }
            });
        }else{
            Toast.makeText(getActivity(), "No server selected", Toast.LENGTH_SHORT).show();
        }

    }



}
