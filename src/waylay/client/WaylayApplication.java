package waylay.client;

import android.app.Application;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import waylay.client.data.BayesServer;
import waylay.client.sensor.AbstractLocalSensor;
import waylay.rest.PostResponseCallback;
import waylay.rest.RestAPI;

public class WaylayApplication extends Application{

    private static final String TAG = "WaylayApplication";

    public static final TimeUnit PUSH_TIMEUNIT = TimeUnit.SECONDS;
    public static final long PUSH_PERIOD = 1;

    public static List<BayesServer> servers = new ArrayList<BayesServer>();
    private static BayesServer selectedBayesServer = null;

    private static Set<ScheduledFuture<?>> pushers = new HashSet<ScheduledFuture<?>>();


    public static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onCreate() {
        super.onCreate();
        com.estimote.sdk.utils.L.enableDebugLogging(true);
        initServer();
    }

    public static RestAPI getRestService(){
        return new RestAPI(selectedBayesServer);
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
        private final RestAPI service;

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
