package waylay.client.sensor;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import waylay.client.service.ActivityManager;

public class ActivitySensor extends AbstractLocalSensor implements ActivityListener {

    private static final String TAG = "ActivitySensor";


    private ActivityResult lastResult;


    public void start(final ActivityManager activityManager){
        Log.i(TAG, "START");
        activityManager.startUpdates(this);
    }

    public void stop(final ActivityManager activityManager){
        Log.i(TAG, "STOP");
        activityManager.stopUpdates();
        lastResult = null;
    }

    @Override
    public void onActivityDetected(final ActivityResult activityResult) {
        lastResult = activityResult;
        Log.i(TAG, activityResult.toString());
    }

    @Override
    public String getStatus() {
        if(lastResult != null){
            return "OK";
        }
        return "not active";
    }

    @Override
    public String getName() {
        return "Activity Recognition";
    }

    @Override
    public Map<String, Object> getData() {
        Map<String,Object> data = new HashMap<String,Object>();
        if(lastResult != null){
            data.put("confidence", lastResult.confidence);
            data.put("type", lastResult.type);
            data.put("name", lastResult.name);
        }
        return data;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public String toString() {
        if(lastResult == null){
            return "unknown";
        }
        return getData().toString();
    }


}
