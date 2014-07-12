package waylay.utils;

import java.net.InetAddress;

import android.util.Log;

public class Ping {
	private static final String TAG = "Ping";
	String error;
    public boolean execute(String address) {
    	try {
			InetAddress inetAddress = InetAddress.getByName(address);
			if(inetAddress.isReachable(5000)){
				return true;
			}
		} catch (Exception e) {
			error = e.toString();
			Log.e(TAG, error);
			return false;
			
		}	
    	return true;
    }
    
    public String getError(){
    	return error;
    }

    
  
}
