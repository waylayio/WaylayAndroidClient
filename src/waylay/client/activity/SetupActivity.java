package waylay.client.activity;

import waylay.client.data.BayesServer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.waylay.client.R;

public class SetupActivity extends Activity{
		
	private EditText mSSO_URL;
	private EditText mSSO_Password;
	private EditText mSSO_Name;
	private Button mSaveSSOButton;
	public static final String TAG = "SSO Manager";

	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.setup);
	        Log.d(TAG, "start sso setup");
	              
	        mSSO_URL = (EditText) findViewById(R.id.setupSSO_URLEditNameText);
	        Linkify.addLinks(mSSO_URL, Linkify.WEB_URLS);
	        mSSO_Name = (EditText) findViewById(R.id.setupSSOEditNameText);
	        mSSO_Password = (EditText) findViewById(R.id.setupSSOPasswordEditText);        
	        mSaveSSOButton = (Button) findViewById(R.id.buttonSaveSSOSetup);
	        
  
	        if(MainActivity.ssoSetup != null){
	        	Log.d(TAG, "selected existing SSO " + MainActivity.ssoSetup);
	        	mSSO_URL.setText((CharSequence) MainActivity.ssoSetup.getURL());
	        	mSSO_Name.setText((CharSequence) MainActivity.ssoSetup.getName());
	        	mSSO_Password.setText((CharSequence) MainActivity.ssoSetup.getPassword());
	        	
	        } else{
	        	Log.d(TAG, "sso not selected, new sso will be created");
	        }
	        

	        mSaveSSOButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Log.d(TAG, "mSaveSSOButton clicked");
	                MainActivity.ssoSetup = getSSOInfo();
	                MainActivity.listSSO.clear();
					MainActivity.listSSO.add(MainActivity.ssoSetup);
					MainActivity.adapterSSO.notifyDataSetChanged();	
					finish();
	            }
	        });     
	 }
	
	private BayesServer getSSOInfo(){
    	String name = mSSO_Name.getText().toString();
		String password = mSSO_Password.getText().toString();
		String URL = mSSO_URL.getText().toString();
		BayesServer ssoSetup = new BayesServer(URL, name, password);
		Log.d(TAG, "get sso " + ssoSetup);
		
		return ssoSetup;
    }
	
}
