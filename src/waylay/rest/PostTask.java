package waylay.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing POSTs on the REST APIs.
 */
public class PostTask extends AsyncTask<String, String, String>{
    private static final String TAG = "Post Task";
	private String mRestUrl;
    private RestTaskCallback mCallback;
    private List<NameValuePair> mRequestBody;

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
    public PostTask(String restUrl, List<NameValuePair> nameValuePairs, RestTaskCallback callback){
        this.mRestUrl = restUrl;
        this.mRequestBody = nameValuePairs;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(String... arg0) {
		 HttpClient httpclient = new DefaultHttpClient();  
	     HttpPost post = new HttpPost(mRestUrl);
	     try {
			post.setEntity(new UrlEncodedFormEntity(mRequestBody));
	     } catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	     
	     String result;
		 try {  
			 HttpResponse response = httpclient.execute(post);  
	            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	            String line = "";
	            result = "";
	            while ((line = rd.readLine()) != null) {
	            	Log.d(TAG, line); 
	            	result+=line;
	            	if (line.startsWith("Auth=")) {
	            		String key = line.substring(5);
	            		// do something with the key
	            	}
	            }
	        } catch (Exception e) {  
	            e.printStackTrace();
	            result = "no result";
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