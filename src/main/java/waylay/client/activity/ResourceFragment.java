package waylay.client.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import waylay.client.R;
import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;
import waylay.client.data.ConnectionSettings;
import waylay.client.service.push.PushService;
import waylay.rest.PostResponseCallback;
import waylay.utils.ResourceId;

/**
 * Created by Jasper De Vrient on 12/05/2016.
 */
public class ResourceFragment extends WaylayFragment {
    private static final String TAG = "ResourceFragment";
    private ConnectionSettings mSettings;
    private OnFragmentInteraction mListener;

    @BindView(R.id.tvResourceName) TextView mTvResourceName;
    @BindView(R.id.tvKey) TextView mTvKey;
    @BindView(R.id.tvValue) TextView mTvValue;
    @BindView(R.id.buttonRegenerate) Button mButtonRegenerate;
    @BindView(R.id.chMqttSecurity) CheckBox mCheckboxMqttSecurity;
    @BindView(R.id.mqttLayout) View mMqttLayout;
    @BindView(R.id.spinnerConnectionOptions) Spinner mOptions;
    @BindView(R.id.etInterval) EditText mEtInterval;
    @BindView(R.id.tvPushingStatus) TextView mTvPushStatus;
    @BindView(R.id.buttonPushAll) Switch buttonPushAll;

    private static final String MQTT = "MQTT", REST = "REST";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        ButterKnife.bind(this, view);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.connection_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOptions.setAdapter(adapter);
        final WaylayApplication WAPP = getWaylayApplication();
        final PushService pushService = WAPP.getPushService();

        mOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = adapter.getItem(position).toString();

                mSettings = WAPP.getConnectionSettings();
                if (selected.equals(MQTT)) {
                    mMqttLayout.setVisibility(View.VISIBLE);
                    mTvKey.setText(mSettings.getKey());
                    mTvValue.setText(mSettings.getValue());
                    mSettings.setConnectionType(ConnectionSettings.MQTT);
                    mSettings.setUseAuth(mCheckboxMqttSecurity.isChecked());
                } else {
                    mMqttLayout.setVisibility(View.GONE);
                    mSettings.setConnectionType(ConnectionSettings.HTTP);
                }
                WAPP.storeSettings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCheckboxMqttSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.setUseAuth(isChecked);
                WAPP.storeSettings();
            }
        });

        mMqttLayout.setVisibility(View.GONE);
        mSettings = getWaylayApplication().getConnectionSettings();

        componentsReady();

        buttonPushAll.setChecked(pushService.isPushing());
        if (buttonPushAll.isChecked())
            mTvPushStatus.setText(getString(R.string.stop_pushing));
        mEtInterval.setEnabled(!buttonPushAll.isChecked());
        buttonPushAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtInterval.setEnabled(!isChecked);
                if (mListener != null) {
                    if (isChecked == false) {
                        mListener.stopPush();
                        Toast.makeText(getActivity(), "Stopped Pushing all sensors", Toast.LENGTH_SHORT).show();
                        mTvPushStatus.setText(getString(R.string.push_all));
                    } else {
                        int interval = 1;
                        try {
                            String intervalString = mEtInterval.getText().toString();
                            interval = Integer.parseInt(intervalString);
                        } catch (NumberFormatException nfe) {
                        }
                        mListener.pushAll(interval);
                        Toast.makeText(getActivity(), "Pushing all sensors", Toast.LENGTH_SHORT).show();
                        mTvPushStatus.setText(getString(R.string.stop_pushing));
                    }
                }
            }
        });
        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteraction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void componentsReady() {
        if (mSettings.getConnectionType() == ConnectionSettings.MQTT) {
            mOptions.setSelection(1);
            mMqttLayout.setVisibility(View.VISIBLE);
            mTvValue.setText(mSettings.getValue());
            mCheckboxMqttSecurity.setChecked(mSettings.isUseAuth());
        } else
            mOptions.setSelection(0);
        mTvResourceName.setText(getWaylayApplication().getResourceId());
    }

    @OnClick(R.id.buttonRegenerate)
    public void regenerateMqttDetails() {
        mButtonRegenerate.setEnabled(false);
        String masterkey = getWaylayApplication().getSelectedServer().getMasterkey();
        WaylayApplication.getRestService().createDevice(masterkey, new PostResponseCallback<Map<String, String>>() {
            @Override
            public void onPostSuccess(Map<String, String> stringStringMap) {
                WaylayApplication wapp = getWaylayApplication();
                mSettings.setKey(stringStringMap.get("token"));
                mSettings.setValue(stringStringMap.get("secret"));
                ResourceId.set(wapp.getApplicationContext(), stringStringMap.get("uuid"));
                wapp.storeSettings();
                mTvKey.setText(mSettings.getKey());
                mTvValue.setText(mSettings.getValue());
                mTvResourceName.setText(wapp.getResourceId());
                mButtonRegenerate.setEnabled(true);
            }

            @Override
            public void onPostFailure(Throwable t) {

            }
        });
    }

    public interface OnFragmentInteraction {
        void stopPush();

        void pushAll(int time);
    }
}
