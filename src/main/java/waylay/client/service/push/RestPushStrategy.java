package waylay.client.service.push;

import android.util.Log;

import java.util.Map;

import waylay.client.WaylayApplication;
import waylay.client.sensor.AbstractLocalSensor;
import waylay.rest.PostResponseCallback;
import waylay.rest.WaylayRestClient;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public class RestPushStrategy implements PushStrategy {
    public static final String TAG = "RestPushStrategy";
    private final WaylayRestClient service = WaylayApplication.getRestService();
    private final AbstractLocalSensor sensor;

    public RestPushStrategy(AbstractLocalSensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public void push(String resource, final Map<String, Object> data) {

        service.postResourceValue(resource, data, new PostResponseCallback<Void>() {
            @Override
            public void onPostSuccess(Void t) {
                Log.i(TAG, "<-- pushed data from " + sensor.getName() + ", sent " + data);
            }

            @Override
            public void onPostFailure(Throwable t) {
                Log.e(TAG, "failed to push data from " + sensor.getName() + ", sent " + data, t);
            }
        });
    }
}
