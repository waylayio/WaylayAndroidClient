package waylay.client.sensor;

import java.util.Map;


public abstract class AbstractLocalSensor implements IdentifiedSensor {
	public abstract String getStatus();

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
        IdentifiedSensor other = (IdentifiedSensor) obj;
		return getId() == other.getId();
	}
	public abstract Map<String, Object> getData();
}
