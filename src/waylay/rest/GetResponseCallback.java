package waylay.rest;


/**
 * Class definition for a callback to be invoked when the response data for the
 * GET call is available.
 */
public interface GetResponseCallback<T>{

    /**
     * Called when the response data for the REST call is ready. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    void onDataReceived(T data);

    void onError(Throwable t);
} 


