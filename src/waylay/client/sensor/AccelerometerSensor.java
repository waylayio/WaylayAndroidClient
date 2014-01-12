package waylay.client.sensor;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import waylay.client.statistcs.LPFWikipedia;
import waylay.client.statistcs.LowPassFilter;
import waylay.client.statistcs.MeanFilter;
import waylay.client.statistcs.SimpleLinearAcceleration;
import android.hardware.SensorManager;

public class AccelerometerSensor extends LocalSensor{
	private static final float TILT_THRESHOLD = 1.05f;
	private SimpleLinearAcceleration simpleLinearAcceleration;
	private float acceleration = -1;
	private long lastUpdate;

	public AccelerometerSensor() {
		this(false);
	}
	
	public AccelerometerSensor(boolean compensate) {
		super();
		if(compensate){
		    LowPassFilter lpfAcceleration = null;
		    MeanFilter meanFilterAcceleration = null;
		    float accelerationLPFAlpha = 0.4f;
		    int accelerationMeanFilterWindow = 10;
			lpfAcceleration = new LPFWikipedia();
			lpfAcceleration.setAlphaStatic(false);
			lpfAcceleration.setAlpha(accelerationLPFAlpha);
	        meanFilterAcceleration = new MeanFilter();
	        meanFilterAcceleration.setWindowSize(accelerationMeanFilterWindow);
			simpleLinearAcceleration = new SimpleLinearAcceleration(lpfAcceleration, meanFilterAcceleration);
			
		}
	}


	@Override
	public String getStatus() {
		if(acceleration == -1)
			return "No data";
		return "OK";
	}
	
	public String getName(){
		return "Accelerometer";
	}

	@Override
	public String toString() {
		return "[acceleration=" + new DecimalFormat("#.##").format(getAcceleration()) + "]";
	}

	@Override
	public int getId() {
		return 2;
	}
    
	public float getAcceleration() {
		return acceleration;
	}

	public long getLastUpdate(){
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public void updateData(long time, float []values) {
		float [] filteredSample = values;
		if(simpleLinearAcceleration != null)
			filteredSample = simpleLinearAcceleration.addSamples(values);
		acceleration  = ((float) (Math.sqrt(filteredSample[0] * filteredSample[0]  + 
				filteredSample[1]  * filteredSample[1]  + filteredSample[2]  * filteredSample[2]))) 
				/SensorManager.GRAVITY_EARTH;
		setLastUpdate(time);
	}

	public boolean isTilt(float[] values) {
		float [] filteredSample = values;
		if(simpleLinearAcceleration != null)
			filteredSample = simpleLinearAcceleration.addSamples(values);
		acceleration  = ((float) (Math.sqrt(filteredSample[0] * filteredSample[0]  + 
				filteredSample[1]  * filteredSample[1]  + filteredSample[2]  * filteredSample[2]))) 
				/SensorManager.GRAVITY_EARTH;
		return acceleration < TILT_THRESHOLD;
	}

	@Override
	public Map<String, String> getRuntimeData() {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("runtime_accelerator", Double.toString(acceleration));
		return map;
	}

}
