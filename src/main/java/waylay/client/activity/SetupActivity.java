package waylay.client.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import waylay.client.data.BayesServer;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import waylay.client.R;

public class SetupActivity extends BaseActivity{

    public static final String TAG = "SetupActivity";

    public static final int RESULT_CHANGED = 1;


    @BindView(R.id.setupSSO_URLEditNameText) EditText mSSO_URL;
    @BindView(R.id.setupSSOEditNameText) EditText mSSO_Password;
    @BindView(R.id.setupSSOPasswordEditText) EditText mSSO_Name;
    @BindView(R.id.setupSSOSecure) CheckBox mSSO_Secure;
    @BindView(R.id.setupSSOMasterkeyEditText) EditText mSSO_Masterkey;
    @BindView(R.id.setupSSODeviceGatewayEditText) EditText mSSO_DeviceGateway;
    @BindView(R.id.setupSSODataBrokerEditText) EditText mSSO_DataBroker;
    @BindView(R.id.buttonSaveSSOSetup) Button mSaveSSOButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        Log.d(TAG, "start sso setup");

        ButterKnife.bind(this);

        Linkify.addLinks(mSSO_URL, Linkify.WEB_URLS);
        Linkify.addLinks(mSSO_DeviceGateway, Linkify.WEB_URLS);
        Linkify.addLinks(mSSO_DataBroker, Linkify.WEB_URLS);

        BayesServer selectedServer = getWaylayApplication().getSelectedServer();
        if(selectedServer != null){
            Log.d(TAG, "selected existing SSO " + selectedServer);
            mSSO_URL.setText(selectedServer.getHost());
            mSSO_Name.setText(selectedServer.getName());
            mSSO_Password.setText(selectedServer.getPassword());
            String master = selectedServer.getMasterkey();
            if (master != null)
                mSSO_Masterkey.setText(selectedServer.getMasterkey());
            String dg = selectedServer.getDeviceGateway();
            if (dg != null)
                mSSO_DeviceGateway.setText(dg);
            String db = selectedServer.getDataBroker();
            if (db != null)
                mSSO_DataBroker.setText(db);
            mSSO_Secure.setChecked(selectedServer.isSecure());
        } else{
            Log.d(TAG, "sso not selected, new sso will be created");
        }
    }

    @OnClick(R.id.buttonSaveSSOSetup)
    public void saveSetup() {
        BayesServer server = getBayesServer();
        getWaylayApplication().selectServer(server);
        setResult(RESULT_CHANGED);
        finish();
    }

    private BayesServer getBayesServer(){
        String name = mSSO_Name.getText().toString();
        String password = mSSO_Password.getText().toString();
        String URL = mSSO_URL.getText().toString();
        String masterkey = mSSO_Masterkey.getText().toString();
        String deviceGateway = mSSO_DeviceGateway.getText().toString();
        String databroker = mSSO_DataBroker.getText().toString();
        boolean secure = mSSO_Secure.isChecked();
        BayesServer server = new BayesServer(URL, name, password, masterkey, deviceGateway, databroker, secure);
        Log.d(TAG, "get sso " + server);
        return server;
    }

}
