package waylay.client.sensor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationSensor extends AbstractLocalSensor {
	private double latitude = -1.0;
	private double longitude = -1.0;
	
	public LocationSensor(){
		
	}
	public LocationSensor(int latitude, int longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getStatus(){
		if(longitude == -1 && latitude == -1)
			return "Searching";
		return "OK";
	}

	@Override
	public String toString() {
		return "[latitude=" + latitude + ", longitude="+ longitude + "]";
	}

	@Override
	public int getId() {
		return 1;
	}
	
	public String getName(){
		return "Location";
	}
	@Override
	public Map<String, String> getRuntimeData() {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("runtime_latitude", Double.toString(latitude));
		map.put("runtime_longitude", Double.toString(longitude));
		return map;
	}

}
