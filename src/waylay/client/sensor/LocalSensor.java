package waylay.client.sensor;

public abstract class LocalSensor {
	public abstract String getStatus();
	public abstract int getId();
	public abstract String getName();
	public static final float NS2S = 1.0f / 1000000000.0f;

	public int hashCode() {
		return getId();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationSensor other = (LocationSensor) obj;
		return getId() == other.getId();
	}
	
}
