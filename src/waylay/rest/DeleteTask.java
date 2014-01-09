package waylay.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing POSTs on the REST APIs.
 */
public class DeleteTask extends AsyncTask<String, String, String>{
    private static final String TAG = "Delete Task";
	private String mRestUrl;
	private RestTaskCallback mCallback;

    /**
     * Creates a new instance of PostTask with the specified URL, callback, and
     * request body.
     * 
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     * 
     */
    public DeleteTask(String restUrl, RestTaskCallback callback){
        this.mRestUrl = restUrl;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(String... arg0) {
		 HttpClient httpclient = new DefaultHttpClient();  
	     HttpDelete delete = new HttpDelete(mRestUrl);
	     String result = "NOK";
	      try {
	    	  HttpResponse response = httpclient.execute(delete);
	          int statusCode = response.getStatusLine().getStatusCode();
	          if( statusCode == 200)
	        	  result = "OK";
		} catch (Exception e1) {
			e1.printStackTrace();
			result = "NOK";
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
}