package waylay.client.activity;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.waylay.client.R;

public class SetupActivity extends Activity{

    public static final String TAG = "SetupActivity";

    private EditText mSSO_URL;
    private EditText mSSO_Password;
    private EditText mSSO_Name;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        Log.d(TAG, "start sso setup");

        mSSO_URL = (EditText) findViewById(R.id.setupSSO_URLEditNameText);
        Linkify.addLinks(mSSO_URL, Linkify.WEB_URLS);
        mSSO_Name = (EditText) findViewById(R.id.setupSSOEditNameText);
        mSSO_Password = (EditText) findViewById(R.id.setupSSOPasswordEditText);
        Button mSaveSSOButton = (Button) findViewById(R.id.buttonSaveSSOSetup);

        BayesServer selectedServer = WaylayApplication.getSelectedServer();
        if(selectedServer != null){
            Log.d(TAG, "selected existing SSO " + selectedServer);
            mSSO_URL.setText(selectedServer.getHost());
            mSSO_Name.setText(selectedServer.getName());
            mSSO_Password.setText(selectedServer.getPassword());
        } else{
            Log.d(TAG, "sso not selected, new sso will be created");
        }


        mSaveSSOButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BayesServer server = getBayesServer();
                WaylayApplication.selectServer(server);
                finish();
            }
        });
    }

    private BayesServer getBayesServer(){
        String name = mSSO_Name.getText().toString();
        String password = mSSO_Password.getText().toString();
        String URL = mSSO_URL.getText().toString();
        BayesServer server = new BayesServer(URL, name, password);
        Log.d(TAG, "get sso " + server);
        return server;
    }

}
