package waylay.rest;

import java.util.ArrayList;
import java.util.List;

import waylay.client.data.MachineInfo;


/**
* Class definition for a callback to be invoked when the response data for the
* GET call is available.
*/
public abstract class GetResponseCallback<T>{

    /**
     * Called when the response data for the REST call is ready. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    public abstract void onDataReceived(T data, boolean error, String message);
} 


