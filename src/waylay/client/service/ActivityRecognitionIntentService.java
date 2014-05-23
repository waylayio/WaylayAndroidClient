package waylay.client.service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Service that receives ActivityRecognition updates. It receives
 * updates in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {

    private static final String TAG = "ActivityRecognitionIntentService";
    public static final String WAYLAY_ACTIVITY_BROADCAST = "waylay.activity.Broadcast";
    public static final String EXTRA_ACTIVITY_NAME = "activityName";
    public static final String EXTRA_ACTIVITY_TYPE = "activityType";
    public static final String EXTRA_ACTIVITY_CONFIDENCE = "activityConfidence";

    public ActivityRecognitionIntentService(){
        this("ActivityRecognitionIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActivityRecognitionIntentService(String name) {
        super(name);
        Log.d(TAG, "created " + getClass().getName());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "started " + getClass().getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed " + getClass().getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Got intent: " + intent);
        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();
            /*
             * Get an integer describing the type of activity
             */
            int activityType = mostProbableActivity.getType();
            String activityName = getNameFromType(activityType);
            /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */
            Log.d(TAG, activityName);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(WAYLAY_ACTIVITY_BROADCAST);
            broadcastIntent.putExtra(EXTRA_ACTIVITY_NAME, activityName);
            broadcastIntent.putExtra(EXTRA_ACTIVITY_TYPE, activityType);
            broadcastIntent.putExtra(EXTRA_ACTIVITY_CONFIDENCE, confidence);
            sendBroadcast(broadcastIntent);

        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        }
    }

    /**
     * Map detected activity types to strings
     *@param activityType The detected activity type
     *@return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

}
