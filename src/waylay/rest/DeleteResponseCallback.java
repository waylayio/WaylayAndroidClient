package waylay.rest;

/**
 *
 * Class definition for a callback to be invoked when the response for the data
 * submission is available.
 *
 */
public interface DeleteResponseCallback {

    /**
     * Called when a DELETE success response is received. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    public abstract void onDeleteSuccess();

    /**
     * Called when a DELETE failure response is received. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    public abstract void onDeleteFailure();


}
