package waylay.rest;

import java.util.ArrayList;

import waylay.client.data.MachineInfo;


/**
* Class definition for a callback to be invoked when the response data for the
* GET call is available.
*/
public abstract class GetResponseCallback{

 /**
  * Called when the response data for the REST call is ready. <br/>
  * This method is guaranteed to execute on the UI thread.
  */
 public abstract void onDataReceived(ArrayList list, boolean error, String message);
 
 public abstract void onUpdate(boolean error, String message);
 
 public abstract void onDashboardReceived(Dashboard dashboard, boolean error, String message);
} 


