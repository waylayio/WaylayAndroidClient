package waylay.client.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.concurrent.TimeUnit;

import waylay.client.sensor.ActivityListener;
import waylay.client.sensor.ActivityResult;

public class ActivityManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = ActivityManager.class.getSimpleName();

    // Constants that define the activity detection interval
    // TODO make this configurable
    public static final int DETECTION_INTERVAL_MILLISECONDS = (int) TimeUnit.SECONDS.toMillis(1);

    private enum REQUEST_TYPE {START, STOP}
    private REQUEST_TYPE mRequestType;

    /*
     * Store the PendingIntent used to send activity recognition events
     * back to the app
     */
    private final PendingIntent mActivityRecognitionPendingIntent;

    private final Context context;

    private final BroadcastReceiver receiver;

    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    // Store the current activity recognition client
    private GoogleApiClient googleApiClient;

    private ActivityListener listener;

    public ActivityManager(Context context){
        this.context = context;

        // Start with the request flag set to false
        mInProgress = false;

        /*
         * Instantiate a new activity recognition client. Since the
         * parent Activity implements the connection listener and
         * connection failure listener, the constructor uses "this"
         * to specify the values of those parameters.
         */
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        /*
         * Create the PendingIntent that Location Services uses
         * to send activity recognition updates back to this app.
         */
        Intent intent = new Intent(context, ActivityRecognitionIntentService.class);
        /*
         * Return a PendingIntent that starts the IntentService.
         */
        mActivityRecognitionPendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.receiver = new ActivityBroadcastReceiver(new ActivityListener() {
            @Override
            public void onActivityDetected(ActivityResult activityResult) {
                if(listener != null){
                    listener.onActivityDetected(activityResult);
                }
            }
        });
    }

    /*
     * Called by Location Services once the location client is connected.
     *
     * Continue by requesting activity updates.
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        Log.i(TAG, "Connected to recognition service");
        switch (mRequestType) {
            case START :
                /*
                 * Request activity recognition updates using the
                 * preset detection interval and PendingIntent.
                 * This call is synchronous.
                 */
                if(googleApiClient.isConnected()) {
                    Log.i(TAG, "Starting activity updates");
                    ActivityRecognition.ActivityRecognitionApi
                            .requestActivityUpdates(googleApiClient, DETECTION_INTERVAL_MILLISECONDS, mActivityRecognitionPendingIntent);
                }
                break;
                /*
                 * An enum was added to the definition of REQUEST_TYPE,
                 * but it doesn't match a known case. Throw an exception.
                 */
            case STOP:
                if(googleApiClient.isConnected()) {
                    Log.i(TAG, "Stopping activity updates");
                    ActivityRecognition.ActivityRecognitionApi
                            .removeActivityUpdates(googleApiClient, mActivityRecognitionPendingIntent);
                }
                break;
            default :
                throw new RuntimeException("Unknown request type in onConnected().");
        }
//        /*
//         * Request activity recognition updates using the preset
//         * detection interval and PendingIntent. This call is
//         * synchronous.
//         */
//        googleApiClient.requestActivityUpdates(
//                DETECTION_INTERVAL_MILLISECONDS,
//                mActivityRecognitionPendingIntent);
        /*
         * Since the preceding call is synchronous, turn off the
         * in progress flag and disconnect the client
         */
        mInProgress = false;
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Turn off the request flag
        mInProgress = false;
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    // Implementation of OnConnectionFailedListener.onConnectionFailed
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Turn off the request flag
        mInProgress = false;
//        /*
//         * If the error has a resolution, start a Google Play services
//         * activity to resolve it.
//         */
//        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(
//                        this,
//                        MainActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//            // If no resolution is available, display an error dialog
//        } else {
//            // Get the error code
//            int errorCode = connectionResult.getErrorCode();
//            // Get the error dialog from Google Play services
//            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
//                    errorCode,
//                    this,
//                    MainActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            // If Google Play services can provide an error dialog
//            if (errorDialog != null) {
//                // Create a new DialogFragment for the error dialog
//                MainActivity.ErrorDialogFragment errorFragment =
//                        new MainActivity.ErrorDialogFragment();
//                // Set the dialog in the DialogFragment
//                errorFragment.setDialog(errorDialog);
//                // Show the error dialog in the DialogFragment
//                errorFragment.show(
//                        get(),
//                        "Activity Recognition");
//            }
//        }

    }

    /**
     * Request activity recognition updates based on the current
     * detection interval.
     *
     */
    public void startUpdates(ActivityListener listener) {
        IntentFilter filter = new IntentFilter(ActivityRecognitionIntentService.WAYLAY_ACTIVITY_BROADCAST);
        context.registerReceiver(receiver, filter);
        this.listener = listener;
        mRequestType = REQUEST_TYPE.START;
        // Check for Google Play services

//        if (!servicesConnected()) {
//            return;
//        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            googleApiClient.connect();
            //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }

    /**
     * Turn off activity recognition updates
     *
     */
    public void stopUpdates() {
        context.unregisterReceiver(receiver);
        this.listener = null;
        // Set the request type to STOP
        mRequestType = REQUEST_TYPE.STOP;
//        /*
//         * Test for Google Play services after setting the request type.
//         * If Google Play services isn't present, the request can be
//         * restarted.
//         */
//        if (!servicesConnected()) {
//            return;
//        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            googleApiClient.connect();
            //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }


    private static class ActivityBroadcastReceiver extends BroadcastReceiver {

        private final ActivityListener listener;

        private ActivityBroadcastReceiver(final ActivityListener listener){
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra(ActivityRecognitionIntentService.EXTRA_ACTIVITY_NAME);
            int type = intent.getIntExtra(ActivityRecognitionIntentService.EXTRA_ACTIVITY_TYPE, -1);
            int confidence = intent.getIntExtra(ActivityRecognitionIntentService.EXTRA_ACTIVITY_CONFIDENCE, -1);
            ActivityResult result = new ActivityResult(confidence, type, name);
            listener.onActivityDetected(result);
        }
    }
}
