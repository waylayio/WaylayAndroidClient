package waylay.rest;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * An AsyncTask implementation for performing POSTs on the REST APIs.
 */
public class DeleteTask extends AsyncTask<String, String, String>{
    private static final String TAG = "Delete Task";
    public static final String RESULT_NOK = "NOK";
    public static final String RESULT_OK = "OK";
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
    protected String doInBackground(String... args) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete delete = new HttpDelete(mRestUrl);
        String result = RESULT_NOK;
        try {
            HttpResponse response = httpclient.execute(delete);
            int statusCode = response.getStatusLine().getStatusCode();
            if( statusCode == HttpStatus.SC_OK) {
                result = RESULT_OK;
            }else{
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                response.getEntity().writeTo(bos);
                Log.e(TAG, statusCode + " " + bos.toString());
            }
        } catch (Exception e1) {
            Log.e(TAG, e1.getMessage(), e1);
            result = RESULT_NOK;
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