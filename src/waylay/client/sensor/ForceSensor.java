package waylay.client.sensor;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import waylay.client.statistcs.LPFWikipedia;
import waylay.client.statistcs.LowPassFilter;
import waylay.client.statistcs.MeanFilter;
import waylay.client.statistcs.SimpleLinearAcceleration;


public class ForceSensor extends AbstractLocalSensor {
	private SimpleLinearAcceleration simpleLinearAcceleration;
    
	private float x = -1;
    private float y =  -1;
    private float z =  -1;
	private float force = -1;
	private long lastUpdate;

	public ForceSensor() {
		this(false);
	}
	
	public ForceSensor(boolean comensate) {
		super();
		if(comensate){
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
		if(force == -1)
			return "No data";
		return "OK";
	}
	
	public String getName(){
		return "Force";
	}

	@Override
	public String toString() {
		return "[force=" +  new DecimalFormat("#.##").format(getForce())+ "]";
	}

	@Override
	public int getId() {
		return 3;
	}
    
	public float getForce(){
		return force;
	}
	
	public long getLastUpdate(){
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public void updateData(long time, float [] values) {
		float diffTime = (time - lastUpdate) * NS2S;
		float [] filteredSample = values;
		if(simpleLinearAcceleration != null)
			filteredSample = simpleLinearAcceleration.addSamples(values) ;
		force = (float) (Math.abs(filteredSample[0] + filteredSample[1]  + filteredSample[2]  - x - y - z)/diffTime);
		x = filteredSample[0];
		y = filteredSample[1] ;
		z = filteredSample[2];
		setLastUpdate(time);
	}

	@Override
	public Map<String, String> getRuntimeData() {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("runtime_force", Double.toString(force));
		return map;
	}

}
