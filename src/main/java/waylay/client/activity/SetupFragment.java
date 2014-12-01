package waylay.client.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waylay.client.R;

import java.lang.reflect.Method;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SetupFragment extends WaylayFragment {

    private static final String TAG = "SetupFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView serverList;
    private static SetupAdapter adapterSetup;


    private OnFragmentInteractionListener mListener;

    protected Object mSetupActionMode;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance(String param1, String param2) {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SetupFragment() {
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
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        final TextView tvResourceId = (TextView) view.findViewById(R.id.tvResourceId);
        tvResourceId.setText("Resource id: " + getWaylayApplication().getResourceId());
        tvResourceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Resource id");
                alert.setMessage("Please enter a new resource id");

                final EditText input = new EditText(getActivity());
                input.setText(getWaylayApplication().getResourceId());
                input.selectAll();
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        getWaylayApplication().setResourceId(value);
                        tvResourceId.setText("Resource id: " + getWaylayApplication().getResourceId());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        serverList = (ListView) view.findViewById(R.id.listSSO);
        adapterSetup = new SetupAdapter(getActivity(),  getWaylayApplication(), getWaylayApplication().getServers());
        serverList.setAdapter(adapterSetup);

        serverList.setClickable(true);
        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectServer((BayesServer) serverList.getItemAtPosition(position));
            }
        });

        serverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSetupActionMode != null) {
                    return false;
                }
                try {
                    mSSOActionModeCallback.setPosition(position);
                    mSetupActionMode = getActivity().startActionMode(mSSOActionModeCallback);
                    view.setSelected(true);
                    return true;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            }
        });

        Button buttonPushAll = (Button) view.findViewById(R.id.buttonPushAll);
        buttonPushAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pushAll();
                Toast.makeText(getActivity(), "Pushing all sensors", Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonStopPushingData = (Button) view.findViewById(R.id.buttonStopPushingData);
        buttonStopPushingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.stopPush();
            }
        });

        return view;
    }

    private void selectServer(BayesServer server) {
        getWaylayApplication().selectServer(server);
        adapterSetup.notifyDataSetChanged();
        mListener.onServerChange();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterSetup.notifyDataSetChanged();
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
        void stopPush();

        void pushAll();

        void onServerChange();
    }


    private void launchSSOSetup(int position) {
		getWaylayApplication().selectServer((BayesServer) serverList.getItemAtPosition(position));
		Intent i = new Intent(getActivity(), SetupActivity.class);
		startActivity(i);
	}

    private void deleteServer(int position) {
        getWaylayApplication().deleteServer((BayesServer) serverList.getItemAtPosition(position));
    }

    private ActionModeCallback mSSOActionModeCallback = new ActionModeCallback();


    protected void startBrowser() {
        Log.d(TAG, "startBrowser");
        Uri uri = Uri.parse(getWaylayApplication().getSelectedServer().constructURLForWebAP());
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
        startActivity(marketIntent);

    }

    private class ActionModeCallback implements ActionMode.Callback {

        int position = 0;

        public void setPosition(Integer pos){
            position = pos;
        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_sso, menu);
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
            case R.id.itemInfo:
                startBrowser();
                mode.finish();
                return true;
            case R.id.itemEditSSO:
                launchSSOSetup(position);
                mode.finish();
                return true;
            case R.id.itemDeleteSSO:
                deleteServer(position);
                mode.finish();
                return true;
            default:
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSetupActionMode = null;
        }
    }
}
