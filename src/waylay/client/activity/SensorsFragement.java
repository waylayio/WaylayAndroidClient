package waylay.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.waylay.client.R;

import java.util.ArrayList;

import waylay.client.sensor.AccelerometerSensor;
import waylay.client.sensor.BeaconSensor;
import waylay.client.sensor.ForceSensor;
import waylay.client.sensor.LocalSensor;
import waylay.client.sensor.LocationSensor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorsFragement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SensorsFragement#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SensorsFragement extends Fragment {

    private static final String TAG = "SensorsFragement";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ListView mLocalSensorList;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorsFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorsFragement newInstance(String param1, String param2) {
        SensorsFragement fragment = new SensorsFragement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SensorsFragement() {
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
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        mLocalSensorList = (ListView) view.findViewById(R.id.listUsers);
        mLocalSensorList.setAdapter(MainActivity.adapterLocalSensors);
        mLocalSensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.selectedLocalSensor = (LocalSensor) mLocalSensorList.getItemAtPosition(position);
                Log.d(TAG, "Selected sensor " + MainActivity.selectedLocalSensor.getName());
                Intent i = new Intent(getActivity(), LocalSensorActivity.class);
                startActivity(i);
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


}
