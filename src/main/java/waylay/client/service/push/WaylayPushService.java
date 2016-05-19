package waylay.client.service.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;
import waylay.client.data.ConnectionSettings;
import waylay.mqtt.MqttManager;
import waylay.mqtt.MqttServer;
import waylay.client.sensor.AbstractLocalSensor;
import waylay.utils.ResourceId;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public class WaylayPushService implements PushService {
    public static final String TAG = "WaylayPushService";
    public static final String ISPUSHING = "IsPushing";
    public static final TimeUnit PUSH_TIMEUNIT = TimeUnit.SECONDS;

    private final WaylayApplication wapp;
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private final Set<ScheduledFuture<?>> pushers = new HashSet<>();
    private final MqttManager mqttManager;

    public WaylayPushService(WaylayApplication wapp, MqttManager mqttManager) {
        this.wapp = wapp;
        this.mqttManager = mqttManager;
    }

    @Override
    public boolean isPushing() {
        SharedPreferences sharedPrefs = wapp.getSharedPreferences(ISPUSHING, Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(ISPUSHING, false);
    }

    @Override
    public void pushOnce(AbstractLocalSensor sensor) {
        executorService.schedule(new Pusher(ResourceId.get(wapp), sensor, createPushStrategy(wapp.getConnectionSettings(), sensor), wapp.getSelectedServer()), 0, PUSH_TIMEUNIT);
    }

    private PushStrategy createPushStrategy(ConnectionSettings settings, final AbstractLocalSensor sensor) {
        if (settings.getConnectionType() == ConnectionSettings.MQTT) {
            MqttServer server = mqttManager.getMqttServer(wapp.getSelectedServer().getDataBroker());
            if (settings.isUseAuth()) {
                server.setUserName(settings.getKey());
                server.setPassword(settings.getValue().toCharArray());
            }
            return new MqttPushStrategy(wapp.getSelectedServer().getHost(), server);
        }
        return new RestPushStrategy(sensor);
    }

    @Override
    public void startPushing(AbstractLocalSensor sensor, long interval) {
        Log.i(TAG, "start pushing data for sensor " + sensor.getName());
        // TODO make sure we don't add a sensor twice
        SharedPreferences sharedPrefs = wapp.getSharedPreferences(ISPUSHING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(ISPUSHING, true);
        editor.apply();
        pushers.add(executorService.scheduleWithFixedDelay(new Pusher(ResourceId.get(wapp), sensor, createPushStrategy(wapp.getConnectionSettings(), sensor), wapp.getSelectedServer()), 0, interval, PUSH_TIMEUNIT));
    }

    @Override
    public void stopPushing() {
        SharedPreferences sharedPrefs = wapp.getSharedPreferences(ISPUSHING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(ISPUSHING, false);
        editor.apply();
        Log.i(TAG, "stop pushing all data");
        for(ScheduledFuture<?> pusher:pushers){
            pusher.cancel(true);
        }
    }

    private static class Pusher implements Runnable {
        private final String resource;
        private final AbstractLocalSensor sensor;
        private final PushStrategy pushStrategy;
        private final BayesServer server;

        private Pusher(final String resource, final AbstractLocalSensor sensor, final PushStrategy strategy, final BayesServer server) {
            this.resource = resource;
            this.sensor = sensor;
            pushStrategy = strategy;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                final Map<String, Object> data = sensor.getData();
                data.put("domain", server.getHost());
                Log.i(TAG, "--> pushing data from " + sensor.getName() + ", sending " + data);
                pushStrategy.push(resource, data);
            }catch (Exception ex){
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }
}
