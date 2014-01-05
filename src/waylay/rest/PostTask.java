package waylay.rest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing POSTs on the Hypothetical REST APIs.
 */
public class PostTask extends AsyncTask<String, String, String>{
    private static final String TAG = "Post Task";
	private String mRestUrl;
    private RestTaskCallback mCallback;
    private String mRequestBody;

    /**
     * Creates a new instance of PostTask with the specified URL, callback, and
     * request body.
     * 
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     * @param requestBody The body of the POST request.
     * 
     */
    public PostTask(String restUrl, String requestBody, RestTaskCallback callback){
        this.mRestUrl = restUrl;
        this.mRequestBody = requestBody;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(String... arg0) {
		 HttpClient httpclient = new DefaultHttpClient();  
	     HttpGet request = new HttpGet(mRestUrl);
	     ResponseHandler<String> handler = new BasicResponseHandler();  
	     String result = "no result";
		 try {  
	            result = httpclient.execute(request, handler);  
	        } catch (ClientProtocolException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
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