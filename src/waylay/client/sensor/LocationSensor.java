package waylay.client.sensor;

public class LocationSensor extends LocalSensor{
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

}
