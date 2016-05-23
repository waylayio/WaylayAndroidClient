package waylay.client;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.joshdholtz.sentry.Sentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import waylay.client.data.BayesServer;
import waylay.client.data.ConnectionSettings;
import waylay.mqtt.MqttManager;
import waylay.mqtt.MqttServer;
import waylay.mqtt.PahoMqttManager;
import waylay.mqtt.PahoMqttServer;
import waylay.client.service.push.PushService;
import waylay.client.service.push.WaylayPushService;
import waylay.rest.WaylayRestClient;
import waylay.utils.ResourceId;

public class WaylayApplication extends Application {
    private static final String TAG = "WaylayApplication";
    private static final String PREF_APP = "servers";
    private static final String PREF_KEY_SERVERS = "servers";
    private static final String PREF_SETTINGS = "connectionsettings";
    private static final Gson GSON = new Gson();
    private static final String CONNECTION_TYPE = "ConnectionType";
    private static final String KEY = "Key";
    private static final String VALUE = "Value";
    private static final String AUTH = "UseAuthForMQTT";
    public static final String SELECTEDSERVER = "SelectedServer";
    public static final String EMPTY = "";
    private static List<BayesServer> servers = new ArrayList<>();
    private static BayesServer selectedBayesServer = null;

    private ConnectionSettings mConnectionSettings;
    private MqttManager mqttManager;
    private PushService pushService;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Sentry.init(this, getApplicationContext().getString(R.string.sentry_private_dsn));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        com.estimote.sdk.EstimoteSDK.enableDebugLogging(true);
        initServers();
        mqttManager = new PahoMqttManager(this);
        pushService = new WaylayPushService(this, mqttManager);

    }

    public MqttManager getMqttManager() {
        return mqttManager;
    }

    public static WaylayRestClient getRestService(){
        return new WaylayRestClient(selectedBayesServer);
    }

    public PushService getPushService() {
       return pushService;
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
        SharedPreferences sharedPrefs = getSharedPreferences(SELECTEDSERVER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(SELECTEDSERVER, bayesServer.hashCode());
        editor.apply();
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
            servers.add(new BayesServer("app.waylay.io", getResources().getString(R.string.waylay_api_key), getResources().getString(R.string.waylay_api_secret), getString(R.string.waylay_api_key), "devices.waylay.io", "data.waylay.io", true));
            servers.add(new BayesServer("demo.waylay.io", getResources().getString(R.string.waylay_api_key), getResources().getString(R.string.waylay_api_secret), getString(R.string.waylay_api_key), "devices.waylay.io", "data.waylay.io", true));
            servers.add(new BayesServer("10.10.131.177:8888/rest/bn", "admin", "admin", getString(R.string.waylay_api_key), "devices.waylay.io", "data.waylay.io", false));
        }
        SharedPreferences sharedPrefs = getSharedPreferences(SELECTEDSERVER, Context.MODE_PRIVATE);
        int hash = sharedPrefs.getInt(SELECTEDSERVER, 0);
        selectedBayesServer = servers.get(0);

        for (BayesServer server : servers) {
            if (hash == server.hashCode()) {
                selectedBayesServer = server;
                break;
            }
        }
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

    public void storeSettings() {
        if (mConnectionSettings != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                editor.putString(CONNECTION_TYPE, mConnectionSettings.getConnectionType());
                editor.putString(KEY, mConnectionSettings.getKey());
                editor.putString(VALUE, mConnectionSettings.getValue());
                editor.putBoolean(AUTH, mConnectionSettings.isUseAuth());

                editor.apply();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

    public ConnectionSettings getConnectionSettings() {
        if (mConnectionSettings == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
            mConnectionSettings = new ConnectionSettings();
            mConnectionSettings.setConnectionType(sharedPreferences.getString(CONNECTION_TYPE, ConnectionSettings.HTTP));
            mConnectionSettings.setKey(sharedPreferences.getString(KEY, EMPTY));
            mConnectionSettings.setValue(sharedPreferences.getString(VALUE, EMPTY));
            mConnectionSettings.setUseAuth(sharedPreferences.getBoolean(AUTH, false));
        }
        return mConnectionSettings;
    }

}
