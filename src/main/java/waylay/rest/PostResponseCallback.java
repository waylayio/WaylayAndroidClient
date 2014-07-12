package waylay.rest;

/**
* 
* Class definition for a callback to be invoked when the response for the data 
* submission is available.
* 
*/
public interface PostResponseCallback <T>{
     /**
      * Called when a POST success response is received. <br/>
      * This method is guaranteed to execute on the UI thread.
      */
     void onPostSuccess(T t);

    /**
     * Called when a DELETE failure response is received. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    void onPostFailure(Throwable t);

}
