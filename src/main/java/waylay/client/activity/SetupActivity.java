package waylay.client.activity;

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

    private EditText mSSO_URL;
    private EditText mSSO_Password;
    private EditText mSSO_Name;
    private CheckBox mSSO_Secure;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        Log.d(TAG, "start sso setup");

        mSSO_URL = viewById(R.id.setupSSO_URLEditNameText);
        Linkify.addLinks(mSSO_URL, Linkify.WEB_URLS);
        mSSO_Name = viewById(R.id.setupSSOEditNameText);
        mSSO_Password = viewById(R.id.setupSSOPasswordEditText);
        mSSO_Secure = viewById(R.id.setupSSOSecure);
        Button mSaveSSOButton = viewById(R.id.buttonSaveSSOSetup);

        BayesServer selectedServer = getWaylayApplication().getSelectedServer();
        if(selectedServer != null){
            Log.d(TAG, "selected existing SSO " + selectedServer);
            mSSO_URL.setText(selectedServer.getHost());
            mSSO_Name.setText(selectedServer.getName());
            mSSO_Password.setText(selectedServer.getPassword());
            mSSO_Secure.setChecked(selectedServer.isSecure());
        } else{
            Log.d(TAG, "sso not selected, new sso will be created");
        }


        mSaveSSOButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BayesServer server = getBayesServer();
                getWaylayApplication().selectServer(server);
                setResult(RESULT_CHANGED);
                finish();
            }
        });
    }

    private BayesServer getBayesServer(){
        String name = mSSO_Name.getText().toString();
        String password = mSSO_Password.getText().toString();
        String URL = mSSO_URL.getText().toString();
        boolean secure = mSSO_Secure.isChecked();
        BayesServer server = new BayesServer(URL, name, password, secure);
        Log.d(TAG, "get sso " + server);
        return server;
    }

}
