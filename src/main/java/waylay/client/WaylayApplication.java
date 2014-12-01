package waylay.client;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

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
import waylay.rest.WaylayRestClient;
import waylay.utils.ResourceId;

public class WaylayApplication extends Application{

    private static final String TAG = "WaylayApplication";

    private static final String PREF_APP = "servers";
    private static final String PREF_KEY_SERVERS = "servers";

    public static final TimeUnit PUSH_TIMEUNIT = TimeUnit.SECONDS;
    public static final long PUSH_PERIOD = 1;
    public static final String XIVELY_PRODUCT_ID = "eN9d24gh_pYMbrPoi1Ll";
    public static final String XIVELY_API_KEY = "???";

    private static final Gson GSON = new Gson();

    private static List<BayesServer> servers = new ArrayList<BayesServer>();
    private static BayesServer selectedBayesServer = null;

    private static Set<ScheduledFuture<?>> pushers = new HashSet<ScheduledFuture<?>>();


    public static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onCreate() {
        super.onCreate();
        Crittercism.initialize(getApplicationContext(), "53bfecd683fb790c3c000004");
        com.estimote.sdk.utils.L.enableDebugLogging(true);
        initServers();

        //final XivelyRestClient xively = new XivelyRestClient(XIVELY_API_KEY);
        //xively.makeSureDeviceExists(XIVELY_PRODUCT_ID, ResourceId.get(this), null);
    }

    public static WaylayRestClient getRestService(){
        return new WaylayRestClient(selectedBayesServer);
    }

    public void startPushing(final AbstractLocalSensor localSensor) {
        Log.i(TAG, "start pushing data for sensor " + localSensor.getName());
        // TODO make sure we don't add a sensor twice
        pushers.add(executorService.scheduleWithFixedDelay(new Pusher(ResourceId.get(this), localSensor), 0, PUSH_PERIOD, PUSH_TIMEUNIT));
    }

    public void stopPushing(){
        Log.i(TAG, "stop pushing all data");
        for(ScheduledFuture<?> pusher:pushers){
            pusher.cancel(true);
        }
    }

    public String getResourceId() {
        return ResourceId.get(this);
    }

    public void setResourceId(String resourceId) {
        ResourceId.set(this, resourceId);
    }

    public List<BayesServer> getServers() {
        return servers;
    }

    public BayesServer getSelectedServer() {
        return selectedBayesServer;
    }

    public void selectServer(BayesServer bayesServer){
        if(!servers.contains(bayesServer)){
            servers.add(bayesServer);
            storeServers(servers);
        }
        selectedBayesServer = bayesServer;
    }

    public void deleteServer(BayesServer bayesServer){
        servers.remove(bayesServer);
        storeServers(servers);
        selectedBayesServer = servers.get(0);
    }

    private void initServers() {
        servers = loadStoredServers();
        if(servers.size() == 0) {
            servers.add(new BayesServer("app.waylay.io", "admin", "admin", true));
            servers.add(new BayesServer("demo.waylay.io", "admin", "admin", true));
            servers.add(new BayesServer("10.10.131.177:8888/rest/bn", "admin", "admin", false));
        }
        selectedBayesServer = servers.get(0);
    }

    private List<BayesServer> loadStoredServers(){
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        String serversJson = sharedPrefs.getString(PREF_KEY_SERVERS, "[]");
        try {
            servers = GSON.fromJson(serversJson, new TypeToken<List<BayesServer>>() {}.getType());
        }catch(JsonParseException ex){
            Log.w(TAG, ex.getMessage());
        }
        return servers;
    }

    private void storeServers(List<BayesServer> servers){
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        try {
            String serversJson = GSON.toJson(servers);
            editor.putString(PREF_KEY_SERVERS, serversJson);
        }catch(JsonParseException ex){
            Log.w(TAG, ex.getMessage());
        }
        editor.apply();
    }

    private static class Pusher implements Runnable{

        private final String resource;
        private final AbstractLocalSensor sensor;

        // keep this around as we don' want to suddenly change servers
        private final WaylayRestClient service;

        private Pusher(final String resource, final AbstractLocalSensor sensor) {
            this.resource = resource;
            this.sensor = sensor;
            this.service =  WaylayApplication.getRestService();
        }

        @Override
        public void run() {
            try {
                final Map<String, Object> data = sensor.getData();
                Log.i(TAG, "--> pushing data from " + sensor.getName() + ", sending " + data);
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
            }catch (Exception ex){
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

}
