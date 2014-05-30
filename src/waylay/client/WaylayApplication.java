package waylay.client;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import waylay.client.data.BayesServer;
import waylay.client.sensor.AbstractLocalSensor;
import waylay.rest.PostResponseCallback;
import waylay.rest.WaylayRestClient;
import waylay.rest.xively.DeviceList;
import waylay.rest.xively.DeviceResponse;
import waylay.rest.xively.Devices;
import waylay.rest.xively.Serial;
import waylay.rest.xively.XivelyError;
import waylay.rest.xively.XivelyRestApi;
import waylay.rest.xively.XivelyRestClient;
import waylay.utils.UniqueId;

public class WaylayApplication extends Application{

    private static final String TAG = "WaylayApplication";

    public static final TimeUnit PUSH_TIMEUNIT = TimeUnit.SECONDS;
    public static final long PUSH_PERIOD = 1;
    public static final String XIVELY_PRODUCT_ID = "eN9d24gh_pYMbrPoi1Ll";
    public static final String XIVELY_API_KEY = "???";

    public static List<BayesServer> servers = new ArrayList<BayesServer>();
    private static BayesServer selectedBayesServer = null;

    private static Set<ScheduledFuture<?>> pushers = new HashSet<ScheduledFuture<?>>();


    public static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onCreate() {
        super.onCreate();
        com.estimote.sdk.utils.L.enableDebugLogging(true);
        initServer();

        final String uniqueId = UniqueId.get(this);

        final XivelyRestClient xively = new XivelyRestClient(XIVELY_API_KEY);
        xively.makeSureDeviceExists(XIVELY_PRODUCT_ID, uniqueId, null);
    }

    public static WaylayRestClient getRestService(){
        return new WaylayRestClient(selectedBayesServer);
    }

    public static void startPushing(final AbstractLocalSensor localSensor, final Long id, final String node) {
        Log.i(TAG, "start pushing data to scenario " + id + " , node " + node);
        pushers.add(executorService.scheduleWithFixedDelay(new Pusher(id, node, localSensor), 0, PUSH_PERIOD, PUSH_TIMEUNIT));
    }

    public static void stopPushing(){
        Log.i(TAG, "stop pushing all data");
        for(ScheduledFuture<?> pusher:pushers){
            pusher.cancel(true);
        }
    }

    public static BayesServer getSelectedServer() {
        return selectedBayesServer;
    }

    public static void selectServer(BayesServer bayesServer){
        if(!servers.contains(bayesServer)){
            servers.add(bayesServer);
        }
        selectedBayesServer = bayesServer;
    }

    private void initServer() {
        if(servers.size() == 0) {
            servers.add(new BayesServer("app.waylay.io", "admin", "admin"));
            servers.add(new BayesServer("107.170.20.30", "admin", "admin"));
        }
        selectedBayesServer = servers.get(0);
    }

    private static class Pusher implements Runnable{

        private final long scenarioId;
        private final String node;
        private final AbstractLocalSensor sensor;

        // keep this around as we don' want to suddenly change servers
        private final WaylayRestClient service;

        private Pusher(final long scenarioId, final String node, final AbstractLocalSensor sensor) {
            this.scenarioId = scenarioId;
            this.node = node;
            this.sensor = sensor;
            this.service =  WaylayApplication.getRestService();
        }

        @Override
        public void run() {
            try {
                Map<String, String> mapping = sensor.getRuntimeData();
                for (Map.Entry<String, String> entry : mapping.entrySet()) {
                    final Map.Entry<String, String> finalEntry = entry;
                    Log.i(TAG, "pushing data from " + sensor.getName() + ", send runtime_property " + finalEntry.getKey() + " = " + finalEntry.getValue());
                    service.postScenarioNodeValueAction(scenarioId, node, finalEntry.getKey(), finalEntry.getValue(), new PostResponseCallback() {
                        @Override
                        public void onPostSuccess() {
                            Log.i(TAG, "pushed data from " + sensor.getName() + ", send runtime_property " + finalEntry);
                        }
                    });
                }
            }catch (Exception ex){
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

}
