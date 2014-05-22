package waylay.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing GETs on the REST APIs.
 */
public class GetTask extends AsyncTask<String, String, String>{

	private static final String TAG = "Get Task";
	private String mRestUrl;
	private RestTaskCallback mCallback;
	private String userName;
	private String password;
	public static String NO_RESULT = "no result";
	private static String error = "No error";

	/**
	 * Creates a new instance of GetTask with the specified URL and callback.
	 * 
	 * @param restUrl The URL for the REST API.
	 * @param callback The callback to be invoked when the HTTP request
	 *            completes.
	 * 
	 */
	public GetTask(String restUrl, String userName, String password, RestTaskCallback callback){
		this.mRestUrl = restUrl;
		this.userName = userName;
		this.password = password;
		this.mCallback = callback;
	}

	@Override
	protected String doInBackground(String... params) {

		URI uri;
		try {
			uri = new URI(mRestUrl);
		} catch (URISyntaxException e) {
            Log.e(TAG,e.getMessage(), e);
			return null;
		}
		Log.d(TAG, "start http sesssion with URL" + mRestUrl);
		AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());    
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		httpclient.getCredentialsProvider().setCredentials(authScope, credentials);


		HttpGet request = new HttpGet(uri);
		ResponseHandler<String> handler = new BasicResponseHandler();  
		String result = NO_RESULT;
		try { 
			result = httpclient.execute(request, handler);  
		} catch (Exception e) {  
			error = e.getMessage();
			Log.e(TAG, e.getMessage());
		} 
		httpclient.getConnectionManager().shutdown();  
		Log.i(TAG, result);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		mCallback.onTaskComplete(result);
		super.onPostExecute(result);
	}

	public static String getError(){
		return error;
	}
}



