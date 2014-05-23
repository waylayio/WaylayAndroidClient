package waylay.client.sensor;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RawSensor extends AbstractLocalSensor implements SensorEventListener{

    private final Sensor sensor;

    private SensorListener listener;
    private SensorEvent lastEvent;

    public RawSensor(final Sensor sensor) {
        this.sensor = sensor;
    }

    public void start(SensorManager manager, SensorListener listener){
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.listener = listener;
    }

    public void stop(SensorManager manager){
        manager.unregisterListener(this);
        this.listener = null;
        this.lastEvent = null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.lastEvent = event;
        if(listener != null){
            listener.onSensorUpdate();
        }
    }

    @Override
    public String getStatus() {
        if(lastEvent != null){
            return "OK";
        }
        return "Waiting for data";
    }

    @Override
    public String getName() {
        return "RAW - " + sensor.getName();
    }

    @Override
    public Map<String, String> getRuntimeData() {
        Map<String,String> data = new HashMap<String, String>();
        if(lastEvent != null) {
            data.put("values", Arrays.toString(lastEvent.values));
        }
        return data;
    }

    @Override
    public int getId() {
        return 100 + sensor.getType();
    }

    @Override
    public String toString() {
        if(lastEvent != null){
            return Arrays.toString(lastEvent.values);
        }
        return sensor.toString();
    }
}
